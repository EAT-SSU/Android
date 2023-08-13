package com.eatssu.android.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.data.model.response.GetFixedMenuResponseDto
import com.eatssu.android.databinding.ItemMenuListBinding
import com.eatssu.android.view.review.ReviewListActivity


class FixedAdapter(private val dataList: GetFixedMenuResponseDto, ) :
    RecyclerView.Adapter<FixedAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemMenuListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.tvMenu.text = dataList.fixMenuInfoList[position].name
            binding.tvPrice.text = dataList.fixMenuInfoList[position].price.toString()
            binding.tvRate.text = String.format("%.1f", dataList.fixMenuInfoList[position].mainGrade)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMenuListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)

        //서버 연결
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ReviewListActivity::class.java)
            intent.putExtra(
                "menuId", dataList.fixMenuInfoList[position].menuId
            )
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    override fun getItemCount(): Int = dataList.fixMenuInfoList.size
}
