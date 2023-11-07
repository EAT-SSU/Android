package com.eatssu.android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.data.model.response.ChangeMenuInfoListDto
import com.eatssu.android.databinding.ItemMenuPickBinding

class MenuPickAdapter(
    private val menuList: ChangeMenuInfoListDto,
) : RecyclerView.Adapter<MenuPickAdapter.ViewHolder>() {

    private val checkedItems: ArrayList<Pair<String, Long>> = ArrayList()

    inner class ViewHolder(private val binding: ItemMenuPickBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val checkBox: CheckBox = binding.checkBox

        fun bind(position: Int) {
            binding.tvMenuName.text = menuList.menuInfoList[position].name
            checkBox.isChecked = checkedItems.contains(getItem(position))
            checkBox.setOnClickListener { onCheckBoxClick(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMenuPickBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return menuList.menuInfoList?.size ?: 0
    }

    fun getItem(position: Int): Pair<String, Long> {
        val menuName = menuList.menuInfoList[position].name
        val menuId = menuList.menuInfoList[position].menuId
        return Pair(menuName, menuId)
    }

    private fun onCheckBoxClick(position: Int) {
        val item = getItem(position)
        if (checkedItems.contains(item)) {
            checkedItems.remove(item)
        } else {
            checkedItems.add(item)
        }
    }

    fun sendItem(): ArrayList<Pair<String, Long>> {
        return checkedItems
    }
}