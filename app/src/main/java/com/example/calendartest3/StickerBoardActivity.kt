package com.example.swu_guru2_17

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.swu_guru2_17.databinding.ActivityStickerBoardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StickerBoardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStickerBoardBinding
    private lateinit var memoDatabaseHelper: MemoDatabaseHelper

    private var isReceiverRegistered = false
    private val completionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.example.swu_guru2_17.ACTION_COMPLETION_CHANGED") {
                updatePuzzleProgress()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStickerBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        memoDatabaseHelper = MemoDatabaseHelper(this)
        setupViews()
        setupDrawer()
        updatePuzzleProgress()
    }

    private fun setupViews() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun updatePuzzleProgress() {
        val completedCount = memoDatabaseHelper.getUniqueCompletedMemoCount()
        val currentPuzzleSet = (completedCount - 1) / 9 + 1
        val currentPuzzlePiece = completedCount % 9

        val puzzleResId = when {
            completedCount == 0 -> R.drawable.puzzle_empty
            currentPuzzlePiece == 0 -> R.drawable.puzzle9  // 9개 모두 완성했을 때
            else -> resources.getIdentifier("puzzle$currentPuzzlePiece", "drawable", packageName)
        }

        binding.puzzleImageView.setImageResource(puzzleResId)

        binding.completionMessage.text = when {
            completedCount == 0 -> "첫번째 책을 완독하고\n퍼즐을 채워보세요!"
            else -> {
                val remaining = 9 - (completedCount % 9)
                if (remaining == 9) {
                    "축하합니다!\n${currentPuzzleSet}번째 퍼즐을 완성했어요!"
                } else {
                    "지금까지 ${completedCount}권 읽었어요.\n${remaining}권 더 읽으면 ${currentPuzzleSet}번째 퍼즐이 완성돼요!"
                }
            }
        }
    }

    private fun setupDrawer() {
        binding.menuButton.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.END) // 오른쪽에서 사이드바 열기
        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> startActivity(Intent(this, MainActivity::class.java))
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



    override fun onResume() {
        super.onResume()
        updatePuzzleProgress()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
