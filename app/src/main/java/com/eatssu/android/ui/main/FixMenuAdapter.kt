package com.eatssu.android.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.databinding.ItemSectionBinding
import com.eatssu.android.ui.main.menu.FixedMenuAdapter

class FixMenuAdapter(
    private val fixedMenuList: ArrayList<FixedSection>
) : RecyclerView.Adapter<FixMenuAdapter.MyViewHolder>() {

    class MyViewHolder(
        private val binding: ItemSectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(sectionModel: Section) {
            binding.tvCafeteria.text = sectionModel.cafeteria.toString()
//            binding.rvMenu.apply {
//                setHasFixedSize(true)
//                layoutManager = LinearLayoutManager(binding.root.context)
//                adapter = MenuAdapter(sectionModel.menuList)
//                addItemDecoration(
//                    DividerItemDecoration(binding.root.context,
//                        DividerItemDecoration.VERTICAL)
//                )
//            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemSectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        fixedMenuList?.get(position)?.let { sectionModel ->
//            holder.bind(sectionModel)
        }

//        fixedModelList?.get(position)?.let { fixedSectionModel ->
//            holder.bind(fixedSectionModel)
//        }

    }

    override fun getItemCount(): Int = fixedMenuList?.size?: 0

}