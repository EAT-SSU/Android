package com.eatssu.android.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.data.model.response.GetFixedMenuResponse
import com.eatssu.android.databinding.ItemSnackBinding
import com.eatssu.android.ui.review.ReviewListActivity

class SnackAdapter(private val dataList: List<GetFixedMenuResponse.FixMenuInfoList>) :
    RecyclerView.Adapter<SnackAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemSnackBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.tvMenu.text = dataList[position].name
            binding.tvPrice.text = dataList[position].price.toString()
            binding.tvRate.text = String.format("%.1f", dataList[position].mainGrade)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSnackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)

        //서버 연결
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ReviewListActivity::class.java)
            intent.putExtra(
                "menuId", dataList[position].menuId
            )
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    override fun getItemCount(): Int = dataList.size
}