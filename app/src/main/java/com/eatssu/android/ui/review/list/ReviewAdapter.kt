package com.eatssu.android.ui.review.list

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

                binding.rbRate.rating = mainGrade.toFloat()
            }



            if (data.imgUrl?.size != 0) {
                Log.d("ReviewAdapter", data.content + data.imgUrl?.size.toString())
                Glide.with(itemView)
                    .load(data.imgUrl?.get(0))
                    .into(binding.ivReviewPhoto)
                binding.ivReviewPhoto.visibility = View.VISIBLE

                if (data.imgUrl?.get(0) == "") {
                    binding.ivReviewPhoto.visibility = View.GONE

                }
                if (data.imgUrl?.get(0) == null) {
                    binding.ivReviewPhoto.visibility = View.GONE
                }
            } else {
                binding.ivReviewPhoto.visibility = View.GONE
            }


            binding.btnDetail.setOnClickListener {
                if (data.isWriter) {
                    val intent =
                        Intent(binding.btnDetail.context, MyReviewDialogActivity::class.java)
                    intent.putExtra("reviewId", data.reviewId)
                    intent.putExtra("menu", data.menu)
                    intent.putExtra("content", data.content)
                    intent.putExtra("mainGrade", data.mainGrade)
                    intent.putExtra("amountGrade", data.amountGrade)
                    intent.putExtra("tasteGrade", data.tasteGrade)

                    Log.d("ReviewFixedActivity", "전전:" + data.reviewId)
                    Log.d("ReviewFixedActivity", "전전:" + data.menu)
                    Log.d("ReviewFixedActivity", "전전:" + data.content)
////                    Timber.d("내용: "+data.content)
                    ContextCompat.startActivity(binding.btnDetail.context, intent, null)
                }
            }
        }
    }

    inner class OthersViewHolder(private val binding: ItemOthersReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {

            val data = dataList[position].apply {
                binding.tvWriterNickname.text = writerNickname
                binding.tvReviewItemComment.text = content
                binding.tvReviewItemDate.text = writeDate


                binding.rbRate.rating = mainGrade.toFloat()
            }


            if (data.imgUrl?.size != 0) {
                Glide.with(itemView)
                    .load(data.imgUrl?.get(0))
                    .into(binding.ivReviewPhoto)
                binding.ivReviewPhoto.visibility = View.VISIBLE

                if (data.imgUrl?.get(0) == "") {
                    binding.ivReviewPhoto.visibility = View.GONE

                }
                if (data.imgUrl?.get(0) == null) {
                    binding.ivReviewPhoto.visibility = View.GONE
                }
            } else {
                binding.ivReviewPhoto.visibility = View.GONE
            }


            binding.tvReviewItemReport.setOnClickListener {
                if (!data.isWriter) {
                    val intent =
                        Intent(binding.tvReviewItemReport.context, ReportActivity::class.java)
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
                val binding =
                    ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ViewHolder(binding)
            }

            VIEW_TYPE_OTHERS_REVIEW -> {
                val othersBinding = ItemOthersReviewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
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
        return if (dataList[position].isWriter) {
            VIEW_TYPE_MY_REVIEW
        } else {
            VIEW_TYPE_OTHERS_REVIEW
        }
    }

    override fun getItemCount(): Int = dataList.size

    companion object {
        private const val VIEW_TYPE_MY_REVIEW = 1
        private const val VIEW_TYPE_OTHERS_REVIEW = 2
    }
}
