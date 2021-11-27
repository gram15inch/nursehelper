package org.techtown.nursehelper.calendarviewpager

import android.content.Context
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.util.*

open class CalendarViewPager(context: Context, attrs: AttributeSet? = null) : ViewPager(context, attrs) {

    var onCalendarChangeListener: ((Calendar) -> Unit)? = null

    override fun setAdapter(adapter: PagerAdapter?) {
        super.setAdapter(adapter)
        if (adapter is CalendarPagerAdapter) {
            this.clearOnPageChangeListeners()

            //어답터 초기화
            //adapter.onDayClickListener = this.onDayClickListener
            adapter.onDayLongClickListener = this.onDayLongClickListener

            //페이져 초기화
            setCurrentItem(CalendarPagerAdapter.MAX_VALUE / 2, false) //현재위치 설정
            this.addOnPageChangeListener(pageChangeListener)

        }
    }

    //뷰페이저 크기조절
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // initialized child views
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // support wrap_content
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        if (mode == MeasureSpec.AT_MOST) {
            val view = focusedChild ?: getChildAt(0)
            view.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
            val newHeight = view.measuredHeight

            val exactlyHeightMeasureSpec = MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.EXACTLY)
            super.onMeasure(widthMeasureSpec, exactlyHeightMeasureSpec)
        }
    }

    //페이지 변경감지
    private val pageChangeListener = object : OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            val calendar = (adapter as? CalendarPagerAdapter)?.getCalendar(position) ?: return
            onCalendarChangeListener?.invoke(calendar)
            Log.d("order","onPageSelected")
        }
        //안쓰는 함수들
        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    }


    //안쓰는 객체들
    var onDayClickListener: ((Day) -> Unit)? = null
        set(value) {
            field = value
            (adapter as? CalendarPagerAdapter)?.onDayClickListener = field
        }
    var onDayLongClickListener: ((Day) -> Boolean)? = null
        set(value) {
            field = value
            (adapter as? CalendarPagerAdapter)?.onDayLongClickListener = field
        }

    //안쓰는 함수들
    fun getCurrentCalendar(): Calendar? = (adapter as? CalendarPagerAdapter)?.getCalendar(currentItem)
    fun moveItemBy(position: Int, smoothScroll: Boolean = true) {
        if (position != 0) {
            setCurrentItem(currentItem + position, smoothScroll)
        }
    }

}
