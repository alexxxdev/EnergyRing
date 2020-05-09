package cn.vove7.energy_ring.floatwindow

import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.R

/**
 * # FullScreenListenerFloatWin
 *
 * @author Vove
 * 2020/5/9
 */
object FullScreenListenerFloatWin {

    private val view by lazy {
        object : View(App.INS) {

            override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
                super.onLayout(changed, left, top, right, bottom)
                val ps = intArrayOf(0, 0)
                getLocationOnScreen(ps)
                if (ps[1] == 0) {//全屏
                    FloatRingWindow.hide()
                } else {
                    FloatRingWindow.show()
                }
            }
        }
    }
    private val layoutParams: WindowManager.LayoutParams
        get() = WindowManager.LayoutParams(
                10, 10,
                100, 0,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                , 0
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
            format = PixelFormat.TRANSLUCENT
            gravity = Gravity.TOP or Gravity.START
        }

    var showing = false

    fun start(wm: WindowManager) {
        if (showing) {
            return
        }
        showing = true
        wm.addView(view, layoutParams)
    }

}