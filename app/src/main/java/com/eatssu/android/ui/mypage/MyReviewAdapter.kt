package com.eatssu.android.ui.review

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.data.model.response.GetMyReviewResponse
import com.eatssu.android.databinding.ItemMyReviewBinding
import kotlin.math.min


//@POST("mypage/myreview") //내가 쓴 리뷰 모아보기
//fun getMyReviews(@Query("lastReviewId") lastReviewId: Int): Call<GetMyReviewResponse>

class MyReviewAdapter(private val dataList: List<GetMyReviewResponse.Data>) :
    RecyclerView.Adapter<MyReviewAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemMyReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
//            binding.tvReviewItemId.text = dataList[position].writerId.toString()
            binding.tvReviewItemComment.text = dataList[position].content.toString()
            binding.tvReviewItemDate.text = dataList[position].writeDate.toString()
            binding.rbReviewItemRate.rating=dataList[position].grade.toFloat()
            val tagList = dataList[position].tagList

            val tagCount = tagList.size

// 태그 초기화
            binding.tvReviewItemTag1.text = ""
            binding.tvReviewItemTag2.text = ""
            binding.tvReviewItemTag3.text = ""

            val tagTextViews = arrayOf(
                binding.tvReviewItemTag1,
                binding.tvReviewItemTag2,
                binding.tvReviewItemTag3
            )

            for (i in 0 until min(tagCount, tagTextViews.size)) {
                tagTextViews[i].text = tagList[i]
            }

            for (i in tagCount until tagTextViews.size) {
                tagTextViews[i].visibility = View.GONE
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding =
            ItemMyReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = dataList.size
}