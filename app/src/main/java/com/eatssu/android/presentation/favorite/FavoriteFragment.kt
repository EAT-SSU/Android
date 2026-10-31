package com.eatssu.android.presentation.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.eatssu.android.analytics.ProvideAnalyticsTracker
import com.eatssu.android.presentation.MainActivity
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import com.eatssu.design_system.theme.EatssuTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    private val viewModel: FavoriteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val restaurant = arguments?.getString(ARG_TODAY_RESTAURANT)?.let { value ->
            Restaurant.entries.firstOrNull { it.name == value }
        }
        val time = arguments?.getString(ARG_TODAY_TIME)?.let { value ->
            Time.entries.firstOrNull { it.name == value }
        }
        viewModel.configureTodayMenus(
            restaurant,
            time,
            arguments?.getBoolean(ARG_TODAY_EXPANDED) == true
        )
    }

    companion object {
        const val ARG_TODAY_RESTAURANT = "today_favorite_restaurant"
        const val ARG_TODAY_TIME = "today_favorite_time"
        const val ARG_TODAY_EXPANDED = "today_favorite_expanded"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setContent {
            ProvideAnalyticsTracker(analyticsTracker) {
                EatssuTheme {
                    FavoriteRoute(
                        viewModel = viewModel,
                        onBackToMap = { mainActivity().openMapTab() },
                        onPartnershipClick = mainActivity()::openFavoriteDetail,
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFavorites()
    }

    private fun mainActivity(): MainActivity = requireActivity() as MainActivity
}
