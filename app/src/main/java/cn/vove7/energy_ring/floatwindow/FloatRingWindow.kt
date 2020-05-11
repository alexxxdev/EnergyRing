package cn.vove7.energy_ring.floatwindow

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.net.Uri
import android.os.BatteryManager
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.Gravity
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.Toast
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.R
import cn.vove7.energy_ring.util.Config
import cn.vove7.energy_ring.view.RingView
import java.lang.Thread.sleep
import kotlin.concurrent.thread


/**
 * # FloatRingWindow
 *
 * @author Vove
 * 2020/5/8
 */
object FloatRingWindow {

    private val hasPermission
        get() = Settings.canDrawOverlays(App.INS)

    lateinit var wm: WindowManager
    fun start(wm: WindowManager) {
        FloatRingWindow.wm = wm
        if (hasPermission) {
            showInternal()
        } else {
            Toast.makeText(App.INS, R.string.request_float_window_permission, Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + App.INS.packageName))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            App.INS.startActivity(intent)
            thread {
                while (!hasPermission) {
                    Log.d("Debug :", "wait p...")
                    sleep(100)
                }
                Log.d("Debug :", "hasPermission")
                if (hasPermission) {
                    Handler(Looper.getMainLooper()).post {
                        showInternal()
                    }
                }
            }
        }
    }

    var isShowing = false
    private val layoutParams: WindowManager.LayoutParams
        get() = WindowManager.LayoutParams(
                Config.size, Config.size,
                Config.posX, Config.posY,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                , 0
        ).apply {
            format = PixelFormat.RGBA_8888
            gravity = Gravity.TOP or Gravity.START
        }

    private val bodyView by lazy {
        FrameLayout(App.INS).apply {
            addView(ringView)
        }
    }
    private val ringView by lazy {
        RingView(App.INS).apply {
            strokeWidth = Config.strokeWidth
            progress = batteryLevel
            doughnutColors = Config.colors
        }
    }

    private fun showInternal() {
        isShowing = true
        FullScreenListenerFloatWin.start(wm)
        try {
            wm.addView(bodyView, layoutParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (charging || isCharging) {
            onCharging()
        }
    }

    fun update(p: Int? = null) {
        if (!isShowing) {
            return
        }
        ringView.apply {
            strokeWidth = Config.strokeWidth
            p?.let { progress = it }
            doughnutColors = Config.colors
        }
        wm.updateViewLayout(bodyView, layoutParams)
    }

    private var charging = false

    fun onCharging() {
        charging = true
        reloadAnimation()
    }


    fun reloadAnimation() {
        if (!isShowing) {
            return
        }
        rotateAnimator?.cancel()
        rotateAnimator = buildAnimator().also {
            it.start()
        }
    }

    var rotateAnimator: Animator? = null

    private fun buildAnimator(
            start: Float = 0f,
            end: Float = 360f,
            rc: Int = -1,
            dur: Int = Config.rotateDuration
    ): Animator {
        return ValueAnimator.ofFloat(start, end).apply {
            repeatCount = rc
            interpolator = LinearInterpolator()
            duration = dur.toLong()
            addUpdateListener {
                ringView.rotation = it.animatedValue as Float
            }
        }
    }

    private fun stopAnimationSmooth() {
        rotateAnimator?.cancel()
        val sa = ringView.rotation
        buildAnimator(start = sa, end = 360f, rc = 0, dur = ((360 - sa) * Config.rotateDuration / 360).toInt()).also {
            it.start()
        }
    }

    fun onDisCharging() {
        charging = false
        stopAnimationSmooth()
    }

    fun hide() {
        if (!isShowing) {
            return
        }
        isShowing = false
        rotateAnimator?.cancel()
        wm.removeView(bodyView)
    }

    fun show() {
        if (!hasPermission || isShowing) {
            return
        }
        showInternal()
    }

    val isCharging: Boolean
        get() = {
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val intent = App.INS.registerReceiver(null, filter)
            val i = intent?.getIntExtra(BatteryManager.EXTRA_STATUS,
                    BatteryManager.BATTERY_STATUS_UNKNOWN) == BatteryManager.BATTERY_STATUS_CHARGING
            Log.d("---", "isCharging ---> $i")
            i
        }.invoke()

    val batteryLevel: Int
        get() {
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val intent = App.INS.registerReceiver(null, filter)
                ?: return 50
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 100) //电量的刻度
            val maxLevel = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100) //最大
            return level * 1000 / maxLevel
        }
}