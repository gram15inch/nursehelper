package org.techtown.nursehelper.Schedule.day_item_fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.techtown.nursehelper.MainActivity
import org.techtown.nursehelper.calendarviewpager.Day
import org.techtown.nursehelper.databinding.DayItemSwipeBinding
import org.techtown.nursehelper.databinding.UserItemBinding
import org.techtown.nursehelper.userSchedule
import kotlin.properties.Delegates
import android.R
import android.util.Log
import android.view.View


abstract class DayItemAdapter(var mainActivity: MainActivity):RecyclerView.Adapter<DayItemAdapter.dayItemHolder>(){

    var Users: List<userSchedule> by Delegates.observable(emptyList()) { _, old, new ->
        DayItemDiff(old, new).calculateDiff().dispatchUpdatesTo(this)
    }
    /**adapter 안의 users를 업데이트하는함수를 객체화 시킴*/
    /*var UserUpdate : ((List<userItem>) -> Unit)? = object :(List<userItem>)->Unit{
        override fun invoke(users: List<userItem>) {
            Users = users
        }
    }*/
    var UserUpdate : ((Day) -> Unit)? = object :(Day)->Unit{
                override fun invoke(day: Day) {
                    Users = mainActivity.searchData(day.calendar.time)
        }
    }
        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            super.onAttachedToRecyclerView(recyclerView)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ): dayItemHolder {
            val binding = DayItemSwipeBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false)
        return dayItemHolder(binding)
    }

    override fun getItemCount() : Int = Users.size
    inner class dayItemHolder(var binding:DayItemSwipeBinding):RecyclerView.ViewHolder(binding.root){}




}