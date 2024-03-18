package com.eatssu.android.ui.main.menu

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.data.model.Menu
import com.eatssu.android.databinding.ItemMenuBinding
import com.eatssu.android.ui.review.show.ReviewActivity


class MenuSubAdapter(
    private val dataList: List<Menu>,
    private val menuType: MenuType
) :
    RecyclerView.Adapter<MenuSubAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.tvMenu.text = dataList[position].name
            binding.tvPrice.text = dataList[position].price.toString()
            binding.tvRate.text =
                when (dataList[position].rate.toString()) {
                    "0.0" -> "-"
                    "NaN" -> "-"
                    "null" -> "-"
                    else -> String.format("%.1f", dataList[position].rate)
                }
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
            val intent = Intent(holder.itemView.context, ReviewActivity::class.java)

            when (menuType) {
                MenuType.FIXED -> {
                    Log.d("SubMenuAdapter", "고정메뉴${dataList[position].name}")
                    intent.putExtra("itemId", dataList[position].id)
                    intent.putExtra("itemName", dataList[position].name)
                    intent.putExtra("menuType", MenuType.FIXED.toString())
                }

                MenuType.VARIABLE -> {
                    Log.d("SubMenuAdapter", "변동메뉴${dataList[position].name}")
                    intent.putExtra("itemId", dataList[position].id)
                    intent.putExtra("itemName", dataList[position].name)
                    intent.putExtra("menuType", MenuType.VARIABLE.toString())
                }
            }
            ContextCompat.startActivity(holder.itemView.context, intent, null)

        }


    }

    override fun getItemCount(): Int = dataList.size
}
