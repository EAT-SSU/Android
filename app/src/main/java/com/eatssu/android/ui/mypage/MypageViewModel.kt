package com.eatssu.android.ui.mypage

import com.eatssu.android.data.repository.FirebaseRemoteConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MypageViewModel @Inject constructor(repository: FirebaseRemoteConfigRepository)
//    private val userRepository: UserRepository,
//) : ViewModel() {
//
//    private val _nickname = MutableLiveData("")
//    val nickname: LiveData<String> = _nickname
//

//
//    fun getNicknameAbility() {
//        viewModelScope.launch {
//            userRepository.checkNickname(nickname)
//                .onSuccess {
//                    _nickname.value = it.
//                    _wishCouponContent.value = it.wishCoupon.content
//                    _wishCouponIsUsed.value = it.wishCoupon.isUsed
//                    _wishCouponImage.value = it.wishCoupon.image
//                }.onFailure {
//                    // TODO: 에러 처리
//                }
//        }
//    }
