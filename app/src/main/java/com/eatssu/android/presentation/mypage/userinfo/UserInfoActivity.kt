package com.eatssu.android.presentation.mypage.userinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivityUserInfoBinding
import com.eatssu.android.presentation.UiEvent
import com.eatssu.android.presentation.UiState
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

    private val viewModel: UserInfoViewModel by viewModels()

    private var selectedCollegeIndex = 0
    private var selectedDepartmentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "내 정보"

        setupListeners()
        observeUiState()
        observeUiEvent()
    }

    private fun setupListeners() {
        // 닉네임 입력
        binding.etChNickname.addTextChangedListener { text ->
            viewModel.onNicknameChanged(text.toString())
        }

        // 중복 확인 버튼
        binding.btnCheckNicknameDuplication.setOnClickListener {
            viewModel.checkNicknameDuplication()
        }

        // 저장 버튼
        binding.btnComplete.setOnClickListener {
            viewModel.saveUserInfo()
        }

        // 단과대 선택
        binding.flCollege.setOnClickListener {
            handleCollegeClick()
        }

        // 학과 선택
        binding.flDepartment.setOnClickListener {
            handleDepartmentClick()
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is UiState.Loading -> {
                        // 로딩 상태 처리 (필요시 ProgressBar 표시)
                    }

                    is UiState.Success -> {
                        renderSuccessState(state.data)
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun observeUiEvent() {
        lifecycleScope.launch {
            viewModel.uiEvent.collectLatest { event ->
                when (event) {
                    is UiEvent.ShowToast -> showToast(event.message)
                }
            }
        }
    }

    private fun renderSuccessState(data: UserInfoData) {
        renderNicknameState(data)
        renderCollegeDepartmentState(data)
        renderButtonsState(data)
        checkDoneAndFinish(data)
    }

    private fun renderNicknameState(data: UserInfoData) {
        // 닉네임 텍스트 동기화 (무한 루프 방지)
        if (binding.etChNickname.text.toString() != data.nickname) {
            binding.etChNickname.setText(data.nickname)
            binding.etChNickname.setSelection(data.nickname.length)
        }

        // 에러가 있는 경우 (규칙 미통과 or 중복)
        if (data.nicknameValidationError != null) {
            binding.tvNicknameStatus.text = data.nicknameValidationError
            binding.tvNicknameStatus.setTextColor(getColor(R.color.error))
            binding.etChNickname.setBackgroundResource(R.drawable.shape_text_field_small_red)
            return
        }

        // 중복 확인 성공한 경우
        if (data.isDuplicationChecked) {
            binding.tvNicknameStatus.text = getString(R.string.set_nickname_able)
            binding.tvNicknameStatus.setTextColor(getColor(R.color.gray600))
            binding.etChNickname.setBackgroundResource(R.drawable.shape_text_field_small)
            return
        }

        // 기본 상태
        binding.tvNicknameStatus.text = getString(
            R.string.set_nickname_length,
            MIN_NICKNAME_LENGTH,
            MAX_NICKNAME_LENGTH
        )
        binding.tvNicknameStatus.setTextColor(getColor(R.color.gray600))
        binding.etChNickname.setBackgroundResource(R.drawable.shape_text_field_small)
    }

    private fun renderCollegeDepartmentState(data: UserInfoData) {
        binding.tvCollege.text = data.selectedCollege.collegeName
        binding.tvDepartment.text = data.selectedDepartment.departmentName
    }

    private fun renderButtonsState(data: UserInfoData) {
        binding.btnCheckNicknameDuplication.isEnabled = data.canCheckDuplication
        binding.btnComplete.isEnabled = data.canSave
    }

    private fun handleCollegeClick() {
        val state = viewModel.uiState.value as? UiState.Success ?: return
        val data = state.data

        val collegeNames = data.collegeList.map { it.collegeName }
        showDropdownPopup(
            anchor = binding.tvCollege,
            items = collegeNames,
            selectedIndex = selectedCollegeIndex
        ) { _, index ->
            selectedCollegeIndex = index
            selectedDepartmentIndex = 0

            val selectedCollege = data.collegeList[index]
            viewModel.selectCollege(selectedCollege)
        }
    }

    private fun handleDepartmentClick() {
        val state = viewModel.uiState.value as? UiState.Success ?: return
        val data = state.data

        // 단과대를 먼저 선택하도록 유도
        if (data.selectedCollege.collegeId == -1) {
            showToast("단과대를 먼저 선택해 주세요.")
            return
        }

        // 학과 목록이 비어있으면 로드
        if (data.departmentList.isEmpty()) {
            viewModel.loadDepartmentList(data.selectedCollege.collegeId)
            return
        }

        val departmentNames = data.departmentList.map { it.departmentName }
        showDropdownPopup(
            anchor = binding.tvDepartment,
            items = departmentNames,
            selectedIndex = selectedDepartmentIndex
        ) { _, index ->
            selectedDepartmentIndex = index

            val selectedDepartment = data.departmentList[index]
            viewModel.selectDepartment(selectedDepartment)
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


    private fun checkDoneAndFinish(data: UserInfoData) {
        if (data.isDone) {
            finish()
        }
    }

}