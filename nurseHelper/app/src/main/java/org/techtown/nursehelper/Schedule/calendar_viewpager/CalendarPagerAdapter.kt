package org.techtown.nursehelper.calendarviewpager
import android.content.Context
import android.util.Log
import androidx.viewpager.widget.PagerAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.apache.commons.lang3.time.DateUtils
import org.techtown.nursehelper.MainActivity
import org.techtown.nursehelper.R
import org.techtown.nursehelper.Schedule.ScheduleFragment
import org.techtown.nursehelper.Schedule.day_item_fragment.DayItemFragment
import java.util.*

/**
 * @param isStartAtMonday 달력 표기를 월요일 시작할지 어떨지.false로 하면 일요일이 시작됩니다. */
 open class CalendarPagerAdapter(val context: Context, var scheduleFragment: ScheduleFragment, base: Calendar = Calendar.getInstance(), val startingAt: DayOfWeek = DayOfWeek.Sunday) : PagerAdapter() {

    //초기 달력 설정 [pagerAdapter 캘린더(파라미터) 현재월 이하 초기화 yyyy-00-00 00:00:00]
    private val baseCalendar: Calendar = DateUtils.truncate(base, Calendar.DAY_OF_MONTH).apply {
        set(Calendar.DAY_OF_MONTH, 1)

        // 시작요일 설정
        firstDayOfWeek = Calendar.SUNDAY + startingAt.getDifference()  // 초기값은 JP/US 등에서는 1(SUNDAY)이지만 일부 지역(UK)은 2(MONDAY)이므로 인수로 지정된 요일로 변경해 둔다.

        // 첫주의 최소일수 설정
        minimalDaysInFirstWeek = 1 // JP/US 등은 '1'이지만 일부 지역(UK)의 경우는 '4'
    }

    private var viewContainer: ViewGroup? = null
    var monthItemUpdate : ((MainActivity)->Unit)? = null

    /** 선택한 날 */
    var selectedDay: Date? = null
        set(value) {
            field = value
            //notifyCalendarItemChanged()
        }

    //어답터 재시작
    var pagerAdapterReflesh : (()->Unit) = object :()->Unit{
        override fun invoke() {
            this@CalendarPagerAdapter.notifyDataSetChanged()
            //this@CalendarPagerAdapter.notifyCalendarItemChanged()
            //this@CalendarPagerAdapter.notifyCalendarChanged()
        }
    }

    //셀 클릭 이벤트 객체
    var onDayClickListener =  object : (Day)->Unit{
        override fun invoke(day: Day) {
            Log.d("tst","dayClick: ${day.calendar.time}")

            //Fragment 생성
            val dayItemFragment = DayItemFragment(day).apply { //데이터는 날짜만 넘김
                //Fragment 초기화
                pagerAdapterReflesh = this@CalendarPagerAdapter.pagerAdapterReflesh
            }

            (context as MainActivity).supportFragmentManager.beginTransaction().run{
                replace(R.id.popUpContainer,dayItemFragment)
                addToBackStack("day_item")
                commit()
            }
            //view 보이기
            scheduleFragment.popUpShow(1)
        }

    }

    //안쓰는 함수
    var onDayLongClickListener: ((Day) -> Boolean)? = null

    //최대 페이지수
    companion object {
        /** 최대 페이지 : 500 */
        const val MAX_VALUE = 500
    }

    //최대 페이지수 설정
    override fun getCount(): Int = MAX_VALUE

    /** 캘린더 View를 생성합니다 */
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        Log.d("postion","${position} / ${getCalendar(position).time}")

        //리클라이어뷰 직접생성(xml바인딩 x)
        val recyclerView = RecyclerView(context).apply {

                 //리클라이어뷰 초기 설정
                layoutManager = GridLayoutManager(context, 7)
                isNestedScrollingEnabled = false
                hasFixedSize()

                //그리드(셀) 어답터 재정의후 생성 (>>핵심<<)
                adapter = object : CalendarCellAdapter(context, getCalendar(position), startingAt, selectedDay) {
                    override fun onBindViewHolder(holder: domHolder, day: Day) {

                        //셀 홀더 클릭시 이벤트
                        holder.itemView.setOnClickListener {
                            Log.d("tst","pageInstantiate")

                            //페이저 어답터에게 클릭한 날짜 전송
                            this@CalendarPagerAdapter.selectedDay = day.calendar.time

                            //셀 클릭 이벤트 객체 실행(이곳에서 만듬)
                            onDayClickListener.invoke(day)
                            //notifyCalendarItemChanged()

                            //임시수정
                           /* for(user in (context as MainActivity).searchData(day.calendar.time))
                                Log.d("day","dayItem: ${user.startTime}")*/
                            /*for(dayUserItem in (context as MainActivity).searchData(day.calendar.time))
                                (context as MainActivity).deleteUser(dayUserItem)
                            monthItemUpdate?.invoke(context as MainActivity)?: Log.d("miu","update null")
                        */
                        }
                    }
                }.apply {

                //셀의 내부 monthUser데이터 업데이트 함수 페이저 어답터에 전달
                this@CalendarPagerAdapter.monthItemUpdate =  this.updateMonthUserItems
                Log.d("update","PagerAdapter")
                }
        }

        //생성된 page에 리클라이어뷰 주입
        container.addView(recyclerView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        //리클라이어뷰가 주입된 페이지를 멤버 변수에 따로 저장
        viewContainer = container

        return recyclerView
    }

    // position 위치에 맞는 날짜 반환 (전체 페이지수 고려)
    fun getCalendar(position: Int): Calendar {
        return (baseCalendar.clone() as Calendar).apply {
            add(Calendar.MONTH, position- MAX_VALUE / 2)
            //Log.d("tst","getCal : ${this.get(Calendar.YEAR)}-${this.get(Calendar.MONTH)}/${position- MAX_VALUE / 2}")
        }
    }

    //달력 설정, 셀홀더 생성시 사용
    enum class DayOfWeek {
        Sunday,
        Monday,
        Tuesday,
        Wednesday,
        Thursday,
        Friday,
        Saturday;

        /** 기준(일요일)과의 일수 차이 */
        fun getDifference(): Int {
            return when (this) {
                Sunday -> 0
                Monday -> 1
                Tuesday -> 2
                Wednesday -> 3
                Thursday -> 4
                Friday -> 5
                Saturday -> 6
            }
        }

        /** 첫 주가 적어지고 있다*/
        fun isLessFirstWeek(calendar: Calendar): Boolean {
            return calendar.get(Calendar.DAY_OF_WEEK) < getDifference() + 1
        }

        /** 마지막 주가 많아지고 있다 */
        fun isMoreLastWeek(calendar: Calendar): Boolean {
            val end = DateUtils.truncate(calendar, Calendar.DAY_OF_MONTH)
            end.add(Calendar.MONTH, 1)
            end.add(Calendar.DATE, -1)
            return end.get(Calendar.DAY_OF_WEEK) < getDifference() + 1
        }
    }

    //사용하지 않는 함수1 (어답터 필수생성 함수)
    open fun onCreateView(parent: ViewGroup, viewType: Int): View {
                return TextView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 96)
        }
    }
    open fun onBindView(view: View, day: Day) { /** day가 이번달이면 view에 현재일을 넣음 */
        val textView = view as TextView
        textView.text = when (day.state) {
            DayState.ThisMonth -> day.calendar.get(Calendar.DAY_OF_MONTH).toString()
            else -> "@"
        }
    }

    //사용하지 않는 함수2
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        /** 화면외의 캘린더 View를 지웁니다. */
        container.removeView(`object` as View?)
    }
    override fun isViewFromObject(view: View, `object`: Any): Boolean = (view == `object`)
    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    //사용하지 않는 함수3
    fun notifyCalendarChanged() {  /** Pager Adapter 내에 있는 캘린더를 다시 그립니다 */
    val views = viewContainer ?: return
        (0 until views.childCount).forEach { i ->
            ((views.getChildAt(i) as? RecyclerView)?.adapter as? CalendarCellAdapter)?.run {
                notifyItemRangeChanged(0, items.size)
            }
        }
    }
    private fun notifyCalendarItemChanged() {
        val views = viewContainer ?: return
        (0 until views.childCount).forEach { i ->
            ((views.getChildAt(i) as? RecyclerView)?.adapter as? CalendarCellAdapter)?.updateItems(selectedDay)
        }
    }

}
