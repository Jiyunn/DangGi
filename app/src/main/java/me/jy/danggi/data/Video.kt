package me.jy.danggi.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Video(
        @PrimaryKey
        var id: String = UUID.randomUUID().toString(),

        var thumbnail: ByteArray? = null,

        var uri: String = "",

        var content: String = "",

        var writeDate: Date? = Date()

) : RealmObject()