package com.eatssu.android.ui.review

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eatssu.android.data.model.response.GetMenuInfoListResponse
import com.eatssu.android.data.model.response.GetReviewListResponse
import com.eatssu.android.databinding.ItemMenuBinding
import com.eatssu.android.databinding.ItemReviewBinding
import java.util.Collections.min
import kotlin.math.min


class ReviewAdapter(private val dataList: List<GetReviewListResponse.Data>) :
    RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.tvReviewItemId.text = dataList[position].writerId.toString()
            binding.tvReviewItemComment.text = dataList[position].content.toString()
            binding.tvReviewItemDate.text = dataList[position].writeDate.toString()

            if (dataList[position].tagList.size == 0) {

                for (i in 0 until min(dataList[position].tagList.size, 3)) {
                    when (i) {
                        0 -> binding.tvReviewItemTag1.text = dataList[position].tagList[i]
                        1 -> binding.tvReviewItemTag2.text = dataList[position].tagList[i]
                        2 -> binding.tvReviewItemTag3.text = dataList[position].tagList[i]
                    }
                }
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