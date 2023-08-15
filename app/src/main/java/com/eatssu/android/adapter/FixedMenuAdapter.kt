package com.eatssu.android.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.data.model.response.GetFixedMenuResponseDto
import com.eatssu.android.databinding.ItemMenuBinding
import com.eatssu.android.view.review.ReviewListActivity
import com.eatssu.android.viewmodel.ReviewListViewModel


class FixedMenuAdapter(
    private val dataList: GetFixedMenuResponseDto
) :
    RecyclerView.Adapter<FixedMenuAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.tvMenu.text = dataList.fixMenuInfoList[position].name
            binding.tvPrice.text = dataList.fixMenuInfoList[position].price.toString()
            binding.tvRate.text =
                String.format("%.1f", dataList.fixMenuInfoList[position].mainGrade)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)

        //intent 사용
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ReviewListActivity::class.java)
            intent.putExtra(
                "itemId", dataList.fixMenuInfoList[position].menuId
            )
            intent.putExtra(
                "menuType", MenuType.FIX.toString()
            )
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }


        //intent 미사용
        /*
        val menuId = dataList.fixMenuInfoList[position].menuId

        holder.itemView.setOnClickListener {
            viewModel.setSelectedItemId(menuId)
            Log.d("post",viewModel.getSelectedItemId().toString())
            viewModel.setSelectedMenuType(MenuType.FIX)
            Log.d("post", "FIX")

            val intent = Intent(holder.itemView.context, ReviewListActivity::class.java)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }*/
    }

    override fun getItemCount(): Int = dataList.fixMenuInfoList.size
}