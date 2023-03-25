package com.eatssu.android.ui.review

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.data.model.review
import com.eatssu.android.databinding.ItemReviewBinding


class ListAdapter(private val dataList: ArrayList<review>): RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.tvReviewItemId.text=dataList[position].id
            binding.tvReviewItemDate.text=dataList[position].date
            binding.tvReviewItemComment.text=dataList[position].content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding =
            ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
        holder.itemView.setOnClickListener {}
    }

    override fun getItemCount(): Int = dataList.size
}