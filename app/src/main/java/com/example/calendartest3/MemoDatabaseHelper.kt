package com.example.swu_guru2_17


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.*

class MemoDatabaseHelper(private val ctx: Context) : SQLiteOpenHelper(ctx, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "memo.db"
        private const val DATABASE_VERSION = 2
        const val TABLE_MEMOS = "memos"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_PUBLISHER = "publisher"
        const val COLUMN_MEMO = "memo"
        const val COLUMN_DATE = "date"
        const val COLUMN_IMAGE_PATH = "image_path"
        const val COLUMN_IS_COMPLETED = "is_completed"
        const val COLUMN_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_MEMOS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_AUTHOR TEXT,
                $COLUMN_PUBLISHER TEXT,
                $COLUMN_MEMO TEXT,
                $COLUMN_DATE TEXT NOT NULL,
                $COLUMN_IMAGE_PATH TEXT,
                $COLUMN_IS_COMPLETED INTEGER DEFAULT 0,
                $COLUMN_CREATED_AT TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_MEMOS RENAME TO temp_$TABLE_MEMOS")

            onCreate(db)

            try {
                db.execSQL("""
                INSERT INTO $TABLE_MEMOS 
                SELECT * FROM temp_$TABLE_MEMOS
            """)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            db.execSQL("DROP TABLE IF EXISTS temp_$TABLE_MEMOS")
        }
    }

    fun getMemosByDate(date: String): List<Memo> {
        if (date.isBlank()) return emptyList()

        val memos = mutableListOf<Memo>()
        val query = "SELECT * FROM $TABLE_MEMOS WHERE $COLUMN_DATE = ?"
        val db = readableDatabase

        try {
            db.rawQuery(query, arrayOf(date)).use { cursor ->
                while (cursor.moveToNext()) {
                    try {
                        memos.add(cursorToMemo(cursor))
                    } catch (e: Exception) {
                        Log.e("MemoDatabaseHelper", "Error parsing memo: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MemoDatabaseHelper", "Error getting memos: ${e.message}")
        }

        return memos
    }

    fun getAllMemos(): List<Memo> {
        val memos = mutableListOf<Memo>()
        val query = "SELECT * FROM $TABLE_MEMOS ORDER BY $COLUMN_CREATED_AT DESC"
        val db = readableDatabase

        db.rawQuery(query, null).use { cursor ->
            while (cursor.moveToNext()) {
                memos.add(cursorToMemo(cursor))
            }
        }
        return memos
    }

    fun insertMemo(title: String, author: String, publisher: String, memo: String, date: String, imageUri: Uri?, isCompleted: Boolean): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_AUTHOR, author)
            put(COLUMN_PUBLISHER, publisher)
            put(COLUMN_MEMO, memo)
            put(COLUMN_DATE, date)
            put(COLUMN_IS_COMPLETED, if (isCompleted) 1 else 0)
            put(COLUMN_CREATED_AT, Calendar.getInstance().timeInMillis.toString())

            imageUri?.let {
                val imagePath = saveImageToInternalStorage(it)
                put(COLUMN_IMAGE_PATH, imagePath)
            }
        }
        return db.insert(TABLE_MEMOS, null, values)
    }

    fun updateMemo(id: Int, title: String, author: String, publisher: String, memo: String, date: String, imageUri: Uri?, isCompleted: Boolean): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_AUTHOR, author)
            put(COLUMN_PUBLISHER, publisher)
            put(COLUMN_MEMO, memo)
            put(COLUMN_DATE, date)
            put(COLUMN_IS_COMPLETED, if (isCompleted) 1 else 0)

            imageUri?.let {
                val imagePath = saveImageToInternalStorage(it)
                put(COLUMN_IMAGE_PATH, imagePath)
            }
        }
        return db.update(TABLE_MEMOS, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun updateMemoCompletion(id: Int, isCompleted: Boolean) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IS_COMPLETED, if (isCompleted) 1 else 0)
        }
        val memo = getMemo(id)
        memo?.let {
            updateCompletionForAllWithTitle(it.title, isCompleted)
        }
    }

    fun getUniqueCompletedMemoCount(): Int {
        val db = readableDatabase
        return db.query(
            TABLE_MEMOS,
            arrayOf("COUNT(DISTINCT $COLUMN_TITLE)"),
            "$COLUMN_IS_COMPLETED = ?",
            arrayOf("1"),
            null,
            null,
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }
    }

    fun deleteMemo(id: Int) {
        val db = writableDatabase
        getMemo(id)?.let { memo ->
            if (memo.imagePath.isNotEmpty()) {
                File(memo.imagePath).delete()
            }
        }
        db.delete(TABLE_MEMOS, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun getMemo(id: Int): Memo? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_MEMOS,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        return cursor.use {
            if (it.moveToFirst()) cursorToMemo(it) else null
        }
    }

    private fun cursorToMemo(cursor: Cursor): Memo {
        return Memo(
            id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)) ?: "",
            author = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTHOR)) ?: "",
            publisher = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PUBLISHER)) ?: "",
            memo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEMO)) ?: "",
            date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)) ?: "",
            imagePath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH)) ?: "",
            isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1
        )
    }

    fun getCompletedMemoCount(): Int {
        val db = readableDatabase
        return db.query(
            TABLE_MEMOS,
            arrayOf("COUNT(*)"),
            "$COLUMN_IS_COMPLETED = ?",
            arrayOf("1"),
            null,
            null,
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String {
        try {
            val inputStream = if (uri.scheme == "content") {
                ctx.contentResolver.openInputStream(uri)  // 수정
            } else {
                File(uri.path!!).inputStream()
            }

            val fileName = "memo_image_${System.currentTimeMillis()}.jpg"
            val file = File(ctx.filesDir, fileName)

            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }

            return file.absolutePath
        } catch (e: Exception) {
            Log.e("MemoDatabaseHelper", "Error saving image: ${e.message}")
            throw e
        }
    }


    fun updateCompletionForAllWithTitle(title: String, isCompleted: Boolean): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IS_COMPLETED, if (isCompleted) 1 else 0)
        }
        return db.update(TABLE_MEMOS, values, "$COLUMN_TITLE = ?", arrayOf(title))
    }

    fun getAllMemosWithSameTitle(title: String): List<Memo> {
        val memos = mutableListOf<Memo>()
        val query = "SELECT * FROM $TABLE_MEMOS WHERE $COLUMN_TITLE = ?"
        val db = readableDatabase

        db.rawQuery(query, arrayOf(title)).use { cursor ->
            while (cursor.moveToNext()) {
                memos.add(cursorToMemo(cursor))
            }
        }
        return memos
    }


}