package com.eatssu.android.ui.info

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.model.RestaurantInfo
import com.eatssu.android.data.repository.FirebaseRemoteConfigRepository

class InfoViewModel(firebaseRemoteConfigRepository: FirebaseRemoteConfigRepository): ViewModel() {

    val infoList: MutableLiveData<ArrayList<RestaurantInfo>> = MutableLiveData()

    val dodamLocation = MutableLiveData<String>()
    val dodamPhotoUrl = MutableLiveData<String>()
    val dodamTime = MutableLiveData<String>()
    val dodamEtc = MutableLiveData<String>()

    val foodLocation = MutableLiveData<String>()
    val foodPhotoUrl = MutableLiveData<String>()
    val foodTime = MutableLiveData<String>()
    val foodEtc = MutableLiveData<String>()

    val dormitoryLocation = MutableLiveData<String>()
    val dormitoryPhotoUrl = MutableLiveData<String>()
    val dormitoryTime = MutableLiveData<String>()
    val dormitoryEtc = MutableLiveData<String>()

    val snackLocation = MutableLiveData<String>()
    val snackPhotoUrl = MutableLiveData<String>()
    val snackTime = MutableLiveData<String>()
    val snackEtc = MutableLiveData<String>()

    val haksikLocation = MutableLiveData<String>()
    val haksikPhotoUrl = MutableLiveData<String>()
    val haksikTime = MutableLiveData<String>()
    val haksikEtc = MutableLiveData<String>()

    init {
        infoList.value = firebaseRemoteConfigRepository.getCafeteriaInfo()
        Log.d("InfoViewModel",infoList.value.toString())

        val dodam = infoList.value!!.find { it.enum == Restaurant.DODAM }
        dodamPhotoUrl.value = dodam?.photoUrl ?: ""
        dodamTime.value = dodam?.time ?: ""
        dodamLocation.value = dodam?.location ?: ""
        dodamEtc.value = dodam?.etc ?: ""

        val food = infoList.value!!.find { it.enum == Restaurant.FOOD_COURT }
        foodPhotoUrl.value = food?.photoUrl ?: ""
        foodTime.value = food?.time ?: ""
        foodLocation.value = food?.location ?: ""
        foodEtc.value = food?.etc ?: ""

        val dormitory = infoList.value!!.find { it.enum == Restaurant.DORMITORY }
        dormitoryPhotoUrl.value = dormitory?.photoUrl ?: ""
        dormitoryTime.value = dormitory?.time ?: ""
        dormitoryLocation.value = dormitory?.location ?: ""
        dormitoryEtc.value = dormitory?.etc ?: ""

        val snack = infoList.value!!.find { it.enum == Restaurant.SNACK_CORNER }
        snackPhotoUrl.value = snack?.photoUrl ?: ""
        snackTime.value = snack?.time ?: ""
        snackLocation.value = snack?.location ?: ""
        snackEtc.value = snack?.etc ?: ""

        val haksik = infoList.value!!.find { it.enum == Restaurant.HAKSIK }
        haksikPhotoUrl.value = haksik?.photoUrl ?: ""
        haksikTime.value = haksik?.time ?: ""
        haksikLocation.value = haksik?.location ?: ""
        haksikEtc.value = haksik?.etc ?: ""
    }
}