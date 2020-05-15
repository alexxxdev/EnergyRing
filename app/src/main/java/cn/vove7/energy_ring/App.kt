package cn.vove7.energy_ring

import android.app.Application
import android.app.KeyguardManager
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.view.WindowManager
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.listener.PowerEventReceiver
import cn.vove7.energy_ring.listener.RotationListener
import cn.vove7.energy_ring.listener.ScreenListener
import cn.vove7.energy_ring.util.Config

/**
 * Created by 11324 on 2020/5/8
 */
class App : Application() {

    companion object {
        lateinit var INS: App

        val powerManager by lazy {
            INS.getSystemService(PowerManager::class.java)!!
        }
        val windowsManager by lazy {
            INS.getSystemService(WindowManager::class.java)!!
        }
        val keyguardManager by lazy {
            INS.getSystemService(KeyguardManager::class.java)!!
        }
    }

    override fun onCreate() {
        INS = this
        super.onCreate()

        FloatRingWindow.start()
        ScreenListener.start()
        PowerEventReceiver.start()
        if (Config.autoHideRotate) {
            RotationListener.start()
        }

        val foreService = Intent(this, ForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(foreService)
        } else {
            startService(foreService)
        }
    }

    override fun startActivity(intent: Intent?) {
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        super.startActivity(intent)
    }
}
