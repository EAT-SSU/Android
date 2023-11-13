package com.eatssu.android.view.infopage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eatssu.android.data.entity.FirebaseInfoItem

class InfoViewModel(firebaseRemoteConfigRepository: FirebaseRemoteConfigRepository): ViewModel() {

    val infoList: MutableLiveData<ArrayList<FirebaseInfoItem>> = MutableLiveData()

    val dodamLocation = MutableLiveData<String>()
    val dodamTime = MutableLiveData<String>()
    val dodamEtc = MutableLiveData<String>()

    val foodLocation = MutableLiveData<String>()
    val foodTime = MutableLiveData<String>()
    val foodEtc = MutableLiveData<String>()

    val dormitoryLocation = MutableLiveData<String>()
    val dormitoryTime = MutableLiveData<String>()
    val dormitoryEtc = MutableLiveData<String>()

    val snackLocation = MutableLiveData<String>()
    val snackTime = MutableLiveData<String>()
    val snackEtc = MutableLiveData<String>()

    val haksikLocation = MutableLiveData<String>()
    val haksikTime = MutableLiveData<String>()
    val haksikEtc = MutableLiveData<String>()

    init {
        infoList.value = firebaseRemoteConfigRepository.getCafeteriaInfo()

        val dodam = infoList.value!!.find { it.name == "숭실도담" }
        dodamTime.value = dodam?.time ?: ""
        dodamLocation.value = dodam?.location ?: ""
        dodamEtc.value = dodam?.etc ?: ""

        val food = infoList.value!!.find { it.name == "FOOD COURT" }
        foodTime.value = food?.time ?: ""
        foodLocation.value = food?.location ?: ""
        foodEtc.value = food?.etc ?: ""

        val dormitory = infoList.value!!.find { it.name == "기숙사 식당" }
        dormitoryTime.value = dormitory?.time ?: ""
        dormitoryLocation.value = dormitory?.location ?: ""
        dormitoryEtc.value = dormitory?.etc ?: ""

        val snack = infoList.value!!.find { it.name == "스낵코너" }
        snackTime.value = snack?.time ?: ""
        snackLocation.value = snack?.location ?: ""
        snackEtc.value = snack?.etc ?: ""

        val haksik = infoList.value!!.find { it.name == "학생식당" }
        haksikTime.value = haksik?.time ?: "tlqof"
        haksikLocation.value = haksik?.location ?: ""
        haksikEtc.value = haksik?.etc ?: ""
    }
}