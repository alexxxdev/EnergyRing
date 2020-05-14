package cn.vove7.energy_ring.util

import cn.vove7.energy_ring.model.ShapeType
import cn.vove7.smartkey.annotation.Config
import cn.vove7.smartkey.key.smartKey
import com.google.gson.annotations.SerializedName

/**
 * # Config
 *
 * @author Vove
 * 2020/5/8
 */
@Config("app")
object Config {

    var energyType by smartKey(ShapeType.RING)

    val autoRotateDisCharging get() = defaultRotateDuration != 180000

    var autoHideRotate by smartKey(true)
    var autoHideFullscreen by smartKey(true)

    //充电旋转速度 跟随功率？
    var chargingRotateDuration by smartKey(3000)

    //默认旋转速度
    var defaultRotateDuration by smartKey(1200000)

    var ringBgColor by smartKey("#a0fffde7".asColor)

    //第二圆环功能
    var secondaryRingFeature by smartKey(0)

    //电池图标位置
    var doubleRingChargingIndex by smartKey(0)

    //粗细百分比
    var strokeWidthF by smartKey(12f)

    var colors by smartKey(intArrayOf(
            "#ff00e676".asColor,
            "#ff64dd17".asColor
    ))

    //2千分比值
    var posXf by smartKey(148)
    val posX get() = ((posXf / 2000f) * screenWidth).toInt()

    //2千分比值
    var posYf by smartKey(22)
    val posY get() = ((posYf / 2000f) * screenHeight).toInt()

    var spacingWidthF by smartKey(10)
    val spacingWidth get() = ((spacingWidthF / 2000f) * screenWidth).toInt()

    //ring size pill 高度
    var sizef by smartKey(0.06736f)

    var size: Int
        get() = ((sizef * screenWidth).toInt())
        set(value) {
            sizef = value.toFloat() / screenWidth
        }

    var tipOfRecent by smartKey(true)

    val devicesWeakLazy = weakLazy {
        listOf(
                Info("一加8 Pro", "IN2020", 148, 22, 8f * 2, 0.06736f),
                Info("一加8", "IN2010", 116, 27, 6f * 2, 0.07037037f),
                Info("小米 10 Pro", "Mi 10 Pro", 176, 20, 7f * 2, 0.07037037f),
                Info("小米 10", "Mi 10", 148, 22, 8f * 2, 0.06736f),
                Info("vivo Z6", "V1963A", 1764, 21, 8f * 2, 0.06944445f),
                Info("vivo Y85", "vivo Y85", 1311, 10, 9f * 2, 0.05462963f),
                Info("vivo X30", "V1938CT", 1752, 23, 9f * 2, 0.06481481f),
                Info("荣耀20 Pro", "YAL-AL10", 61, 16, 11f * 2, 0.08888889f),
                Info("荣耀20", "YAL-AL00", 65, 18, 10f * 2, 0.085185182f),
                Info("荣耀20S", "YAL-AL50", 57, 14, 20f * 2, 0.094444446f),
                Info("荣耀V20", "PCT-AL10", 91, 19, 5f * 2, 0.075f),
                Info("荣耀Play3", "ASK-AL00x", 83, 14, 5f * 2, 0.08611111f),
                Info("华为Nova7 SE", "CDY-AN00", 60, 16, 12f * 2, 0.087037034f),
                Info("华为Nova3", "PAR-AL00", 1644, 0, 3f * 2, 0.027777778f),
                Info("华为Mate30", "TAS-AL00", 148, 22, 8f * 2, 0.06736f),
                Info("红米 K30", "Readmi K30", 1545, 22, 8f * 2, 0.06726f),
                Info("红米 Note 8 Pro", "Readmi Note 8 Pro", 1752, 7, 8f * 2, 0.075f),
                Info("Samsung S20+", "SM-G9860", 938, 10, 7f * 2, 0.062962964f),
                Info("Samsung S20", "SM-G9810", 936, 12, 8f * 2, 0.06736f),
                Info("Samsung Galaxy Note 10+ 5G", "SM-N9760", 931, 12, 6f * 2, 0.0712963f),
                Info("Samsung Galaxy Note 10+", "SM-N975U1", 935, 14, 9f * 2, 0.06736f),
                Info("Samsung Galaxy Note 10", "SM-N9700", 924, 11, 6f * 2, 0.07777778f),
                Info("Samsung S20 Ultra 5G", "SM-G9880", 91, 23, 8f * 2, 0.08888889f),
                Info("Samsung S10", "SM-G9730", 1703, 21, 10f * 2, 0.08958333f),
                Info("Samsung S10e", "SM-G9708", 1700, 12, 13f * 2, 0.10833334f),
                Info("Samsung A60", "SM-A6060", 88, 22, 10f * 2, 0.08888889f),
                Info("VIVO Z5X", "V1911A", 80, 14, 8f * 2, 0.083333336f),
                Info("IQOO Neo", "V1936A", 1770, 0, 8f * 2, 0.06111111f),
                Info("IQOO Neo3", "V1981A", 1739, 13, 10f * 2, 0.085185185f),
                Info("一加7T(配合圆形电池)", "HD1900", 1796, 16, 16f * 2, 0.05277778f),
                Info("华为MatePad Pro", "MRX-AL09", 1894, 0, 12f * 2, 0.05625f),
                Info("OPPO Ace2", "PDHM00", 117, 28, 5f * 2, 0.06851852f),
                Info("OPPO Find X2 Pro", "PDEM30", 148, 22, 8f * 2, 0.06736f),
                Info("OPPO Reno", "PCAT00", 1887, 46, 6f * 2, 0.028703704f)
        )
    }
    val presetDevices by devicesWeakLazy

    var localConfig by smartKey(arrayOf<Info>())

    //形状
    @Suppress("ArrayInDataClass")
    data class Info(
            var name: String,
            val model: String,
            val posxf: Int,
            val posyf: Int,
            @SerializedName("strokeWidth", alternate = ["strokeWith"])
            val strokeWidth: Float,
            val sizef: Float,
            val energyType: ShapeType? = ShapeType.RING,
            val spacingWidth: Int = -1,
            val bgColor: Int? = null,
            val doubleRingChargingIndex: Int = 0,
            val secondaryRingFeature: Int? = 0,
            val colors: IntArray? = null
    ) {
        companion object {
            fun fromConfig(model: String): Info {
                return Info(
                        model, model, posXf, posYf, strokeWidthF, sizef, energyType,
                        spacingWidthF, ringBgColor, doubleRingChargingIndex, secondaryRingFeature,
                        colors
                )
            }
        }

        fun save() {
            localConfig = localConfig.toMutableList().apply { add(this@Info) }.toTypedArray()
        }
    }
}