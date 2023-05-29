package com.eatssu.android.ui.review

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.data.model.response.GetReviewListResponse
import com.eatssu.android.databinding.ItemReviewBinding
import kotlin.math.min


class ReviewAdapter(private val dataList: List<GetReviewListResponse.Data>) :
    RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.tvReviewItemId.text = dataList[position].writerId.toString()
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
            ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = dataList.size
}