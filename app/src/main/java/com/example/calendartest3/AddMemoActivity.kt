package com.example.swu_guru2_17

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.swu_guru2_17.databinding.ActivityAddMemoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddMemoActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var titleEditText: EditText
    private lateinit var authorEditText: EditText
    private lateinit var publisherEditText: EditText
    private lateinit var memoEditText: EditText
    private lateinit var completedCheckBox: CheckBox
    private lateinit var memoDatabaseHelper: MemoDatabaseHelper
    private lateinit var binding: ActivityAddMemoBinding
    private lateinit var dateEditText: EditText

    private var selectedImageUri: Uri? = null
    private var memoId: Int = -1
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_memo)
        binding = ActivityAddMemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.titleEditText.imeOptions = EditorInfo.IME_FLAG_NO_FULLSCREEN
        binding.authorEditText.imeOptions = EditorInfo.IME_FLAG_NO_FULLSCREEN
        binding.publisherEditText.imeOptions = EditorInfo.IME_FLAG_NO_FULLSCREEN
        binding.memoEditText.imeOptions = EditorInfo.IME_FLAG_NO_FULLSCREEN

        setupDatePicker()
        initializeViews()
        setupListeners()
        loadExistingMemo()
    }


    private fun setupDatePicker() {
        dateEditText = findViewById(R.id.dateEditText)

        intent.getStringExtra("SELECTED_DATE")?.let {
            dateEditText.setText(it)
        }

        dateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }

                    if (selectedDate.after(Calendar.getInstance())) {
                        Toast.makeText(this, "미래의 날짜는 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
                        return@DatePickerDialog
                    }

                    val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
                    dateEditText.setText(dateFormat.format(selectedDate.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun initializeViews() {
        imageView = findViewById(R.id.imageView)
        titleEditText = findViewById(R.id.titleEditText)
        authorEditText = findViewById(R.id.authorEditText)
        publisherEditText = findViewById(R.id.publisherEditText)
        memoEditText = findViewById(R.id.memoEditText)
        completedCheckBox = findViewById(R.id.completedCheckBox)
        memoDatabaseHelper = MemoDatabaseHelper(this)

        selectedDate = intent.getStringExtra("SELECTED_DATE") ?: ""
    }

    private fun setupListeners() {
        findViewById<Button>(R.id.selectImageButton).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        findViewById<Button>(R.id.cancelButton).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.completeButton).setOnClickListener {
            saveMemo()
        }
    }

    private fun loadExistingMemo() {
        memoId = intent.getIntExtra("MEMO_ID", -1)
        if (memoId != -1) {
            CoroutineScope(Dispatchers.IO).launch {
                val memo = memoDatabaseHelper.getMemo(memoId)
                withContext(Dispatchers.Main) {
                    memo?.let {
                        titleEditText.setText(it.title)
                        authorEditText.setText(it.author)
                        publisherEditText.setText(it.publisher)
                        memoEditText.setText(it.memo)
                        completedCheckBox.isChecked = it.isCompleted
                        if (it.imagePath.isNotEmpty()) {
                            imageView.setImageURI(Uri.parse(it.imagePath))
                            selectedImageUri = Uri.parse(it.imagePath)
                        }
                    }
                }
            }
        }
    }

    private fun saveMemo() {
        val title = binding.titleEditText.text.toString()
        val author = binding.authorEditText.text.toString()
        val publisher = binding.publisherEditText.text.toString()
        val memo = binding.memoEditText.text.toString()
        val date = binding.dateEditText.text.toString()
        val isCompleted = binding.completedCheckBox.isChecked

        if (title.isEmpty()) {
            Toast.makeText(this, "제목을 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        if (date.isEmpty()) {
            Toast.makeText(this, "날짜를 선택해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (memoId == -1) {
                    memoDatabaseHelper.insertMemo(
                        title = title,
                        author = author,
                        publisher = publisher,
                        memo = memo,
                        date = date,
                        imageUri = selectedImageUri,
                        isCompleted = isCompleted
                    )
                } else {
                    memoDatabaseHelper.updateMemo(
                        id = memoId,
                        title = title,
                        author = author,
                        publisher = publisher,
                        memo = memo,
                        date = date,
                        imageUri = selectedImageUri,
                        isCompleted = isCompleted
                    )
                }

                withContext(Dispatchers.Main) {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddMemoActivity, "메모 저장 실패", Toast.LENGTH_SHORT).show()
                }
                Log.e("AddMemoActivity", "Error saving memo: ${e.message}")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                loadImageFromUri(uri)
            }
        }
    }

    private fun loadImageFromUri(uri: Uri) {
        try {
            lifecycleScope.launch(Dispatchers.IO) {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    withContext(Dispatchers.IO) {
                        val source = ImageDecoder.createSource(contentResolver, uri)
                        ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                            decoder.isMutableRequired = true
                        }
                    }
                } else {
                    withContext(Dispatchers.IO) {
                        @Suppress("DEPRECATION")
                        MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    }
                }

                withContext(Dispatchers.Main) {
                    binding.imageView.setImageBitmap(bitmap)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "이미지를 불러오는데 실패했습니다", Toast.LENGTH_SHORT).show()
            Log.e("AddMemoActivity", "Error loading image", e)
        }
    }

    private fun validateDate(selectedDate: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        val selected = dateFormat.parse(selectedDate)
        val today = Calendar.getInstance().time

        if (selected?.after(today) == true) {
            Toast.makeText(this, "미래의 날짜는 선택할 수 없습니다", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
}