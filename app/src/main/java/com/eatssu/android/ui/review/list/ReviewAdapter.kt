package com.eatssu.android.ui.review

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eatssu.android.data.model.response.GetReviewListResponse
import com.eatssu.android.databinding.ItemOthersReviewBinding
import com.eatssu.android.databinding.ItemReviewBinding
import com.eatssu.android.ui.review.etc.MyReviewDialogActivity
import com.eatssu.android.ui.review.etc.ReportActivity


class ReviewAdapter(private val dataList: GetReviewListResponse) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val data = dataList.dataList?.get(position)
            binding.tvWriterNickname.text = data?.writerNickname.toString()
            binding.tvReviewItemComment.text = data?.content
            binding.tvReviewItemDate.text = data?.writeDate
            binding.tvMenuName.text = data?.menu

            binding.tvTotalRating.text = data?.mainGrade?.toString()
            binding.tvTasteRating.text = data?.tasteGrade?.toString()
            binding.tvAmountRating.text = data?.amountGrade?.toString()

            val imgUrlList: List<String>? = data?.imgUrlList

            val imageView: ImageView = binding.ivReviewPhoto

            if (imgUrlList.isNullOrEmpty()) {
                imageView.visibility = View.GONE
            } else {
                val imageUrl = imgUrlList[0]

                Glide.with(itemView)
                    .load(imageUrl)
                    .into(imageView)
                imageView.visibility = View.VISIBLE
            }

            binding.btnDetail.setOnClickListener {
                if (data?.isWriter == true) {
                    val intent = Intent(binding.btnDetail.context, MyReviewDialogActivity::class.java)
                    intent.putExtra("reviewId", data.reviewId)
                    intent.putExtra("menu", data.menu)
                    ContextCompat.startActivity(binding.btnDetail.context, intent, null)
                }
            }
        }
    }

    inner class OthersViewHolder(private val binding: ItemOthersReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val data = dataList.dataList?.get(position)
            binding.tvWriterNickname.text = data?.writerNickname.toString()
            binding.tvReviewItemComment.text = data?.content
            binding.tvReviewItemDate.text = data?.writeDate
            binding.tvMenuName.text = data?.menu

            binding.tvTotalRating.text = data?.mainGrade?.toFloat().toString()
            binding.tvTasteRating.text = data?.tasteGrade?.toFloat().toString()
            binding.tvAmountRating.text = data?.amountGrade?.toFloat().toString()

            val imgUrlList: List<String>? = data?.imgUrlList

            val imageView: ImageView = binding.ivReviewPhoto

            if (imgUrlList.isNullOrEmpty()) {
                imageView.visibility = View.GONE
            } else {
                val imageUrl = imgUrlList[0]

                Glide.with(itemView)
                    .load(imageUrl)
                    .into(imageView)
                imageView.visibility = View.VISIBLE
            }

            binding.tvReviewItemReport.setOnClickListener {
                if (data?.isWriter == false) {
                    val intent = Intent(binding.tvReviewItemReport.context, ReportActivity::class.java)
                    intent.putExtra("reviewId", data.reviewId)
                    intent.putExtra("menu", data.menu)
                    ContextCompat.startActivity(binding.tvReviewItemReport.context, intent, null)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_MY_REVIEW -> {
                val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ViewHolder(binding)
            }
            VIEW_TYPE_OTHERS_REVIEW -> {
                val othersBinding = ItemOthersReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                OthersViewHolder(othersBinding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> holder.bind(position)
            is OthersViewHolder -> holder.bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (dataList.dataList?.get(position)?.isWriter == true) {
            VIEW_TYPE_MY_REVIEW
        } else {
            VIEW_TYPE_OTHERS_REVIEW
        }
    }

    override fun getItemCount(): Int = dataList.dataList?.size ?: 0

    companion object {
        private const val VIEW_TYPE_MY_REVIEW = 1
        private const val VIEW_TYPE_OTHERS_REVIEW = 2
    }
}
