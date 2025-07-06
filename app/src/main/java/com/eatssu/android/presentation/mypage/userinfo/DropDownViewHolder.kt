package com.eatssu.android.presentation.mypage.userinfo

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.R

class DropdownViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val textView: TextView = itemView.findViewById(R.id.tv_item)

    fun bind(text: String, onClick: (String) -> Unit) {
        textView.text = text
        itemView.setOnClickListener { onClick(text) }
    }
}
