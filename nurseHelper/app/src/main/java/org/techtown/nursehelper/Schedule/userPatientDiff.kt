package org.techtown.nursehelper.Schedule

import androidx.recyclerview.widget.DiffUtil

class userPatientDiff (private val old: List<userPatient>, private val new: List<userPatient>) : DiffUtil.Callback()  {
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