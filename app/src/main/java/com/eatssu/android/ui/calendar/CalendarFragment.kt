package com.eatssu.android.ui.calendar

import android.content.Intent
import android.content.Intent.getIntent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
            //binding.textYearMonth.text = localDate


            var preSunday: LocalDateTime = LocalDateTime.now().with(
                TemporalAdjusters.previous(
                    DayOfWeek.SUNDAY
                )
            )

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
        }
        binding.weekRecycler.adapter = calendarAdapter
        binding.weekRecycler.layoutManager = GridLayoutManager(context, 7)

        /*binding.textYearMonth.setOnClickListener {
            val intent = Intent(context, CalendarActivity::class.java);
            startActivity(intent);
        }*/



    }

    override fun onResume() {
        val intent = Intent()
        //text 키값으로 데이터를 받는다. String을 받아야 하므로 getStringExtra()를 사용함
        /*val selectdate = this.arguments?.getString("changedate")
        Log.d("selectdate", selectdate.toString())*/
        val intentdate = intent.getStringExtra("intentdate")
        Log.d("intentdate", intentdate.toString())

        /*binding.btnCalendarLeft.setOnClickListener {
            binding.textYearMonth.text = null
            binding.textYearMonth.text = intentdate
            *//*binding.btnCalendarRight.setOnClickListener{
            binding.textYearMonth.text = null
            binding.textYearMonth.text = LocalDateTime.now().plusDays(1).format(monthFormat).toString()
        }
        }*/
        super.onResume()
    }
}
