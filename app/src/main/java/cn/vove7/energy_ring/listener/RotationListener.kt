package cn.vove7.energy_ring.listener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.Surface
import android.view.WindowManager
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.floatwindow.FullScreenListenerFloatWin


/**
 * # RotationListener
 *
 * @author Vove
 * 2020/5/9
 */
object RotationListener : BroadcastReceiver() {

    var enabled = false
    fun start() {
        val intentFilter = IntentFilter("android.intent.action.CONFIGURATION_CHANGED")
        App.INS.registerReceiver(this, intentFilter)
        enabled = true
    }

    fun stop() {
        enabled = false
        try {
            App.INS.unregisterReceiver(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var rotation = 0

    val canShow: Boolean get() = if (!enabled) true else (rotation == 0)

    override fun onReceive(context: Context?, intent: Intent?) {
        val wm = App.INS.getSystemService(WindowManager::class.java)!!
        val roa = wm.defaultDisplay.rotation
        rotation = roa
        Log.d("Debug :", "onReceive  ----> $roa")

        if (roa == Surface.ROTATION_0 && !FullScreenListenerFloatWin.isFullScreen) {
            Log.d("Debug :", "onReceive  ----> $roa show")
            FloatRingWindow.show()
        } else {
            Log.d("Debug :", "onReceive  ----> $roa hide")
            FloatRingWindow.hide()
        }

    }
}