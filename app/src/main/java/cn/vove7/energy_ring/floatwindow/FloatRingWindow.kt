package cn.vove7.energy_ring.floatwindow

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.Toast
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.R
import cn.vove7.energy_ring.listener.PowerEventReceiver
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

    val powerManager by lazy {
        App.INS.getSystemService(PowerManager::class.java)!!
    }

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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else WindowManager.LayoutParams.TYPE_PHONE,
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
            strokeWidthF = Config.strokeWidthF
            progress = batteryLevel
            doughnutColors = Config.colors
            bgColor = Config.ringBgColor
        }
    }

    private fun showInternal() {
        isShowing = true
        FullScreenListenerFloatWin.start(wm)
        try {
            bodyView.visibility = View.VISIBLE
            if (bodyView.tag != true) {
                wm.addView(bodyView, layoutParams)
                bodyView.tag = true
                if (charging || isCharging) {
                    PowerEventReceiver.isCharging = true
                    onCharging()
                } else {
                    reloadAnimation(Config.defaultRotateDuration)
                }
            } else {
                rotateAnimator.resume()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun update(p: Int? = null) {
        if (!isShowing) {
            return
        }
        ringView.apply {
            strokeWidthF = Config.strokeWidthF
            p?.let { progress = it }
            doughnutColors = Config.colors
            bgColor = Config.ringBgColor
        }
        wm.updateViewLayout(bodyView, layoutParams)
    }

    private var charging = false

    fun onCharging() {
        charging = true
        reloadAnimation(Config.chargingRotateDuration)
    }


    fun reloadAnimation(speedDuration: Int = 5000) {
        if (!isShowing) {
            return
        }
        if (::rotateAnimator.isInitialized) {
            rotateAnimator.cancel()
        }
        rotateAnimator = buildAnimator(ringView.rotation, speedDuration)
        rotateAnimator.start()
    }

    private lateinit var rotateAnimator: Animator

    private fun buildAnimator(
            start: Float = 0f,
            dur: Int
    ): Animator {
        val end: Float = 360 + start

        var lastUpdate = 0L

        return ValueAnimator.ofFloat(start, end).apply {
            repeatCount = -1
            interpolator = LinearInterpolator()
            duration = dur.toLong()
            addUpdateListener {
                if (!powerManager.isInteractive) {
                    return@addUpdateListener
                }
                if (PowerEventReceiver.isCharging) {
                    ringView.rotation = it.animatedValue as Float
                } else {
                    val now = SystemClock.elapsedRealtime()
                    if (now - lastUpdate > 2000) {
                        lastUpdate = now
                        ringView.rotation = it.animatedValue as Float
                    }
                }
            }
        }
    }

    fun onDisCharging() {
        charging = false
        reloadAnimation(Config.defaultRotateDuration)
    }

    fun hide() {
        if (!isShowing) {
            return
        }
        bodyView.visibility = View.INVISIBLE
        isShowing = false
        rotateAnimator.pause()
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
            val j = intent?.extras?.get("charge_status") == "1"
            Log.d("---", "isCharging ---> $i")
            i || j
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