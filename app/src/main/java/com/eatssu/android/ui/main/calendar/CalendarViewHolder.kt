package com.eatssu.android.ui.main.calendar

import android.os.Build
import com.eatssu.android.R
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.databinding.ItemCalendarListBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalendarViewHolder internal constructor(
    binding: ItemCalendarListBinding,
    itemView: View,
    onItemListener: CalendarAdapter.OnItemListener,
    days: ArrayList<LocalDate>
) :
    RecyclerView.ViewHolder(binding.root), View.OnClickListener {
    private val days: ArrayList<LocalDate>
    val parentView: View
    val dayText: TextView
    val dayOfMonth: TextView
    private val onItemListener: CalendarAdapter.OnItemListener

    init {
        parentView = binding.weekCardview
        dayText = binding.day
        dayOfMonth = binding.date
        this.onItemListener = onItemListener
        binding.root.setOnClickListener(this)
        this.days = days
    }

    override fun onClick(view: View) {
        onItemListener.onItemClick(adapterPosition, days[adapterPosition])
    }
}