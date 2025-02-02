//package com.example.swu_guru2_17
//
//import android.content.Context
//import android.content.Intent
//import android.content.SharedPreferences
//import android.os.Bundle
//import android.view.Gravity
//import android.view.MenuItem
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.GravityCompat
//import androidx.drawerlayout.widget.DrawerLayout
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.google.android.material.navigation.NavigationView
//import com.example.swu_guru2_17.databinding.ActivityAlarmBinding
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//
//class AlarmActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityAlarmBinding
//    private lateinit var sharedPreferences: SharedPreferences
//    private var alarmList = mutableListOf<AlarmData>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityAlarmBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        setupDrawer() // 🔹 사이드바 기능 추가
//        setupAlarms()
//    }
//
//    private fun setupDrawer() {
//        binding.menuButton.setOnClickListener {
//            binding.drawerLayout.openDrawer(GravityCompat.END) // 🔹 오른쪽에서 사이드바 열기
//        }
//
//        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.nav_book_memo -> startActivity(Intent(this, MemoListActivity::class.java))
//                R.id.nav_story_book -> startActivity(Intent(this, TimerActivity::class.java))
//                R.id.nav_alarm -> startActivity(Intent(this, AlarmActivity::class.java))
//                R.id.nav_goal -> startActivity(Intent(this, GoalActivity::class.java))
//                R.id.nav_statistics -> startActivity(Intent(this, StatisticsActivity::class.java))
//                R.id.nav_sticker -> startActivity(Intent(this, StickerBoardActivity::class.java))
//            }
//            binding.drawerLayout.closeDrawers() // 메뉴 클릭 후 닫기
//            true
//        }
//    }
//
//    private fun setupAlarms() {
//        sharedPreferences = getSharedPreferences("AlarmPreferences", Context.MODE_PRIVATE)
//
//        binding.alarmRecyclerView.layoutManager = LinearLayoutManager(this)
//        binding.alarmRecyclerView.adapter = AlarmAdapter(alarmList) { alarm, position ->
//            editAlarm(alarm, position)
//        }
//
//        binding.addAlarmButton.setOnClickListener {
//            val intent = Intent(this, AddAlarmActivity::class.java)
//            startActivityForResult(intent, REQUEST_ADD_ALARM)
//        }
//
//        loadAlarms()
//    }
//
//    private fun loadAlarms() {
//        val json = sharedPreferences.getString("alarms", null)
//        if (!json.isNullOrEmpty()) {
//            val type = object : TypeToken<MutableList<AlarmData>>() {}.type
//            alarmList = Gson().fromJson(json, type)
//            binding.alarmRecyclerView.adapter?.notifyDataSetChanged()
//        }
//    }
//
//    private fun saveAlarms() {
//        sharedPreferences.edit().putString("alarms", Gson().toJson(alarmList)).apply()
//    }
//
//    private fun editAlarm(alarm: AlarmData, position: Int) {
//        val intent = Intent(this, EditAlarmActivity::class.java)
//        intent.putExtra("alarm_id", position)
//        startActivityForResult(intent, REQUEST_EDIT_ALARM)
//    }
//
//    override fun onStop() {
//        super.onStop()
//        saveAlarms()
//    }
//
//    companion object {
//        private const val REQUEST_ADD_ALARM = 1
//        private const val REQUEST_EDIT_ALARM = 2
//    }
//}

package com.example.swu_guru2_17

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.example.swu_guru2_17.databinding.ActivityAlarmBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AlarmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var alarmList = mutableListOf<AlarmData>()
    private lateinit var adapter: AlarmAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDrawer()
        setupAlarms()
    }

    private fun setupDrawer() {
        binding.menuButton.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.END)
        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_book_memo -> startActivity(Intent(this, MemoListActivity::class.java))
                R.id.nav_story_book -> startActivity(Intent(this, TimerActivity::class.java))
                R.id.nav_alarm -> startActivity(Intent(this, AlarmActivity::class.java))
                R.id.nav_goal -> startActivity(Intent(this, GoalActivity::class.java))
                R.id.nav_statistics -> startActivity(Intent(this, StatisticsActivity::class.java))
                R.id.nav_sticker -> startActivity(Intent(this, StickerBoardActivity::class.java))
            }
            binding.drawerLayout.closeDrawers()
            true
        }
    }

    private fun setupAlarms() {
        sharedPreferences = getSharedPreferences("AlarmPreferences", Context.MODE_PRIVATE)
        binding.alarmRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AlarmAdapter(alarmList) { alarm, position ->
            editAlarm(alarm, position)
        }
        binding.alarmRecyclerView.adapter = adapter

        binding.addAlarmButton.setOnClickListener {
            val intent = Intent(this, AddAlarmActivity::class.java)
            startActivityForResult(intent, REQUEST_ADD_ALARM)
        }

        loadAlarms()
    }

    private fun loadAlarms() {
        val json = sharedPreferences.getString("alarms", null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<AlarmData>>() {}.type
            alarmList = Gson().fromJson(json, type)
            adapter.updateAlarms(alarmList)
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
                    loadAlarms()
                }
                REQUEST_EDIT_ALARM -> {
                    loadAlarms()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        saveAlarms()
    }

    companion object {
        private const val REQUEST_ADD_ALARM = 1
        private const val REQUEST_EDIT_ALARM = 2
    }
}

