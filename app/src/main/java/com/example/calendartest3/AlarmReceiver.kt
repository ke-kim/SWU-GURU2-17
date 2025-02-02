package com.example.swu_guru2_17

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onReceive(context: Context, intent: Intent) {
        val alarmName = intent.getStringExtra("alarm_name") ?: "알람"
        val soundFileName = intent.getStringExtra("alarm_sound") ?: "default_sound"

        // res/raw 폴더의 알람 사운드 가져오기
        val soundResId = context.resources.getIdentifier(soundFileName, "raw", context.packageName)

        if (soundResId != 0) {
            mediaPlayer = MediaPlayer.create(context, soundResId)
            mediaPlayer?.start()
        } else {
            Log.e("AlarmReceiver", "해당 사운드 파일을 찾을 수 없음: $soundFileName")
        }
    }
}
