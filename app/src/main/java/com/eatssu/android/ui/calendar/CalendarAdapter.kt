package com.eatssu.android.ui.calendar

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.R
import com.eatssu.android.databinding.ItemCalendarListBinding
import com.eatssu.android.ui.main.MainActivity
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
            /*
            binding.root.setOnClickListener {
                // Update the selectedDate to the clicked date
                selectedDate = today as String?
                // Update the background of the clicked date
                for(holder in allViewHolders){
                    holder.binding.weekCardview.setBackgroundResource(com.eatssu.android.R.drawable.ic_selector_background_white)
                    Log.d("searchfor", holder.toString())
                }
                selectedDate?.let { it1 -> Log.d("rootdata", it1) }
                val bundle : Bundle = Bundle()
                bundle.putString("calendardata", selectedDate)

                val intent = Intent(binding.root.context, MainActivity::class.java)
                intent.putExtra("calendardata", selectedDate)
                ContextCompat.startActivity(binding.root.context, intent, null)

                *//*val lunchFragment = LunchFragment()
                lunchFragment.arguments = bundle*//*

                *//*val caledarFragment = CalendarFragment()

                lateinit var dataPassListener : onDataPassListener

                dataPassListener = caledarFragment.context as onDataPassListener
                dataPassListener.onDataPass(selectedDate)*//*

                binding.weekCardview.setBackgroundResource(com.eatssu.android.R.drawable.selector_background_blue)
            }*/
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
