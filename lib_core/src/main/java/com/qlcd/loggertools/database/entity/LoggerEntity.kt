package com.qlcd.loggertools.database.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "logger_table")
data class LoggerEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var time: Long? = null,
    var level: String = "",
    var content: String? = "",
) : Parcelable