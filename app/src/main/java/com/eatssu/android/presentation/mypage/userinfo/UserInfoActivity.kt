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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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

    private val userInfoViewModel: UserInfoViewModel by viewModels()
    private var inputNickname: String = ""
    private var force: Boolean = false
    private var selectedCollegeIndex = 0
    private var selectedDepartmentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "내 정보"

        force = intent.getBooleanExtra("force", false)

        collectUiState()
        collectUiEvent()
        collectButtonEnableState()

        setButtonEnabledDefault()
        setNicknameTextWatcher()
        setOnCheckNicknameDuplicationClickListener()
        setCollegeDepartmentClickListener()
    }

    private fun setButtonEnabledDefault() {
        binding.btnCheckNicknameDuplication.isEnabled = false
        binding.btnComplete.isEnabled = false
    }

    private fun collectUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userInfoViewModel.uiState.collectLatest { state ->
                    when (state) {
                        is UiState.Loading -> {
                            // 로딩 중 처리
                        }

                        is UiState.Success -> {
                            val data = state.data
                            if (binding.etChNickname.text.toString() != data?.nickname) {
                                binding.etChNickname.setText(data?.nickname)
                                binding.etChNickname.setSelection(binding.etChNickname.text.length) // 커서 끝으로 이동
                            }
                            binding.tvCollege.text = data?.selectedCollege?.collegeName
                            binding.tvDepartment.text = data?.selectedDepartment?.departmentName

                            if (data?.isDone == true) {
                                showToast("정보가 업데이트 되었습니다.")
                                finish()
                            }
                        }

                        is UiState.Error -> {
                            // viewModel에서 토스트 메시지 처리하므로 여기서는 별도 처리 없음
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    // UiEvent
    private fun collectUiEvent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userInfoViewModel.uiEvent.collectLatest { event ->
                    when (event) {
                        is UiEvent.ShowToast -> showToast(event.message)
                    }
                }
            }
        }
    }

    private fun setNicknameTextWatcher() {
        binding.etChNickname.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                inputNickname = binding.etChNickname.text.trim().toString()
                val nicknameLength = inputNickname.length
                val isValidLength = nicknameLength in 2..8
                userInfoViewModel.updateNickname(inputNickname)

                val currentState =
                    (userInfoViewModel.uiState.value as? UiState.Success)?.data ?: return
                val isNicknameChanged = inputNickname != currentState.originalNickname

                binding.btnCheckNicknameDuplication.isEnabled = isValidLength && isNicknameChanged
                binding.btnComplete.isEnabled = false

                if (!isValidLength && inputNickname.isNotEmpty()) {
                    binding.tvNickname28.setTextColor(getColor(R.color.error))
                    binding.tvNickname28.text = getString(R.string.set_nickname_2_8)
                    binding.etChNickname.setBackgroundResource(R.drawable.shape_text_field_small_red)
                } else {
                    binding.tvNickname28.setTextColor(getColor(R.color.gray600))
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun collectButtonEnableState() {
        lifecycleScope.launch {
            userInfoViewModel.uiState.collectLatest { it ->
                if (it !is UiState.Success) return@collectLatest // 초기 상태나 로딩, 에러 시에는 아무 것도 하지 않음
                val state =
                    it.data ?: return@collectLatest // Success일 때 내부 data(UserInfoUiState) 가져오기

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
            userInfoViewModel.checkNicknameDuplication(inputNickname)

            // 닉네임 중복 확인 후 UI 상태 업데이트 로직
            // TODO 이 부분은 ViewModel에서 처리하는 것이 더 좋음
            // 입력 변화 이벤트(TextWatcher) 시점에서 ViewModel로 값을 보내고,
            // 검증 결과를 StateFlow로 내려주게 수정하자
            lifecycleScope.launch {
                userInfoViewModel.uiState.collectLatest { state ->
                    if (state !is UiState.Success) return@collectLatest
                    val state = state.data ?: return@collectLatest

                    if (state.isEnableName) {
                        binding.btnCheckNicknameDuplication.isEnabled = false // 중복확인 비활성화
                        binding.btnComplete.isEnabled = true // 저장하기 활성화
                        binding.tvNickname28.text = getString(R.string.set_nickname_able)
                        binding.etChNickname.setBackgroundResource(R.drawable.shape_text_field_small)
                        binding.tvNickname28.setTextColor(getColor(R.color.gray600))
                    } else {
                        binding.btnComplete.isEnabled = false
                        binding.etChNickname.setBackgroundResource(R.drawable.shape_text_field_small_red)
                        binding.tvNickname28.text = getString(R.string.set_nickname_unable)
                        binding.tvNickname28.setTextColor(getColor(R.color.error))
                    }
                }
            }
        }

        binding.btnComplete.setOnClickListener {
            val currentState = (userInfoViewModel.uiState.value as? UiState.Success)?.data
                ?: return@setOnClickListener

            if (currentState.isNicknameChanged) {
                // 닉네임 변경 → 닉네임 저장 + 완료 시 학과 저장도 호출
                userInfoViewModel.changeUserNickname()

                lifecycleScope.launch {
                    userInfoViewModel.uiState.collectLatest { state ->
                        if (state !is UiState.Success) return@collectLatest
                        val state = state.data ?: return@collectLatest

                        if (state.isDone) {
                            // 닉네임 저장 성공 후 학과 변경도 필요하다면 호출
                            if (state.isCollegeChanged || state.isDepartmentChanged) {
                                userInfoViewModel.updateUserDepartment()
                            } else {
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

    private fun setCollegeDepartmentClickListener() {
        binding.flCollege.setOnClickListener {

            // 최신 state 사용
            val currentState = (userInfoViewModel.uiState.value as? UiState.Success)?.data
                ?: return@setOnClickListener

            // 닉네임이 변경되었고 + 아직 중복확인을 안 했다면 막기
            if (userInfoViewModel.shouldBlockCollegeDepartmentChange()) {
                showToast("닉네임 중복 확인을 완료해 주세요.")
                return@setOnClickListener
            }

            // 단과대 목록 요청
            userInfoViewModel.loadCollegeList()

            if (currentState.collegeList.isNotEmpty()) {
                val collegeNames = currentState.collegeList.map { it.collegeName }
                showDropdownPopup(binding.tvCollege, collegeNames, selectedCollegeIndex) { selected, index ->
                    selectedCollegeIndex = index
                    binding.tvCollege.text = selected

                    selectedDepartmentIndex = 0
                    binding.tvDepartment.text = "학과"

                    val selectedCollege = currentState.collegeList[index]
                    userInfoViewModel.updateInputCollege(selectedCollege)
                    userInfoViewModel.loadDepartmentList(selectedCollege.collegeId)
                }
            }
        }

        binding.flDepartment.setOnClickListener {
            val currentState = (userInfoViewModel.uiState.value as? UiState.Success)?.data
                ?: return@setOnClickListener

            // 닉네임이 변경되었고 + 아직 중복확인을 안 했다면 막기
            if (userInfoViewModel.shouldBlockCollegeDepartmentChange()) {
                showToast("닉네임 중복 확인을 완료해 주세요.")
                return@setOnClickListener
            }

            // 학과 리스트가 비어있다면 현재 단과대 기준으로 다시 로드
            if (currentState.departmentList.isEmpty() && currentState.selectedCollege.collegeId != -1) {
                userInfoViewModel.loadDepartmentList(currentState.selectedCollege.collegeId)
            }

            if (currentState.departmentList.isNotEmpty()) {
                val departmentNames = currentState.departmentList.map { it.departmentName }
                showDropdownPopup(binding.tvDepartment, departmentNames, selectedDepartmentIndex) { departmentName, departmentIndex ->
                    selectedDepartmentIndex = departmentIndex
                    binding.tvDepartment.text = departmentName
                    userInfoViewModel.updateInputDepartment(currentState.departmentList[departmentIndex])
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
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(this@UserInfoActivity, android.R.color.transparent))
                }
            }
        }

        popupWindow.elevation = 8f
        popupWindow.isOutsideTouchable = true
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.shape_text_field_small))

        popupWindow.showAsDropDown(anchor, -24, binding.tvDepartment.height + 8)

        // 현재 팝업 윈도우를 저장
        currentPopup = popupWindow
    }
}
