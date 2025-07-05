package com.eatssu.android.presentation.mypage.usernamechange

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
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

    private val userNameChangeViewModel: UserNameChangeViewModel by viewModels()

    private var inputNickname: String = ""

    private var force: Boolean = false

    private var nicknameDuplicate: Boolean = false

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

        setupCollegeSpinners(binding.spinnerCollege, college)
        setupCollegeSpinners(binding.spinnerMajor, majors.values.toList().flatten())

        setOnClickListener()
    }

//    override fun onBackPressed() {
//        if (force) {
//            // force가 true일 때는 뒤로가기 버튼을 무시하고 현재 화면에 머물게 함
//            // 여기에 다른 동작을 추가할 수도 있음
//            showToast("닉네임 설정 후, 서비스를 이용하실 수 있습니다.")
//        } else {
//            // force가 false일 때는 기본 뒤로가기 동작을 수행
//            super.onBackPressed()
//        }
//    }

    private fun setOnClickListener() {
        binding.btnCheckNickname.setOnClickListener {
            userNameChangeViewModel.checkNickname(inputNickname)

            lifecycleScope.launch {
                userNameChangeViewModel.uiState.collectLatest {
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
            userNameChangeViewModel.changeNickname(inputNickname)

            lifecycleScope.launch {
                userNameChangeViewModel.uiState.collectLatest {
                    if (it.isDone) {
                        showToast(it.toastMessage)
                        finish()
                    }
                }
            }
        }
    }

    private fun setupCollegeSpinners(spinner: Spinner, items: List<String>) {
        val adapter = object : ArrayAdapter<String>(
            this,
            R.layout.item_spinner_selected,
            items
        ) {
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val inflater = layoutInflater
                val view = inflater.inflate(R.layout.item_spinner, parent, false)
                val textView = view.findViewById<TextView>(R.id.tv_spinner_item)
                textView.text = getItem(position)

                // 선택된 아이템 강조 배경
                if (spinner.selectedItemPosition == position && position != 0) {
                    view.setBackgroundResource(R.drawable.bg_spinner_selected_item)
                } else {
                    view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
                }

                return view
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val tv = view.findViewById<TextView>(R.id.tv_spinner_item)
                tv.setTextColor(
                    if (position == 0)
                        ContextCompat.getColor(context, R.color.gray400)
                    else
                        ContextCompat.getColor(context, R.color.black)
                )
                return view
            }
        }

        adapter.setDropDownViewResource(R.layout.item_spinner)
        spinner.adapter = adapter
    }
}