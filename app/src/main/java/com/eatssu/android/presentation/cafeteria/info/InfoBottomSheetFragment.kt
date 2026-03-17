package com.eatssu.android.presentation.cafeteria.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.eatssu.android.databinding.FragmentBottomsheetInfoBinding
import com.eatssu.common.EventLogger
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.ScreenId
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import timber.log.Timber

class InfoBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomsheetInfoBinding? = null
    private val binding get() = _binding!!

    private val infoViewModel: InfoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomsheetInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = arguments?.getString("name")
        val restaurantType = enumValues<Restaurant>().find { it.name == name } ?: Restaurant.HAKSIK
        Timber.d("onViewCreated: $name $restaurantType")

        EventLogger.clickRestaurantInfo(restaurantType)

        binding.tvName.text = getString(restaurantType.displayNameResId)

        viewLifecycleOwner.lifecycleScope.launch {
            val restaurantInfo = infoViewModel.getRestaurantInfo(restaurantType)
            val binding = _binding ?: return@launch

            restaurantInfo?.let {
                binding.tvLocation.text = it.location
                binding.tvTime.text = it.time
                binding.tvEtc.text = it.etc

                Glide.with(binding.ivCafeteriaPhoto)
                    .load(it.image)
                    .into(binding.ivCafeteriaPhoto)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        EventLogger.screenView(ScreenId.HOME_INFO)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(data: String): InfoBottomSheetFragment {
            val fragment = InfoBottomSheetFragment()
            val args = Bundle().apply { putString("name", data) }
            fragment.arguments = args
            return fragment
        }
    }
}
