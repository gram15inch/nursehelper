package org.techtown.nursehelper

import androidx.recyclerview.widget.DiffUtil

class userScheduleDiff(private val old: List<userSchedule>, private val new: List<userSchedule>) : DiffUtil.Callback()  {
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        //Log.d("diff","content")
        return  old[oldItemPosition] == new[newItemPosition]
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        //Log.d("diff","Item")
        return old[oldItemPosition] == new[newItemPosition]
    }

    override fun getOldListSize(): Int = old.size

    override fun getNewListSize(): Int = new.size

    fun calculateDiff(): DiffUtil.DiffResult {
        return DiffUtil.calculateDiff(this, false)
    }
}