package com.eatssu.android.ui.main.menu

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.data.entity.Menu
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.databinding.ItemMenuBinding
import com.eatssu.android.ui.review.list.ReviewActivity


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
                if (dataList[position].rate == 0.0) {
                    "-"
                } else {
                    String.format("%.1f", dataList[position].rate)
                }        }
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

            if(menuType==MenuType.FIX){
                Log.d("SubMenuAdapter","고정메뉴${dataList[position].name}")
                intent.putExtra("itemId", dataList[position].id)
                intent.putExtra("itemName", dataList[position].name)
                intent.putExtra("menuType", MenuType.FIX.toString())
            }

            if(menuType==MenuType.CHANGE){
                Log.d("SubMenuAdapter","변동메뉴${dataList[position].name}")
                intent.putExtra("itemId", dataList[position].id)
                intent.putExtra("itemName", dataList[position].name)
                intent.putExtra("menuType", MenuType.CHANGE.toString())
            }
            ContextCompat.startActivity(holder.itemView.context, intent, null)

        }


    }

    override fun getItemCount(): Int = dataList.size
}
