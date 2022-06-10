package com.qlcd.loggertools.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "logger_table")
data class LoggerEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var time: Long? = null,
    var level: String = "",
    var fileName: String = "",
    var fucName: String = "",
    var lineNum: String = "",
    var content: String? = "",
) {

}