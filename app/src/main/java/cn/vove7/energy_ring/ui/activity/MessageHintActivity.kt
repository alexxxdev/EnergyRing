package cn.vove7.energy_ring.ui.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.SystemClock
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.R
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.service.LockScreenService
import cn.vove7.energy_ring.util.Config
import kotlinx.android.synthetic.main.activity_message_hint.*


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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    App.keyguardManager.requestDismissKeyguard(this, null)
                } else {
                    //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
                    val wl = App.powerManager.newWakeLock(
                            PowerManager.ACQUIRE_CAUSES_WAKEUP or
                                    PowerManager.SCREEN_DIM_WAKE_LOCK, "cn.vove7.energy_ring.bright")
                    //点亮屏幕
                    wl.acquire()
                    //释放
                    wl.release()
                }
                INS?.finish()
            }
        }

        fun cancel() {
            INS?.apply {
                finish()
                LockScreenService.screenOff()
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
    }

    private val screenOnAction by lazy {
        intent?.hasExtra("finish") == true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFlags()

        if (screenOnAction) {
            finish()
            return
        }
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode != 134) {
            Log.d("Debug :", "onKeyDown  ----> ${keyCode}")
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        energyStyle.displayView.animation?.cancel()
        super.onDestroy()
        INS = null
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }
}