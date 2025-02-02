package com.example.swu_guru2_17

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.swu_guru2_17.AlarmData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AddAlarmActivity : AppCompatActivity() {

    private lateinit var hourPicker: NumberPicker
    private lateinit var minutePicker: NumberPicker
    private lateinit var ampmPicker: NumberPicker
    private lateinit var editTextAlarmName: EditText
    private lateinit var repeatOption: TextView
    private lateinit var alarmSound: TextView
    private lateinit var buttonSave: TextView
    private lateinit var buttonCancel: TextView
    private lateinit var sharedPreferences: SharedPreferences

    private var selectedDays = mutableSetOf<Int>()
    private var selectedSound = "Rader"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_alarm)

        sharedPreferences = getSharedPreferences("AlarmPreferences", MODE_PRIVATE)

        hourPicker = findViewById(R.id.hourPicker)
        minutePicker = findViewById(R.id.minutePicker)
        ampmPicker = findViewById(R.id.ampmPicker)
        editTextAlarmName = findViewById(R.id.editAlarmName)
        repeatOption = findViewById(R.id.repeatOption)
        alarmSound = findViewById(R.id.alarmSound)
        buttonSave = findViewById(R.id.buttonSave)
        buttonCancel = findViewById(R.id.buttonCancel)

        // ✅ NumberPicker 설정
        hourPicker.minValue = 1
        hourPicker.maxValue = 12
        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        ampmPicker.minValue = 0
        ampmPicker.maxValue = 1
        ampmPicker.displayedValues = arrayOf("AM", "PM")

        repeatOption.setOnClickListener { showRepeatDialog() }
        alarmSound.setOnClickListener { showSoundDialog() }
        buttonSave.setOnClickListener { saveAlarm() }
        buttonCancel.setOnClickListener { finish() }
    }

    private fun showRepeatDialog() {
        val days = arrayOf("일", "월", "화", "수", "목", "금", "토")
        val checkedItems = BooleanArray(7) { selectedDays.contains(it) }

        AlertDialog.Builder(this)
            .setTitle("반복 요일 선택")
            .setMultiChoiceItems(days, checkedItems) { _, which, isChecked ->
                if (isChecked) selectedDays.add(which) else selectedDays.remove(which)
            }
            .setPositiveButton("확인") { _, _ ->
                repeatOption.text = if (selectedDays.isEmpty()) "반복 안함" else "반복: ${selectedDays.joinToString(", ")}"
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun showSoundDialog() {
        val sounds = arrayOf("Steps", "Tilt", "Rader", "Robot", "Silk", "Slow Rise", "Twinkle")

        AlertDialog.Builder(this)
            .setTitle("알람 사운드 선택")
            .setItems(sounds) { _, which ->
                selectedSound = sounds[which]
                alarmSound.text = "사운드: $selectedSound"
            }
            .show()
    }

    private fun saveAlarm() {
        val hour = if (ampmPicker.value == 1) hourPicker.value + 12 else hourPicker.value
        val minute = minutePicker.value
        val alarmName = editTextAlarmName.text.toString()

        val newAlarm = AlarmData(hour, minute, alarmName, true, selectedDays, selectedSound)
        val alarmList = getAlarmList()
        alarmList.add(newAlarm)

        sharedPreferences.edit().putString("alarms", Gson().toJson(alarmList)).apply()
        setResult(RESULT_OK, Intent().putExtra("new_alarm", Gson().toJson(newAlarm)))
        finish()
    }

    private fun getAlarmList(): MutableList<AlarmData> {
        val json = sharedPreferences.getString("alarms", null)
        return if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<AlarmData>>() {}.type
            Gson().fromJson(json, type)
        } else mutableListOf()
    }
}
