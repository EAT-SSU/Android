package com.eatssu.android.view.review

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.adapter.MenuPickAdapter
import com.eatssu.android.data.RetrofitImpl.retrofit
import com.eatssu.android.data.service.MenuService
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityMenuPickBinding


class MenuPickActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuPickBinding
    private lateinit var menuPickAdapter: MenuPickAdapter


    private lateinit var menuService: MenuService
    private lateinit var reviewService: ReviewService

    private lateinit var items: ArrayList<Pair<String, Long>>

    private var currentItemIndex = 0


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuPickBinding.inflate(layoutInflater)
        setContentView(binding.root)

        menuService = retrofit.create(MenuService::class.java)
        reviewService = retrofit.create(ReviewService::class.java)


        val menuNameArray = intent.getStringArrayListExtra("menuNameArray")
        val menuIdArray = intent.getLongArrayExtra("menuIdArray")

        Log.d("post", "받은" + menuNameArray.toString())


        /* 리사이클러뷰 어댑터 */
        menuPickAdapter = MenuPickAdapter(menuNameArray, menuIdArray)
        val recyclerView = binding.rvMenuPicker
        recyclerView.adapter = menuPickAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        // "다음" 버튼 클릭 시, 다음 항목의 리뷰화면을 시작합니다.
        binding.btnNextReview.setOnClickListener {

            items = menuPickAdapter.sendItem()

            if (currentItemIndex < items.size) {
                val item = items[currentItemIndex]
                val intent = Intent(this, WriteReviewActivity::class.java)
                intent.putExtra("itemName", item.first)
                intent.putExtra("itemId", item.second)

                Log.d("post", "넘길게$item")
                startActivityForResult(intent, 1)
            } else {
                // 모든 항목을 처리한 경우, A 화면으로 돌아갑니다.
                currentItemIndex = 0
                finish()
            }
        }
    }

    // B 화면에서 돌아올 때 호출되는 콜백 메서드
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // B 화면에서 돌아온 경우, 다음 항목을 처리합니다.
            currentItemIndex++
            // "Go to B Screen" 버튼을 클릭하여 B 화면을 다시 열 수 있도록 합니다.
            binding.btnNextReview.performClick()
        }
    }

}

