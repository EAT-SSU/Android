package com.eatssu.android.view.review

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.eatssu.android.data.RetrofitImpl.retrofit
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.databinding.ActivityReviewListBinding
import com.eatssu.android.repository.ReviewRepository
import com.eatssu.android.ui.review.ReviewAdapter
import com.eatssu.android.viewmodel.ReviewViewModel
import com.eatssu.android.viewmodel.factory.ReviewViewModelFactory
import kotlin.properties.Delegates

class ReviewListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewListBinding

    private lateinit var viewModel: ReviewViewModel
    private lateinit var reviewService: ReviewService

    private lateinit var menuType: String
    private var itemId by Delegates.notNull<Long>()
//    private lateinit var itemIdList: ArrayList<Long>
//    private lateinit var itemName: ArrayList<String>

    private lateinit var itemName: String
    var adapter: ReviewAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReviewListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //binding.included.actionBar.text = "리뷰"

        supportActionBar?.title = "리뷰"


        reviewService = retrofit.create(ReviewService::class.java)

        val repository = ReviewRepository(reviewService)

        viewModel = ViewModelProvider(this, ReviewViewModelFactory(repository))[ReviewViewModel::class.java]

        //get menuId
        menuType = intent.getStringExtra("menuType").toString()
        itemId = intent.getLongExtra("itemId", 0)
//        itemIdList = intent.getLongArrayExtra("itemIdList")
        itemName = intent.getStringExtra("itemName").toString()
//        intent.putExtra(
//            "itemName", dataList.fixMenuInfoList[position].name
//        )
        Log.d("post", "고정메뉴${itemName}")

        val menuIdArray = intent.getLongArrayExtra("menuIdArray")
        val menuNameArray = intent.getStringArrayListExtra("menuNameArray")


        Log.d("post", menuType + itemId)

        when (menuType) {
            "FIX" -> {
                viewModel.loadReviewList(MenuType.FIX, 0, itemId)
                viewModel.loadReviewInfo(MenuType.FIX, 0, itemId)

                binding.btnNextReview.setOnClickListener() {
                    val intent = Intent(this, WriteReviewActivity::class.java)  // 인텐트를 생성해줌,
                    intent.putExtra("itemId", itemId)
                    intent.putExtra("menuType", menuType)
//                    intent.putStringArrayListExtra("itemName", itemName)
                    intent.putExtra("itemName", itemName)
                    startActivity(intent)  // 화면 전환을 시켜줌
                }
            }
            "CHANGE" -> {
                viewModel.loadReviewList(MenuType.CHANGE, itemId, 0)
                viewModel.loadReviewInfo(MenuType.CHANGE, itemId, 0)

                binding.btnNextReview.setOnClickListener() {
                    val intent = Intent(this, MenuPickActivity::class.java)  // 인텐트를 생성해줌,
                    intent.putExtra("itemId", itemId)
                    intent.putExtra("menuType", menuType)

                    intent.putStringArrayListExtra("menuNameArray", menuNameArray)
                    intent.putExtra("menuIdArray", menuIdArray)

                    if (menuNameArray != null) {
                        Log.d("post", (menuNameArray + menuIdArray).toString())
                    }

                    startActivity(intent)  // 화면 전환을 시켜줌
                }

            }
            else -> {
                Log.d("post", "잘못된 식당 정보입니다.")
            }
        }

        setInfoData()
        setListData()
    }

    private fun setListData(){
        viewModel.reviewList.observe(this) { reviewList ->
            Log.d("post", reviewList.dataList.toString())
            adapter = ReviewAdapter(reviewList)
            val recyclerView = binding.rvReview
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.setHasFixedSize(true)
            //구분선 주석처리
//            recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        }
    }
    private fun setInfoData(){

        viewModel.reviewInfo.observe(this) { reviewInfo ->
            Log.d("post", reviewInfo.toString())

            Log.d("post", reviewInfo.menuName.javaClass.name)
//            itemName = reviewInfo.menuName as ArrayList<String>

            binding.tvMenu.text = reviewInfo.menuName.toString()

            binding.tvRate.text =
                String.format("%.1f", reviewInfo.mainGrade.toFloat())
                    .toFloat().toString()
            binding.tvGradeTaste.text =
                String.format("%.1f", reviewInfo.tasteGrade.toFloat())
                    .toFloat().toString()
            binding.tvGradeAmount.text =
                String.format("%.1f", reviewInfo.amountGrade.toFloat())
                    .toFloat().toString()
            binding.tvReviewNumCount.text = reviewInfo.totalReviewCount.toString()

            val cnt = reviewInfo.totalReviewCount

            binding.progressBar1.max = cnt
            binding.progressBar2.max = cnt
            binding.progressBar3.max = cnt
            binding.progressBar4.max = cnt
            binding.progressBar5.max = cnt

            binding.progressBar1.progress = reviewInfo.reviewGradeCnt.oneCnt
            binding.progressBar2.progress = reviewInfo.reviewGradeCnt.twoCnt
            binding.progressBar3.progress = reviewInfo.reviewGradeCnt.threeCnt
            binding.progressBar4.progress = reviewInfo.reviewGradeCnt.fourCnt
            binding.progressBar5.progress = reviewInfo.reviewGradeCnt.fiveCnt

        }
    }

    override fun onRestart() {
        super.onRestart()

        Log.d("post", "onRestart")

        // 다시 데이터를 로드하고 어댑터를 업데이트
        when (menuType) {
            "FIX" -> {
                viewModel.loadReviewList(MenuType.FIX, 0, itemId)
                viewModel.loadReviewInfo(MenuType.FIX, 0, itemId)
            }
            "CHANGE" -> {
                viewModel.loadReviewList(MenuType.CHANGE, itemId, 0)
                viewModel.loadReviewInfo(MenuType.CHANGE, itemId, 0)
            }
            else -> {
                Log.d("post", "잘못된 식당 정보입니다.")
            }
        }
        Log.d("post", "onRestart")

        setInfoData()

        Log.d("post", "onRestart"+viewModel.reviewInfo.value)
        setListData()
        Log.d("post", "onRestart")


    }

    override fun onResume() {
        super.onResume()
        Log.d("post", "resume")

        // 다시 데이터를 로드하고 어댑터를 업데이트
        when (menuType) {
            "FIX" -> {
                viewModel.loadReviewList(MenuType.FIX, 0, itemId)
                viewModel.loadReviewInfo(MenuType.FIX, 0, itemId)
            }
            "CHANGE" -> {
                viewModel.loadReviewList(MenuType.CHANGE, itemId, 0)
                viewModel.loadReviewInfo(MenuType.CHANGE, itemId, 0)
            }
            else -> {
                Log.d("post", "잘못된 식당 정보입니다.")
            }
        }
        Log.d("post", "resume시작")

        setInfoData()

        Log.d("post", "resume중간")
        setListData()
        Log.d("post", "resume끝")


    }
}
