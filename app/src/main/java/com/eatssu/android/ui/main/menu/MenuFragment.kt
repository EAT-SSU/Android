package com.eatssu.android.ui.main.menu

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.App
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.enums.Time
import com.eatssu.android.data.model.response.ChangeMenuInfo
import com.eatssu.android.data.model.response.GetFixedMenuResponseDto
import com.eatssu.android.data.model.response.GetTodayMealResponseDto
import com.eatssu.android.data.repository.MenuRepository
import com.eatssu.android.data.service.MenuService
import com.eatssu.android.databinding.FragmentMenuBinding
import com.eatssu.android.ui.main.calendar.CalendarViewModel
import com.eatssu.android.util.RetrofitImpl
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MenuFragment(val time: Time) : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private lateinit var menuViewModel: MenuViewModel
    private lateinit var menuService: MenuService
    private lateinit var menuRepository: MenuRepository

    private lateinit var menuDate: String

//    private var resultHaksik: GetTodayMealResponseDto? = null
//    private var resultDodam: GetTodayMealResponseDto? = null
//    private var resultDormitory: GetTodayMealResponseDto? = null
//    private var resultFoodCourt: GetFixedMenuResponseDto? = null
//    private var resultSnackCorner: GetFixedMenuResponseDto? = null

    private val totalMenuList = ArrayList<Section>()

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
        menuService = RetrofitImpl.retrofit.create(MenuService::class.java)

        Log.d("MenuFragment", App.token_prefs.accessToken+"여기부터"+App.token_prefs.refreshToken)

        val calendardate = this.arguments?.getString("calendardata")
        Log.d("lunchdate", "$calendardate")

        menuRepository = MenuRepository(menuService)
        menuViewModel =
            ViewModelProvider(this, MenuViewModelFactory(menuRepository))[MenuViewModel::class.java]

        val calendarViewModel = ViewModelProvider(requireActivity())[CalendarViewModel::class.java]
        // ViewModel에서 데이터 가져오기
        calendarViewModel.getData().observe(viewLifecycleOwner) { dataReceived ->
            menuDate =
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + dataReceived


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
                        Section(MenuType.FIX,
                            Restaurant.FOOD_COURT,
                            mapFixedMenuResponseToMenu(result)
                        )
                    )
                    setupTodayRecyclerView()
                }

                //스낵코너
                menuViewModel.loadFixedMenu(Restaurant.SNACK_CORNER)
                menuViewModel.fixedMenuDataSnack.observe(viewLifecycleOwner) { result ->
                    totalMenuList.add(
                        Section(MenuType.FIX,
                            Restaurant.SNACK_CORNER,
                            mapFixedMenuResponseToMenu(result)
                        )
                    )
                    setupTodayRecyclerView()

                }

                totalMenuList.sortBy { it.cafeteria.ordinal }
                setupTodayRecyclerView()

                loadData()
                Log.d("MenuFragment", "The date $menuDate is not on a weekend.")


            } else {
                // The date is on a weekend
                Log.d("MenuFragment", "The date $menuDate is on a weekend.")
            }


            //학생식당
            menuViewModel.loadTodayMeal(menuDate, Restaurant.HAKSIK, time)
            menuViewModel.todayMealDataHaksik.observe(viewLifecycleOwner) { result ->
                if (result.isNotEmpty()) {
                    totalMenuList.add(
                        Section(MenuType.CHANGE,
                            Restaurant.HAKSIK,
                            mapTodayMenuResponseToMenu(result)
                        )
                    )
                    setupTodayRecyclerView()
                }
            }

            //숭실도담
            menuViewModel.loadTodayMeal(menuDate, Restaurant.DODAM, time)
            menuViewModel.todayMealDataDodam.observe(viewLifecycleOwner) { result ->
                if (result.isNotEmpty()) {
                    totalMenuList.add(Section(MenuType.CHANGE,Restaurant.DODAM, mapTodayMenuResponseToMenu(result)))
                    setupTodayRecyclerView()
                }
            }

            //기숙사식당
            menuViewModel.loadTodayMeal(menuDate, Restaurant.DORMITORY, time)
            menuViewModel.todayMealDataDormitory.observe(viewLifecycleOwner) { result ->
                if (result.isNotEmpty()) {
                    totalMenuList.add(
                        Section(MenuType.CHANGE,
                            Restaurant.DORMITORY,
                            mapTodayMenuResponseToMenu(result)
                        )
                    )
                    setupTodayRecyclerView()
                }
            }
        }

    }


    fun loadData() {}

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