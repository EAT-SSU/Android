package com.eatssu.android.ui.main.calendar

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.R
import com.eatssu.android.databinding.ItemCalendarListBinding
import com.eatssu.android.util.CalendarUtil
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale


internal class CalendarAdapter(
    private val days: ArrayList<LocalDate>,
    private val onItemListener: OnItemListener
) :
    RecyclerView.Adapter<CalendarViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding =
            ItemCalendarListBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_list, parent, false)
            .apply {
                layoutParams?.height = parent.height
            }
            .run {
                CalendarViewHolder(binding, this, onItemListener, days)
            }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val date = days[position]
        holder.dayOfMonth.text = date.dayOfMonth.toString()
        holder.dayText.text =
            date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN).toString()

        /**
         * iOS의 FSCalendar를 Custom으로 만들었습니다.
         * 1. 선택한 날짜는 primary color로 select합니다.
         * 2. 오늘 날짜 != 선택한 날짜 일 경우에는, 오늘 날짜의 text 색상을 primary color 표기하여, 오늘 날짜를 강조합니다.
         */

        if (date == CalendarUtil.selectedDate) { //셀렉트 된 날짜
            holder.dayOfMonth.setBackgroundResource(R.drawable.selector_background_blue)
            holder.dayOfMonth.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.selector_calendar_colortext
                )
            )
        } else if (date == LocalDate.now() && date != CalendarUtil.selectedDate) {
            //오늘 날짜가 선택 되지 않았을 때, 오늘 날 text 색 지정
            holder.dayOfMonth.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.primary
                )
            )
        } else { //다른 날짜들
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