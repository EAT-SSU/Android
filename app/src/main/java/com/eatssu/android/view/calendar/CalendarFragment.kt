package com.eatssu.android.view.calendar

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.eatssu.android.R
import com.eatssu.android.databinding.FragmentCalendarBinding
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.*

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    lateinit var calendarAdapter: CalendarAdapter
    private var calendarList = ArrayList<CalendarData>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var week_day: Array<String> = resources.getStringArray(R.array.calendar_day)

        calendarAdapter = CalendarAdapter(calendarList)

        calendarList.apply {
            val dateFormat =
                DateTimeFormatter.ofPattern("dd").withLocale(Locale.forLanguageTag("ko"))
            val monthFormat = DateTimeFormatter.ofPattern("yyyy . MM . dd")
                .withLocale(Locale.forLanguageTag("ko"))

            val localDate = LocalDateTime.now().format(monthFormat)
            val newDate = arguments?.getString("Date")

            if (newDate != null) {
                Log.d("Date", newDate)
            }

            val preSunday: LocalDateTime = LocalDateTime.now().with(
                TemporalAdjusters.previous(
                    DayOfWeek.SUNDAY
                )
            )
            Log.d("preSunday", preSunday.toString())
            for (i in 0..6) {
                Log.d("날짜만", week_day[i])

                calendarList.apply {
                    add(
                        CalendarData(
                            preSunday.plusDays(i.toLong()).format(dateFormat),
                            week_day[i]
                        )
                    )
                }
                Log.d("저번 주 일요일 기준으로 시작!", preSunday.plusDays(i.toLong()).format(dateFormat))
            }
            binding.weekRecycler.adapter = calendarAdapter
            binding.weekRecycler.layoutManager = GridLayoutManager(context, 7)

        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("fragment", "onResumed")
    }
}