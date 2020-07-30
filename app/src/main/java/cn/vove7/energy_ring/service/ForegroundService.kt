package cn.vove7.energy_ring.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import cn.vove7.energy_ring.R

/**
 * # ForegroundService
 *
 * @author Vove
 * 2020/5/13
 */
class ForegroundService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(1000, foreNotification)
    }

    private val channel
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel("foreground_service", "前台服务", NotificationManager.IMPORTANCE_MIN).apply {
                setShowBadge(false)
                setSound(null, null)
                enableVibration(false)
                enableLights(false)
            }
        } else null

    private val builder
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val c = channel!!
            getSystemService(NotificationManager::class.java)!!.createNotificationChannel(c)
            NotificationCompat.Builder(this, c.id)
        } else {
            NotificationCompat.Builder(this)
        }

    private val foreNotification
        get() = builder.apply {
//            addAction(0, "唤醒".spanColor(googleBlue), getPendingIntent(VoiceAssistActivity.WAKE_UP))
//            addAction(0, "屏幕助手".spanColor(googleBlue), PendingIntent.getActivity(this@ForegroundService, 0, ScreenAssistActivity.createIntent(delayCapture = true), 0))
            priority = NotificationCompat.PRIORITY_MIN
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setSmallIcon(R.mipmap.ic_launcher)
            setOngoing(true)
            setContentTitle(getString(R.string.foreground_service_title))
        }.build()

}