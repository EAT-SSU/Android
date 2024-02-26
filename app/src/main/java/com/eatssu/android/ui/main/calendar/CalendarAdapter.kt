package com.eatssu.android.ui.main.calendar

import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.R
import com.eatssu.android.databinding.ItemCalendarListBinding
import com.eatssu.android.util.CalendarUtils
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList


internal class CalendarAdapter(
    private val days: ArrayList<LocalDate>,
    private val onItemListener: OnItemListener
) :
    RecyclerView.Adapter<CalendarViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding =
            ItemCalendarListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_calendar_list, parent, false)
        val layoutParams = view.layoutParams
        layoutParams.height = parent.height
        layoutParams.width = Gravity.RIGHT
        return CalendarViewHolder(binding, view, onItemListener, days)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val date = days[position]
        holder.dayOfMonth.text = date.dayOfMonth.toString()
        holder.dayText.text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN).toString()

        if (date == CalendarUtils.selectedDate) {
            holder.parentView.setBackgroundResource(R.drawable.selector_background_blue)
            holder.dayOfMonth.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.selector_calendar_colortext))
            holder.dayText.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.selector_calendar_colortext))
        }
        else {
            holder.parentView.setBackgroundResource(R.drawable.ic_selector_background_white)
        }
    }


    override fun getItemCount(): Int {
        return days.size
    }

    interface OnItemListener {
        fun onItemClick(position: Int, date: LocalDate)
    }
}