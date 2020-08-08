package cn.vove7.energy_ring.listener

import android.app.Notification
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.annotation.RequiresApi
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.ui.activity.MessageHintActivity
import cn.vove7.energy_ring.util.Config
import cn.vove7.energy_ring.util.inTimeRange
import cn.vove7.energy_ring.util.weakLazy
import java.util.*

/**
 * # NotificationListener
 *
 * 新通知 in SupportedApp
 * postDelay
 *
 * onRemove
 * removePost
 *
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

    private val handler by weakLazy {
        Handler(Looper.getMainLooper())
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        Log.d("Debug :", "onNotificationRemoved  ----> $sbn")
        if (checkCurrentNs() == null) {
            if (MessageHintActivity.isShowing) {
                MessageHintActivity.exit()
            }
        }
    }

    private fun checkCurrentNs(): Unit? {
        return if (activeNotifications.find { it.packageName in Config.notifyApps } == null) {
            Log.d("Debug :", "checkCurrentNs  ----> 取消通知")
            handler.removeCallbacks(showHintRunnable)
            null
        } else {
            Log.d("Debug :", "checkCurrentNs  ----> 存在活跃通知")
            Unit
        }
    }

    private fun checkNeeded(): Unit? {
        if (!Config.notificationListenerEnabled || App.powerManager.isInteractive) {
            Log.d("Debug :", "onNotificationPosted  ----> 未开启 or 开屏")
            return null
        }
        val time = Calendar.getInstance()
        val hour = time.get(Calendar.HOUR_OF_DAY)
        val (begin, end) = Config.doNotDisturbRange

        if (inTimeRange(hour, begin, end)) {
            Log.d("Debug :", "checkNeeded  ----> 勿扰时间段 $hour")
            return null
        }
        return Unit
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return
        checkNeeded() ?: return

        val no = sbn.notification


        val es = no.extras
        val title = es.getString(Notification.EXTRA_TITLE)

        Log.d("Debug :", "onNotificationPosted  package ----> ${sbn.packageName}")
        Log.d("Debug :", "onNotificationPosted  title ----> $title")

        es.keySet().forEach {
            Log.d("Debug :", "onNotificationPosted $it ----> ${es.get(it)}")
        }

        kotlin.runCatching {
            if (sbn.packageName in Config.notifyApps) {
                Log.w("Debug :", "熄屏通知  ----> ${sbn.packageName}")
                handler.postDelayed(showHintRunnable, 6000)
            } else {
                Log.w("Debug :", "不通知  ----> ${sbn.packageName} ")
            }
        }.onFailure {
            it.printStackTrace()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onListenerConnected() {
        Log.d("Debug :", "onListenerConnected  ----> ")
        INS = this
    }

    override fun onListenerDisconnected() {
        Log.d("Debug :", "onListenerDisconnected  ----> ")
        INS = null
    }

    private val showHintRunnable = Runnable {
        checkCurrentNs() ?: return@Runnable
        showHint()
    }

    private fun showHint() {
        checkNeeded() ?: return
        if (isNear()) {
            Log.d("Debug :", "贴近不显示")
            return
        }
        Log.d("Debug :", "showHint  ----> 屏幕关闭 -> 通知")
        startActivity(Intent(this, MessageHintActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    private fun isNear(): Boolean {
        val sm = App.INS.getSystemService(SensorManager::class.java)
        val sensor = sm?.getDefaultSensor(Sensor.TYPE_PROXIMITY) ?: return false
        var isNear = false
        val lock = Object()

        val newLooper = HandlerThread("sensor-lis").run {
            start()
            looper
        }
        val newHandler = Handler(newLooper)
        val lis = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            override fun onSensorChanged(event: SensorEvent?) {
                event ?: return
                Log.d("Debug :", "onSensorChanged  ----> ${event.values?.contentToString()}")

                isNear = try {
                    event.values[0] == 0.0f
                } catch (e: Throwable) {
                    false
                }
                sm.unregisterListener(this)
                synchronized(lock) {
                    lock.notify()
                }
            }
        }
        sm.registerListener(lis, sensor, SensorManager.SENSOR_DELAY_FASTEST, newHandler)
        synchronized(lock) {
            lock.wait()
        }
        newLooper.quitSafely()
        Log.d("Debug :", "isNear  ----> $isNear")
        return isNear
    }

}