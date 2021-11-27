package org.techtown.nursehelper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.techtown.nursehelper.databinding.UserItemBinding
import java.text.SimpleDateFormat
import kotlin.properties.Delegates

open class userScheduleAdapter(val mainActivity: MainActivity, val pusers :List<userSchedule>) : RecyclerView.Adapter<userScheduleAdapter.scheduleItemHolder>(){
    val timeFormat = SimpleDateFormat("HH:mm")

    var Users: List<userSchedule> by Delegates.observable(emptyList()) { _, old, new ->
        userScheduleDiff(old, new).calculateDiff().dispatchUpdatesTo(this)
    }
    init {
        Users = pusers
    }
    /**adapter 안의 users를 업데이트하는함수를 객체화 시킴*/
    /*  var UserUpdate : ((List<userItem>) -> Unit)? = object :(List<userItem>)->Unit{
          override fun invoke(users: List<userItem>) {
              Users = users
          }
      }*/
    var UserUpdate : ((String) -> Unit)? = object :(String)->Unit{
        override fun invoke(day: String) {
            //get users
            //Users = mainActivity.searchData(day.calendar.time)
        }
    }

    override fun onBindViewHolder(holder: scheduleItemHolder, position: Int) {
        holder.binding.apply {
            textName.text = Users[position].name
            textAddr.text = Users[position].addr
            textStart.text = timeFormat.format(Users[position].startTime)
            textEnd.text = timeFormat.format(Users[position].endTime)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ): scheduleItemHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(mainActivity))
        return scheduleItemHolder(binding)
    }

    override fun getItemCount() : Int = Users.size

   open inner class scheduleItemHolder(var binding:UserItemBinding): RecyclerView.ViewHolder(binding.root){}



}