package com.eatssu.android.ui.info

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.databinding.ActivityInfoBinding
import com.eatssu.android.data.repository.FirebaseRemoteConfigRepository

class InfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfoBinding

    private lateinit var infoViewModel: InfoViewModel
    private lateinit var restaurantType: Restaurant
    private lateinit var firebaseRemoteConfigRepository: FirebaseRemoteConfigRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseRemoteConfigRepository = FirebaseRemoteConfigRepository()
        infoViewModel = ViewModelProvider(this, InfoViewModelFactory(firebaseRemoteConfigRepository))[InfoViewModel::class.java]

        restaurantType = intent.getSerializableExtra("restaurantType") as Restaurant
        binding.tvName.text = restaurantType.displayName

        infoViewModel.infoList.observe(this) {

            when (restaurantType) {
                Restaurant.DODAM -> {
                    binding.tvLocation.text = infoViewModel.dodamEtc.value
                    binding.tvTime.text = infoViewModel.dodamTime.value
                    binding.tvEtc.text = infoViewModel.dodamEtc.value
                }

                Restaurant.HAKSIK -> {
                    binding.tvLocation.text = infoViewModel.dodamLocation.value
                    binding.tvTime.text = infoViewModel.haksikTime.value
                    binding.tvEtc.text = infoViewModel.haksikEtc.value
                }

                Restaurant.FOOD_COURT -> {
                    binding.tvLocation.text = infoViewModel.foodLocation.value
                    binding.tvTime.text = infoViewModel.foodTime.value
                    binding.tvEtc.text = infoViewModel.foodEtc.value
                }

                Restaurant.SNACK_CORNER -> {
                    binding.tvLocation.text = infoViewModel.snackLocation.value
                    binding.tvTime.text = infoViewModel.snackTime.value
                    binding.tvEtc.text = infoViewModel.snackEtc.value
                }

                Restaurant.DORMITORY -> {
                    binding.tvLocation.text = infoViewModel.dodamLocation.value
                    binding.tvTime.text = infoViewModel.dormitoryTime.value
                    binding.tvEtc.text = infoViewModel.dormitoryEtc.value
                }

                else -> {}
            }
        }
    }
}
