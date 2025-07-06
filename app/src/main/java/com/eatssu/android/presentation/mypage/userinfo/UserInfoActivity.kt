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

    private val college = listOf("단과대", "인문대", "자연대", "법과대", "사회대", "경통대", "경영대", "공과대", "IT대", "자유전공")

    private val majors = mapOf(
        "인문대" to listOf("기독교학과", "국어국문학과", "영어영문학과"),
        "자연대" to listOf("수학과", "물리학과"),
        "법과대" to listOf("법학과"),
        "사회대" to listOf("사회학과"),
        "경통대" to listOf("경제학과", "통계학과"),
        "경영대" to listOf("경영학과"),
        "공과대" to listOf("기계공학과", "전기전자공학과"),
        "IT대" to listOf("컴퓨터학과", "소프트웨어학과"),
        "자유전공" to listOf("자유전공학부")
    )

    // 선택된 단과대와 전공의 인덱스
    private var selectedCollegeIndex = 0
    private var selectedMajorIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle.text = "내 정보" // 툴바 제목 설정


        force = intent.getBooleanExtra("force", false)
        //Todo null 일때 한정으로 화면에서 못 벗어나게 기능 추가

        binding.btnCheckNickname.isEnabled = false
        binding.btnComplete.isEnabled = false

        binding.etChNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            //값 변경 시 실행되는 함수
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //입력값 담기
                inputNickname = binding.etChNickname.text.trim().toString()
                // 값 유무에 따른 활성화 여부
                if (binding.etChNickname.text != null) {
                    val nicknameLength = inputNickname.length
                    binding.btnCheckNickname.isEnabled = nicknameLength in 2..8

                    if (nicknameLength !in 2..8) {
                        binding.btnComplete.isEnabled = false
                        binding.btnCheckNickname.isEnabled = false
                        binding.tvNickname28.setTextColor(getColor(R.color.error))
                        binding.tvNickname28.text = getString(R.string.set_nickname_2_8)
                        binding.etChNickname.setBackgroundResource(R.drawable.shape_text_field_small_red)
                    } else {
                        binding.tvNickname28.setTextColor(getColor(R.color.gray600))

                    }
                }
                /*
                2~8 안되면 중복확인, 완료 둘다 X -> 빨간 보더
                2~8 되면 중복확인 O, 완료 X
                2~8 되고 중복 통과 안되면 -> 발간 보더, 완료 X
                 */

            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        setOnCheckNicknameClickListener()

        setCollegeMajorClickListener()
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

                    } else {
                        binding.btnComplete.isEnabled = false
                        binding.etChNickname.setBackgroundResource(R.drawable.shape_text_field_small_red)
                        binding.tvNickname28.text = getString(R.string.set_nickname_unable)
                        binding.tvNickname28.setTextColor(getColor(R.color.error))
//                        showToast(it.toastMessage) //Todo 사용가능 토스트가 무슨 3번이나 나옴
                    }
                }
            }
        }

        binding.btnComplete.setOnClickListener {
            userInfoViewModel.changeNickname(inputNickname)

            lifecycleScope.launch {
                userInfoViewModel.uiState.collectLatest {
                    if (it.isDone) {
                        showToast(it.toastMessage)
                        finish()
                    }
                }
            }
        }
    }

    /**
     * 단과대와 전공 선택을 위한 드롭다운 팝업 설정
     */
    private fun setCollegeMajorClickListener() {
        binding.tvCollege.setOnClickListener {
            showDropdownPopup(binding.tvCollege, college, selectedCollegeIndex) { selected, index ->
                selectedCollegeIndex = index
                binding.tvCollege.text = selected

                // 학과 초기화
                selectedMajorIndex = 0
                binding.tvMajor.text = "학과"

                showDropdownPopup(
                    binding.tvMajor,
                    majors[selected] ?: emptyList(),
                    selectedMajorIndex
                ) { major, majorIndex ->
                    selectedMajorIndex = majorIndex
                    binding.tvMajor.text = major
                }
            }
        }

        binding.tvMajor.setOnClickListener {
            val selectedCollege = binding.tvCollege.text.toString()

            val majorList = if (selectedCollege == "단과대") {
                // 단과대가 선택되지 않았으면 모든 학과를 하나의 리스트로 보여줌
                majors.values.flatten()
            } else {
                majors[selectedCollege] ?: emptyList()
            }

            showDropdownPopup(
                binding.tvMajor,
                majorList,
                selectedMajorIndex
            ) { major, majorIndex ->
                selectedMajorIndex = majorIndex
                binding.tvMajor.text = major
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

        var selectedItemPosition = items.indexOf(
            when (anchor.id) {
                R.id.fl_college -> binding.tvCollege.text.toString()
                R.id.fl_major -> binding.tvMajor.text.toString()
                else -> ""
            }
        )

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

        popupWindow.showAsDropDown(anchor, -24, binding.tvMajor.height + 8) // 펼쳐지는 위치 조정
    }

}