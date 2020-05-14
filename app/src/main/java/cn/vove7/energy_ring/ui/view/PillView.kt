package cn.vove7.energy_ring.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View

/**
 * # PillView
 *
 * @author Vove
 * 2020/5/12
 */
class PillView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var bgColor: Int = 0

    val accuracy = 1000f

    val progressf get() = progress / accuracy

    var pillRotation: Float = 0f

    var progress: Int = accuracy.toInt() / 2

    var mainColor: Int = Color.RED

    var process: Int = 50

    var strokeWidthF = 8f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val rectF = RectF()
    override fun onDraw(canvas: Canvas) {
        canvas.translate((width / 2).toFloat(), (height / 2).toFloat())
        val strokeWidth = height / 2 * strokeWidthF / 100f + 1
        Log.d("Debug :", "strokeWidth  ----> ${strokeWidth}")

        val tw = width - strokeWidth
        val th = height - strokeWidth
        val w_2 = tw / 2
        val h_2 = th / 2

        //左圆
        paint.flags
        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.STROKE
        paint.color = mainColor
        rectF.set(-w_2, -h_2, th - w_2, h_2)

        canvas.drawArc(rectF, 90f, 180f, false, paint)

        //右圆
        rectF.set(w_2 - th, -h_2, w_2, h_2)
        canvas.drawArc(rectF, 270f, 180f, false, paint)

        canvas.drawLine(h_2 - w_2 - lineAcc, -h_2, w_2 - h_2 + lineAcc, -h_2, paint)
        canvas.drawLine(h_2 - w_2 - lineAcc, h_2, w_2 - h_2 + lineAcc, h_2, paint)

    }

    companion object {
        const val lineAcc = 0.5f
    }
}