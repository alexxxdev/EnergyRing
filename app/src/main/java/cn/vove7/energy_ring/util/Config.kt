package cn.vove7.energy_ring.util

import cn.vove7.energy_ring.model.ShapeType
import cn.vove7.smartkey.BaseConfig
import cn.vove7.smartkey.key.smartKey
import cn.vove7.smartkey.key.smartKeySet
import com.google.gson.annotations.SerializedName
import cn.vove7.smartkey.annotation.Config as SC

/**
 * # Config
 *
 * @author Vove
 * 2020/5/8
 */
@SC("app")
object Config : BaseConfig {

    var doNotDisturbRange by smartKey(0 to 7, key = "do_not_disturb_range")

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

    var colors by smartKey(
            intArrayOf(
                    "#ff00e676".asColor,
                    "#ff64dd17".asColor
            )
    )

    //0 纯色渐变 1 纯色 2 均匀渐变
    var colorMode by smartKey(0)

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


    var notificationListenerEnabled by smartKey(false)

    val devicesWeakLazy = weakLazy {
        listOf(
                ConfigInfo("一加8 Pro", "IN2020", 148, 22, 8f * 2, 0.06736f),
                ConfigInfo("一加8", "IN2010", 116, 27, 6f * 2, 0.07037037f),
                ConfigInfo("小米 10 Pro", "Mi 10 Pro", 176, 20, 7f * 2, 0.07037037f),
                ConfigInfo("小米 10", "Mi 10", 148, 22, 8f * 2, 0.06736f),
                ConfigInfo("vivo Z6", "V1963A", 1764, 21, 8f * 2, 0.06944445f),
                ConfigInfo("vivo Y85", "vivo Y85", 1311, 10, 9f * 2, 0.05462963f),
                ConfigInfo("vivo X30", "V1938CT", 1752, 23, 9f * 2, 0.06481481f),
                ConfigInfo("荣耀20 Pro", "YAL-AL10", 61, 16, 11f * 2, 0.08888889f),
                ConfigInfo("荣耀20", "YAL-AL00", 65, 18, 10f * 2, 0.085185182f),
                ConfigInfo("荣耀20S", "YAL-AL50", 57, 14, 20f * 2, 0.094444446f),
                ConfigInfo("荣耀V20", "PCT-AL10", 91, 19, 5f * 2, 0.075f),
                ConfigInfo("荣耀Play3", "ASK-AL00x", 83, 14, 5f * 2, 0.08611111f),
                ConfigInfo("华为Nova7 SE", "CDY-AN00", 60, 16, 12f * 2, 0.087037034f),
                ConfigInfo("华为Nova3", "PAR-AL00", 1644, 0, 3f * 2, 0.027777778f),
                ConfigInfo("华为Mate30", "TAS-AL00", 148, 22, 8f * 2, 0.06736f),
                ConfigInfo("红米 K30", "Readmi K30", 1545, 22, 8f * 2, 0.06726f),
                ConfigInfo("红米 Note 8 Pro", "Readmi Note 8 Pro", 1752, 7, 8f * 2, 0.075f),
                ConfigInfo("Samsung S20+", "SM-G9860", 938, 10, 7f * 2, 0.062962964f),
                ConfigInfo("Samsung S20", "SM-G9810", 936, 12, 8f * 2, 0.06736f),
                ConfigInfo("Samsung Galaxy Note 10+ 5G", "SM-N9760", 931, 12, 6f * 2, 0.0712963f),
                ConfigInfo("Samsung Galaxy Note 10+", "SM-N975U1", 935, 14, 9f * 2, 0.06736f),
                ConfigInfo("Samsung Galaxy Note 10", "SM-N9700", 924, 11, 6f * 2, 0.07777778f),
                ConfigInfo("Samsung S20 Ultra 5G", "SM-G9880", 91, 23, 8f * 2, 0.08888889f),
                ConfigInfo("Samsung S10", "SM-G9730", 1703, 21, 10f * 2, 0.08958333f),
                ConfigInfo("Samsung S10e", "SM-G9708", 1700, 12, 13f * 2, 0.10833334f),
                ConfigInfo("Samsung A60", "SM-A6060", 88, 22, 10f * 2, 0.08888889f),
                ConfigInfo("VIVO Z5X", "V1911A", 80, 14, 8f * 2, 0.083333336f),
                ConfigInfo("IQOO Neo", "V1936A", 1770, 0, 8f * 2, 0.06111111f),
                ConfigInfo("IQOO Neo3", "V1981A", 1739, 13, 10f * 2, 0.085185185f),
                ConfigInfo("一加7T(配合圆形电池)", "HD1900", 1796, 16, 16f * 2, 0.05277778f),
                ConfigInfo("华为MatePad Pro", "MRX-AL09", 1894, 0, 12f * 2, 0.05625f),
                ConfigInfo("OPPO Ace2", "PDHM00", 117, 28, 5f * 2, 0.06851852f),
                ConfigInfo("OPPO Find X2 Pro", "PDEM30", 148, 22, 8f * 2, 0.06736f),

                ConfigInfo("OPPO Reno", "PCAT00", 1887, 46, 6f * 2, 0.028703704f),
                ConfigInfo("荣耀30S", "CDY-AN90", 62, 19, 23f, 0.08472222f, ShapeType.RING),
                ConfigInfo(
                        "红米K30",
                        "Readmi K30",
                        1567,
                        20,
                        14f,
                        0.07037037f,
                        ShapeType.PILL,
                        spacingWidth = 58
                ),
                ConfigInfo(
                        "红米K30 5G",
                        "Readmi K30 5G",
                        1577,
                        22,
                        9f,
                        0.06481481f,
                        ShapeType.DOUBLE_RING,
                        spacingWidth = 220
                ),
                ConfigInfo("Vivo IQOO Z1", "V1986A", 1740, 21, 21f, 0.085185185f, ShapeType.RING),
                ConfigInfo(
                        "Huawei P40 Pro",
                        "ELS-AN00",
                        120,
                        17,
                        21f,
                        0.1125f,
                        ShapeType.PILL,
                        spacingWidth = 336
                ),
                ConfigInfo(
                        "Huawei V30 Pro",
                        "OXF-AN10",
                        72,
                        27,
                        13f,
                        0.08796296f,
                        ShapeType.PILL,
                        spacingWidth = 252
                )

        )
    }
    val presetDevices by devicesWeakLazy

    var localConfig by smartKey(arrayOf<ConfigInfo>())

    val notifyApps by smartKeySet(
            mutableSetOf(
                    "com.tencent.mobileqq",
                    "com.tencent.tim",
                    "com.tencent.eim",
                    "com.tencent.qqlite",
                    "com.tencent.minihd.qq",
                    "com.tencent.mobileqqi",
                    "com.tencent.mm",
                    "com.alibaba.android.rimet",
                    "com.immomo.momo",
                    "cn.soulapp.android",
                    "im.yixin",
                    "com.hpbr.bosszhipin",
                    "com.eg.android.AlipayGphone"
            )
    )

}

//形状
@Suppress("ArrayInDataClass")
data class ConfigInfo(
        @SerializedName("name", alternate = ["a"])
        var name: String,
        @SerializedName("model", alternate = ["b"])
        val model: String,
        @SerializedName("posxf", alternate = ["c"])
        val posxf: Int,
        @SerializedName("posyf", alternate = ["d"])
        val posyf: Int,
        @SerializedName("strokeWidth", alternate = ["strokeWith"])
        val strokeWidth: Float,
        @SerializedName("sizef", alternate = ["e"])
        val sizef: Float,
        @SerializedName("energyType", alternate = ["f"])
        val energyType: ShapeType? = ShapeType.RING,
        @SerializedName("spacingWidth", alternate = ["g"])
        val spacingWidth: Int = -1,
        @SerializedName("bgColor", alternate = ["h"])
        val bgColor: Int? = null,
        @SerializedName("doubleRingChargingIndex", alternate = ["i"])
        val doubleRingChargingIndex: Int = 0,
        @SerializedName("secondaryRingFeature", alternate = ["j"])
        val secondaryRingFeature: Int? = 0,
        @SerializedName("colors", alternate = ["k"])
        val colors: IntArray? = null,
        @SerializedName("colorMode", alternate = ["l"])
        val colorMode: Int = 0
) {
    companion object {
        fun fromConfig(model: String): ConfigInfo {
            return ConfigInfo(
                    model, model, Config.posXf, Config.posYf, Config.strokeWidthF,
                    Config.sizef, Config.energyType,
                    Config.spacingWidthF, Config.ringBgColor, Config.doubleRingChargingIndex,
                    Config.secondaryRingFeature, Config.colors, Config.colorMode
            )
        }
    }

    fun save() {
        Config.localConfig =
            Config.localConfig.toMutableList().apply { add(this@ConfigInfo) }.toTypedArray()
    }

    fun applyConfig() {
        Config.posXf = this.posxf
        Config.posYf = this.posyf
        Config.sizef = this.sizef
        Config.strokeWidthF = this.strokeWidth
        Config.colorMode = this.colorMode

        this.colors?.also {
            if (it.isNotEmpty()) {
                Config.colors = it
            }
        }

        this.bgColor?.also {
            Config.ringBgColor = it
        }

        when (this.energyType) {
            ShapeType.DOUBLE_RING -> {
                Config.spacingWidthF = this.spacingWidth
                this.secondaryRingFeature?.also {
                    Config.secondaryRingFeature = it
                }
            }
            ShapeType.PILL -> {
                Config.spacingWidthF = this.spacingWidth
            }
        }
    }
}