package com.eatssu.android.presentation.mypage.userinfo

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivityUserInfoBinding
import com.eatssu.android.presentation.base.BaseActivity
import com.eatssu.android.presentation.util.showToast
import com.eatssu.common.enums.ScreenId
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserInfoActivity :
    BaseActivity<ActivityUserInfoBinding>(
        ActivityUserInfoBinding::inflate,
        ScreenId.MYPAGE_USERINFO
    ) {

    companion object {
        private const val MIN_NICKNAME_LENGTH = 2
        private const val MAX_NICKNAME_LENGTH = 16
    }

    private val userInfoViewModel: UserInfoViewModel by viewModels()

    private var inputNickname: String = ""

    private var force: Boolean = false

    private var selectedCollegeIndex = 0
    private var selectedDepartmentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "내 정보"

        // 현재 설정된 유저 정보 가져오기
        userInfoViewModel.loadUserInfo()

        force = intent.getBooleanExtra("force", false)

        binding.btnCheckNicknameDuplication.isEnabled = false
        binding.btnComplete.isEnabled = false

        binding.tvNicknameStatus.text = getString(
            R.string.set_nickname_length,
            MIN_NICKNAME_LENGTH,
            MAX_NICKNAME_LENGTH
        )

        lifecycleScope.launch {
            userInfoViewModel.uiState.collectLatest { state ->
                if (binding.etChNickname.text.toString() != state.nickname) {
                    binding.etChNickname.setText(state.nickname)
                    binding.etChNickname.setSelection(binding.etChNickname.text.length) // 커서 끝으로 이동
                }
                binding.tvCollege.text = state.selectedCollege.collegeName
                binding.tvDepartment.text = state.selectedDepartment.departmentName

                // 닉네임 검증 결과에 따른 UI 업데이트
                val validationError = state.nicknameValidationError
                val isValid = validationError == null

                binding.btnCheckNicknameDuplication.isEnabled =
                    isValid && state.isNicknameChanged && !state.loading

                if (!state.isNicknameChanged) {
                    binding.btnCheckNicknameDuplication.isEnabled = false
                }

                if (validationError != null) {
                    binding.tvNicknameStatus.text = validationError
                    binding.tvNicknameStatus.setTextColor(getColor(R.color.error))
                    binding.etChNickname.setBackgroundResource(R.drawable.shape_text_field_small_red)
                } else if (state.nickname.isNotEmpty()) {
                    binding.tvNicknameStatus.text = getString(
                        R.string.set_nickname_length,
                        MIN_NICKNAME_LENGTH,
                        MAX_NICKNAME_LENGTH
                    )
                    binding.tvNicknameStatus.setTextColor(getColor(R.color.gray600))
                    binding.etChNickname.setBackgroundResource(R.drawable.shape_text_field_small)
                }
            }
        }

        binding.etChNickname.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                inputNickname = binding.etChNickname.text.trim().toString()
                userInfoViewModel.validateAndUpdateNickname(inputNickname)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
        })

        setOnCheckNicknameDuplicationClickListener()
        setCollegeDepartmentClickListener()
        collectButtonEnableState()
        collectUIState()
    }

    private fun collectButtonEnableState() {
        lifecycleScope.launch {
            userInfoViewModel.uiState.collectLatest { state ->
                binding.btnComplete.isEnabled =
                        // 닉네임이 바뀌었으면 중복 확인까지 통과해야만 활성화
                    (state.isNicknameChecked && state.isNicknameChanged && state.isEnableName) ||
                            // 닉네임이 안 바뀌었을 때는 학과/단과대만 바뀌면 활성화
                            (!state.isNicknameChanged && state.isDepartmentChanged)
            }
        }
    }

    private fun setOnCheckNicknameDuplicationClickListener() {
        binding.btnCheckNicknameDuplication.setOnClickListener {
            userInfoViewModel.checkNicknameRemote(inputNickname)

            // 닉네임 중복 확인 후 UI 상태 업데이트 로직
            lifecycleScope.launch {
                userInfoViewModel.uiState.collectLatest {
                    if (it.isEnableName) {
                        binding.btnCheckNicknameDuplication.isEnabled = false // 중복확인 비활성화
                        binding.btnComplete.isEnabled = true // 저장하기 활성화
                        binding.tvNicknameStatus.text = getString(R.string.set_nickname_able)
                        binding.etChNickname.setBackgroundResource(R.drawable.shape_text_field_small)
                        binding.tvNicknameStatus.setTextColor(getColor(R.color.gray600))
                        userInfoViewModel.updateNickname(inputNickname)
                    } else {
                        binding.btnComplete.isEnabled = false
                        binding.etChNickname.setBackgroundResource(R.drawable.shape_text_field_small_red)
                        binding.tvNicknameStatus.text = getString(R.string.set_nickname_unable)
                        binding.tvNicknameStatus.setTextColor(getColor(R.color.error))
                    }
                }
            }
        }

        binding.btnComplete.setOnClickListener {
            val currentState = userInfoViewModel.uiState.value

            if (currentState.isNicknameChanged) {
                // 닉네임 변경 → 닉네임 저장 + 완료 시 학과 저장도 호출
                userInfoViewModel.changeUserNickname()

                lifecycleScope.launch {
                    userInfoViewModel.uiState.collectLatest {
                        if (it.isDone) {
                            // 닉네임 저장 성공 후 학과 변경도 필요하다면 호출
                            if (it.isCollegeChanged || it.isDepartmentChanged) {
                                userInfoViewModel.updateUserDepartment()
                            } else {
                                showToast(it.toastMessage)
                                finish()
                            }
                        }
                    }
                }
            } else {
                // 닉네임 변경 없음 → 학과만 변경
                userInfoViewModel.updateUserDepartment()
            }
        }
    }

    private fun collectUIState() {
        lifecycleScope.launch {
            userInfoViewModel.uiState.collectLatest { state ->
                if (state.success) {
                    showToast("정보가 업데이트 되었습니다.")
                    finish()
                }
            }
        }
    }

    private fun setCollegeDepartmentClickListener() {
        binding.flCollege.setOnClickListener {

            // 최신 state 사용
            val state = userInfoViewModel.uiState.value

            // 닉네임이 변경되었고 + 아직 중복확인을 안 했다면 막기
            if (state.isNicknameChanged && !state.isNicknameChecked) {
                showToast("닉네임 중복 확인을 완료해 주세요.")
                return@setOnClickListener
            }

            // 단과대 목록 요청
            userInfoViewModel.loadCollegeList()

            if (state.collegeList.isNotEmpty()) {
                val collegeNames = state.collegeList.map { it.collegeName }
                showDropdownPopup(
                    binding.tvCollege,
                    collegeNames,
                    selectedCollegeIndex
                ) { selected, index ->
                    selectedCollegeIndex = index
                    binding.tvCollege.text = selected

                    selectedDepartmentIndex = 0
                    binding.tvDepartment.text = "학과"

                    val selectedCollege = state.collegeList[index]
                    userInfoViewModel.updateInputCollege(selectedCollege)
                    userInfoViewModel.loadDepartmentList(selectedCollege.collegeId)
                }
            }
        }

        binding.flDepartment.setOnClickListener {
            val state = userInfoViewModel.uiState.value

            // 닉네임이 변경되었고 + 아직 중복확인을 안 했다면 막기
            if (state.isNicknameChanged && !state.isNicknameChecked) {
                showToast("닉네임 중복 확인을 완료해 주세요.")
                return@setOnClickListener
            }

            // 학과 리스트가 비어있다면 현재 단과대 기준으로 다시 로드
            if (state.departmentList.isEmpty() && state.selectedCollege.collegeId != -1) {
                userInfoViewModel.loadDepartmentList(state.selectedCollege.collegeId)
            }

            if (state.departmentList.isNotEmpty()) {
                val departmentNames = state.departmentList.map { it.departmentName }
                showDropdownPopup(
                    binding.tvDepartment,
                    departmentNames,
                    selectedDepartmentIndex
                ) { departmentName, departmentIndex ->
                    selectedDepartmentIndex = departmentIndex
                    binding.tvDepartment.text = departmentName
                    userInfoViewModel.updateInputDepartment(state.departmentList[departmentIndex])
                }
            }
        }
    }

    // 팝업 여닫기 관리
    private var currentPopup: PopupWindow? = null

    private fun showDropdownPopup(
        anchor: View,
        items: List<String>,
        selectedIndex: Int,
        onItemClick: (selected: String, selectedIndex: Int) -> Unit
    ) {
        // 기존 팝업이 열려있다면 닫기
        currentPopup?.dismiss()

        val inflater = LayoutInflater.from(this)
        val popupView = inflater.inflate(R.layout.popup_dropdown_list, null)

        val popupWindow = PopupWindow(
            popupView,
            anchor.width + 52,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        )

        val recyclerView = popupView.findViewById<RecyclerView>(R.id.recycler_dropdown)
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = object : RecyclerView.Adapter<DropdownViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DropdownViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_dropdown, parent, false)
                return DropdownViewHolder(view)
            }

            override fun getItemCount() = items.size

            override fun onBindViewHolder(holder: DropdownViewHolder, position: Int) {
                holder.bind(items[position]) {
                    onItemClick(it, position)
                    popupWindow.dismiss()
                    currentPopup?.dismiss()
                    currentPopup = null
                }

                if (position == selectedIndex) {
                    holder.itemView.setBackgroundResource(R.drawable.bg_menu_selected_item)
                } else {
                    holder.itemView.setBackgroundColor(
                        ContextCompat.getColor(
                            this@UserInfoActivity,
                            android.R.color.transparent
                        )
                    )
                }
            }
        }

        popupWindow.elevation = 8f
        popupWindow.isOutsideTouchable = true
        popupWindow.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.shape_text_field_small
            )
        )

        popupWindow.showAsDropDown(anchor, -24, binding.tvDepartment.height + 8)

        // 현재 팝업 윈도우를 저장
        currentPopup = popupWindow
    }
}
