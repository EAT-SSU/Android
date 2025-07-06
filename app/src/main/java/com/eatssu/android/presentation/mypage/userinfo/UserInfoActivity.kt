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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserInfoActivity :
    BaseActivity<ActivityUserInfoBinding>(ActivityUserInfoBinding::inflate) {

    private val userInfoViewModel: UserInfoViewModel by viewModels()

    private var inputNickname: String = ""

    private var force: Boolean = false

    private var selectedCollegeIndex = 0
    private var selectedMajorIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "내 정보"

        // 현재 설정된 유저 정보 가져오기
        userInfoViewModel.loadUserInfo()

        force = intent.getBooleanExtra("force", false)

        binding.btnCheckNickname.isEnabled = false
        binding.btnComplete.isEnabled = false

        lifecycleScope.launch {
            userInfoViewModel.uiState.collectLatest {
                binding.etChNickname.setText(it.nickname)
                binding.tvCollege.text = it.selectedCollege.ifEmpty { "단과대" }
                binding.tvMajor.text = it.selectedMajor.ifEmpty { "학과" }
            }
        }

        binding.etChNickname.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                inputNickname = binding.etChNickname.text.trim().toString()
                val nicknameLength = inputNickname.length
                val isValidLength = nicknameLength in 2..8
                val isNicknameChanged = inputNickname != userInfoViewModel.uiState.value.originalNickname

                binding.btnCheckNickname.isEnabled = isValidLength && isNicknameChanged

                if (!isValidLength) {
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

        setOnCheckNicknameClickListener()
        setCollegeMajorClickListener()
        collectButtonEnableState()
    }

    private fun collectButtonEnableState() {
        lifecycleScope.launch {
            userInfoViewModel.uiState.collectLatest {
                binding.btnComplete.isEnabled = it.isNicknameChanged || it.isMajorChanged
            }
        }
    }

    private fun setOnCheckNicknameClickListener() {
        binding.btnCheckNickname.setOnClickListener {
            userInfoViewModel.checkNickname(inputNickname)

            lifecycleScope.launch {
                userInfoViewModel.uiState.collectLatest {
                    if (it.isEnableName) {
                        binding.btnComplete.isEnabled = true
                        binding.tvNickname28.text = getString(R.string.set_nickname_able)
                        binding.etChNickname.setBackgroundResource(R.drawable.shape_text_field_small)
                        binding.tvNickname28.setTextColor(getColor(R.color.gray600))
                        userInfoViewModel.updateNickname(inputNickname)
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
            val currentState = userInfoViewModel.uiState.value

            if (currentState.isNicknameChanged) {
                // 닉네임 변경이 있는 경우 서버에 전체 정보 저장
                userInfoViewModel.changeUserInfo()

                lifecycleScope.launch {
                    userInfoViewModel.uiState.collectLatest {
                        if (it.isDone) {
                            showToast(it.toastMessage)
                            finish()
                        }
                    }
                }
            } else {
                // 닉네임은 그대로이고 학과/단과대만 변경된 경우
                // TODO: 서버에 학과/단과대 정보만 저장하는 로직 추가
                showToast("정보가 성공적으로 저장되었습니다.")
                finish()
            }
        }
    }

    private fun setCollegeMajorClickListener() {
        binding.tvCollege.setOnClickListener {
            val colleges = userInfoViewModel.getTotalColleges()
            showDropdownPopup(binding.tvCollege, colleges, selectedCollegeIndex) { selected, index ->
                selectedCollegeIndex = index
                binding.tvCollege.text = selected

                selectedMajorIndex = 0
                binding.tvMajor.text = "학과"

                val majors = userInfoViewModel.getTotalMajors(selected)
                showDropdownPopup(
                    binding.tvMajor,
                    majors,
                    selectedMajorIndex
                ) { major, majorIndex ->
                    selectedMajorIndex = majorIndex
                    binding.tvMajor.text = major
                    userInfoViewModel.updateMajor(major)
                }
                userInfoViewModel.updateCollege(selected)
            }
        }

        binding.tvMajor.setOnClickListener {
            val selectedCollege = binding.tvCollege.text.toString()
            val majorList = userInfoViewModel.getTotalMajors(selectedCollege)

            showDropdownPopup(
                binding.tvMajor,
                majorList,
                selectedMajorIndex
            ) { major, majorIndex ->
                selectedMajorIndex = majorIndex
                binding.tvMajor.text = major
                userInfoViewModel.updateMajor(major)
            }
        }
    }

    private fun showDropdownPopup(
        anchor: View,
        items: List<String>,
        selectedIndex: Int,
        onItemClick: (selected: String, selectedIndex: Int) -> Unit
    ) {
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

        popupWindow.showAsDropDown(anchor, -24, binding.tvMajor.height + 8)
    }
}
