package cn.vove7.energy_ring.util

import android.content.res.Configuration
import android.util.DisplayMetrics
import android.util.Size
import android.view.WindowManager
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.floatwindow.FloatRingWindow

/**
 * # utils
 *
 * @author Vove
 * 2020/5/9
 */

val isDarkMode: Boolean
    get() = (App.INS.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES


//正常 rotation 时的高宽
val screenSize: Size by lazy {
    val dm = DisplayMetrics()
    FloatRingWindow.wm.defaultDisplay.getMetrics(dm)

    FloatRingWindow.wm.defaultDisplay.getMetrics(dm)
    val roa = App.INS.getSystemService(WindowManager::class.java)!!.defaultDisplay.rotation
    if (roa == 0 || roa == 2) {
        Size(dm.widthPixels, dm.heightPixels)
    } else {
        Size(dm.heightPixels, dm.widthPixels)
    }
}


val screenWidth: Int get() = screenSize.width
val screenHeight: Int get() = screenSize.height