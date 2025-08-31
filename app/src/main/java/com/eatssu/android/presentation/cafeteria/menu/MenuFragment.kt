package com.eatssu.android.presentation.cafeteria.menu

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.data.dto.response.mapFixedMenuResponseToMenu
import com.eatssu.android.data.dto.response.mapTodayMenuResponseToMenu
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.enums.Time
import com.eatssu.android.databinding.FragmentMenuBinding
import com.eatssu.android.domain.model.Section
import com.eatssu.android.presentation.UiState
import com.eatssu.android.presentation.cafeteria.info.InfoViewModel
import com.eatssu.android.presentation.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()
    private val infoViewModel by activityViewModels<InfoViewModel>()
    private val menuViewModel by viewModels<MenuViewModel>()

    val foodCourtDataLoaded = MutableLiveData<Boolean>()
    val snackCornerDataLoaded = MutableLiveData<Boolean>()
    val haksikDataLoaded = MutableLiveData<Boolean>()
    val dodamDataLoaded = MutableLiveData<Boolean>()
    val dormitoryDataLoaded = MutableLiveData<Boolean>()
    val facultyDataLoaded = MutableLiveData<Boolean>()

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

        // StateFlow 수집은 단 1번만 실행
        collectMealData()
        collectFixedMenuData()
        collectUiState()

        // 날짜 바뀔 때마다 ViewModel API 호출
        observeViewModel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun observeViewModel() {
        mainViewModel.getData().observe(viewLifecycleOwner) { dataReceived ->

            val menuDate = dataReceived.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            val dayOfWeek = dataReceived.dayOfWeek

            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY && time == Time.LUNCH) {
                menuViewModel.loadFixedMenu(Restaurant.FOOD_COURT)
                menuViewModel.loadFixedMenu(Restaurant.SNACK_CORNER)
            } else {
                foodCourtDataLoaded.value = true
                snackCornerDataLoaded.value = true
                checkDataLoaded()
            }

            if (time != Time.LUNCH) {
                foodCourtDataLoaded.value = true
                snackCornerDataLoaded.value = true
                checkDataLoaded()
            }

            menuViewModel.loadTodayMeal(menuDate, Restaurant.HAKSIK, time)
            menuViewModel.loadTodayMeal(menuDate, Restaurant.DODAM, time)
            menuViewModel.loadTodayMeal(menuDate, Restaurant.DORMITORY, time)
            menuViewModel.loadTodayMeal(menuDate, Restaurant.FACULTY, time)
        }
    }

    private fun collectMealData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                menuViewModel.todayMealDataHaksik.collect { result ->
                    totalMenuList.removeAll { it.cafeteria == Restaurant.HAKSIK }
                    if (result.isNotEmpty()) {
                        totalMenuList.add(
                            Section(MenuType.VARIABLE, Restaurant.HAKSIK,
                                result.mapTodayMenuResponseToMenu(),
                                infoViewModel.getRestaurantInfo(Restaurant.HAKSIK)?.location ?: ""
                            )
                        )
                    }
                    haksikDataLoaded.value = true
                    checkDataLoaded()
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                menuViewModel.todayMealDataDodam.collect { result ->
                    totalMenuList.removeAll { it.cafeteria == Restaurant.DODAM }
                    if (result.isNotEmpty()) {
                        totalMenuList.add(
                            Section(MenuType.VARIABLE, Restaurant.DODAM,
                                result.mapTodayMenuResponseToMenu(),
                                infoViewModel.getRestaurantInfo(Restaurant.DODAM)?.location ?: ""
                            )
                        )
                    }
                    dodamDataLoaded.value = true
                    checkDataLoaded()
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                menuViewModel.todayMealDataDormitory.collect { result ->
                    totalMenuList.removeAll { it.cafeteria == Restaurant.DORMITORY }
                    if (result.isNotEmpty()) {
                        totalMenuList.add(
                            Section(MenuType.VARIABLE, Restaurant.DORMITORY,
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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                menuViewModel.todayMealDataFaculty.collect { result ->
                    totalMenuList.removeAll { it.cafeteria == Restaurant.FACULTY }
                    if (result.isNotEmpty()) {
                        totalMenuList.add(
                            Section(MenuType.VARIABLE, Restaurant.FACULTY,
                                result.mapTodayMenuResponseToMenu(),
                                infoViewModel.getRestaurantInfo(Restaurant.FACULTY)?.location ?: ""
                            )
                        )
                    }
                    facultyDataLoaded.value = true
                    checkDataLoaded()
                }
            }
        }
    }

    private fun collectFixedMenuData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                menuViewModel.fixedMenuDataFood.collect { result ->
                    totalMenuList.removeAll { it.cafeteria == Restaurant.FOOD_COURT }
                    if (result.mapFixedMenuResponseToMenu().isNotEmpty()) {
                        totalMenuList.add(
                            Section(MenuType.FIXED, Restaurant.FOOD_COURT,
                                result.mapFixedMenuResponseToMenu(),
                                infoViewModel.getRestaurantInfo(Restaurant.FOOD_COURT)?.location ?: ""
                            )
                        )
                    }
                    foodCourtDataLoaded.value = true
                    checkDataLoaded()
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                menuViewModel.fixedMenuDataSnack.collect { result ->
                    totalMenuList.removeAll { it.cafeteria == Restaurant.SNACK_CORNER }
                    if (result.mapFixedMenuResponseToMenu().isNotEmpty()) {
                        totalMenuList.add(
                            Section(MenuType.FIXED, Restaurant.SNACK_CORNER,
                                result.mapFixedMenuResponseToMenu(),
                                infoViewModel.getRestaurantInfo(Restaurant.SNACK_CORNER)?.location ?: ""
                            )
                        )
                    }
                    snackCornerDataLoaded.value = true
                    checkDataLoaded()
                }
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
            dormitoryDataLoaded.value == true &&
            facultyDataLoaded.value == true
        ) {
            totalMenuList.sortBy { it.cafeteria.ordinal }
            setupTodayRecyclerView()
        }
    }

    private fun collectUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                menuViewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Init -> {
                            // init
                        }
                        is UiState.Loading -> {
                            // Loading
                        }
                        is UiState.Success -> {
                            // Success
                        }
                        is UiState.Error -> {
                            // Error
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}