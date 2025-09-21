package com.eatssu.android.presentation.cafeteria.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.eatssu.android.databinding.FragmentBottomsheetInfoBinding
import com.eatssu.common.EventLogger
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.ScreenId
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

        binding.tvName.text = restaurantType.korean

        CoroutineScope(Dispatchers.Main).launch {
            infoViewModel.infoList.collect {
                val restaurantInfo = infoViewModel.getRestaurantInfo(restaurantType)

                restaurantInfo?.let {
                    binding.tvLocation.text = it.location
                    binding.tvTime.text = it.time
                    binding.tvEtc.text = it.etc

                    Glide.with(this@InfoBottomSheetFragment)
                        .load(it.photoUrl)
                        .into(binding.ivCafeteriaPhoto)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        EventLogger.screenView(ScreenId.HOME_INFO)
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
