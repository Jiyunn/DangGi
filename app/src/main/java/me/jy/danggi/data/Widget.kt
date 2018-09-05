package me.jy.danggi.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Widget : RealmObject(){

    @PrimaryKey
    var id:Int=0
    var widgetId:Int=0

    var memo: Memo?=null
}