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
import com.eatssu.android.data.enums.ReviewTag
import com.eatssu.android.databinding.ActivityMyPageBinding
import com.eatssu.android.databinding.ActivityWriteReview1Binding
import com.eatssu.android.ui.BaseActivity

class WriteReview1Activity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteReview1Binding

    private val selectedTags = mutableListOf<ReviewTag>()
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
            handleTagSelection(ReviewTag.GOOD)
        }
        binding.btnChoose2.setOnClickListener(){
            handleTagSelection(ReviewTag.BAD)
        }
        binding.btnChoose3.setOnClickListener(){
            handleTagSelection(ReviewTag.FULL)
        }
        binding.btnChoose4.setOnClickListener(){
            handleTagSelection(ReviewTag.SOSO)
        }
        binding.btnChoose5.setOnClickListener(){
            handleTagSelection(ReviewTag.SAD)
        }
        binding.btnChoose6.setOnClickListener(){
            handleTagSelection(ReviewTag.SPICY)
        }
        binding.btnChoose7.setOnClickListener(){
            handleTagSelection(ReviewTag.BLAND)
        }
        binding.btnChoose8.setOnClickListener(){
            handleTagSelection(ReviewTag.SALTY)
        }
        binding.btnChoose9.setOnClickListener(){
            handleTagSelection(ReviewTag.FAST)
        }
        binding.btnChoose10.setOnClickListener(){
            handleTagSelection(ReviewTag.BIG)
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
            intent.putExtra("selectedTags", ArrayList(selectedTags))
            startActivity(intent)  // 화면 전환을 시켜줌
            Log.d("menuId", MENU_ID.toString());
        }
    }

    private fun handleTagSelection(tag: ReviewTag) {
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

    private fun updateTagButtonUI(tag: ReviewTag, isSelected: Boolean) {
        // Update the UI for the tag button based on selection state
        when (tag) {
            ReviewTag.GOOD -> binding.btnChoose1.isSelected = isSelected
            ReviewTag.BAD -> binding.btnChoose2.isSelected = isSelected
            ReviewTag.FULL -> binding.btnChoose3.isSelected = isSelected
            ReviewTag.SOSO -> binding.btnChoose4.isSelected = isSelected
            ReviewTag.SAD -> binding.btnChoose5.isSelected = isSelected
            ReviewTag.SPICY -> binding.btnChoose6.isSelected = isSelected
            ReviewTag.BLAND -> binding.btnChoose7.isSelected = isSelected
            ReviewTag.SALTY -> binding.btnChoose8.isSelected = isSelected
            ReviewTag.FAST -> binding.btnChoose9.isSelected = isSelected
            ReviewTag.BIG -> binding.btnChoose10.isSelected = isSelected


            // Repeat the above line for the appropriate enum values, updating the corresponding button.
            else -> {
                false
            }
        }
    }



    private fun initializeTagButtonUI() {
        // Initialize the UI for tag buttons based on initial selection state
        // Replace ReviewTag.GOOD with the appropriate enum values, updating the corresponding button.
        binding.btnChoose1.isSelected = selectedTags.contains(ReviewTag.GOOD)
        binding.btnChoose2.isSelected = selectedTags.contains(ReviewTag.BAD)
        binding.btnChoose3.isSelected = selectedTags.contains(ReviewTag.FULL)
        binding.btnChoose4.isSelected = selectedTags.contains(ReviewTag.SOSO)
        binding.btnChoose5.isSelected = selectedTags.contains(ReviewTag.SAD)
        binding.btnChoose6.isSelected = selectedTags.contains(ReviewTag.SPICY)
        binding.btnChoose7.isSelected = selectedTags.contains(ReviewTag.BLAND)
        binding.btnChoose8.isSelected = selectedTags.contains(ReviewTag.SALTY)
        binding.btnChoose9.isSelected = selectedTags.contains(ReviewTag.FAST)
        binding.btnChoose10.isSelected = selectedTags.contains(ReviewTag.FULL)


        // Repeat the above line for the appropriate enum values, updating the corresponding button.
    }

//
//    override fun getLayoutResourceId(): Int {
//        return R.layout.activity_write_review1
//    }
}