package com.example.swu_guru2_17

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.swu_guru2_17.databinding.ActivityStatisticsBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class StatisticsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatisticsBinding
    private lateinit var sharedPref: SharedPreferences
    private val gson by lazy { Gson() }
    private val dateFormatter by lazy { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = getSharedPreferences("StatisticsPreferences", MODE_PRIVATE)

        setupDrawer()
        setupToggleButtons() // 🔹 토글 버튼 설정 추가
        setupModifyGoalButton() // 🔹 목표 시간 수정 버튼 클릭 이벤트 추가
        loadDailyData() // 초기 화면을 Daily로 설정
    }

    // 🔹 "목표시간 수정하기" 버튼 클릭 시 EditDailyGoalActivity로 이동
    private fun setupModifyGoalButton() {
        binding.modifyGoalButton.setOnClickListener {
            val intent = Intent(this, EditDailyGoalActivity::class.java)
            startActivity(intent)
        }
    }

    // 🔹 사이드 메뉴 설정
    private fun setupDrawer() {
        binding.menuButton.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.END) // 오른쪽에서 사이드바 열기
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

    private fun setupToggleButtons() {
        binding.dailyButton.setOnClickListener {
            loadDailyData()
            updateButtonStyles(true)  // Daily 버튼 활성화
        }

        binding.weeklyButton.setOnClickListener {
            loadWeeklyData()
            updateButtonStyles(false) // Weekly 버튼 활성화
        }
    }

    // 🔹 버튼 스타일 변경 함수
    private fun updateButtonStyles(isDailySelected: Boolean) {
        if (isDailySelected) {
            binding.dailyButton.setBackgroundResource(R.drawable.toggle_left_selected)
            binding.dailyButton.setTextColor(resources.getColor(R.color.white))

            binding.weeklyButton.setBackgroundResource(R.drawable.toggle_right_unselected)
            binding.weeklyButton.setTextColor(resources.getColor(R.color.gray_3))
        } else {
            binding.dailyButton.setBackgroundResource(R.drawable.toggle_left_unselected)
            binding.dailyButton.setTextColor(resources.getColor(R.color.gray_3))

            binding.weeklyButton.setBackgroundResource(R.drawable.toggle_right_selected)
            binding.weeklyButton.setTextColor(resources.getColor(R.color.white))
        }
    }

    // 🔹 Daily 데이터 로드
    private fun loadDailyData() {
        val date = dateFormatter.format(Date())
        val dailyDataJson = sharedPref.getString("daily", "{}") ?: "{}"
        val dailyData: MutableMap<String, MutableList<TimePeriod>> =
            gson.fromJson(dailyDataJson, object : TypeToken<MutableMap<String, MutableList<TimePeriod>>>() {}.type) ?: mutableMapOf()

        val todayData = dailyData[date] ?: emptyList()

        Log.d("RECORD_DEBUG", "Loaded Daily Data: $todayData")

        updateCircleGraph(todayData)
    }

    // 🔹 Weekly 데이터 로드
    private fun loadWeeklyData() {
        val calendar = Calendar.getInstance().apply { set(Calendar.DAY_OF_WEEK, Calendar.MONDAY) }
        val startOfWeek = dateFormatter.format(calendar.time)
        val weeklyKey = "week_$startOfWeek"

        val weeklyDataJson = sharedPref.getString("weekly", "{}") ?: "{}"
        val weeklyData: MutableMap<String, MutableMap<String, Double>> =
            gson.fromJson(weeklyDataJson, object : TypeToken<MutableMap<String, MutableMap<String, Double>>>() {}.type) ?: mutableMapOf()

        val weeklyUsage = weeklyData[weeklyKey] ?: mutableMapOf()

        Log.d("WEEKLY_DEBUG", "Loaded Weekly Data: $weeklyUsage")

        updateWeeklyGraph(weeklyUsage)
    }

    // 🔹 원형 그래프 업데이트 (Daily)
    private fun updateCircleGraph(todayData: List<TimePeriod>) {
        binding.circleGraph.visibility = View.VISIBLE
        binding.barGraph.visibility = View.GONE

        binding.circleGraph.setData(todayData)
        binding.circleGraph.invalidate()
        binding.circleGraph.requestLayout()

        Log.d("GRAPH_DEBUG", "Showing Circle Graph with data: $todayData")
    }

    // 🔹 막대 그래프 업데이트 (Weekly)
    private fun updateWeeklyGraph(weeklyUsage: Map<String, Double>) {
        binding.circleGraph.visibility = View.GONE
        binding.barGraph.visibility = View.VISIBLE

        Log.d("GRAPH_DEBUG", "Showing Bar Graph with data: $weeklyUsage")
    }
}
