package com.example.swu_guru2_17

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import com.example.swu_guru2_17.databinding.ActivityStickerBoardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StickerBoardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStickerBoardBinding
    private lateinit var memoDatabaseHelper: MemoDatabaseHelper

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

        val puzzleResId = when (currentPuzzlePiece) {
            0 -> R.drawable.puzzle9
            else -> resources.getIdentifier("puzzle$currentPuzzlePiece", "drawable", packageName)
        }

        binding.puzzleImageView.setImageResource(puzzleResId)

        binding.completionMessage.text = when {
            completedCount == 0 -> getString(R.string.first_book_guide)
            else -> getString(
                R.string.puzzle_progress_extended,
                currentPuzzleSet,
                completedCount,
                9 - (completedCount % 9)
            )
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
}
