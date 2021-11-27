package org.techtown.nursehelper.Schedule.calendar_viewpager

import androidx.recyclerview.widget.DiffUtil
import org.techtown.nursehelper.userSchedule

class MonthItemDiff(private val old: List<List<userSchedule>>, private val new: List<List<userSchedule>>) : DiffUtil.Callback()  {
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        //Log.d("diff","content : ${old[oldItemPosition][0].startTime.time} == ${new[newItemPosition][0].startTime.time} ${old[oldItemPosition] == new[newItemPosition]}")
        return old[oldItemPosition] == new[newItemPosition]
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
       /* var count =0
        for (oldUser in old[oldItemPosition])
            for(newUser in new[newItemPosition])
                if(oldUser == newUser){
                    count++
                }
        return count ==old.size*/
        return old[oldItemPosition] == new[newItemPosition]
    }

    override fun getOldListSize(): Int = old.size

    override fun getNewListSize(): Int = new.size

    fun calculateDiff(): DiffUtil.DiffResult {
        return DiffUtil.calculateDiff(this, false)
    }
}