package com.eatssu.android.presentation.cafeteria.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.databinding.FragmentMenuBinding
import com.eatssu.android.domain.model.Section
import com.eatssu.android.presentation.MainViewModel
import com.eatssu.android.presentation.cafeteria.info.InfoViewModel
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
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

    private val dataLoadedMap = mutableMapOf<Restaurant, Boolean>()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 메뉴 정보 수집
        collectMenuData()

        // 날짜 바뀔 때마다 ViewModel API 호출
        observeViewModel()
    }

    fun observeViewModel() {
        mainViewModel.getData().observe(viewLifecycleOwner) { dataReceived ->

            val menuDate = dataReceived.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            val dayOfWeek = dataReceived.dayOfWeek

            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY && time == Time.LUNCH) {
                menuViewModel.loadFixedMenu(Restaurant.FOOD_COURT)
                menuViewModel.loadFixedMenu(Restaurant.SNACK_CORNER)
            } else {
                dataLoadedMap[Restaurant.FOOD_COURT] = true
                dataLoadedMap[Restaurant.SNACK_CORNER] = true
                checkDataLoaded()
            }

            if (time != Time.LUNCH) {
                dataLoadedMap[Restaurant.FOOD_COURT] = true
                dataLoadedMap[Restaurant.SNACK_CORNER] = true
                checkDataLoaded()
            }

            // 고정 메뉴 식당 불러오기
            for (restaurant in Restaurant.getVariableRestaurantList()) {
                menuViewModel.loadTodayMeal(menuDate, restaurant, time)
            }
        }
    }

    private fun collectMenuData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                menuViewModel.menuData.collect { menuMap ->
                    menuMap.forEach { (restaurant, menuList) ->
                        totalMenuList.removeAll { it.cafeteria == restaurant }
                        if (menuList.isNotEmpty()) {
                            totalMenuList.add(
                                Section(
                                    restaurant.menuType,
                                    restaurant,
                                    menuList,
                                    infoViewModel.getRestaurantInfo(restaurant)?.location ?: ""
                                )
                            )
                        }
                        dataLoadedMap[restaurant] = true
                        checkDataLoaded()
                    }
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
        val requiredRestaurants = setOf(
            Restaurant.FOOD_COURT,
            Restaurant.SNACK_CORNER,
            Restaurant.HAKSIK,
            Restaurant.DODAM,
            Restaurant.DORMITORY,
            Restaurant.FACULTY,
        )

        if (requiredRestaurants.all { dataLoadedMap[it] == true }) {
            totalMenuList.sortBy { it.cafeteria.ordinal }
            setupTodayRecyclerView()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}