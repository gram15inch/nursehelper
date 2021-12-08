package org.techtown.nursehelper.Document

import androidx.recyclerview.widget.DiffUtil
import org.techtown.nursehelper.userDocument

class userDocumentDiff(private val old: List<userDocument>, private val new: List<userDocument>) : DiffUtil.Callback()  {
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        //Log.d("diff","content")
        return  old[oldItemPosition].memo == new[newItemPosition].memo
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