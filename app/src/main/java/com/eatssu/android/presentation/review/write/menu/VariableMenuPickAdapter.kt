package com.eatssu.android.presentation.review.write.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.databinding.ItemMenuPickBinding
import com.eatssu.android.domain.model.MenuMini

class VariableMenuPickAdapter(private val menuList: List<MenuMini>?) :
    RecyclerView.Adapter<VariableMenuPickAdapter.ViewHolder>() {

    private val checkedItems: ArrayList<Pair<String, Long>> = ArrayList()

    inner class ViewHolder(private val binding: ItemMenuPickBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val menuItem = menuList?.get(position)
            with(binding) {
                tvMenuName.text = menuItem?.name
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

    override fun getItemCount(): Int = menuList?.size ?: 0

    private fun getItem(position: Int): Pair<String?, Long?> {
        val menuItem = menuList?.get(position)
        return Pair(menuItem?.name, menuItem?.id)
    }

    private fun onCheckBoxClick(position: Int) {
        val item = getItem(position)
        if (checkedItems.contains(item)) {
            checkedItems.remove(item)
        } else {
            checkedItems.add(item as Pair<String, Long>)
        }
    }

    fun sendCheckedItem(): ArrayList<Pair<String, Long>> = checkedItems
}
