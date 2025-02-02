package com.example.swu_guru2_17

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.swu_guru2_17.databinding.ActivityMemoListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MemoListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMemoListBinding
    private lateinit var memoDatabaseHelper: MemoDatabaseHelper
    private lateinit var memoListAdapter: MemoListAdapter
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private var originalMemoList: List<Memo> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMemoListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDrawer()
        initializeViews()
        setupRecyclerView()
        setupSearchView()
        loadMemos()
    }

    private fun initializeViews() {
        memoDatabaseHelper = MemoDatabaseHelper(this)
        recyclerView = binding.memoRecyclerView
        searchView = binding.searchView

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.addMemoButton.setOnClickListener {
            startActivity(Intent(this, AddMemoActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        memoListAdapter = MemoListAdapter(
            onDeleteClick = { memo -> deleteMemo(memo) },
            onEditClick = { memo -> editMemo(memo) },
            onCompletedChange = { memo, isCompleted -> updateMemoCompletion(memo, isCompleted) }
        )
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MemoListActivity)
            adapter = memoListAdapter
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterMemos(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterMemos(newText)
                return true
            }
        })
    }

    private fun filterMemos(query: String?) {
        if (query.isNullOrBlank()) {
            memoListAdapter.submitList(originalMemoList)
        } else {
            val filteredList = originalMemoList.filter { memo ->
                memo.title.contains(query, true) ||
                        memo.author.contains(query, true) ||
                        memo.publisher.contains(query, true) ||
                        memo.memo.contains(query, true)
            }
            memoListAdapter.submitList(filteredList)
        }
    }

    private fun loadMemos() {
        CoroutineScope(Dispatchers.IO).launch {
            val memos = memoDatabaseHelper.getAllMemos()
            withContext(Dispatchers.Main) {
                originalMemoList = memos
                memoListAdapter.submitList(memos)
            }
        }
    }

    private fun deleteMemo(memo: Memo) {
        CoroutineScope(Dispatchers.IO).launch {
            memoDatabaseHelper.deleteMemo(memo.id)
            loadMemos()
        }
    }

    private fun editMemo(memo: Memo) {
        val intent = Intent(this, AddMemoActivity::class.java).apply {
            putExtra("MEMO_ID", memo.id)
            putExtra("TITLE", memo.title)
            putExtra("AUTHOR", memo.author)
            putExtra("PUBLISHER", memo.publisher)
            putExtra("MEMO", memo.memo)
            putExtra("DATE", memo.date)
            putExtra("IMAGE_PATH", memo.imagePath)
        }
        startActivity(intent)
    }

    private fun updateMemoCompletion(memo: Memo, isCompleted: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            memoDatabaseHelper.updateMemoCompletion(memo.id, isCompleted)
            loadMemos()
        }
    }



    override fun onResume() {
        super.onResume()
        loadMemos()
    }

    private fun setupDrawer() {
        binding.menuButton.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.END)
        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_book_memo -> {
                    startActivity(Intent(this, MemoListActivity::class.java))
                    true
                }
                R.id.nav_sticker -> {
                    startActivity(Intent(this, StickerBoardActivity::class.java))
                    true
                }
                R.id.nav_book_memo -> {
                    startActivity(Intent(this, MemoListActivity::class.java))
                    true
                }
                R.id.nav_sticker -> {
                    startActivity(Intent(this, StickerBoardActivity::class.java))
                    true
                }
                R.id.nav_story_book -> {
                    startActivity(Intent(this, TimerActivity::class.java))
                    true
                }
                R.id.nav_alarm -> {
                    startActivity(Intent(this, AlarmActivity::class.java))
                    true
                }
                R.id.nav_goal -> {
                    startActivity(Intent(this, GoalActivity::class.java))
                    true
                }
                R.id.nav_statistics -> {
                    startActivity(Intent(this, StatisticsActivity::class.java))
                    true
                }
                else -> {
                    Toast.makeText(this, R.string.feature_not_ready, Toast.LENGTH_SHORT).show()
                    true
                }
            }
        }
    }
}