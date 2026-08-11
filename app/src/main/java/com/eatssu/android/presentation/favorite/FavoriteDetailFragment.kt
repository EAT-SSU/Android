package com.eatssu.android.presentation.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eatssu.android.analytics.ProvideAnalyticsTracker
import com.eatssu.android.domain.model.PartnershipRestaurant
import com.eatssu.android.presentation.map.MapExternalNavigator
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.design_system.theme.EatssuTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteDetailFragment : Fragment() {

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    @Inject
    lateinit var mapExternalNavigator: MapExternalNavigator

    private val viewModel: FavoriteDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val partnership = requireNotNull(
            BundleCompat.getParcelable(
                requireArguments(),
                ARG_PARTNERSHIP,
                PartnershipRestaurant::class.java,
            ),
        )

        return ComposeView(requireContext()).apply {
            setContent {
                ProvideAnalyticsTracker(analyticsTracker) {
                    EatssuTheme {
                        FavoriteDetailRoute(
                            partnership = partnership,
                            viewModel = viewModel,
                            mapExternalNavigator = mapExternalNavigator,
                            onBack = { findNavController().popBackStack() },
                        )
                    }
                }
            }
        }
    }

    companion object {
        const val ARG_PARTNERSHIP_ID = "partnershipId"
        const val ARG_PARTNERSHIP = "partnership"
    }
}
