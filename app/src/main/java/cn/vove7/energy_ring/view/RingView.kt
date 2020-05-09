package cn.vove7.energy_ring.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.lang.Float.min


/**
 * # RingView
 *
 * @author Vove
 * 2020/5/8
 */
class RingView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    val accuracy = 1000f

    val progressf get() = progress / accuracy

    var progress: Int = accuracy.toInt() / 2
        set(value) {
            field = value
            invalidate()
        }

    var strokeWidth = 15f
        set(value) {
            field = value
            invalidate()
        }
    private val paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLUE
        }
    }

    private val rectF = RectF()

    var doughnutColors = intArrayOf(
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.RED
    )
        set(value) {
            field = value
            shader = SweepGradient(0f, 0f, doughnutColors, null)
        }

    private var shader = SweepGradient(0f, 0f, doughnutColors, null)


    private fun initPaint() {
        paint.reset()
        paint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas?) {
        canvas ?: return
        val size = min((width / 2).toFloat(), (height / 2).toFloat())
        canvas.translate(size, size)
        canvas.rotate(-90f)
        val r = size - strokeWidth / 2
        initPaint()
        //圆环外接矩形

        rectF.set(-r, -r, r, r)
        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.STROKE
        paint.shader = shader
        canvas.drawArc(rectF, 0f, 360f * progressf, false, paint)
    }
}