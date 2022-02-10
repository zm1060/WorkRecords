package com.benjaminwan.ocr.ncnn.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Records(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var date: String,
    var about_address: String,
    var detail_address: String,
    var area: String,
    var length: String,
    var status: String,
    )

