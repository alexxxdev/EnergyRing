package cn.vove7.energy_ring.util

import android.graphics.Color
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.smartkey.annotation.Config
import cn.vove7.smartkey.key.smartKey

/**
 * # Config
 *
 * @author Vove
 * 2020/5/8
 */
@Config("app")
object Config {

    var rotateDuration by smartKey(3000)

    var strokeWidth by smartKey(8f)

    var colors by smartKey(intArrayOf(
            Color.parseColor("#fd3322"),
            Color.parseColor("#0085f4"),
            Color.parseColor("#fdb701"),
            Color.parseColor("#fd3322")
    ))

    //2千分比值
    var posXf by smartKey(148)
    val posX get() = ((posXf / 2000f) * FloatRingWindow.screenSize.width).toInt()

    //2千分比值
    var posYf by smartKey(22)
    val posY get() = ((posYf / 2000f) * FloatRingWindow.screenSize.height).toInt()

    var sizef by smartKey(0.06736f)

    var size: Int
        get() = ((sizef * FloatRingWindow.screenSize.width).toInt())
        set(value) {
            sizef = value.toFloat() / FloatRingWindow.screenSize.width
        }

    var tipOfRecent by smartKey(true)

    val presetDevices by weakLazy {
        listOf(
                Info("一加8 Pro", 148, 22, 8f, 0.06736f),
                Info("Vivo Z6", 1764, 21, 8f, 0.06944445f)
        )
    }

    data class Info(
            val name: String,
            val posxf: Int,
            val posyf: Int,
            val strokeWith: Float,
            val sizef: Float
    ) {
        companion object {
            fun fromConfig(name: String): Info {
                return Info(
                        name, posXf, posYf, strokeWidth, sizef
                )
            }
        }
    }
}