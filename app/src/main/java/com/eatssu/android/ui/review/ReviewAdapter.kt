package com.eatssu.android.ui.review

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.data.model.response.GetMyReviewResponse
import com.eatssu.android.data.model.response.GetReviewListResponse
import com.eatssu.android.databinding.ItemReviewBinding
import kotlin.math.min


class ReviewAdapter(private val dataList: List<GetReviewListResponse.Data>) :
    RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.tvReviewItemId.text = dataList[position].writerNickname
            binding.tvReviewItemComment.text = dataList[position].content
            binding.tvReviewItemDate.text = dataList[position].writeDate
            binding.tvRatingTotal.text= dataList[position].grade.toFloat().toString()
            val tagList = dataList[position].tagList
            val tagCount = tagList.size
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