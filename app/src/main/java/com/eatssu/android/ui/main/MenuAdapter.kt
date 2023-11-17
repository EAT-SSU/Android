package com.eatssu.android.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.databinding.ItemSectionBinding

class MenuAdapter(
    private val totalMenuList: ArrayList<Section>

) : RecyclerView.Adapter<MenuAdapter.MyViewHolder>() {

    class MyViewHolder(
        private val binding: ItemSectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(sectionModel: Section) {
            binding.tvCafeteria.text = sectionModel.cafeteria.displayName
            binding.rvMenu.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(binding.root.context)
                adapter = sectionModel.menuList?.let {
                    SubMenuAdapter(it,sectionModel.menuType)
                }
                addItemDecoration(
                    DividerItemDecoration(binding.root.context,
                        DividerItemDecoration.VERTICAL)
                )
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemSectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        totalMenuList[position].let { sectionModel ->
            holder.bind(sectionModel)
        }
    }

    override fun getItemCount(): Int = totalMenuList.size

}