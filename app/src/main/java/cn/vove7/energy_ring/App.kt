package cn.vove7.energy_ring

import android.app.Application
import android.app.KeyguardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.StringRes
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.listener.PowerEventReceiver
import cn.vove7.energy_ring.listener.PowerSaveModeListener
import cn.vove7.energy_ring.listener.RotationListener
import cn.vove7.energy_ring.listener.ScreenListener
import cn.vove7.energy_ring.util.Config
import cn.vove7.smartkey.android.AndroidSettings
import cn.vove7.smartkey.get
import cn.vove7.energy_ring.service.ForegroundService
import kotlin.concurrent.thread

/**
 * Created by 11324 on 2020/5/8
 */
class App : Application() {

    companion object {
        fun toast(msg: String, dur: Int = Toast.LENGTH_SHORT) {
            Toast.makeText(INS, msg, dur).show()
        }

        fun toast(@StringRes sId: Int, dur: Int = Toast.LENGTH_SHORT) {
            Toast.makeText(INS, sId, dur).show()
        }

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
        PowerSaveModeListener.start(this)

        val foreService = Intent(this, ForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(foreService)
        } else {
            startService(foreService)
        }
        AndroidSettings.init(this)

        if ("app_version_code" in Config) {
            val lastVersion = Config["app_version_code", 0]
            if (BuildConfig.VERSION_CODE > lastVersion) {
                onNewVersion(lastVersion, BuildConfig.VERSION_CODE)
                Config["app_version_code"] = BuildConfig.VERSION_CODE
            }
        } else {
            Config["app_version_code"] = BuildConfig.VERSION_CODE
            onFirstLaunch()
        }

    }

    private fun onFirstLaunch() = thread {
        initSmsApp2NotifyApps()
        initPhone2NotifyApps()
    }

    private fun initPhone2NotifyApps() {
        val i = Intent(Intent.ACTION_CALL, Uri.parse("tel:123"))
        val ri = packageManager.resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY)

        Log.d("Debug :", "sms  ----> $ri")
        ri?.activityInfo?.packageName?.also { smsPkg ->
            Log.d("Debug :", "拨号应用  ----> $smsPkg")
            Config.notifyApps.add(smsPkg)
        }
    }

    private fun initSmsApp2NotifyApps() {
        val i = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:123"))
        val ri = packageManager.resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY)

        Log.d("Debug :", "sms  ----> $ri")
        ri?.activityInfo?.packageName?.also { smsPkg ->
            Log.d("Debug :", "短信应用  ----> $smsPkg")
            Config.notifyApps.add(smsPkg)
        }
    }

    private fun onNewVersion(lastVersion: Int, newVersion: Int) {
        if (lastVersion <= 20401 && newVersion >= 20402) {
            thread { initPhone2NotifyApps() }
        }
    }

    override fun startActivity(intent: Intent?) {
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        super.startActivity(intent)
    }
}
