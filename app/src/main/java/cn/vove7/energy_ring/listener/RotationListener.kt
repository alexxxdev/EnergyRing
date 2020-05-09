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


/**
 * # RotationListener
 *
 * @author Vove
 * 2020/5/9
 */
object RotationListener : BroadcastReceiver() {

    fun start() {
        val intentFilter = IntentFilter("android.intent.action.CONFIGURATION_CHANGED")
        App.INS.registerReceiver(this, intentFilter)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val wm = App.INS.getSystemService(WindowManager::class.java)!!
        val roa = wm.defaultDisplay.rotation
        wm.defaultDisplay.mode.modeId
        Log.d("Debug :", "onReceive  ----> $roa")

        if (roa == Surface.ROTATION_0) {
            Log.d("Debug :", "onReceive  ----> $roa show")
            FloatRingWindow.show()
        } else {
            Log.d("Debug :", "onReceive  ----> $roa hide")
            FloatRingWindow.hide()
        }

    }
}