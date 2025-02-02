import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import java.io.File

class EventDecorator(
    private val imagePath: String,
    private val date: CalendarDay,
    private val context: Context
) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day == date
    }

    override fun decorate(view: DayViewFacade) {
        try {
            // 이미지 파일이 존재하는지 확인
            val imageFile = File(imagePath)
            if (!imageFile.exists()) {
                Log.e("EventDecorator", "Image file does not exist: $imagePath")
                return
            }

            // Glide를 사용하여 이미지 로드 및 변환
            Glide.with(context)
                .asBitmap()
                .load(imageFile)
                .apply(RequestOptions()
                    .override(40, 60)
                    .centerCrop())
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val drawable = BitmapDrawable(context.resources, resource)
                        view.setBackgroundDrawable(drawable)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // 필요한 경우 여기서 초기화 작업 수행
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        Log.e("EventDecorator", "Failed to load image: $imagePath")
                    }
                })
        } catch (e: Exception) {
            Log.e("EventDecorator", "Error decorating date: ${e.message}")
        }
    }
}