package me.jy.danggi.model

import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey
import java.io.Serializable
import java.util.*

open class Memo : RealmObject(), Serializable {

    @PrimaryKey
    var id: Int= 0
    var content: String = ""
    var writeDate: Date? = null

    @LinkingObjects("memo")
    val widgets: RealmResults<Widget>?=null

}