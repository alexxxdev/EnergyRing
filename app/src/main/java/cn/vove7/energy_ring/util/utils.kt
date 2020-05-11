package cn.vove7.energy_ring.util

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.os.BatteryManager
import android.util.DisplayMetrics
import android.util.Log
import android.util.Size
import android.view.WindowManager
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.colorChooser

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
    App.windowsManager.defaultDisplay.getMetrics(dm)
    val roa = App.INS.getSystemService(WindowManager::class.java)!!.defaultDisplay.rotation
    if (roa == 0 || roa == 2) {
        Size(dm.widthPixels, dm.heightPixels)
    } else {
        Size(dm.heightPixels, dm.widthPixels)
    }
}


val screenWidth: Int get() = screenSize.width
val screenHeight: Int get() = screenSize.height


fun pickColor(context: Context, initColor: Int? = null, title: String = "选取颜色", onColorPick: (color: Int) -> Unit) {
    val colors = mainColors
    MaterialDialog(context).show {
        title(text = title)
        colorChooser(colors, subColors = subColors, allowCustomArgb = true,
                showAlphaSelector = true,
                initialSelection = initColor ?: colors[0],
                waitForPositiveButton = true) { _, c ->
            onColorPick(c)
        }
        positiveButton()
    }
}

val mainColors
    get() = intArrayOf(
            "#D50000".asColor,
            "#C51162".asColor,
            "#AA00FF".asColor,
            "#6200EA".asColor,
            "#304FFE".asColor,
            "#2962FF".asColor,
            "#0091EA".asColor,
            "#00B8D4".asColor,
            "#00BFA5".asColor,
            "#00C853".asColor,
            "#64DD17".asColor,
            "#AEEA00".asColor,
            "#FFD600".asColor,
            "#FFAB00".asColor,
            "#FF6D00".asColor,
            "#DD2C00".asColor,
            "#3E2723".asColor,
            "#212121".asColor,
            "#263238".asColor,
            "#000000".asColor,
            "#FFFFFF".asColor
    )

val subColors
    get() = arrayOf(
            intArrayOf("#FFEBEE".asColor, "#FFCDD2".asColor, "#EF9A9A".asColor, "#E57373".asColor, "#EF5350".asColor, "#F44336".asColor, "#E53935".asColor, "#D32F2F".asColor, "#C62828".asColor, "#B71C1C".asColor, "#FF8A80".asColor, "#FF5252".asColor, "#FF1744".asColor, "#D50000".asColor),
            intArrayOf("#FCE4EC".asColor, "#F8BBD0".asColor, "#F48FB1".asColor, "#F06292".asColor, "#EC407A".asColor, "#E91E63".asColor, "#D81B60".asColor, "#C2185B".asColor, "#AD1457".asColor, "#880E4F".asColor, "#FF80AB".asColor, "#FF4081".asColor, "#F50057".asColor, "#C51162".asColor),
            intArrayOf("#F3E5F5".asColor, "#E1BEE7".asColor, "#CE93D8".asColor, "#BA68C8".asColor, "#AB47BC".asColor, "#9C27B0".asColor, "#8E24AA".asColor, "#7B1FA2".asColor, "#6A1B9A".asColor, "#4A148C".asColor, "#EA80FC".asColor, "#E040FB".asColor, "#D500F9".asColor, "#AA00FF".asColor),
            intArrayOf("#EDE7F6".asColor, "#D1C4E9".asColor, "#B39DDB".asColor, "#9575CD".asColor, "#7E57C2".asColor, "#673AB7".asColor, "#5E35B1".asColor, "#512DA8".asColor, "#4527A0".asColor, "#311B92".asColor, "#B388FF".asColor, "#7C4DFF".asColor, "#651FFF".asColor, "#6200EA".asColor),
            intArrayOf("#E8EAF6".asColor, "#C5CAE9".asColor, "#9FA8DA".asColor, "#7986CB".asColor, "#5C6BC0".asColor, "#3F51B5".asColor, "#3949AB".asColor, "#303F9F".asColor, "#283593".asColor, "#1A237E".asColor, "#8C9EFF".asColor, "#536DFE".asColor, "#3D5AFE".asColor, "#304FFE".asColor),
            intArrayOf("#E3F2FD".asColor, "#BBDEFB".asColor, "#90CAF9".asColor, "#64B5F6".asColor, "#42A5F5".asColor, "#2196F3".asColor, "#1E88E5".asColor, "#1976D2".asColor, "#1565C0".asColor, "#0D47A1".asColor, "#82B1FF".asColor, "#448AFF".asColor, "#2979FF".asColor, "#2962FF".asColor),
            intArrayOf("#E1F5FE".asColor, "#B3E5FC".asColor, "#81D4FA".asColor, "#4FC3F7".asColor, "#29B6F6".asColor, "#03A9F4".asColor, "#039BE5".asColor, "#0288D1".asColor, "#0277BD".asColor, "#01579B".asColor, "#80D8FF".asColor, "#40C4FF".asColor, "#00B0FF".asColor, "#0091EA".asColor),
            intArrayOf("#E0F7FA".asColor, "#B2EBF2".asColor, "#80DEEA".asColor, "#4DD0E1".asColor, "#26C6DA".asColor, "#00BCD4".asColor, "#00ACC1".asColor, "#0097A7".asColor, "#00838F".asColor, "#006064".asColor, "#84FFFF".asColor, "#18FFFF".asColor, "#00E5FF".asColor, "#00B8D4".asColor),
            intArrayOf("#E0F2F1".asColor, "#B2DFDB".asColor, "#80CBC4".asColor, "#4DB6AC".asColor, "#26A69A".asColor, "#009688".asColor, "#00897B".asColor, "#00796B".asColor, "#00695C".asColor, "#004D40".asColor, "#A7FFEB".asColor, "#64FFDA".asColor, "#1DE9B6".asColor, "#00BFA5".asColor),
            intArrayOf("#E8F5E9".asColor, "#C8E6C9".asColor, "#A5D6A7".asColor, "#81C784".asColor, "#66BB6A".asColor, "#4CAF50".asColor, "#43A047".asColor, "#388E3C".asColor, "#2E7D32".asColor, "#1B5E20".asColor, "#B9F6CA".asColor, "#69F0AE".asColor, "#00E676".asColor, "#00C853".asColor),
            intArrayOf("#F1F8E9".asColor, "#DCEDC8".asColor, "#C5E1A5".asColor, "#AED581".asColor, "#9CCC65".asColor, "#8BC34A".asColor, "#7CB342".asColor, "#689F38".asColor, "#558B2F".asColor, "#33691E".asColor, "#CCFF90".asColor, "#B2FF59".asColor, "#76FF03".asColor, "#64DD17".asColor),
            intArrayOf("#F9FBE7".asColor, "#F0F4C3".asColor, "#E6EE9C".asColor, "#DCE775".asColor, "#D4E157".asColor, "#CDDC39".asColor, "#C0CA33".asColor, "#AFB42B".asColor, "#9E9D24".asColor, "#827717".asColor, "#F4FF81".asColor, "#EEFF41".asColor, "#C6FF00".asColor, "#AEEA00".asColor),
            intArrayOf("#FFFDE7".asColor, "#FFF9C4".asColor, "#FFF59D".asColor, "#FFF176".asColor, "#FFEE58".asColor, "#FFEB3B".asColor, "#FDD835".asColor, "#FBC02D".asColor, "#F9A825".asColor, "#F57F17".asColor, "#FFFF8D".asColor, "#FFFF00".asColor, "#FFEA00".asColor, "#FFD600".asColor),
            intArrayOf("#FFF8E1".asColor, "#FFECB3".asColor, "#FFE082".asColor, "#FFD54F".asColor, "#FFCA28".asColor, "#FFC107".asColor, "#FFB300".asColor, "#FFA000".asColor, "#FF8F00".asColor, "#FF6F00".asColor, "#FFE57F".asColor, "#FFD740".asColor, "#FFC400".asColor, "#FFAB00".asColor),
            intArrayOf("#FFF3E0".asColor, "#FFE0B2".asColor, "#FFCC80".asColor, "#FFB74D".asColor, "#FFA726".asColor, "#FF9800".asColor, "#FB8C00".asColor, "#F57C00".asColor, "#EF6C00".asColor, "#E65100".asColor, "#FFD180".asColor, "#FFAB40".asColor, "#FF9100".asColor, "#FF6D00".asColor),
            intArrayOf("#FBE9E7".asColor, "#FFCCBC".asColor, "#FFAB91".asColor, "#FF8A65".asColor, "#FF7043".asColor, "#FF5722".asColor, "#F4511E".asColor, "#E64A19".asColor, "#D84315".asColor, "#BF360C".asColor, "#FF9E80".asColor, "#FF6E40".asColor, "#FF3D00".asColor, "#DD2C00".asColor),
            intArrayOf("#EFEBE9".asColor, "#D7CCC8".asColor, "#BCAAA4".asColor, "#A1887F".asColor, "#8D6E63".asColor, "#795548".asColor, "#6D4C41".asColor, "#5D4037".asColor, "#4E342E".asColor, "#3E2723".asColor),
            intArrayOf("#FAFAFA".asColor, "#F5F5F5".asColor, "#EEEEEE".asColor, "#E0E0E0".asColor, "#BDBDBD".asColor, "#9E9E9E".asColor, "#757575".asColor, "#616161".asColor, "#424242".asColor, "#212121".asColor),
            intArrayOf("#ECEFF1".asColor, "#CFD8DC".asColor, "#B0BEC5".asColor, "#90A4AE".asColor, "#78909C".asColor, "#607D8B".asColor, "#546E7A".asColor, "#455A64".asColor, "#37474F".asColor, "#263238".asColor),
            intArrayOf("#000000".asColor), intArrayOf("#FFFFFF".asColor)

    )

val String.asColor get() = Color.parseColor(this)

val Int.antiColor: Int
    get() {
        val anc = Color.WHITE - (this and 0x00ffffff)
        return if (this shr 24 < 30f) {
            if (isDarkMode) Color.WHITE else Color.BLACK
        } else anc
    }

val isOnCharging: Boolean
    get() = {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val intent = App.INS.registerReceiver(null, filter)
        val i = intent?.getIntExtra(BatteryManager.EXTRA_STATUS,
                BatteryManager.BATTERY_STATUS_UNKNOWN) == BatteryManager.BATTERY_STATUS_CHARGING
        val j = intent?.extras?.get("charge_status") == "1"
        Log.d("---", "isCharging ---> $i")
        i || j
    }.invoke()

val batteryLevel: Int
    get() {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val intent = App.INS.registerReceiver(null, filter)
            ?: return 50
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 100) //电量的刻度
        val maxLevel = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100) //最大
        return level * 1000 / maxLevel
    }