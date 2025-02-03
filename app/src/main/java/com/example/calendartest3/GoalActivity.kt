package com.example.swu_guru2_17

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.swu_guru2_17.databinding.ActivityGoalBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class GoalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGoalBinding
    private lateinit var sharedPref: SharedPreferences
    private val gson = Gson()
    private var isDailyView = true  // Daily / Weekly 토글 상태

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = getSharedPreferences("GoalPreferences", MODE_PRIVATE)

        setupDrawer()
        setupMenuButton()
        setupToggleButtons()
        updateGoalProgress()

        // modifyGoalButton 클릭 시 EditDailyGoalActivity로 이동
        binding.modifyGoalButton.setOnClickListener {
            val intent = Intent(this, EditDailyGoalActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_EDIT_GOAL)
        }
    }

    private fun setupMenuButton() {
        binding.menuButton.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                binding.drawerLayout.closeDrawer(GravityCompat.END)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.END)
            }
        }
    }

    private fun setupDrawer() {
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_book_memo -> startActivity(Intent(this, MemoListActivity::class.java))
                R.id.nav_story_book -> startActivity(Intent(this, TimerActivity::class.java))
                R.id.nav_alarm -> startActivity(Intent(this, AlarmActivity::class.java))
                R.id.nav_goal -> startActivity(Intent(this, GoalActivity::class.java))
                R.id.nav_statistics -> startActivity(Intent(this, StatisticsActivity::class.java))
                R.id.nav_sticker -> startActivity(Intent(this, StickerBoardActivity::class.java))
            }
            binding.drawerLayout.closeDrawer(GravityCompat.END)
            true
        }
    }

    // Daily / Weekly 버튼 클릭 이벤트 추가
    private fun setupToggleButtons() {
        binding.dailyButton.setOnClickListener {
            isDailyView = true
            updateGoalProgress()
            binding.dailyButton.setBackgroundResource(R.drawable.toggle_left_selected)
            binding.weeklyButton.setBackgroundResource(R.drawable.toggle_right_unselected)
        }

        binding.weeklyButton.setOnClickListener {
            isDailyView = false
            updateGoalProgress()
            binding.dailyButton.setBackgroundResource(R.drawable.toggle_left_unselected)
            binding.weeklyButton.setBackgroundResource(R.drawable.toggle_right_selected)
        }
    }

    // 목표 달성률 업데이트 (Daily / Weekly에 따라 변경)
    private fun updateGoalProgress() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val goalDataJson = sharedPref.getString("goalData", "{}")
        val goalData: MutableMap<String, Int> =
            gson.fromJson(goalDataJson, object : TypeToken<MutableMap<String, Int>>() {}.type) ?: mutableMapOf()

        val dailyGoal = goalData["daily"] ?: 60
        val weeklyGoal = goalData["weekly"] ?: 420

        val usageDataJson = sharedPref.getString("daily", "{}")
        val usageData: MutableMap<String, MutableList<TimePeriod>> =
            gson.fromJson(usageDataJson, object : TypeToken<MutableMap<String, MutableList<TimePeriod>>>() {}.type) ?: mutableMapOf()

        val todayUsage = usageData[today] ?: emptyList()
        val totalTimeUsed = todayUsage.sumOf { it.getDurationInMinutes() }

        val goalTime = if (isDailyView) dailyGoal else weeklyGoal
        val completionPercentage = if (goalTime > 0) (totalTimeUsed.toDouble() / goalTime * 100).toInt() else 0

        binding.completionTextView.text = "$completionPercentage% 달성!"
        binding.hourglassGraph.setProgress(completionPercentage / 100f)
    }

    // EditDailyGoalActivity에서 목표를 수정한 후 결과 받기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT_GOAL && resultCode == RESULT_OK) {
            updateGoalProgress()
        }
    }

    companion object {
        private const val REQUEST_CODE_EDIT_GOAL = 1001
    }
}
