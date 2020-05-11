package cn.vove7.energy_ring.floatwindow

import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.R
import cn.vove7.energy_ring.energystyle.DoubleRingStyle
import cn.vove7.energy_ring.energystyle.EnergyStyle
import cn.vove7.energy_ring.energystyle.PillStyle
import cn.vove7.energy_ring.energystyle.RingStyle
import cn.vove7.energy_ring.model.ShapeType
import cn.vove7.energy_ring.util.Config
import cn.vove7.energy_ring.util.batteryLevel
import cn.vove7.energy_ring.util.weakLazy
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

    private val displayEnergyStyleDelegate = weakLazy {
        when (Config.energyType) {
            ShapeType.RING -> RingStyle()
            ShapeType.DOUBLE_RING -> DoubleRingStyle()
            ShapeType.PILL -> PillStyle()
        } as EnergyStyle
    }
    private val displayEnergyStyle by displayEnergyStyleDelegate

    private val wm: WindowManager = App.windowsManager

    fun start() {
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
            addView(displayEnergyStyle.displayView)
        }
    }

    fun onChangeShapeType() {
        displayEnergyStyle.onRemove()
        displayEnergyStyleDelegate.clearWeakValue()
        bodyView.apply {
            removeAllViews()
            addView(displayEnergyStyle.displayView)
        }
        showInternal()
    }

    private fun showInternal() {
        isShowing = true
        FullScreenListenerFloatWin.start()
        try {
            bodyView.visibility = View.VISIBLE
            displayEnergyStyle.update(batteryLevel)
            if (bodyView.tag != true) {
                wm.addView(bodyView, layoutParams)
                bodyView.tag = true

                reloadAnimation()
            } else {
                displayEnergyStyle.resumeAnimator()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun update(p: Int? = null) {
        if (!isShowing) {
            return
        }
        displayEnergyStyle.update(p)
        wm.updateViewLayout(bodyView, layoutParams)
    }

    fun onCharging() {
        reloadAnimation()
    }


    fun reloadAnimation() {
        displayEnergyStyle.reloadAnimation()
    }

    fun onDisCharging() {
        reloadAnimation()
    }

    fun hide() {
        if (!isShowing) {
            return
        }
        bodyView.visibility = View.INVISIBLE
        isShowing = false
        displayEnergyStyle.onHide()
    }

    fun show() {
        if (!hasPermission || isShowing) {
            return
        }
        showInternal()
    }

}