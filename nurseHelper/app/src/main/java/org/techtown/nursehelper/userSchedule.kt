package org.techtown.nursehelper

import java.util.*

data class userSchedule(
    var idCode :Int,
    var name:String,
    var addr:String,
    var startTime: Date,
    var endTime:Date,
    val sex :String,
    val birth : Date,
    var color :Int){

    override fun equals(other: Any?): Boolean {
        return this.idCode == (other as userSchedule).idCode
    }
}
