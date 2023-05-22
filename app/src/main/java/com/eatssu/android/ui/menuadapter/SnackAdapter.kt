package com.eatssu.android.ui.menuadapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.R
import com.eatssu.android.data.model.Snack
import com.eatssu.android.data.model.response.GetMenuInfoListResponse
import com.eatssu.android.databinding.ItemMenuBinding
import com.eatssu.android.databinding.ItemSnackBinding
import com.eatssu.android.ui.review.ReviewListActivity

class SnackAdapter(private val dataList: List<GetMenuInfoListResponse.MenuInfo>):
    RecyclerView.Adapter<SnackAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemSnackBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.tvMenu.text = dataList[position].name
            binding.tvPrice.text = dataList[position].price.toString()
            binding.tvRate.text = dataList[position].grade.toString()
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