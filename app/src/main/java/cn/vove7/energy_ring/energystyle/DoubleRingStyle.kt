package cn.vove7.energy_ring.energystyle

import android.util.Log
import android.view.View
import android.widget.LinearLayout
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.monitor.DeviceMonitor
import cn.vove7.energy_ring.monitor.MemoryMonitor
import cn.vove7.energy_ring.monitor.MonitorListener
import cn.vove7.energy_ring.ui.view.RingView
import cn.vove7.energy_ring.util.Config
import cn.vove7.energy_ring.util.getColorByRange
import cn.vove7.energy_ring.util.weakLazy

/**
 * # DoubleRingStyle
 *
 * @author Vove
 * 2020/5/11
 */
class DoubleRingStyle : RotateAnimatorSupporter(), MonitorListener {
    private val ringView1Delegate = weakLazy {
        RingView(App.INS)
    }
    private val ringView1 by ringView1Delegate
    private val ringView2Delegate = weakLazy {
        RingView(App.INS)
    }
    private val ringView2 by ringView2Delegate

    private val spacingViewDelegate = weakLazy {
        View(App.INS)
    }

    private val spacingView by spacingViewDelegate

    override val displayView: View by lazy {
        LinearLayout(App.INS).apply {
            orientation = LinearLayout.HORIZONTAL
            addView(ringView1, Config.size, Config.size)
            addView(spacingView, Config.spacingWidth, 0)
            addView(ringView2, Config.size, Config.size)
        }
    }

    var monitor: DeviceMonitor? = null

    private fun buildSecondaryFeature() {
        if (Config.secondaryRingFeature == 0 && monitor is MemoryMonitor) {
            return
        }
        monitor?.onStop()

        monitor = (when (Config.secondaryRingFeature) {
            0 -> MemoryMonitor(this)
            else -> {
                MemoryMonitor(this)
            }
        }).also {
            it.onStart()
        }
    }

    var lastMonitorValue: Int = 0

    override fun onProgress(ps: Int) {
        (if (Config.doubleRingChargingIndex == 0) ringView2 else ringView1).apply {
            Log.d(TAG, "update monitor p ----> ${1 - Config.doubleRingChargingIndex} $ps")
            this.progress = ps
            lastMonitorValue = ps
            mainColor = getColorByRange(this.progressf, Config.colors)
            invalidate()
        }
    }

    override fun setColor(color: Int) {
        arrayOf(ringView1, ringView2).forEach {
            it.mainColor = color
        }
    }

    override fun update(progress: Int?) {
        arrayOf(ringView1, ringView2).forEachIndexed { index, it ->
            it.apply {
                strokeWidthF = Config.strokeWidthF
                if (index == Config.doubleRingChargingIndex) {
                    if (progress != null) {
                        this.progress = progress
                    }
                } else {
                    this.progress = lastMonitorValue
                }
                if (Config.colorMode == 2) {
                    doughnutColors = Config.colors
                } else {
                    mainColor = getColorByRange(this.progressf, Config.colors)
                }
                bgColor = Config.ringBgColor
                reSize(Config.size)
                requestLayout()
            }
        }
        buildSecondaryFeature()

        spacingView.layoutParams = spacingView.layoutParams?.also {
            it.width = Config.spacingWidth
        } ?: (LinearLayout.LayoutParams(Config.spacingWidth, 0))
    }

    override fun onAnimatorUpdate(rotateValue: Float) {
        ringView1.rotation = rotateValue
        ringView2.rotation = rotateValue
    }

    override fun onRemove() {
        super.onRemove()
        monitor?.onStop()
        ringView1Delegate.clearWeakValue()
        spacingViewDelegate.clearWeakValue()
        ringView2Delegate.clearWeakValue()
    }

}