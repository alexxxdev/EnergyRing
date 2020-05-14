package cn.vove7.energy_ring.listener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.floatwindow.FloatRingWindow

/**
 * # ScreenListener
 *
 * @author Vove
 * 2020/5/14
 */
object ScreenListener : BroadcastReceiver() {

    fun start() {
        val intentFilter: IntentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        App.INS.registerReceiver(this, intentFilter)
    }

    @JvmStatic
    var screenOn: Boolean = true

    @JvmStatic
    var screenLocked: Boolean = false

    override fun onReceive(context: Context?, intent: Intent?) {

        when (intent?.action) {
            Intent.ACTION_SCREEN_ON -> {
                Log.d("Debug :", "onReceive  ----> 亮屏")
                screenOn = true
            }
            Intent.ACTION_SCREEN_OFF -> {
                Log.d("Debug :", "onReceive  ----> 关屏")
                screenLocked = true
                screenOn = false
                FloatRingWindow.hide()
            }
            Intent.ACTION_USER_PRESENT -> {
                Log.d("Debug :", "onReceive  ----> 解锁")
                FloatRingWindow.show()
                screenLocked = false
            }
        }
    }
}