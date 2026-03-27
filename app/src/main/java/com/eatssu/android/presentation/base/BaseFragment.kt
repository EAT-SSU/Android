package com.eatssu.android.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.common.analytics.ScreenViewEvent
import com.eatssu.common.enums.ScreenId
import timber.log.Timber
import javax.inject.Inject

abstract class BaseFragment<B : ViewBinding>(
    val screenId: ScreenId
) : Fragment() {

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    private var _binding: B? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = setBinding(inflater)
        return binding.root
    }

    abstract fun setBinding(layoutInflater: LayoutInflater): B

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        analyticsTracker.track(ScreenViewEvent(screenId))
        Timber.d("screen view logging: $screenId")
    }
}
