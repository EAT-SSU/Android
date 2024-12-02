package com.eatssu.android.presentation.mypage.myreview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.eatssu.android.data.MySharedPreferences
import com.eatssu.android.databinding.ItemReviewBinding
import com.eatssu.android.domain.model.Review
import timber.log.Timber


class MyReviewAdapter(private val dataList: List<Review>) :
    RecyclerView.Adapter<MyReviewAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.tvReviewItemComment.text = dataList[position].content
            binding.tvReviewItemDate.text = dataList[position].writeDate
            binding.tvMenuName.text = dataList[position].menu

            binding.rbRate.rating = dataList[position].mainGrade.toFloat()
            binding.tvWriterNickname.text = MySharedPreferences.getUserName(binding.root.context)

            val imageView: ImageView = binding.ivReviewPhoto

            if (dataList[position].imgUrl?.isEmpty() == true) {
                imageView.visibility = View.GONE
            } else {
                Timber.d("사진 있다")
                Glide.with(itemView)
                    .load(dataList[position].imgUrl?.get(0))
                    .into(imageView)
                imageView.visibility = View.VISIBLE

                if (dataList[position].imgUrl?.get(0) == "" || dataList[position].imgUrl?.get(0) == null) {
                    binding.ivReviewPhoto.visibility = View.GONE
                }
            }

            binding.btnDetail.setOnClickListener {
                val intent = Intent(binding.btnDetail.context, MyReviewDialogActivity::class.java)
                intent.putExtra("reviewId", dataList[position].reviewId)
                intent.putExtra("menu", dataList[position].menu)

                intent.putExtra("content", dataList[position].content)

                intent.putExtra("mainGrade", dataList[position].mainGrade)
                intent.putExtra("amountGrade", dataList[position].amountGrade)
                intent.putExtra("tasteGrade", dataList[position].tasteGrade)

                Timber.d(
                    "리뷰 상세 정보 - ID: %d, 메뉴: %s, 내용: %s",
                    dataList[position].reviewId,
                    dataList[position].menu,
                    dataList[position].content
                )

                ContextCompat.startActivity(binding.btnDetail.context, intent, null)
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
