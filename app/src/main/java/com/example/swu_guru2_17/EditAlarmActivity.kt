package com.example.swu_guru2_17

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class EditAlarmActivity : AppCompatActivity() {

    private lateinit var hourPicker: NumberPicker
    private lateinit var minutePicker: NumberPicker
    private lateinit var ampmPicker: NumberPicker
    private lateinit var editTextAlarmName: EditText
    private lateinit var repeatOption: TextView
    private lateinit var alarmSound: TextView
    private lateinit var buttonSave: TextView
    private lateinit var buttonCancel: TextView
    private lateinit var buttonDelete: Button
    private lateinit var sharedPreferences: SharedPreferences

    private var alarmId: Int = -1
    private var selectedDays = mutableSetOf<Int>()
    private var selectedSound = "Rader"
    private lateinit var alarmList: MutableList<AlarmData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_alarm)

        sharedPreferences = getSharedPreferences("AlarmPreferences", Context.MODE_PRIVATE)

        hourPicker = findViewById(R.id.hourPicker)
        minutePicker = findViewById(R.id.minutePicker)
        ampmPicker = findViewById(R.id.ampmPicker)
        editTextAlarmName = findViewById(R.id.editAlarmName)
        repeatOption = findViewById(R.id.repeatOption)
        alarmSound = findViewById(R.id.alarmSound)
        buttonSave = findViewById(R.id.buttonSave)
        buttonCancel = findViewById(R.id.buttonCancel)
        buttonDelete = findViewById(R.id.buttonDelete)

        setupNumberPickers()
        loadAlarmData()
        setupListeners()
    }

    private fun setupNumberPickers() {
        hourPicker.minValue = 1
        hourPicker.maxValue = 12

        minutePicker.minValue = 0
        minutePicker.maxValue = 59

        ampmPicker.minValue = 0
        ampmPicker.maxValue = 1
        ampmPicker.displayedValues = arrayOf("AM", "PM")
    }

    private fun loadAlarmData() {
        alarmId = intent.getIntExtra("alarm_id", -1)
        alarmList = getAlarmList()

        if (alarmId in alarmList.indices) {
            val alarm = alarmList[alarmId]

            // ✅ 기존 알람 시간 설정
            val hour = if (alarm.hour > 12) alarm.hour - 12 else alarm.hour
            val isPM = alarm.hour >= 12

            hourPicker.value = hour
            minutePicker.value = alarm.minute
            ampmPicker.value = if (isPM) 1 else 0

            editTextAlarmName.setText(alarm.name)
            selectedDays = alarm.repeatDays?.toMutableSet() ?: mutableSetOf()
            selectedSound = alarm.sound ?: "Rader"

            repeatOption.text = if (selectedDays.isEmpty()) "반복 안함" else "반복: ${selectedDays.joinToString(", ")}"
            alarmSound.text = "사운드: $selectedSound"
        }
    }

    private fun setupListeners() {
        repeatOption.setOnClickListener { showRepeatDialog() }
        alarmSound.setOnClickListener { showSoundDialog() }
        buttonSave.setOnClickListener { saveEditedAlarm() } // ✅ 변경 사항 저장
        buttonCancel.setOnClickListener { finish() } // ✅ 이전 화면으로 이동
        buttonDelete.setOnClickListener { deleteAlarm() }
    }

    private fun saveEditedAlarm() {
        val selectedHour = if (ampmPicker.value == 1) hourPicker.value + 12 else hourPicker.value
        val selectedMinute = minutePicker.value
        val alarmName = editTextAlarmName.text.toString()

        val updatedAlarm = AlarmData(selectedHour, selectedMinute, alarmName, true, selectedDays, selectedSound)
        if (alarmId in alarmList.indices) {
            alarmList[alarmId] = updatedAlarm
        }

        sharedPreferences.edit().putString("alarms", Gson().toJson(alarmList)).apply()
        setResult(RESULT_OK, Intent().putExtra("updated_alarm", Gson().toJson(updatedAlarm)))
        finish()
    }

    private fun deleteAlarm() {
        if (alarmId in alarmList.indices) {
            alarmList.removeAt(alarmId)
        }
        sharedPreferences.edit().putString("alarms", Gson().toJson(alarmList)).apply()
        setResult(RESULT_OK)
        finish()
    }

    private fun getAlarmList(): MutableList<AlarmData> {
        val json = sharedPreferences.getString("alarms", null)
        return if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<AlarmData>>() {}.type
            Gson().fromJson(json, type)
        } else mutableListOf()
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
            .setTitle("알람음 선택")
            .setItems(sounds) { _, which ->
                selectedSound = sounds[which]
                alarmSound.text = "사운드: $selectedSound"
            }
            .show()
    }
}
