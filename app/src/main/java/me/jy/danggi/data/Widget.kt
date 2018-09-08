package me.jy.danggi.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Widget(
        @PrimaryKey
        var id: String = UUID.randomUUID().toString(),

        var widgetId: Int = 0,

        var memo: Memo? = null

) : RealmObject()