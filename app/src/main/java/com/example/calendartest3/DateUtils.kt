import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateUtils {
    private val displayDateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
    private val databaseDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun formatDisplayDate(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(year, month - 1, day)
        }
        return displayDateFormat.format(calendar.time)
    }

    fun formatDatabaseDate(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(year, month - 1, day)
        }
        return databaseDateFormat.format(calendar.time)
    }

    fun parseDisplayDate(displayDate: String): Triple<Int, Int, Int>? {
        return try {
            val date = displayDateFormat.parse(displayDate) ?: return null
            val calendar = Calendar.getInstance().apply { time = date }
            Triple(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        } catch (e: Exception) {
            null
        }
    }

    fun convertDisplayToDatabase(displayDate: String): String? {
        return try {
            val date = displayDateFormat.parse(displayDate) ?: return null
            databaseDateFormat.format(date)
        } catch (e: Exception) {
            null
        }
    }

    fun convertDatabaseToDisplay(databaseDate: String): String? {
        return try {
            val date = databaseDateFormat.parse(databaseDate) ?: return null
            displayDateFormat.format(date)
        } catch (e: Exception) {
            null
        }
    }
}