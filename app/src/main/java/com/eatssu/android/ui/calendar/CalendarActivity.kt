package com.eatssu.android.ui.calendar
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.Window
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.eatssu.android.databinding.ActivityCalendarBinding
import com.eatssu.android.ui.main.MainActivity
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.format.DateFormatTitleFormatter
import com.prolificinteractive.materialcalendarview.format.TitleFormatter
import java.util.*


class CalendarActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityCalendarBinding
    lateinit var calendar: MaterialCalendarView

    val today = CalendarDay.today()

    lateinit var selectedDate: CalendarDay
    lateinit var changedate : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCalendarBinding.inflate(layoutInflater)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(viewBinding.root)

        calendar = viewBinding.calendarView
        calendar.selectedDate = today
        val disabledDates = hashSetOf<CalendarDay>()
        disabledDates.add(CalendarDay.from(2022, 7, 12))

        viewBinding.calendarView.apply {
            // 휴무일 지정을 위한 Decorator 설정
            addDecorator(DayDisableDecorator(disabledDates, today))
            // 요일을 지정하귀 위해 {"월", "화", ..., "일"} 배열을 추가한다.
            setWeekDayLabels(arrayOf("월", "화", "수", "목", "금", "토", "일"))
            // 달력 상단에 `월 년` 포맷을 수정하기 위해 TitleFormatter 설정
            setTitleFormatter(MyTitleFormatter())
            addDecorator(TodayDecorator())

        }
        DateFormatTitleFormatter()
        calendar.setOnDateChangedListener(object: OnDateSelectedListener{
            override fun onDateSelected(
                widget: MaterialCalendarView,
                date: CalendarDay,
                selected: Boolean
            ) {
                selectedDate = calendar.selectedDate
                changedate = selectedDate.toString()
                Log.d("changedate", changedate)
                //인텐트 선언 및 정의
                //인텐트 선언 및 정의

                val intent = Intent(this@CalendarActivity, MainActivity::class.java)
                intent.putExtra("intentdate", changedate)
                startActivity(intent)

                /*val bundle = Bundle()
                bundle.putString("changedate", changedate)
                Log.d("changedate", changedate)
                Log.d("bundle", bundle.toString())
                val calendarfragment = CalendarFragment()
                calendarfragment.arguments = bundle*/

                finish()

            }
        })
    }

    inner class MyTitleFormatter : TitleFormatter {
        override fun format(day: CalendarDay?): CharSequence {
            /*val simpleDateFormat =
                SimpleDateFormat("yyyy . MM", Locale.US) //"February 2016" format

            return simpleDateFormat.format(Calendar.getInstance().getTime())*/
                return "${day!!.year} . ${day.month + 1}"

        }

    }

    inner class DayDisableDecorator(
        private var dates: HashSet<CalendarDay>,
        private var today: CalendarDay
    ) : DayViewDecorator {

        override fun shouldDecorate(day: CalendarDay): Boolean {
            // 휴무일 || 이전 날짜
            return dates.contains(day) || day.isBefore(today)
        }

        override fun decorate(view: DayViewFacade?) {
            view?.let { it.setDaysDisabled(false) }
        }
    }

    inner class TodayDecorator: DayViewDecorator {
        private var date = CalendarDay.today()

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return day?.equals(date)!!
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(StyleSpan(Typeface.BOLD))
            view?.addSpan(RelativeSizeSpan(1.4f))
            view?.addSpan(ForegroundColorSpan(Color.parseColor("#DF5758")))
        }
    }


}
