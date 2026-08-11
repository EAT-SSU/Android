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
import com.eatssu.design_system.theme.EatssuTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    private val viewModel: FavoriteViewModel by viewModels()

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
