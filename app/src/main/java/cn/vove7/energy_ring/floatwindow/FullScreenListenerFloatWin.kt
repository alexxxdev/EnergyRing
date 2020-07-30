package cn.vove7.energy_ring.floatwindow

import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.BuildConfig
import cn.vove7.energy_ring.R
import cn.vove7.energy_ring.listener.RotationListener
import cn.vove7.energy_ring.service.AccService
import cn.vove7.energy_ring.util.Config

/**
 * # FullScreenListenerFloatWin
 *
 * @author Vove
 * 2020/5/9
 */
object FullScreenListenerFloatWin {

    var isFullScreen = false

    private val view by lazy {
        object : View(App.INS) {

            init {
                if (BuildConfig.DEBUG) {
                    setBackgroundColor(R.color.colorPrimary)
                }
            }

            override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

                super.onLayout(changed, left, top, right, bottom)
                val ps = intArrayOf(0, 0)
                getLocationOnScreen(ps)
                isFullScreen = ps[1] == 0
                if (!Config.autoHideFullscreen) {
                    return
                }
                when {
                    isFullScreen -> {//全屏
                        FloatRingWindow.hide()
                    }
                    RotationListener.canShow -> {
                        FloatRingWindow.show()
                    }
                }
            }
        }
    }
    private val layoutParams: WindowManager.LayoutParams
        get() = WindowManager.LayoutParams(
            10, 10,
            100, 0,
            when {
                AccService.hasOpend -> WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else -> WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            , 0
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
            format = PixelFormat.RGBA_8888
            gravity = Gravity.TOP or Gravity.START
        }

    var showing = false

    fun start() {
        if (showing) {
            return
        }
        showing = true
        wm.addView(view, layoutParams)
    }

    private val wm: WindowManager
        get() = AccService.wm ?: App.windowsManager

    fun reload() {
        try {
            showing = false
            wm.removeViewImmediate(view)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        start()
    }

}