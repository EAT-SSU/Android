package com.eatssu.android.ui.review

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.eatssu.android.App
import com.eatssu.android.R
import com.eatssu.android.databinding.ActivityMyPageBinding
import com.eatssu.android.databinding.ActivityWriteReview1Binding
import com.eatssu.android.ui.BaseActivity

class WriteReview1Activity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteReview1Binding

    private val selectedTags = mutableListOf<String>()
    private val maxTagSelection = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteReview1Binding.inflate(layoutInflater)

//        val inflater = LayoutInflater.from(this)
//        inflater.inflate(R.layout.activity_write_review1, findViewById(R.id.frame_layout), true)
        setContentView(binding.root)
        initializeTagButtonUI()


        binding.included.actionBar.text = "리뷰남기기"

        var MENU_ID: Int = intent.getIntExtra("menuId", -1)
        var menu: String? = intent.getStringExtra("menu")

        var rate: Int = binding.rbReview1.rating.toInt()


        binding.btnChoose1.setOnClickListener(){
//            handleTagSelection(ReviewTag.GOOD)
        }

        binding.btnChoose2.setOnClickListener(){

        }
        binding.btnChoose3.setOnClickListener(){

        }
        binding.btnChoose4.setOnClickListener(){

        }
        binding.btnChoose5.setOnClickListener(){

        }
        binding.btnChoose6.setOnClickListener(){

        }
        binding.btnChoose7.setOnClickListener(){

        }
        binding.btnChoose8.setOnClickListener(){

        }
        binding.btnChoose9.setOnClickListener(){

        }
        binding.btnChoose10.setOnClickListener(){

        }


        binding.menu.text = menu.toString()
        binding.btnNextReview1.setOnClickListener() {
            //0점 처리
            val intent = Intent(this, WriteReview2Activity::class.java)  // 인텐트를 생성해줌,
            Log.d("intent", "click");
            intent.putExtra("rating", rate)
            intent.putExtra("menuId", MENU_ID)
            intent.putExtra("menu", menu)
//            intent.putExtra("tag",selectedTags)
            startActivity(intent)  // 화면 전환을 시켜줌
        }
    }

    private fun handleTagSelection(tag: String) {
        if (selectedTags.contains(tag)) {
            // Tag already selected, remove it
            selectedTags.remove(tag)
            updateTagButtonUI(tag, false)
        } else {
            // Check if maximum tag selection limit is reached
            if (selectedTags.size >= maxTagSelection) {
                Toast.makeText(
                    this,
                    "You can select a maximum of $maxTagSelection tags.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Tag not selected, add it
                selectedTags.add(tag)
                updateTagButtonUI(tag, true)
            }
        }
    }

    private fun updateTagButtonUI(tag: String, isSelected: Boolean) {
        // Update the UI for the tag button based on selection state
        when (tag) {
            "Tag1" -> binding.btnChoose1.isSelected = isSelected
            // Repeat the above line for Tag2 to Tag10, replacing "Tag1" with the appropriate tag names.
        }
    }

    private fun initializeTagButtonUI() {
        // Initialize the UI for tag buttons based on initial selection state
        // Replace "Tag1" to "Tag10" with the appropriate tag names.
        binding.btnChoose1.isSelected = selectedTags.contains("Tag1")
        // Repeat the above line for Tag2 to Tag10, replacing "Tag1" with the appropriate tag names.
    }


//
//    override fun getLayoutResourceId(): Int {
//        return R.layout.activity_write_review1
//    }
}