package me.jy.danggi.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Video : RealmObject() {

    @PrimaryKey
    var id:Int =0

    var thumbnail:ByteArray?=null
    var uri: String=""
    var content:String=""
    var writeDate: Date?=null
}