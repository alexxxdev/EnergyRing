package cn.vove7.energy_ring

import android.app.Application
import android.view.WindowManager
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.listener.PowerEventReceiver
import cn.vove7.energy_ring.listener.RotationListener

/**
 * Created by 11324 on 2020/5/8
 */
class App : Application() {

    companion object {
        lateinit var INS: App
    }

    override fun onCreate() {
        INS = this
        super.onCreate()

        val wm = getSystemService(WindowManager::class.java)
        FloatRingWindow.start(wm!!)

        PowerEventReceiver.start()
        RotationListener.start()
    }
}
