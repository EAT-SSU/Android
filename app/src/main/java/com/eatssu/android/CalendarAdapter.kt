package com.eatssu.android

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.databinding.ItemCalendarListBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class CalendarAdapter(private val cList: List<CalendarData>) :
    RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    class CalendarViewHolder(private val binding: ItemCalendarListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: CalendarData) {
            binding.date.text = item.cl_date
            binding.day.text = item.cl_day

            var today = binding.date.text

            // 오늘 날짜
            val now = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd").withLocale(Locale.forLanguageTag("ko")))
            // 오늘 날짜와 캘린더의 오늘 날짜가 같을 경우 background_blue 적용하기
            if (today == now) {
                binding.weekCardview.setBackgroundResource(R.drawable.selector_background_blue)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding =
            ItemCalendarListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(cList[position])
    }

    override fun getItemCount(): Int {
        return cList.size
    }
}
