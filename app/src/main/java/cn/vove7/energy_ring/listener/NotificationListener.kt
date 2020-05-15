package cn.vove7.energy_ring.listener

import android.app.Notification
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import cn.vove7.energy_ring.ui.activity.MessageHintActivity
import cn.vove7.energy_ring.util.Config
import cn.vove7.energy_ring.util.weakLazy

/**
 * # NotificationListener
 *
 * @author Vove
 * 2020/5/14
 */
class NotificationListener : NotificationListenerService() {

    companion object {
        var INS: NotificationListener? = null

        val isConnect get() = INS != null

        val isOpen get() = INS != null && Config.notificationListenerEnabled

        fun stop() {
            Config.notificationListenerEnabled = false
        }

        fun resume() {
            Config.notificationListenerEnabled = true
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        sbn ?: return
    }

    private val handler by weakLazy {
        Handler(Looper.getMainLooper())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (!Config.notificationListenerEnabled || ScreenListener.screenOn) {
            Log.d("Debug :", "onNotificationPosted  ----> 未开启 or 开屏")
            return
        }

        sbn ?: return
        val no = sbn.notification
        val es = no.extras
        val title = es.getString(Notification.EXTRA_TITLE)

        Log.d("Debug :", "onNotificationPosted  package ----> ${sbn.packageName}")
        Log.d("Debug :", "onNotificationPosted  title ----> $title")
        Log.d("Debug :", "onNotificationPosted  channelId----> ${no.channelId}")

        es.keySet().forEach {
            Log.d("Debug :", "onNotificationPosted $it ----> ${es.get(it)}")
        }

        kotlin.runCatching {

            if (no.ledARGB != 0) {
                Log.w("Debug :", "闪光通知  ----> ${no.ledARGB.toString(16)}")
                handler.postDelayed({
                    showHint(no.ledARGB)
                }, 6000)
            } else {
                Log.w("Debug :", "dontShowLights  ----> ${no.ledARGB.toString(16)} ")
            }
        }.onFailure {
            it.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onListenerConnected() {

        Log.d("Debug :", "onListenerConnected  ----> ")

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Toast.makeText(this, "此服务仅8.0+系统可用", Toast.LENGTH_SHORT).show()
            stop()
            return
        }
        INS = this

    }

    override fun onListenerDisconnected() {
        Log.d("Debug :", "onListenerDisconnected  ----> ")
        INS = null
    }

    private fun showHint(led: Int) {
        if (!isOpen || ScreenListener.screenOn) {
            Log.d("Debug :", "showHint  ----> 屏幕开启 or 已关闭 不通知")
            return
        }
        Log.d("Debug :", "showHint  ----> 屏幕关闭 -> 通知")
        startActivity(Intent(this, MessageHintActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("color", led)
        })
    }
}