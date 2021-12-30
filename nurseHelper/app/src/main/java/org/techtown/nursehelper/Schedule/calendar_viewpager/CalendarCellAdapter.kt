package org.techtown.nursehelper.calendarviewpager

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.apache.commons.lang3.time.DateUtils
import org.techtown.nursehelper.MainActivity
import org.techtown.nursehelper.Schedule.calendar_viewpager.MonthItemDiff
import org.techtown.nursehelper.databinding.DOMItemBinding
import org.techtown.nursehelper.userSchedule
import java.util.*
import kotlin.properties.Delegates

abstract class CalendarCellAdapter : RecyclerView.Adapter<CalendarCellAdapter.domHolder> {
    private val context: Context
    val calendar: Calendar
    private val weekOfMonth: Int
    private val startDate: Calendar //달력의 제일 첫일(전 달 포함)

    //셀의 날짜 리스트 (하루에 날짜 하나)
    var items: List<Day> by Delegates.observable(emptyList()) { _, old, new ->
        MonthCalendarDiff(old, new).calculateDiff().dispatchUpdatesTo(this)
    }

    //셀의 일정 리스트의 리스트 (하루에 여러 일정)
    var monthUserItems: List<List<userSchedule>> by Delegates.observable(emptyList()) { _, old, new ->
        MonthItemDiff(old, new).calculateDiff().dispatchUpdatesTo(this)
        Log.d("diff","DayGridItem")
    }

    constructor(context: Context, date: Date, preselectedDay: Date? = null) : this(context, Calendar.getInstance().apply { time = date }, CalendarPagerAdapter.DayOfWeek.Sunday, preselectedDay)
    constructor(context: Context, calendar: Calendar, startingAt: CalendarPagerAdapter.DayOfWeek, preselectedDay: Date? = null) : super() {
        this.context = context
        this.calendar = calendar // adapter의 positon(월)날짜

        //월 이하 초기화
        val start = DateUtils.truncate(calendar, Calendar.DAY_OF_MONTH)

        //달의 첫날 설정
        if (start.get(Calendar.DAY_OF_WEEK) != (startingAt.getDifference() + 1)) {
            // 표시를 시작하는 날짜의 요일이 기준이 되는 요일과 다른 요일이면 전월 마지막 주 날짜를 산출
            // 같은 요일의 경우 산출해 버리면 1주일 어긋남
            start.set(Calendar.DAY_OF_MONTH, if (startingAt.isLessFirstWeek(calendar)) - startingAt.getDifference() else 0)
            start.add(Calendar.DAY_OF_MONTH, -start.get(Calendar.DAY_OF_WEEK) + 1 + startingAt.getDifference())
        }
        startDate = start
        Log.d("tststart",start.time.toString())
        //생성해야할 주의 개수
        this.weekOfMonth = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH) + (if (startingAt.isLessFirstWeek(calendar)) 1 else 0) - (if (startingAt.isMoreLastWeek(calendar)) 1 else 0)

        //items초기화
        updateItems(preselectedDay)

        //items(days)로 monthUserItems(userSchedules)를 초기화
        updateMonthUserItems?.invoke(context as MainActivity)
        Log.d("update","DayCellAdapter")
    }

    //items(days) 초기화 날짜 가져오기)
    fun updateItems(selectedDate: Date? = null) {
        val now = Calendar.getInstance()

        //날짜 리스트 초기화 (첫날 ~ 마지막위치[셀개수])
        this.items = (0..itemCount).map {

            //달력에 제일 첫날짜로 캘린더 객체  생성
            val cal = Calendar.getInstance().apply { time = startDate.time }

            //첫날 부터 날짜의 상대위치만큼 이동
            cal.add(Calendar.DAY_OF_MONTH, it)

            //
            val thisTime = calendar.get(Calendar.YEAR) * 12 + calendar.get(Calendar.MONTH) //Log.d("tst","thistime = $thisTime =${calendar.get(Calendar.YEAR)}* 12 + ${calendar.get(Calendar.MONTH)}")

            //이동후 현재날짜
            val compareTime = cal.get(Calendar.YEAR) * 12 + cal.get(Calendar.MONTH) //Log.d("tst","compareTime = $compareTime =${cal.get(Calendar.YEAR)}* 12 + ${cal.get(Calendar.MONTH)}- ${cal.get(Calendar.DAY_OF_MONTH)}")

            //현재날짜에 포지션에 따른 달정보 추가
            val state = when (thisTime.compareTo(compareTime)) {
                -1 -> DayState.NextMonth
                0 -> DayState.ThisMonth
                1 -> DayState.PreviousMonth
                else -> throw IllegalStateException()
            }
            val isSelected = when (selectedDate) {
                null -> false
                else -> {
                    //#1
                    //Log.d("tst","update : ${selectedDate}")
                    DateUtils.isSameDay(cal.time, selectedDate)
                }
            }
            val isToday = DateUtils.isSameDay(cal, now)

            Day(cal, state, isToday, isSelected)
        }


    }

    //monthUserItems 초기화 (db데이터가져오기)
    var updateMonthUserItems : ((MainActivity) -> Unit)? = object : (MainActivity)->Unit{
        override fun invoke(mainActivity: MainActivity) {
            this@CalendarCellAdapter.monthUserItems = (0..itemCount).map{
                mainActivity.searchData(items[it].calendar.time)
            }
        }
    }

    //홀더에 일정 바인딩
    inner class domHolder(var binding : DOMItemBinding):RecyclerView.ViewHolder(binding.root){

        fun onBindHolder(day: Day,users :List<userSchedule>) {

            var dayUserItems = (context as MainActivity).sortScheData(users)
            binding.textDay.text = when (day.state) {
                DayState.ThisMonth -> day.calendar.get(Calendar.DAY_OF_MONTH).toString()
                else -> day.calendar.get(Calendar.DAY_OF_MONTH).toString()
            }
            when(day.state){
                DayState.NextMonth,
                DayState.PreviousMonth ->
                    binding.textDay.setTextColor(Color.parseColor("#A4A4A4"))
            }
            var userCount = 0
            for(user in dayUserItems){
                when(userCount++){
                    0->{binding.one.setBackgroundColor(user.color)
                        binding.one.visibility = View.VISIBLE }
                    1->{binding.two.setBackgroundColor(user.color)
                        binding.two.visibility = View.VISIBLE}
                    2->{binding.three.setBackgroundColor(user.color)
                        binding.three.visibility = View.VISIBLE}
                    3->{binding.four.setBackgroundColor(user.color)
                        binding.four.visibility = View.VISIBLE}
                    else->{binding.other.text = "+${dayUserItems.size - 4}"
                        binding.other.visibility = View.VISIBLE
                        break}
                }

            }
        }
    }

    ///어답터 필수 생성함수
    abstract fun onBindViewHolder(holder: domHolder, day: Day)
    override fun onBindViewHolder(holder: domHolder, position: Int) {
        onBindViewHolder(holder, items[holder.layoutPosition])
        holder.onBindHolder(items[holder.layoutPosition],monthUserItems[holder.layoutPosition])

    }

    override fun getItemCount(): Int = 7 * weekOfMonth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): domHolder {
        val binding = DOMItemBinding.inflate(LayoutInflater.from(parent.context))
        return domHolder(binding)
    }


}


data class Day(
        var calendar: Calendar,
        var state: DayState,
        var isToday: Boolean,
        var isSelected: Boolean
)

enum class DayState {
    PreviousMonth,
    ThisMonth,
    NextMonth
}
