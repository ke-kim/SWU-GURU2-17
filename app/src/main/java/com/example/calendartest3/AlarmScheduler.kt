package com.example.swu_guru2_17

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*

class AlarmScheduler(private val context: Context) {

    fun setAlarm(alarm: AlarmData) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarm_name", alarm.name)
            putExtra("alarm_sound", alarm.sound)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, alarm.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
        }
        
        Log.d("AlarmScheduler", "알람이 예약됨: ${alarm.name} (${alarm.hour}:${alarm.minute})")
    }
}
