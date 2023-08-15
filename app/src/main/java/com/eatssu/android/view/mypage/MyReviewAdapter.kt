package com.eatssu.android.view.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eatssu.android.data.model.response.GetMyReviewResponseDto
import com.eatssu.android.databinding.ItemMyReviewBinding


//@POST("mypage/myreview") //내가 쓴 리뷰 모아보기
//fun getMyReviews(): Call<GetMyReviewResponse>

class MyReviewAdapter(private val dataList: List<GetMyReviewResponseDto.Data>) :
    RecyclerView.Adapter<MyReviewAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemMyReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
//            binding.tvReviewItemId.text = dataList[position].writerId.toString()
            binding.tvReviewItemComment.text = dataList[position].content.toString()
            binding.tvReviewItemDate.text = dataList[position].writeDate.toString()
            binding.tvReviewMainGrade.text=dataList[position].mainGrade.toString()
            binding.tvReviewTasteGrade.text=dataList[position].tasteGrade.toString()
            binding.tvReviewAmountGrade.text=dataList[position].amountGrade.toString()
            binding.tvReviewItemId.text=dataList[position].menuName.toString()
            // Assuming imgUrlList is a List<String>
            val imgUrlList: List<String>? =
                dataList?.get(position)?.imgUrlList// The list of image URLs

            // Get the ImageView reference from your layout
            val imageView: ImageView =
                binding.ivReviewPhoto// Replace R.id.imageView with the ID of your ImageView
            // Check if the imgUrlList is not empty before loading the image
            if (dataList?.get(position)?.imgUrlList.isNullOrEmpty()) {
                imageView.visibility = View.GONE
            } else {
                val imageUrl =
                    imgUrlList?.get(0) // Assuming you want to load the first image from the list

                Glide.with(itemView)
                    .load(imageUrl)
                    .into(imageView)
                imageView.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding =
            ItemMyReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = dataList.size
}