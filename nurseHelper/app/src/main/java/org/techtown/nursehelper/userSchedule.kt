package org.techtown.nursehelper

import java.util.*

data class userSchedule(val idCode :Int,
                        val name:String,
                        val addr:String,
                        val startTime: Date,
                        val endTime:Date,
                        val sex :String,
                        val birth : Date,
                        val color :Int){

    override fun equals(other: Any?): Boolean {
        return this.idCode == (other as userSchedule).idCode
    }
}
