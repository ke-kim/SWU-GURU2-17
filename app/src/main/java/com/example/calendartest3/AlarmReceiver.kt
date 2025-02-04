package com.example.swu_guru2_17

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.PowerManager
import android.util.Log
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onReceive(context: Context, intent: Intent) {
        val alarmName = intent.getStringExtra("alarm_name") ?: "알람"
        val soundFileName = intent.getStringExtra("alarm_sound") ?: "default_sound"

        // WakeLock 추가 (절전 모드에서 알람 실행 가능하게)
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AlarmReceiver:WakeLock")
        wakeLock.acquire(10 * 1000L) // 10초 동안 WakeLock 유지

        // 알람 실행 로그 출력 및 토스트 메시지
        Toast.makeText(context, "알람 실행됨: $alarmName", Toast.LENGTH_LONG).show()
        Log.d("AlarmReceiver", "알람 실행됨: $alarmName")

        // res/raw 폴더에서 사운드 파일 찾기
        val soundResId = context.resources.getIdentifier(soundFileName, "raw", context.packageName)

        if (soundResId != 0) {
            mediaPlayer = MediaPlayer.create(context, soundResId).apply {
                start()
                setOnCompletionListener {
                    release()
                    mediaPlayer = null
                }
            }
        } else {
            Log.e("AlarmReceiver", "해당 사운드 파일을 찾을 수 없음: $soundFileName, 기본 알람 사용")
            mediaPlayer = MediaPlayer.create(context, R.raw.city)
            mediaPlayer?.start()
        }

        // WakeLock 해제
        wakeLock.release()
    }
}
