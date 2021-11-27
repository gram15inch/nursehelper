package org.techtown.nursehelper

import java.util.*

data class userDocument (val dCode :Int,
                         val pCode :Int,
                         val name:String,
                         val addr:String,
                         val date: Date,
                         val sex :String,
                         val birth : Date,
                         val memo :String){

    override fun equals(other: Any?): Boolean {
        return this.dCode == (other as userDocument).dCode
    }
}
