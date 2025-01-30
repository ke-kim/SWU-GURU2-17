package com.example.swu_guru2_17

data class AlarmData(
    val hour: Int,
    val minute: Int,
    val name: String,
    var isEnabled: Boolean,
    val repeatDays: Set<Int>? = null,
    val sound: String? = null
)

