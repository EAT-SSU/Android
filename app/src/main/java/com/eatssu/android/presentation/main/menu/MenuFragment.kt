package com.eatssu.android.presentation.main.menu

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.data.RetrofitImpl
import com.eatssu.android.data.dto.response.mapFixedMenuResponseToMenu
import com.eatssu.android.data.dto.response.mapTodayMenuResponseToMenu
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.enums.Time
import com.eatssu.android.data.service.MealService
import com.eatssu.android.data.service.MenuService
import com.eatssu.android.databinding.FragmentMenuBinding
import com.eatssu.android.domain.model.Section
import com.eatssu.android.presentation.info.InfoViewModel
import com.eatssu.android.presentation.main.calendar.CalendarViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var menuViewModel: MenuViewModel

    private lateinit var menuService: MenuService
    private lateinit var mealService: MealService

    private lateinit var menuDate: String
    private lateinit var cafeteriaLocation: String

    val foodCourtDataLoaded = MutableLiveData<Boolean>()
    val snackCornerDataLoaded = MutableLiveData<Boolean>()
    val haksikDataLoaded = MutableLiveData<Boolean>()
    val dodamDataLoaded = MutableLiveData<Boolean>()
    val dormitoryDataLoaded = MutableLiveData<Boolean>()

    private val totalMenuList = ArrayList<Section>()

    private lateinit var restaurantType: Restaurant

    private val infoViewModel: InfoViewModel by activityViewModels()


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

    @RequiresApi(Build.VERSION_CODES.O)
    fun observeViewModel() {
        menuService = RetrofitImpl.retrofit.create(MenuService::class.java)
        mealService = RetrofitImpl.retrofit.create(MealService::class.java)

//        Log.d("MenuFragment", App.token_prefs.accessToken + "여기부터" + App.token_prefs.refreshToken)
        val calendardate = this.arguments?.getString("calendardata")
        Log.d("lunchdate", "$calendardate")

//        menuRepository = MenuRepository(menuService)
        menuViewModel =
            ViewModelProvider(
                this,
                MenuViewModelFactory(menuService, mealService)
            )[MenuViewModel::class.java]

        // ViewModel에서 데이터 가져오기
        calendarViewModel = ViewModelProvider(requireActivity())[CalendarViewModel::class.java]
        retainInstance = true

        val dayFormat = DateTimeFormatter.ofPattern("dd")
        val todayDate = LocalDateTime.now().format(dayFormat)

        // ViewModel에서 데이터 가져오기
        calendarViewModel.getData().observe(viewLifecycleOwner) { dataReceived ->

            val parsedDate =
                LocalDate.parse(dataReceived.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            menuDate = parsedDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))

            // Assuming menuDate is a String in the format "yyyyMMdd"
            val formattedDate = LocalDate.parse(menuDate, DateTimeFormatter.BASIC_ISO_DATE)

            val dayOfWeek = formattedDate.dayOfWeek
            Log.d("menudate", menuDate)

            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY && time == Time.LUNCH) {
                // The date is not on a weekend
                //푸드코트
                menuViewModel.loadFixedMenu(Restaurant.FOOD_COURT)
                menuViewModel.fixedMenuDataFood.observe(viewLifecycleOwner) { result ->
                    if (result.mapFixedMenuResponseToMenu().isNotEmpty()) {
                        Log.d("menu", result.categoryMenuListCollection.toString())
                        totalMenuList.add(
                            Section(
                                MenuType.FIXED,
                                Restaurant.FOOD_COURT,
                                result.mapFixedMenuResponseToMenu(),
                                infoViewModel.getRestaurantInfo(Restaurant.FOOD_COURT)?.location
                                    ?: ""
                            )
                        )
                    }
                    foodCourtDataLoaded.value = true
                    checkDataLoaded()
//
                }

                //스낵코너
                menuViewModel.loadFixedMenu(Restaurant.SNACK_CORNER)
                menuViewModel.fixedMenuDataSnack.observe(viewLifecycleOwner) { result ->
                    if (result.mapFixedMenuResponseToMenu().isNotEmpty()) {
                        totalMenuList.add(
                            Section(
                                MenuType.FIXED,
                                Restaurant.SNACK_CORNER,
                                result.mapFixedMenuResponseToMenu(),
                                infoViewModel.getRestaurantInfo(Restaurant.SNACK_CORNER)?.location
                                    ?: ""
                            )
                        )
                    }
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

            if (time != Time.LUNCH) {
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
                            MenuType.VARIABLE,
                            Restaurant.HAKSIK,
                            result.mapTodayMenuResponseToMenu(),
                            infoViewModel.getRestaurantInfo(Restaurant.HAKSIK)?.location ?: ""
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
                            MenuType.VARIABLE,
                            Restaurant.DODAM,
                            result.mapTodayMenuResponseToMenu(),
                            infoViewModel.getRestaurantInfo(Restaurant.DODAM)?.location ?: ""
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
                            MenuType.VARIABLE,
                            Restaurant.DORMITORY,
                            result.mapTodayMenuResponseToMenu(),
                            infoViewModel.getRestaurantInfo(Restaurant.DORMITORY)?.location ?: ""
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
            adapter = fragmentManager?.let { MenuAdapter(it, totalMenuList) }

        }
    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}