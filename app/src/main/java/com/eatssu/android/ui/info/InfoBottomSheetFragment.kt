package com.eatssu.android.ui.info

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.repository.FirebaseRemoteConfigRepository
import com.eatssu.android.databinding.FragmentBottomsheetInfoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class InfoBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomsheetInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var infoViewModel: InfoViewModel
    private lateinit var firebaseRemoteConfigRepository: FirebaseRemoteConfigRepository


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBottomsheetInfoBinding.inflate(inflater, container, false)

        firebaseRemoteConfigRepository = FirebaseRemoteConfigRepository()
        infoViewModel = ViewModelProvider(
            this,
            InfoViewModelFactory(firebaseRemoteConfigRepository)
        )[InfoViewModel::class.java]

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 전달된 데이터 받기
        val name = arguments?.getString("name")
        val restaurantType = enumValues<Restaurant>().find { it.name == name } ?: Restaurant.HAKSIK
        Log.d("InfoBottomSheetFragment", "onViewCreated: $name $restaurantType")

        binding.tvName.text = restaurantType.displayName

        //TODO 방법 개선
        infoViewModel.infoList.observe(this) {

            when (restaurantType) {
                Restaurant.DODAM -> {
                    binding.tvLocation.text = infoViewModel.dodamLocation.value
                    binding.tvTime.text = infoViewModel.dodamTime.value
                    binding.tvEtc.text = infoViewModel.dodamEtc.value

                    Glide.with(this)
                        .load(infoViewModel.dodamPhotoUrl.value)
                        .into(binding.ivCafeteriaPhoto)
                }

                Restaurant.HAKSIK -> {
                    binding.tvLocation.text = infoViewModel.haksikLocation.value
                    binding.tvTime.text = infoViewModel.haksikTime.value
                    binding.tvEtc.text = infoViewModel.haksikEtc.value

                    Glide.with(this)
                        .load(infoViewModel.haksikPhotoUrl.value)
                        .into(binding.ivCafeteriaPhoto)
                }

                Restaurant.FOOD_COURT -> {
                    binding.tvLocation.text = infoViewModel.foodLocation.value
                    binding.tvTime.text = infoViewModel.foodTime.value
                    binding.tvEtc.text = infoViewModel.foodEtc.value

                    Glide.with(this)
                        .load(infoViewModel.foodPhotoUrl.value)
                        .into(binding.ivCafeteriaPhoto)
                }

                Restaurant.SNACK_CORNER -> {
                    binding.tvLocation.text = infoViewModel.snackLocation.value
                    binding.tvTime.text = infoViewModel.snackTime.value
                    binding.tvEtc.text = infoViewModel.snackEtc.value

                    Glide.with(this)
                        .load(infoViewModel.snackPhotoUrl.value)
                        .into(binding.ivCafeteriaPhoto)
                }

                Restaurant.DORMITORY -> {
                    binding.tvLocation.text = infoViewModel.dormitoryLocation.value
                    binding.tvTime.text = infoViewModel.dormitoryTime.value
                    binding.tvEtc.text = infoViewModel.dormitoryEtc.value

                    Glide.with(this)
                        .load(infoViewModel.dormitoryPhotoUrl.value)
                        .into(binding.ivCafeteriaPhoto)
                }

                else -> {}
            }
        }
    }

    companion object {
        // newInstance 메서드를 통해 데이터를 전달하는 방법
        fun newInstance(data: String): InfoBottomSheetFragment {
            val fragment = InfoBottomSheetFragment()
            val args = Bundle()
            args.putString("name", data)
            fragment.arguments = args
            return fragment
        }
    }
}
