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
import com.eatssu.android.data.model.Review
import com.eatssu.android.databinding.ItemReviewBinding
import com.eatssu.android.ui.review.delete.MyReviewDialogActivity
import com.eatssu.android.util.MySharedPreferences


class MyReviewAdapter(private val dataList: List<Review>) :
    RecyclerView.Adapter<MyReviewAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.tvReviewItemComment.text = dataList[position].content
            binding.tvReviewItemDate.text = dataList[position].writeDate
            binding.tvMenuName.text = dataList[position].menu
            binding.tvTotalRating.text = dataList[position].mainGrade.toString()
            binding.tvTasteRating.text = dataList[position].tasteGrade.toString()
            binding.tvAmountRating.text = dataList[position].amountGrade.toString()
            binding.tvWriterNickname.text = MySharedPreferences.getUserName(binding.root.context)

            val imageView: ImageView = binding.ivReviewPhoto

            if (dataList[position].imgUrl?.isEmpty() == true) {
                imageView.visibility = View.GONE
            } else {
                Log.d("qwer", "사진 있다")
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
                intent.putExtra("comment", dataList[position].content)
                intent.putExtra("amountRating", dataList[position].amountGrade)
                intent.putExtra("tasteRating", dataList[position].tasteGrade)
                intent.putExtra("mainRating", dataList[position].mainGrade)
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