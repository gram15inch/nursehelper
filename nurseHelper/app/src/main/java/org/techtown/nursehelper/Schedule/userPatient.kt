package org.techtown.nursehelper.Schedule

import org.techtown.nursehelper.userDocument
import java.util.*

data class userPatient(
                       val pCode :Int,
                       val name:String,
                       val sex :String,
                       val birth : Date,
                       val addr :String){

    override fun equals(other: Any?): Boolean {
        return this.pCode == (other as userPatient).pCode
    }
}

