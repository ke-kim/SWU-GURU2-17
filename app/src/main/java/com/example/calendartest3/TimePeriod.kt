package com.example.swu_guru2_17

import java.text.SimpleDateFormat
import java.util.*

data class TimePeriod(val start: String, val end: String) {
    fun getDurationInMinutes(): Int {
        return try {
            val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val startTime = format.parse(start)?.time ?: 0
            val endTime = format.parse(end)?.time ?: 0
            ((endTime - startTime) / 1000 / 60).toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }
    fun getDurationInHours(): Double {
        return getDurationInMinutes() / 60.0
    }
}