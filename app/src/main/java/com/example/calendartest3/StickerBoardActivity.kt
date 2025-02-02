package com.example.swu_guru2_17

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
}
