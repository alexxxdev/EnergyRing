package cn.vove7.energy_ring.ui.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.R
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.util.Config
import cn.vove7.energy_ring.util.inTimeRange
import kotlinx.android.synthetic.main.activity_message_hint.*
import java.util.*


/**
 * # MessageHintActivity
 *
 * @author Vove
 * 2020/5/14
 */
class MessageHintActivity : AppCompatActivity() {
    companion object {
        val isShowing get() = INS != null
        var INS: MessageHintActivity? = null

        //电源键 亮屏实现
        @SuppressLint("InvalidWakeLockTag", "WakelockTimeout")
        fun stopAndScreenOn() {
            INS?.apply {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    App.keyguardManager.requestDismissKeyguard(this, null)
//                } else {
                val wl = App.powerManager.newWakeLock(
                        PowerManager.ACQUIRE_CAUSES_WAKEUP or
                                PowerManager.SCREEN_DIM_WAKE_LOCK, "cn.vove7.energy_ring.bright")
                wl.acquire()
                wl.release()
//                }
                finish()
            }
        }

        fun exit() {
            INS?.apply {
                finish()
            }
        }
    }

    private val ledColor: Int by lazy {
        intent?.getIntExtra("color", Color.GREEN) ?: Color.GREEN
    }

    private fun initFlags() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lp = window.attributes
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp
        }
        var commonFlags = WindowManager.LayoutParams.FLAG_FULLSCREEN or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            App.keyguardManager.requestDismissKeyguard(this, null)
            window.addFlags(commonFlags)
        } else {
            commonFlags = commonFlags or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            window.addFlags(
                    commonFlags or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }
        val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = uiOptions
    }

    private val screenOnAction by lazy {
        intent?.hasExtra("finish") == true
    }

    private val checkTimer by lazy {
        Timer()
    }

    private val task by lazy {
        object : TimerTask() {
            override fun run() {
                val time = Calendar.getInstance()
                val hour = time.get(Calendar.HOUR_OF_DAY)
                val (b, e) = Config.doNotDisturbRange
                if (inTimeRange(hour, b, e)) {
                    Log.d("Debug :", "checkNeeded  ----> 进入勿扰时间段 $hour")
                    exit()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFlags()

        if (screenOnAction) {
            finish()
            return
        }
        checkTimer.schedule(task, 10 * 60 * 1000)
        setContentView(R.layout.activity_message_hint)
        val cv = findViewById<View>(android.R.id.content)

        var lastClick = 0L
        cv.setOnClickListener {
            val now = SystemClock.elapsedRealtime()
            if (now - lastClick < 200) {
                stopAndScreenOn()
            }
            lastClick = now
        }
        cv.fitsSystemWindows = false
        rootView.fitsSystemWindows = false
        applyRingViewStyle()
        startAnimator()
        INS = this
    }

    private val energyStyle by lazy {
        FloatRingWindow.buildEnergyStyle()
    }

    private fun applyRingViewStyle() {
        energyStyle.update(1000)
        energyStyle.displayView.layoutParams = (energyStyle.displayView.layoutParams as ViewGroup.MarginLayoutParams).apply {
            setMargins(Config.posX, Config.posY, 0, 0)
        }
        energyStyle.setColor(ledColor)
        rootView.addView(energyStyle.displayView)
    }

    private fun startAnimator() {
        energyStyle.displayView.startAnimation(AlphaAnimation(-0.5f, 1.0f).apply {
            duration = 3000
            repeatCount = -1
            repeatMode = Animation.REVERSE
        })
    }

    override fun onDestroy() {
        energyStyle.displayView.animation?.cancel()
        checkTimer.cancel()
        super.onDestroy()
        INS = null
    }

    override fun onBackPressed() {
        App.toast(R.string.double_click_to_exit)
    }
}