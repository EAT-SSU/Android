package com.eatssu.android.ui.review

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.databinding.ItemReviewBinding
/*
class ReviewAdapter(private val dataList: List<GetTrainerListResponse.Result.Dto>): RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.tvName.text = dataList[position].name
            binding.tvRating.text= dataList[position].grade.toString()
            binding.tvUniv.text=dataList[position].school
            binding.tvPr.text=dataList[position].contents
            binding.tvMoney.text= dataList[position].cost.toString()

            if (dataList[position].profile == "trainerProfile"){
                binding.ivProfile.setImageResource(R.drawable.ic_profile)
            }
            else if (dataList[position].profile != "trainerProfile"|| dataList[position].profile != null) {
                Glide.with(itemView)
                    .load("${dataList[position].profile}")
                    .into(binding.ivProfile)
                binding.ivProfile.clipToOutline = true
                Log.d("post", dataList[position].profile)
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
//        holder.itemView.setOnClickListener {
//            val intent = Intent(holder.itemView.context, ProfileActivity::class.java)
//            intent.putExtra(
//                "trainerIdx", dataList[position].id)
//            startActivity(holder.itemView.context, intent, null)
//        }
    }

    override fun getItemCount(): Int = dataList.size
}*/