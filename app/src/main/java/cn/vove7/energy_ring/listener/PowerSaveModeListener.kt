package cn.vove7.energy_ring.listener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import android.util.Log
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.util.Config

/**
 * # PowerSaveModeListener
 *
 * @author Vove
 * 2020/9/8
 */
object PowerSaveModeListener : BroadcastReceiver() {

    fun start(ctx: Context) {
        val intentFilter = IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        ctx.registerReceiver(this, intentFilter)
    }

    override fun onReceive(p0: Context?, p1: Intent?) {
        if (!Config.powerSaveHide) {
            Log.d("Debug :", "onReceive  ----> 未开启省电模式隐藏")
            return
        }
        val inSaveMode = App.powerManager.isPowerSaveMode
        Log.d("Debug :", "onReceive  ----> 省电模式状态: $inSaveMode")

        if (inSaveMode) {
            FloatRingWindow.hide()
        } else {
            FloatRingWindow.show()
        }
    }
}