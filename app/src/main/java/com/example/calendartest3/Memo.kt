package com.example.swu_guru2_17

data class Memo(
    val id: Int,
    val title: String,
    val author: String,
    val publisher: String,
    val memo: String,
    val date: String,
    val imagePath: String,
    val isCompleted: Boolean = false
)