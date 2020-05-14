package cn.vove7.energy_ring.ui.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import cn.vove7.energy_ring.BuildConfig
import cn.vove7.energy_ring.util.Config
import kotlin.math.min


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

    var mainColor = Color.GREEN
    var bgColor = if (BuildConfig.DEBUG) Color.argb(10, 10, 10, 10) else Color.TRANSPARENT

    var doughnutColors = intArrayOf(
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.RED
    )
        set(value) {
            field = value
            field = value.let {
                when (it.size) {
                    1 -> intArrayOf(it[0], it[0])
                    0 -> intArrayOf(Color.RED, Color.GREEN, Color.BLUE, Color.RED)
                    else -> it
                }
            }
            shader = SweepGradient(0f, 0f, doughnutColors, null)
        }

    private var shader = SweepGradient(0f, 0f, doughnutColors, null)

    var strokeWidthF = 8f
    private val paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLUE
        }
    }

    private val rectF = RectF()

    private fun initPaint() {
        paint.reset()
        paint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas?) {
        canvas ?: return
        val size = min((width / 2).toFloat(), (height / 2).toFloat())
        canvas.translate(size, size)
        canvas.rotate(-90f)
        val strokeWidth = size * (strokeWidthF / 100f)
        val r = size - strokeWidth / 2
        initPaint()

        //圆环外接矩形
        rectF.set(-r, -r, r, r)

        //背景
        paint.color = bgColor
        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.STROKE
        canvas.drawArc(rectF, 0f, 360f, true, paint)

        initPaint()
        //圆环
        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.STROKE
        if (Config.colorMode == 2) {
            paint.shader = shader
        } else {
            paint.color = mainColor
        }
        canvas.drawArc(rectF, 0f, 360f * progressf, false, paint)
    }

    fun reSize(size: Int) {
        val lp = layoutParams ?: ViewGroup.LayoutParams(0, 0)
        if (lp.height == size) {
            return
        }
        layoutParams = lp.also {
            it.width = size
            it.height = size
        }
    }

}