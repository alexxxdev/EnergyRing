package cn.vove7.energy_ring

import android.app.Application
import android.os.PowerManager
import android.view.WindowManager
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.listener.PowerEventReceiver
import cn.vove7.energy_ring.listener.RotationListener
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
    }

    override fun onCreate() {
        INS = this
        super.onCreate()

        FloatRingWindow.start()
        PowerEventReceiver.start()
        if (Config.autoHideRotate) {
            RotationListener.start()
        }
    }
}
