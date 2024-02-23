package com.eatssu.android.ui.main.menu

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.App
import com.eatssu.android.data.model.Menu
import com.eatssu.android.data.model.Section
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.enums.Time
import com.eatssu.android.data.dto.response.ChangeMenuInfo
import com.eatssu.android.data.dto.response.GetFixedMenuResponseDto
import com.eatssu.android.data.dto.response.GetTodayMealResponseDto
import com.eatssu.android.data.service.MenuService
import com.eatssu.android.databinding.FragmentMenuBinding
import com.eatssu.android.ui.main.calendar.CalendarViewModel
import com.eatssu.android.util.RetrofitImpl
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private lateinit var menuViewModel: MenuViewModel
    private lateinit var menuService: MenuService

    private lateinit var menuDate: String

    val foodCourtDataLoaded = MutableLiveData<Boolean>()
    val snackCornerDataLoaded = MutableLiveData<Boolean>()
    val haksikDataLoaded = MutableLiveData<Boolean>()
    val dodamDataLoaded = MutableLiveData<Boolean>()
    val dormitoryDataLoaded = MutableLiveData<Boolean>()

    private val totalMenuList = ArrayList<Section>()


    companion object {
        fun newInstance(time: Time): MenuFragment {
            val fragment = MenuFragment()
            val args = Bundle()
            args.putSerializable("TIME", time)
            fragment.arguments = args
            return fragment
        }
    }

    private val time: Time
        get() = arguments?.getSerializable("TIME") as Time //Todo deprecated

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun observeViewModel(){
        menuService = RetrofitImpl.retrofit.create(MenuService::class.java)

        Log.d("MenuFragment", App.token_prefs.accessToken + "여기부터" + App.token_prefs.refreshToken)

        val calendardate = this.arguments?.getString("calendardata")
        Log.d("lunchdate", "$calendardate")

//        menuRepository = MenuRepository(menuService)
        menuViewModel =
            ViewModelProvider(this, MenuViewModelFactory(menuService))[MenuViewModel::class.java]

        val calendarViewModel = ViewModelProvider(requireActivity())[CalendarViewModel::class.java]

        val dayFormat = DateTimeFormatter.ofPattern("dd")
        val todayDate = LocalDateTime.now().format(dayFormat)

        // ViewModel에서 데이터 가져오기
        calendarViewModel.getData().observe(viewLifecycleOwner) { dataReceived ->

            val preSunday: LocalDateTime = LocalDateTime.now().with(
                TemporalAdjusters.previousOrSame(
                    DayOfWeek.SUNDAY
                )
            )

            val dateFormat =
                DateTimeFormatter.ofPattern("dd").withLocale(Locale.forLanguageTag("ko"))
            val fullFormat = DateTimeFormatter.ofPattern("yyyyMMdd").withLocale(Locale.forLanguageTag("ko"))

            for (i in 0..6) {
                if (preSunday.plusDays(i.toLong()).format(dateFormat) == dataReceived) {
                    menuDate = preSunday.plusDays(i.toLong()).format(fullFormat)
                }
            }

            Log.d("menucalendar", menuDate)

            // Assuming menuDate is a String in the format "yyyyMMdd"
            val formattedDate =
                LocalDate.parse(menuDate.substring(0, 8), DateTimeFormatter.BASIC_ISO_DATE)

            val dayOfWeek = formattedDate.dayOfWeek

            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY && time == Time.LUNCH) {
                // The date is not on a weekend
                //푸드코트
                menuViewModel.loadFixedMenu(Restaurant.FOOD_COURT)
                menuViewModel.fixedMenuDataFood.observe(viewLifecycleOwner) { result ->
                    totalMenuList.add(
                        Section(
                            MenuType.FIX,
                            Restaurant.FOOD_COURT,
                            mapFixedMenuResponseToMenu(result)
                        )
                    )
                    foodCourtDataLoaded.value = true
                    checkDataLoaded()
                }

                //스낵코너
                menuViewModel.loadFixedMenu(Restaurant.SNACK_CORNER)
                menuViewModel.fixedMenuDataSnack.observe(viewLifecycleOwner) { result ->
                    totalMenuList.add(
                        Section(
                            MenuType.FIX,
                            Restaurant.SNACK_CORNER,
                            mapFixedMenuResponseToMenu(result)
                        )
                    )
                    snackCornerDataLoaded.value = true
                    checkDataLoaded()
                }
                Log.d("MenuFragment", "The date $menuDate is not on a weekend.")
            }

            if ((dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY)) {
                // The date is not on a weekend
                foodCourtDataLoaded.value = true //푸드코트
                snackCornerDataLoaded.value = true //스낵코너
                checkDataLoaded()
                Log.d("MenuFragment", "The date $menuDate is not on a weekend.")
            }

            if(time!= Time.LUNCH){
                foodCourtDataLoaded.value = true //푸드코트
                snackCornerDataLoaded.value = true //스낵코너
                checkDataLoaded()
            }



            //학생식당
            menuViewModel.loadTodayMeal(menuDate, Restaurant.HAKSIK, time)
            menuViewModel.todayMealDataHaksik.observe(viewLifecycleOwner) { result ->
                if (result.isNotEmpty()) {
                    totalMenuList.add(
                        Section(
                            MenuType.CHANGE,
                            Restaurant.HAKSIK,
                            mapTodayMenuResponseToMenu(result)
                        )
                    )

//                    setupTodayRecyclerView()
                }
                haksikDataLoaded.value = true
                checkDataLoaded()
            }

            //숭실도담
            menuViewModel.loadTodayMeal(menuDate, Restaurant.DODAM, time)
            menuViewModel.todayMealDataDodam.observe(viewLifecycleOwner) { result ->
                if (result.isNotEmpty()) {
                    totalMenuList.add(
                        Section(
                            MenuType.CHANGE,
                            Restaurant.DODAM,
                            mapTodayMenuResponseToMenu(result)
                        )
                    )
                }
                dodamDataLoaded.value = true
                checkDataLoaded()
            }

            //기숙사식당
            menuViewModel.loadTodayMeal(menuDate, Restaurant.DORMITORY, time)
            menuViewModel.todayMealDataDormitory.observe(viewLifecycleOwner) { result ->
                if (result.isNotEmpty()) {
                    totalMenuList.add(
                        Section(
                            MenuType.CHANGE,
                            Restaurant.DORMITORY,
                            mapTodayMenuResponseToMenu(result)
                        )
                    )
                }
                dormitoryDataLoaded.value = true
                checkDataLoaded()
            }
        }
    }

    private fun setupTodayRecyclerView() {
        binding.rv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = MenuAdapter(totalMenuList)

        }
    }


    private fun createNameList(menuInfoList: List<ChangeMenuInfo>): String {
        val nameList = StringBuilder()

        for (menuInfo in menuInfoList) {
            nameList.append(menuInfo.name)
            nameList.append("+")
        }

        if (nameList.isNotEmpty()) {
            nameList.deleteCharAt(nameList.length - 1) // Remove the last '+'
        }

        return nameList.toString()
    }

    // Function to check if all data is loaded and then call setupTodayRecyclerView
    private fun checkDataLoaded() {
        if (foodCourtDataLoaded.value == true &&
            snackCornerDataLoaded.value == true &&
            haksikDataLoaded.value == true &&
            dodamDataLoaded.value == true &&
            dormitoryDataLoaded.value == true
        ) {
            totalMenuList.sortBy { it.cafeteria.ordinal }
            setupTodayRecyclerView()
        }
    }

    private fun mapTodayMenuResponseToMenu(todayMealResponseDto: GetTodayMealResponseDto): List<Menu> {
        return todayMealResponseDto.mapNotNull { todayMealResponseDto ->
            val name = createNameList(todayMealResponseDto.changeMenuInfoList)
            if (name.isNotEmpty()) {
                Menu(
                    id = todayMealResponseDto.mealId,
                    name = name,
                    price = todayMealResponseDto.price, // Assuming price is Int in Menu
                    rate = todayMealResponseDto.mainGrade
                )
            } else {
                null
            }
        }
    }

    private fun mapFixedMenuResponseToMenu(fixedMenuResponse: GetFixedMenuResponseDto): List<Menu> {
        return fixedMenuResponse.fixMenuInfoList.map { fixMenuInfo ->
            Menu(
                id = fixMenuInfo.menuId,
                name = fixMenuInfo.name,
                price = fixMenuInfo.price, // Assuming price is Int in Menu
                rate = fixMenuInfo.mainGrade
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}