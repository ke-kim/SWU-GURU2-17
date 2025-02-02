package com.example.swu_guru2_17

import EventDecorator
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swu_guru2_17.databinding.ActivityMainBinding
import com.prolificinteractive.materialcalendarview.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var memoDatabaseHelper: MemoDatabaseHelper
    private lateinit var memoListAdapter: MemoListAdapter
    private var selectedCalendarDay: CalendarDay = CalendarDay.today()
    private val dateFormatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    private val monthFormatter = SimpleDateFormat(MONTH_FORMAT, Locale.getDefault())
    private val yearFormatter = SimpleDateFormat(YEAR_FORMAT, Locale.getDefault())
    private val mainScope = CoroutineScope(Dispatchers.Main + Job())
    private lateinit var calendarMemoAdapter: CalendarMemoAdapter

    companion object {
        private const val DATE_FORMAT = "yyyy년 MM월 dd일"
        private const val MONTH_FORMAT = "M월"
        private const val YEAR_FORMAT = "yyyy"
    }

    private fun CalendarDay.formatToDisplayString(): String {
        val calendar = Calendar.getInstance().apply {
            set(year, month - 1, day)
        }
        return dateFormatter.format(calendar.time)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.apply {
            setWindowAnimations(R.style.WindowAnimationTransition)
            setBackgroundDrawableResource(android.R.color.white)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
        setupCalendarView()
        setupRecyclerView()
        setupClickListeners()
        setupDrawer()
    }

    private fun initializeViews() {
        memoDatabaseHelper = MemoDatabaseHelper(this)
        binding.calendarView.post {
            binding.calendarView.getChildAt(0)?.let { header ->
                header.layoutParams.height = 0
                header.requestLayout()
            }
        }
    }

    private fun setupCalendarView() {
        val calendar = Calendar.getInstance()

        with(binding.calendarView) {

            setOnDateChangedListener { _, date, selected ->
                if (selected) {
                    selectedCalendarDay = date
                    calendar.set(date.year, date.month - 1, date.day)
                    binding.bookListDate.text = dateFormatter.format(calendar.time)
                    updateMemoListView()
                }
            }

            setOnMonthChangedListener { _, date ->
                calendar.set(date.year, date.month - 1, 1)
                binding.textMonth.text = monthFormatter.format(calendar.time)
                binding.textYear.text = yearFormatter.format(calendar.time)
            }

            val today = CalendarDay.today()
            setCurrentDate(today)
            setSelectedDate(today)

            calendar.set(today.year, today.month - 1, today.day)
            binding.bookListDate.text = dateFormatter.format(calendar.time)
        }

        updateMemoListView()
    }


    private fun setupRecyclerView() {
        calendarMemoAdapter = CalendarMemoAdapter()
        binding.memoListView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = calendarMemoAdapter
        }
    }


    private fun setupClickListeners() {
        binding.apply {
            binding.apply {

                btnAddRecord.setOnClickListener {
                    val intent = Intent(this@MainActivity, AddMemoActivity::class.java).apply {
                        putExtra("SELECTED_DATE", selectedCalendarDay.formatToDisplayString())
                    }
                    startActivity(intent)
                }

                menuButton.setOnClickListener {
                    drawerLayout.openDrawer(GravityCompat.END)
                }
            }
        }
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

    override fun onDestroy() {
        mainScope.cancel()
        super.onDestroy()
    }

    private fun updateMemoListView() {
        mainScope.launch {
            try {
                val memos = withContext(Dispatchers.IO) {
                    memoDatabaseHelper.getMemosByDate(selectedCalendarDay.formatToDisplayString())
                }

                if (memos.isEmpty()) {
                    binding.emptyStateLayout.visibility = View.VISIBLE
                    binding.memoListView.visibility = View.GONE
                } else {
                    binding.emptyStateLayout.visibility = View.GONE
                    binding.memoListView.visibility = View.VISIBLE
                    calendarMemoAdapter.submitList(memos)
                }

                updateCalendarDecorators()
            } catch (e: Exception) {
                Log.e("MainActivity", "Error updating memo list", e)
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            binding.drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }


    private fun updateCalendarDecorators() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val memosByDate = memoDatabaseHelper.getAllMemos().groupBy { it.date }

                withContext(Dispatchers.Main) {
                    binding.calendarView.removeDecorators()

                    memosByDate.forEach { (date, memos) ->
                        memos.firstOrNull()?.let { memo ->
                            if (memo.imagePath.isNotEmpty() && memo.date.isNotEmpty()) {
                                try {
                                    val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
                                    val parsedDate = dateFormat.parse(memo.date)

                                    parsedDate?.let {
                                        val cal = Calendar.getInstance().apply {
                                            time = it
                                        }

                                        val calendarDay = CalendarDay.from(
                                            cal.get(Calendar.YEAR),
                                            cal.get(Calendar.MONTH) + 1,
                                            cal.get(Calendar.DAY_OF_MONTH)
                                        )

                                        binding.calendarView.addDecorator(
                                            EventDecorator(memo.imagePath, calendarDay, this@MainActivity)
                                        )
                                    }
                                } catch (e: Exception) {
                                    Log.e("MainActivity", "Error parsing date: ${memo.date}", e)
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error updating calendar decorators: ${e.message}")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateMemoListView()
    }

}