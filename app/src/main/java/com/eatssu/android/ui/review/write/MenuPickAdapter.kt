package com.eatssu.android.ui.review.write

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.data.model.response.ChangeMenuInfoListDto
import com.eatssu.android.databinding.ItemMenuPickBinding

class MenuPickAdapter(private val menuList: ChangeMenuInfoListDto) :
    RecyclerView.Adapter<MenuPickAdapter.ViewHolder>() {

    private val checkedItems: ArrayList<Pair<String, Long>> = ArrayList()

    inner class ViewHolder(private val binding: ItemMenuPickBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val checkBox = binding.checkBox

        fun bind(position: Int) {
            val menuItem = menuList.menuInfoList[position]
            with(binding) {
                tvMenuName.text = menuItem.name
                checkBox.isChecked = checkedItems.contains(getItem(position))
                checkBox.setOnClickListener { onCheckBoxClick(position) }
            }
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

    override fun getItemCount(): Int = menuList.menuInfoList?.size ?: 0

    private fun getItem(position: Int): Pair<String, Long> {
        val menuItem = menuList.menuInfoList[position]
        return Pair(menuItem.name, menuItem.menuId)
    }

    private fun onCheckBoxClick(position: Int) {
        val item = getItem(position)
        if (checkedItems.contains(item)) {
            checkedItems.remove(item)
        } else {
            checkedItems.add(item)
        }
    }

    fun sendItem(): ArrayList<Pair<String, Long>> = checkedItems
}
