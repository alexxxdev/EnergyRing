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

    /**
     * r4___________r1
     *  /           \
     * |             |
     *  \___________/
     * r3            r2
     * @param canvas Canvas
     */
    override fun onDraw(canvas: Canvas) {
        canvas.translate((width / 2).toFloat(), (height / 2).toFloat())
        val strokeWidth = height / 2 * strokeWidthF / 100f + 1
        val pf = progressf

        val tw = width - strokeWidth
        val th = height - strokeWidth
        val w_2 = tw / 2
        val h_2 = th / 2


        val v = tw - th
        val v_2 = v / 2
        val C = (2 * v + Math.PI * th).toFloat()


        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.STROKE

        //背景色
        paint.color = bgColor

        //上右
        canvas.drawLine(0f, -h_2, (w_2 - h_2 + lineAcc), -h_2, paint)
        //右
        rectF.set(w_2 - th, -h_2, w_2, h_2)
        canvas.drawArc(rectF, 270f, 180f, false, paint)
        //下
        canvas.drawLine(v_2 + lineAcc, h_2, -v_2 - lineAcc, h_2, paint)
        //左
        rectF.set(-w_2, -h_2, th - w_2, h_2)
        canvas.drawArc(rectF, 90f, 180f, false, paint)
        //上左
        canvas.drawLine(-v_2 - lineAcc, -h_2, 0f, -h_2, paint)

        paint.color = mainColor


        val r1 = v / 2 / C
        val r2 = (r1 + Math.PI * th / (2 * C)).toFloat()
        val r3 = r2 + (v / C)
        val r4 = 1f - r1

        val rf = pillRotation % 360 / 360
        drawOneCircle(canvas, rf, (pf + rf).let { if (it > 1) 1f else it }, r1, r2, r3, r4, h_2, w_2, th, v, C)
        if (rf + pf > 1) {
            drawOneCircle(canvas, 0f, pf - (1 - rf), r1, r2, r3, r4, h_2, w_2, th, v, C)
        }
    }

    private fun drawOneCircle(
            canvas: Canvas, sf: Float, ef: Float,
            r1: Float, r2: Float, r3: Float, r4: Float,
            h_2: Float, w_2: Float, th: Float, v: Float, C: Float
    ) {
        //上边线 右
        if (sf <= r1) {
            if (ef > r1) {
                canvas.drawLine(C * sf, -h_2, (w_2 - h_2 + lineAcc), -h_2, paint)
            } else {
                canvas.drawLine(C * sf, -h_2, v / 2 * ef / r1, -h_2, paint)
                return
            }
        }

        rectF.set(w_2 - th, -h_2, w_2, h_2)
        //右圆
        if (sf <= r2) {
            val offsetAngle = if (sf <= r1) 0f
            else (C * 360f * (sf - r1) / Math.PI / th).toFloat()

            if (ef > r2) {
                canvas.drawArc(rectF, 270f + offsetAngle, 180f - offsetAngle, false, paint)
            } else {
                canvas.drawArc(rectF, 270f + offsetAngle, 180f * (ef - r1) / (r2 - r1) - offsetAngle, false, paint)
                return
            }
        }

        //下边线
        if (sf <= r3) {
            val startXOffset = if (sf <= r2) 0f else (sf - r2) * C
            val startX = v / 2 + lineAcc - startXOffset
            if (ef > r3) {
                canvas.drawLine(startX, h_2, -v / 2 - lineAcc, h_2, paint)
            } else {
                val end = v / 2 + lineAcc - v * (ef - r2) / (r3 - r2)
                canvas.drawLine(startX, h_2, end, h_2, paint)
                return
            }
        }

        //左圆
        rectF.set(-w_2, -h_2, th - w_2, h_2)
        if (sf < r4) {
            val saOffset = if (sf <= r3) 0f else ((sf - r3) * C * 360f / Math.PI / th).toFloat()

            if (ef > r4) {
                canvas.drawArc(rectF, 90f + saOffset, 180f - saOffset, false, paint)
            } else {
                canvas.drawArc(rectF, 90f + saOffset, 180f * (ef - r3) / (r4 - r3) - saOffset, false, paint)
                return
            }
        }

        //上边线左
        val sxOffset = if (sf <= r4) 0f else C * (sf - r4)

        val startX = h_2 - w_2 - lineAcc + sxOffset
        canvas.drawLine(startX, -h_2, startX + (ef - r4) / r1 * v / 2 + lineAcc - sxOffset, -h_2, paint)
    }


    companion object {
        const val lineAcc = 0.5f
    }
}