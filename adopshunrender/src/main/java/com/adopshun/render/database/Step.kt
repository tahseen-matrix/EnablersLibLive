package com.adopshun.render.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
@Entity(tableName = "step_data")
data class Step(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "step_no") val stepNo: Int?
)
