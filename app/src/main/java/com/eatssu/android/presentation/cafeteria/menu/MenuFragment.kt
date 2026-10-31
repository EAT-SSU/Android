package com.eatssu.android.presentation.cafeteria.menu

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
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
import com.eatssu.android.presentation.cafeteria.CafeteriaScrollViewModel
import com.eatssu.android.presentation.cafeteria.info.InfoViewModel
import com.eatssu.common.UiState
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.common.enums.Time
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class MenuFragment : Fragment() {
    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()
    private val infoViewModel by activityViewModels<InfoViewModel>()
    private val menuViewModel by viewModels<MenuViewModel>()
    private val scrollViewModel by viewModels<CafeteriaScrollViewModel>({ requireParentFragment() })
    private var displayedState: MenuState? = null
    private var displayedSections: List<Section> = emptyList()

    private val time: Time by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(ARG_TIME, Time::class.java) ?: Time.LUNCH
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable(ARG_TIME) as? Time ?: Time.LUNCH
        }
    }

    companion object {
        private const val ARG_TIME = "ARG_TIME"

        fun newInstance(time: Time): MenuFragment {
            return MenuFragment().apply {
                arguments = bundleOf(ARG_TIME to time)
            }
        }
    }

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
            Timber.d("Loading menus for date: $menuDate, time: $time")

            // 메뉴 로딩 (ViewModel이 공휴일 포함하여 식당 목록 결정)
            menuViewModel.loadMenus(dataReceived, time)
        }
    }

    private fun observeUiState() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                menuViewModel.uiState.collectLatest { state ->
                    if (state !is UiState.Success) return@collectLatest
                    if (displayedState == state.data && _binding?.rv?.adapter != null) {
                        scrollToNotificationRestaurant()
                        return@collectLatest
                    }
                    val sections = buildMenuSections(state.data.menuMap) { restaurant ->
                        infoViewModel.getRestaurantInfo(restaurant)?.location.orEmpty()
                    }
                    if (menuViewModel.uiState.value != state) return@collectLatest
                    displayedState = state.data
                    displayedSections = sections
                    setupRecyclerView(sections)
                    scrollToNotificationRestaurant()
                }
            }
            launch { scrollViewModel.target.collect { scrollToNotificationRestaurant() } }
        }
    }

    private fun scrollToNotificationRestaurant() {
        val target = scrollViewModel.target.value ?: return
        val current = (menuViewModel.uiState.value as? UiState.Success)?.data ?: return
        if (target.time != time || current.date != target.date || current.time != target.time || displayedState != current) return
        val position = displayedSections.indexOfFirst { it.cafeteria == target.restaurant }
        if (position < 0) return
        val recycler = _binding?.rv ?: return
        recycler.post {
            if (_binding?.rv !== recycler || scrollViewModel.target.value?.id != target.id ||
                (menuViewModel.uiState.value as? UiState.Success)?.data != current
            ) return@post
            (recycler.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
                position,
                0
            )
            scrollViewModel.consume(target.id)
        }
    }

    private fun setupRecyclerView(sectionList: List<Section>) {
        binding.rv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = MenuAdapter(getParentFragmentManager(), sectionList, analyticsTracker, time)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        displayedState = null
        displayedSections = emptyList()
    }
}
