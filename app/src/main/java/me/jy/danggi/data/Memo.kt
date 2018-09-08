package me.jy.danggi.data

import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey
import java.io.Serializable
import java.util.*

open class Memo(
        @PrimaryKey
        var id: String = UUID.randomUUID().toString(),

        var content: String = "",

        var writeDate: Date = Date()

) : RealmObject(), Serializable