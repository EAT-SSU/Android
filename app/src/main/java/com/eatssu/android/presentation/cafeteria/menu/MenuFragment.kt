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
import com.eatssu.android.presentation.UiState
import com.eatssu.android.presentation.cafeteria.info.InfoViewModel
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()
    private val infoViewModel by activityViewModels<InfoViewModel>()
    private val menuViewModel by viewModels<MenuViewModel>()

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

        // UiState 관찰
        observeUiState()

        // 날짜 바뀔 때마다 ViewModel API 호출
        observeViewModel()
    }

    fun observeViewModel() {
        mainViewModel.getData().observe(viewLifecycleOwner) { dataReceived ->

            val menuDate = dataReceived.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            val dayOfWeek = dataReceived.dayOfWeek

            // 로딩할 식당 목록 결정
            val restaurantsToLoad = buildList {
                // 변동 메뉴 식당
                addAll(Restaurant.getVariableRestaurantList())

                // 고정 메뉴 식당 (평일 점심만)
                if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY && time == Time.LUNCH) {
                    add(Restaurant.FOOD_COURT)
                    add(Restaurant.SNACK_CORNER)
                }
            }

            Timber.d("Loading menus for date: $menuDate, time: $time, restaurants: $restaurantsToLoad")

            // 메뉴 로딩
            menuViewModel.loadMenus(restaurantsToLoad, menuDate, time)
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                menuViewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Success -> {
                            val menuMap = state.data.menuMap
                            Timber.d("Menu map received: $menuMap")

                            val sectionList = menuMap
                                .filter { (_, menuList) -> menuList.isNotEmpty() }
                                .map { (restaurant, menuList) ->
                                    Section(
                                        restaurant.menuType,
                                        restaurant,
                                        menuList,
                                        infoViewModel.getRestaurantInfo(restaurant)?.location ?: ""
                                    )
                                }
                                .sortedBy { it.cafeteria.ordinal }

                            setupRecyclerView(sectionList)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun setupRecyclerView(sectionList: List<Section>) {
        binding.rv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = fragmentManager?.let { MenuAdapter(it, sectionList) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}