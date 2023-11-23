package com.eatssu.android.ui.main.menu

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.data.entity.Section
import com.eatssu.android.databinding.ItemSectionBinding
import com.eatssu.android.ui.info.InfoActivity

class MenuAdapter(
    private val totalMenuList: ArrayList<Section>

) : RecyclerView.Adapter<MenuAdapter.MyViewHolder>() {

    class MyViewHolder(
        private val binding: ItemSectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(sectionModel: Section) {

            binding.btnInfo.setOnClickListener {
                val intent = Intent(itemView.context, InfoActivity::class.java)
                intent.putExtra("restaurantType",  sectionModel.cafeteria)
                ContextCompat.startActivity(itemView.context, intent, null)
            }

            binding.tvCafeteria.text = sectionModel.cafeteria.displayName
            binding.rvMenu.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(binding.root.context)
                adapter = sectionModel.menuList?.let {
                    MenuSubAdapter(it,sectionModel.menuType)
                }
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