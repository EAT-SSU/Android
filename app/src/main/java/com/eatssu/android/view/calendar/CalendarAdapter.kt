package com.eatssu.android.view.calendar

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.R
import com.eatssu.android.databinding.ItemCalendarListBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class CalendarAdapter(private val cList: List<CalendarData>) :
    RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {
    private var allViewHolders : List<CalendarViewHolder> = mutableListOf()
    private var mListener: OnItemClickListener? = null

    inner class CalendarViewHolder(val binding: ItemCalendarListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var selectedDate: String? = null
        lateinit var today : String
        lateinit var day : String

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: CalendarData) {
            binding.date.text = item.cl_date
            binding.day.text = item.cl_day

            today = binding.date.text as String
            day = binding.day.text as String

            // 오늘 날짜
            val now = LocalDate.now()
                .format(
                    DateTimeFormatter.ofPattern("dd").withLocale(Locale.forLanguageTag("ko"))
                )

            // 오늘 날짜와 캘린더의 오늘 날짜가 같을 경우 background_blue 적용하기
            if (now.equals(today)) {
                binding.date.isSelected = true;
                binding.day.isSelected = true;
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

        allViewHolders = allViewHolders.plus(holder)

        holder.itemView.setOnClickListener { v ->
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                mListener?.onItemClick(v, cList.get(position))
            }
        }

    }

    override fun getItemCount(): Int {
        return cList.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }

    fun returnViewHolderList(): List<CalendarViewHolder> {
        return allViewHolders
    }
}

    interface OnItemClickListener {
        fun onItemClick(v: View?, data: CalendarData)
    }
