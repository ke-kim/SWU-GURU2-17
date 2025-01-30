package com.example.swu_guru2_17

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AlarmActivity : AppCompatActivity() {

    private lateinit var alarmRecyclerView: RecyclerView
    private lateinit var alarmAdapter: AlarmAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private var alarmList = mutableListOf<AlarmData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        sharedPreferences = getSharedPreferences("AlarmPreferences", Context.MODE_PRIVATE)

        alarmRecyclerView = findViewById(R.id.alarmRecyclerView)
        val addAlarmButton: Button = findViewById(R.id.addAlarmButton)
        val menuButton: ImageButton? = findViewById(R.id.menuButton)

        alarmAdapter = AlarmAdapter(alarmList) { alarm, position ->
            editAlarm(alarm, position)
        }
        alarmRecyclerView.layoutManager = LinearLayoutManager(this)
        alarmRecyclerView.adapter = alarmAdapter

        addAlarmButton.setOnClickListener {
            val intent = Intent(this, AddAlarmActivity::class.java)
            startActivityForResult(intent, REQUEST_ADD_ALARM)
        }

        menuButton!!.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        loadAlarms()
    }

    private fun loadAlarms() {
        val json = sharedPreferences.getString("alarms", null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<AlarmData>>() {}.type
            alarmList = Gson().fromJson(json, type)
            alarmAdapter.updateAlarms(alarmList)
        }
    }

    private fun saveAlarms() {
        sharedPreferences.edit().putString("alarms", Gson().toJson(alarmList)).apply()
    }

    private fun editAlarm(alarm: AlarmData, position: Int) {
        val intent = Intent(this, EditAlarmActivity::class.java)
        intent.putExtra("alarm_id", position)
        startActivityForResult(intent, REQUEST_EDIT_ALARM)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_ADD_ALARM -> {
                    val alarmJson = data?.getStringExtra("new_alarm")
                    alarmJson?.let {
                        val newAlarm = Gson().fromJson(it, AlarmData::class.java)
                        alarmList.add(newAlarm)
                        saveAlarms()
                        alarmAdapter.notifyDataSetChanged()
                    }
                }
                REQUEST_EDIT_ALARM -> {
                    val alarmJson = data?.getStringExtra("updated_alarm")
                    val alarmId = data?.getIntExtra("alarm_id", -1)

                    if (alarmJson != null && alarmId != null && alarmId in alarmList.indices) {
                        val updatedAlarm = Gson().fromJson(alarmJson, AlarmData::class.java)
                        alarmList[alarmId] = updatedAlarm
                        saveAlarms()
                        alarmAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        saveAlarms() // ✅ 앱을 종료하거나 다른 화면으로 이동할 때 자동 저장
    }

    companion object {
        private const val REQUEST_ADD_ALARM = 1
        private const val REQUEST_EDIT_ALARM = 2
    }
}

