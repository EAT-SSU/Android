package com.eatssu.android.presentation.cafeteria.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.databinding.ItemMenuBinding
import com.eatssu.android.domain.model.Menu
import com.eatssu.android.presentation.cafeteria.review.list.ReviewActivity
import com.eatssu.common.EventLogger
import com.eatssu.common.enums.Restaurant
import timber.log.Timber


class MenuSubAdapter(
    private val dataList: List<Menu>,
    private val restaurant: Restaurant,
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
            val menuType = restaurant.menuType.toString()
            Timber.d("SubMenuAdapter - ${restaurant.menuType}메뉴${dataList[position].name}")

            ReviewActivity.start(
                holder.itemView.context,
                ReviewActivity.Args(
                    menuType = menuType,
                    itemId = dataList[position].id,
                    itemName = dataList[position].name
                )
            )
            EventLogger.clickMenu(restaurant)
        }
    }

    override fun getItemCount(): Int = dataList.size
}
