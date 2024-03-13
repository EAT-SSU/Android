package com.eatssu.android.ui.review.list

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eatssu.android.data.model.Review
import com.eatssu.android.databinding.ItemOthersReviewBinding
import com.eatssu.android.databinding.ItemReviewBinding
import com.eatssu.android.ui.review.delete.MyReviewDialogActivity
import com.eatssu.android.ui.review.report.ReportActivity


class ReviewAdapter(private val dataList: List<Review>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val data = dataList[position].apply {
                binding.tvWriterNickname.text = writerNickname
                binding.tvReviewItemComment.text = content
                binding.tvReviewItemDate.text = writeDate
                binding.tvMenuName.text = menu

                binding.tvTotalRating.text = mainGrade.toString()
                binding.tvTasteRating.text = tasteGrade.toString()
                binding.tvAmountRating.text = amountGrade.toString()

            }

            val imgUrl: String? = data.imgUrlList

            val imageView: ImageView = binding.ivReviewPhoto

            if (imgUrl.isNullOrEmpty()) {
                imageView.visibility = View.GONE
            } else {

                Glide.with(itemView)
                    .load(imgUrl)
                    .into(imageView)
                imageView.visibility = View.VISIBLE
            }

            binding.btnDetail.setOnClickListener {
                if (data.isWriter) {
                    val intent =
                        Intent(binding.btnDetail.context, MyReviewDialogActivity::class.java)
                    intent.putExtra("reviewId", data.reviewId)
                    intent.putExtra("menu", data.menu)
                    intent.putExtra("comment", data.content)
                    intent.putExtra("mainRating", data.mainGrade)
                    intent.putExtra("amountRating", data.amountGrade)
                    intent.putExtra("tasteRating", data.tasteGrade)
                    Log.d("ReviewAdapter", data.toString())
                    ContextCompat.startActivity(binding.btnDetail.context, intent, null)
                }
            }
        }
    }

    inner class OthersViewHolder(private val binding: ItemOthersReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val data = dataList?.get(position)
            binding.tvWriterNickname.text = data?.writerNickname.toString()
            binding.tvReviewItemComment.text = data?.content
            binding.tvReviewItemDate.text = data?.writeDate
            binding.tvMenuName.text = data?.menu

            binding.tvTotalRating.text = data?.mainGrade?.toString()
            binding.tvTasteRating.text = data?.tasteGrade?.toString()
            binding.tvAmountRating.text = data?.amountGrade?.toString()

            val imgUrlList: String? = data?.imgUrlList

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
        return if (dataList?.get(position)?.isWriter == true) {
            VIEW_TYPE_MY_REVIEW
        } else {
            VIEW_TYPE_OTHERS_REVIEW
        }
    }

    override fun getItemCount(): Int = dataList?.size ?: 0

    companion object {
        private const val VIEW_TYPE_MY_REVIEW = 1
        private const val VIEW_TYPE_OTHERS_REVIEW = 2
    }
}
