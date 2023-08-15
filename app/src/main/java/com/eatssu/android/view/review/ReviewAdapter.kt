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
import com.eatssu.android.databinding.ItemReviewBinding
import com.eatssu.android.view.review.MyReviewDialogActivity
import com.eatssu.android.view.review.OthersReviewDialogActivity

class ReviewAdapter(private val dataList: GetReviewListResponse) :
    RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val dataList = dataList.dataList
            binding.tvReviewItemId.text = dataList?.get(position)?.writerNickname.toString()
            binding.tvReviewItemComment.text = dataList?.get(position)?.content
            binding.tvReviewItemDate.text = dataList?.get(position)?.writeDate
            binding.tvMenuName.text = dataList?.get(position)?.menu

            binding.tvTotalRating.text = dataList?.get(position)?.mainGrade?.toFloat().toString()
            binding.tvTasteRating.text = dataList?.get(position)?.tasteGrade?.toFloat().toString()
            binding.tvAmountRating.text = dataList?.get(position)?.amountGrade?.toFloat().toString()


// ... other code in your class ...

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

            binding.btnDetail.setOnClickListener() {
                if(dataList?.get(position)?.isWriter == false) {
                    val intent = Intent(binding.btnDetail.context, OthersReviewDialogActivity::class.java)
                    ContextCompat.startActivity(binding.btnDetail.context, intent, null)
                }
                else if (dataList?.get(position)?.isWriter == true) {
                    val intent = Intent(binding.btnDetail.context, MyReviewDialogActivity::class.java)
                    ContextCompat.startActivity(binding.btnDetail.context, intent, null)
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

    override fun getItemCount(): Int = dataList.dataList?.size ?: 0
}