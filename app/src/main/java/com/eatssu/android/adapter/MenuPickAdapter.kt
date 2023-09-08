package com.eatssu.android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.databinding.ItemMenuPickBinding

class MenuPickAdapter(
    private val menuNameArray: ArrayList<String>?,
    private val menuIdArray: LongArray?
) : RecyclerView.Adapter<MenuPickAdapter.ViewHolder>() {

    private val checkedItems: ArrayList<Pair<String, Long>> = ArrayList()

    inner class ViewHolder(private val binding: ItemMenuPickBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val checkBox: CheckBox = binding.checkBox

        fun bind(position: Int) {
            binding.tvMenuName.text = menuNameArray?.get(position)
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
        return menuNameArray?.size ?: 0
    }

    fun getItem(position: Int): Pair<String, Long> {
        val menuName = menuNameArray?.get(position) ?: ""
        val menuId = menuIdArray?.get(position) ?: 0L
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