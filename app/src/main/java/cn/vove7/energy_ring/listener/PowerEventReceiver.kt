package cn.vove7.energy_ring.listener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.floatwindow.FloatRingWindow

/**
 * # PowerEventReceiver
 * 充电状态监听
 * fixed 启动App时无法获得当前充电状态
 * @author Vove7
 */
object PowerEventReceiver : BroadcastReceiver() {

    /**
     * 注册广播接收器
     */
    fun start() {
        val intent = App.INS.registerReceiver(this, intentFilter)
        if (intent != null) onReceive(App.INS, intent)//注册时即通知
    }

    private val intentFilter: IntentFilter
        get() = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_BATTERY_OKAY)
            addAction(Intent.ACTION_BATTERY_CHANGED)
        }

    var isCharging: Boolean = false

    private var lastValue = 0

    override fun onReceive(context: Context?, intent: Intent?) {
        //打开充电自动开启唤醒
        when (intent?.action) {
            Intent.ACTION_POWER_CONNECTED -> {//连接充电器
                Log.d("Debug :", "onReceive  ----> onCharging")
                isCharging = true
                FloatRingWindow.onCharging()
            }
            Intent.ACTION_POWER_DISCONNECTED -> {//断开
                Log.d("Debug :", "onReceive  ----> onDisCharging")
                isCharging = false
                FloatRingWindow.onDisCharging()
            }
            Intent.ACTION_BATTERY_LOW -> {//低电量
            }
            Intent.ACTION_BATTERY_CHANGED -> {
                isCharging = true
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) //电量的刻度
                val maxLevel = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1) //最大
                val l = level * 1000 / maxLevel
                if (l != lastValue) {
                    lastValue = l
                    Log.d("Debug :", "onReceive  ----> ACTION_BATTERY_CHANGED $l")
                    FloatRingWindow.update(l)
                }
            }
            Intent.ACTION_BATTERY_OKAY -> {
                Log.d("Debug :", "onReceive  ----> ACTION_BATTERY_OKAY")
                FloatRingWindow.update(1000)
            }
        }

    }

}
