package com.eatssu.android.presentation.review.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eatssu.android.databinding.ItemReviewBinding
import com.eatssu.android.domain.model.Review


class ReviewAdapter :
    ListAdapter<Review, ReviewAdapter.ViewHolder>(ReviewDiffCallback()) {

    interface OnItemClickListener {
        fun onMyReviewClicked(view: View, reviewData: Review)
        fun onOthersReviewClicked(view: View, reviewData: Review)
    }

    private lateinit var mOnItemClickListener: OnItemClickListener

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        mOnItemClickListener = onItemClickListener
    }

    inner class ViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Review) {
            binding.tvWriterNickname.text = data.writerNickname
            binding.tvReviewItemComment.text = data.content
            binding.tvReviewItemDate.text = data.writeDate
            binding.tvMenuName.text = data.menu
            binding.rbRate.rating = data.mainGrade.toFloat()

            if (!data.imgUrl.isNullOrEmpty()) {
                Glide.with(itemView)
                    .load(data.imgUrl[0])
                    .into(binding.ivReviewPhoto)
                binding.ivReviewPhoto.visibility = View.VISIBLE
                binding.cvPhotoReview.visibility = View.VISIBLE

                if (data.imgUrl[0].isEmpty()) {
                    binding.ivReviewPhoto.visibility = View.GONE
                    binding.cvPhotoReview.visibility = View.GONE
                }
            } else {
                binding.ivReviewPhoto.visibility = View.GONE
                binding.cvPhotoReview.visibility = View.GONE
            }

            binding.btnDetail.setOnClickListener { v: View ->
                if (data.isWriter) {
                    mOnItemClickListener.onMyReviewClicked(v, data)
                } else {
                    mOnItemClickListener.onOthersReviewClicked(v, data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = getItem(position) // `ListAdapter`에서 제공
        holder.bind(review)
    }
}

class ReviewDiffCallback : DiffUtil.ItemCallback<Review>() {
    override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
        // 고유 식별자를 비교 (예: id)
        return oldItem.reviewId == newItem.reviewId
    }

    override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
        // 객체의 내용 전체를 비교
        return oldItem == newItem
    }
}
