package org.techtown.nursehelper.Schedule.day_item_fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.techtown.nursehelper.MainActivity
import org.techtown.nursehelper.R

import org.techtown.nursehelper.calendarviewpager.Day
import org.techtown.nursehelper.databinding.FragmentDayItemBinding
import org.techtown.nursehelper.userSchedule

import java.util.*


class DayItemFragment(var day: Day) : Fragment() {
    lateinit var binding : FragmentDayItemBinding
    lateinit var mainActivity : MainActivity

    // pagerAdapter 에서 생성후 초기화시 넘겨받음
    var pagerAdapterReflesh : (()->Unit)? = null

    var parseCal : Calendar

    var dayItemUpdate : ((Day)->Unit)? = null            //var dayItemUpdate : ((List<userItem>)->Unit)? = null
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
        //전체삭제
        binding.textView2.setOnClickListener {
            for(dayUserItem in dayUserItems)
                //mainActivity.deleteUser(dayUserItem)
            dayItemUpdate?.invoke(day)
            //pagerAdapterReflesh?.invoke()
            //Adapter.notifyDataSetChanged()

        }
    }

    fun setAdapter(users : List<userSchedule>):DayItemAdapter{
        Log.d("day","daySetAdapter")

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
                    parseCal.time = Users[position].startTime
                    textStart.text= "${parseCal.get(Calendar.MONTH)+1}-${parseCal.get(Calendar.DAY_OF_MONTH)} ${parseCal.get(Calendar.HOUR)}:${parseCal.get(Calendar.MINUTE)}"
                    parseCal.time = Users[position].endTime
                    textEnd.text=   "${parseCal.get(Calendar.MONTH)+1}-${parseCal.get(Calendar.DAY_OF_MONTH)} ${parseCal.get(Calendar.HOUR)}:${parseCal.get(Calendar.MINUTE)}"
                }

                holder.itemView.setOnClickListener {
                    var dayItemDetailFragment = DayItemDetailFragment(Users[position])
                    mainActivity.supportFragmentManager.beginTransaction().run{
                        replace(R.id.popUpContainer,dayItemDetailFragment)
                        addToBackStack("day_item_detail")
                        commit()
                    }
                }
            }

            override fun getItemCount() : Int = Users.size


        }
        binding.dayItemRcycleView.adapter = Adapter
        binding.dayItemRcycleView.layoutManager = LinearLayoutManager(mainActivity)
        dayItemUpdate = Adapter.UserUpdate

        return Adapter
    }
    fun setBgColor(binding : DayItemAdapter.dayItemHolder, color: Int) {
        var shape = ContextCompat.getDrawable(mainActivity, R.drawable.user_item_bg)
        shape?.setTint(color)
        binding.binding.innerLayout.setBackground(shape)
    }

}