package com.eatssu.android.ui.mypage.myreview

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eatssu.android.data.dto.response.GetMyReviewResponse
import com.eatssu.android.databinding.ItemReviewBinding
import com.eatssu.android.ui.review.etc.MyReviewDialogActivity
import com.eatssu.android.util.MySharedPreferences


class MyReviewAdapter(private val dataList: List<GetMyReviewResponse.Data>) :
    RecyclerView.Adapter<MyReviewAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.tvReviewItemComment.text = dataList[position].content
            binding.tvReviewItemDate.text = dataList[position].writeDate
            binding.tvMenuName.text = dataList[position].menuName
            binding.tvTotalRating.text = dataList[position].mainGrade.toString()
            binding.tvTasteRating.text = dataList[position].tasteGrade.toString()
            binding.tvAmountRating.text = dataList[position].amountGrade.toString()
            binding.tvWriterNickname.text = MySharedPreferences.getUserName(binding.root.context)

            val imgUrlList: List<String> =
                dataList[position].imgUrlList// The list of image URLs

            // Get the ImageView reference from your layout
            val imageView: ImageView =
                binding.ivReviewPhoto// Replace R.id.imageView with the ID of your ImageView
            // Check if the imgUrlList is not empty before loading the image
            if (dataList[position].imgUrlList.isEmpty()) {
                imageView.visibility = View.GONE
            } else {
                val imageUrl =
                    imgUrlList[0] // Assuming you want to load the first image from the list

                Glide.with(itemView)
                    .load(imageUrl)
                    .into(imageView)
                imageView.visibility = View.VISIBLE
            }

            binding.btnDetail.setOnClickListener {
                val intent = Intent(binding.btnDetail.context, MyReviewDialogActivity::class.java)
                intent.putExtra("reviewId", dataList[position].reviewId.toLong())
                intent.putExtra("menu", dataList[position].menuName)
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
        holder.bind(position)
    }

    override fun getItemCount(): Int = dataList.size
}