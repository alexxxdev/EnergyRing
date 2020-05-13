package cn.vove7.energy_ring.listener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * # BootCompleteListener
 *
 * @author Vove
 * 2020/5/12
 */
class BootCompleteListener : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Debug :", "onReceive  ----> 系统启动")
    }
}