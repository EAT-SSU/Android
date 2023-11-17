package com.eatssu.android.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.data.model.response.GetTodayMealResponseDto
import com.eatssu.android.databinding.ItemMenuBinding

class MenuSubAdapter(
    private val menuList: GetTodayMealResponseDto
) : RecyclerView.Adapter<MenuSubAdapter.MyViewHolder>() {

    class MyViewHolder(
        private val binding: ItemMenuBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(menu: Menu) {
            with(binding) {
                tvMenu.text = menu.name
                tvPrice.text = "${menu.price}"
                tvRate.text = menu.rate.toString()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemMenuBinding.inflate(
                LayoutInflater.from(parent.context),
            parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        holder.bind(menuList[position])
    }

    override fun getItemCount() = menuList.size

}