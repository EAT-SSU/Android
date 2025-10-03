package com.eatssu.android.presentation.mypage.myreview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.eatssu.android.databinding.ItemReviewBinding
import com.eatssu.android.domain.model.Review
import timber.log.Timber

class MyReviewAdapter :
    ListAdapter<Review, MyReviewAdapter.ViewHolder>(ReviewDiffCallback()) {

    interface OnItemClickListener {
        fun onMyReviewClicked(view: View, reviewData: Review)
    }

    // 객체 저장 변수
    private lateinit var mOnItemClickListener: OnItemClickListener

    // 객체 전달 메서드
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        mOnItemClickListener = onItemClickListener
    }

    inner class ViewHolder(private val binding: ItemReviewBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Review) {
            binding.tvWriterNickname.text = ""
            binding.tvReviewItemComment.text = data.content
            binding.tvReviewItemDate.text = data.writeDate
            binding.tvMenuName.text = data.menu
            binding.rbRate.rating = data.mainGrade.toFloat()

            val firstImageUrl = data.imgUrl?.firstOrNull()

            if (!firstImageUrl.isNullOrEmpty()) {
                Glide.with(itemView)
                    .load(firstImageUrl)
                    .into(binding.ivReviewPhoto)
                binding.ivReviewPhoto.visibility = View.VISIBLE
                binding.cvPhotoReview.visibility = View.VISIBLE
            } else {
                binding.ivReviewPhoto.visibility = View.GONE
                binding.cvPhotoReview.visibility = View.GONE
            }

            binding.btnDetail.setOnClickListener { v: View ->
                mOnItemClickListener.onMyReviewClicked(v, data)

                Timber.d(
                    "리뷰 상세 정보 - ID: %d, 메뉴: %s, 내용: %s",
                    data.reviewId,
                    data.menu,
                    data.content
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding =
            ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class ReviewDiffCallback : DiffUtil.ItemCallback<Review>() {
    override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
        return oldItem.reviewId == newItem.reviewId
    }

    override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
        return oldItem == newItem
    }
}
