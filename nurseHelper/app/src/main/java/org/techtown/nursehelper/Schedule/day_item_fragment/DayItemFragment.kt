package org.techtown.nursehelper.Schedule.day_item_fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.techtown.nursehelper.MainActivity
import org.techtown.nursehelper.R

import org.techtown.nursehelper.calendarviewpager.Day
import org.techtown.nursehelper.databinding.FragmentDayItemBinding
import org.techtown.nursehelper.userSchedule
import java.text.SimpleDateFormat

import java.util.*


class DayItemFragment(var day: Day) : Fragment() {
    lateinit var binding : FragmentDayItemBinding
    lateinit var mainActivity : MainActivity
    val dayItemFormat = SimpleDateFormat("MM/dd HH:mm")
    // pagerAdapter 에서 생성후 초기화시 넘겨받음
    var pagerAdapterReflesh : (()->Unit)? = null

    var parseCal : Calendar

    var dayItemAdapterUpdate : ((Day)->Unit)? = null            //var dayItemUpdate : ((List<userItem>)->Unit)? = null
    var monthItemUpdate : ((MainActivity)->Unit)? = null
    init{
        parseCal = Calendar.getInstance()
        //monthItemUpdate = calFragment.monthItemUpdate
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,): View? {
        binding = FragmentDayItemBinding.inflate(LayoutInflater.from(mainActivity))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("day","dayViewCreated")
        var dayUserItems = mainActivity.searchData(day.calendar.time)
        var Adapter = setAdapter(dayUserItems)
        //전체삭제 -test
        binding.dayItemTxt.setOnClickListener {
            for(dayUserItem in dayUserItems)
                mainActivity.deleteUser(dayUserItem.idCode)
            dayItemAdapterUpdate?.invoke(day)
            pagerAdapterReflesh?.invoke()
            //Adapter.notifyDataSetChanged()
            Log.d("tst","Delete db")
        }

    }

    fun setAdapter(users : List<userSchedule>):DayItemAdapter{
        Log.d("day","daySetAdapter")


        //스와이프 설정값 행성
        val swipeHelperCallback = SwipeHelperCallback().apply {
            setClamp(200f)
        }
        val itemTouchHelper = ItemTouchHelper(swipeHelperCallback)
        //설정값 리클라이어뷰에 바인딩
        itemTouchHelper.attachToRecyclerView(binding.dayItemRcycleView)
        var Adapter = object : DayItemAdapter(mainActivity){

            override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
                super.onAttachedToRecyclerView(recyclerView)
                Users = users
            }

            override fun onBindViewHolder(holder: dayItemHolder, position: Int) {
                //Log.d("day","dayTime ${Users[position].startTime}")
                setBgColor(holder,Users[position].color)
                holder.binding.run{
                    //innerLayout.setBackgroundColor(Users[position].color)
                    textName.text =Users[position].name
                    textAddr.text=Users[position].addr
                    textStart.text= dayItemFormat.format(Users[position].startTime)
                    textEnd.text=   dayItemFormat.format(Users[position].endTime)
                    sid = users[position].idCode
                }


                holder.binding.apply {
                    //스와이프뷰 설정
                    swipeView.setOnClickListener {
                        var dayItemDetailFragment = DayItemDetailFragment(Users[position])
                        mainActivity.supportFragmentManager.beginTransaction().run{
                            replace(R.id.popUpContainer,dayItemDetailFragment)
                            addToBackStack("day_item_detail")
                            commit()
                        }
                    }


                    //아이템뷰 삭제
                    delBtn.setOnClickListener {
                            mainActivity.deleteUser(sid)
                        dayItemAdapterUpdate?.invoke(day)
                    }
                }


            }

            override fun getItemCount() : Int = Users.size


        }
        binding.dayItemRcycleView.apply{
            adapter = Adapter
            layoutManager = LinearLayoutManager(mainActivity)
            setOnTouchListener { _, _ ->
                swipeHelperCallback.removePreviousClamp(this)
                false
            }
        }

        //어답터에 쓸 데이터를 가져오는 함수
        dayItemAdapterUpdate = Adapter.UserUpdate
        return Adapter
    }
    fun setBgColor(binding : DayItemAdapter.dayItemHolder, color: Int) {
        var shape = ContextCompat.getDrawable(mainActivity, R.drawable.user_item_bg)
        shape?.setTint(color)
        binding.binding.swipeView.setBackground(shape)
    }

}