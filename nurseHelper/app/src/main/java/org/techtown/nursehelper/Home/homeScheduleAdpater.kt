package org.techtown.nursehelper.Home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.techtown.nursehelper.*
import org.techtown.nursehelper.Login.loginFragment
import org.techtown.nursehelper.Schedule.day_item_fragment.DayItemDetailFragment
import org.techtown.nursehelper.databinding.UserItemHomeBinding
import java.text.SimpleDateFormat
import kotlin.properties.Delegates

class homeScheduleAdpater(val mainActivity: MainActivity, pusers :List<userSchedule>): RecyclerView.Adapter<homeScheduleAdpater.homeScheduleItemHolder>() {
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

    override fun onBindViewHolder(holder: homeScheduleItemHolder, position: Int) {

        //값 바인딩
        holder.binding.apply {
            setBgColor(holder,Users[position].color)
            textName.text = Users[position].name
            textAddr.text = Users[position].addr
            textStart.text = timeFormat.format(Users[position].startTime)
            textEnd.text = timeFormat.format(Users[position].endTime)

        }

        //쓰기로 이동
        holder.itemView.setOnClickListener {
            Log.d("tst","click")
            var writingFragment = WritingFragment(2).apply { userSchedule=this@homeScheduleAdpater.Users[position] }
            //var writingFragment = StockFragment()
            mainActivity.supportFragmentManager.beginTransaction().run{
                add(R.id.SubFrame,writingFragment)
                addToBackStack("write")
                commit()
            }
            if(mainActivity.binding.SubFrame.visibility != View.VISIBLE)
                mainActivity.binding.SubFrame.visibility = View.VISIBLE
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ): homeScheduleItemHolder {
        val binding = UserItemHomeBinding.inflate(LayoutInflater.from(mainActivity))
        return homeScheduleItemHolder(binding)
    }

    override fun getItemCount() : Int = Users.size

    inner class homeScheduleItemHolder(var binding: UserItemHomeBinding): RecyclerView.ViewHolder(binding.root){}

    fun setBgColor(holder : homeScheduleItemHolder, color: Int) {
        var shape = ContextCompat.getDrawable(mainActivity, R.drawable.user_item_bg)
        shape?.setTint(color)
        holder.binding.innerLayout.setBackground(shape)
    }
}