package com.eatssu.android.ui.calendar

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.eatssu.android.R
import com.eatssu.android.databinding.ItemCalendarListBinding
import com.eatssu.android.ui.main.MainActivity
import com.eatssu.android.ui.review.ReviewListActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

    class CalendarAdapter(private val cList: List<CalendarData>) :
        RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {
        private var allViewHolders : List<CalendarViewHolder> = mutableListOf()

        inner class CalendarViewHolder(private val binding: ItemCalendarListBinding) :
            RecyclerView.ViewHolder(binding.root) {

            private var selectedDate: String? = null

            @RequiresApi(Build.VERSION_CODES.O)
            fun bind(item: CalendarData) {
                binding.date.text = item.cl_date
                binding.day.text = item.cl_day

                var today = binding.date.text
                var day = binding.day.text

                // 오늘 날짜
                val now = LocalDate.now()
                    .format(
                        DateTimeFormatter.ofPattern("dd").withLocale(Locale.forLanguageTag("ko"))
                    )

                // 오늘 날짜와 캘린더의 오늘 날짜가 같을 경우 background_blue 적용하기
                if (now.equals(today)) {
                    binding.weekCardview.setBackgroundResource(R.drawable.selector_background_blue)
                }

                binding.root.setOnClickListener {
                    // Update the selectedDate to the clicked date
                    selectedDate = today as String?
                    // Update the background of the clicked date
                    for(holder in allViewHolders){
                        holder.binding.weekCardview.setBackgroundResource(R.drawable.ic_selector_background_white)
                        Log.d("searchfor", holder.toString())
                    }
                    selectedDate?.let { it1 -> Log.d("rootdata", it1) }
                    val intent = Intent(binding.root.context, MainActivity::class.java)
                    intent.putExtra(
                        "menuId", selectedDate
                    )
                    ContextCompat.startActivity(binding.root.context, intent, null)
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

            /*//서버 연결
            holder.itemView.setOnClickListener {
                val intent = Intent(holder.itemView.context, MainActivity::class.java)
                intent.putExtra(
                    "menuId", cList[position].cl_date
                )
            }*/
        }

        override fun getItemCount(): Int {
            return cList.size
        }
    }