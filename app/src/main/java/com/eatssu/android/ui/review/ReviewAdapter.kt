package com.eatssu.android.ui.review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.data.model.response.GetReviewListResponse
import com.eatssu.android.databinding.ItemReviewBinding


class ReviewAdapter(private val dataList: List<GetReviewListResponse.Data>?) :
    RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.tvReviewItemId.text = dataList?.get(position)?.writerNickname.toString()
            binding.tvReviewItemComment.text = dataList?.get(position)?.content
            binding.tvReviewItemDate.text= dataList?.get(position)?.writeDate
            binding.tvMenuName.text=dataList?.get(position)?.menu

            binding.tvTotalRating.text = dataList?.get(position)?.mainGrade?.toFloat().toString()
            binding.tvTasteRating.text = dataList?.get(position)?.tasteGrade?.toFloat().toString()
            binding.tvAmountRating.text = dataList?.get(position)?.amountGrade?.toFloat().toString()
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

    override fun getItemCount(): Int = dataList?.size ?: 0
}