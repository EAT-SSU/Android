package com.eatssu.android.ui.mypage.myreview

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eatssu.android.data.dto.response.MyReviewResponse
import com.eatssu.android.databinding.ItemReviewBinding
import com.eatssu.android.ui.review.delete.MyReviewDialogActivity
import com.eatssu.android.util.MySharedPreferences


class MyReviewAdapter(private val dataList: List<MyReviewResponse.DataList>) :
    RecyclerView.Adapter<MyReviewAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.tvReviewItemComment.text = dataList[position].content
            binding.tvReviewItemDate.text = dataList[position].writeDate
            binding.tvMenuName.text = dataList[position].menuName
            binding.tvTotalRating.text = dataList[position].mainRating.toString()
            binding.tvTasteRating.text = dataList[position].tasteRating.toString()
            binding.tvAmountRating.text = dataList[position].amountRating.toString()
            binding.tvWriterNickname.text = MySharedPreferences.getUserName(binding.root.context)

            val imageView: ImageView = binding.ivReviewPhoto

            if (dataList[position].imgUrlList.isEmpty()) {
                imageView.visibility = View.GONE
            } else {
                Log.d("qwer", "사진 있다")
                Glide.with(itemView)
                    .load(dataList[position].imgUrlList[0])
                    .into(imageView)
                imageView.visibility = View.VISIBLE
                if (dataList[position].imgUrlList[0] == "") {
                    binding.ivReviewPhoto.visibility = View.GONE

                }
                if (dataList[position].imgUrlList[0] == null) {
                    binding.ivReviewPhoto.visibility = View.GONE
                }
            }

            binding.btnDetail.setOnClickListener {
                val intent = Intent(binding.btnDetail.context, MyReviewDialogActivity::class.java)
                intent.putExtra("reviewId", dataList[position].reviewId.toLong())
                intent.putExtra("menu", dataList[position].menuName)
                intent.putExtra("comment", dataList[position].content)
                intent.putExtra("amountRating", dataList[position].amountRating)
                intent.putExtra("tasteRating", dataList[position].tasteRating)
                intent.putExtra("mainRating", dataList[position].amountRating)
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