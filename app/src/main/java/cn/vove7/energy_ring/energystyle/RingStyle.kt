package cn.vove7.energy_ring.energystyle

import android.view.View
import android.widget.FrameLayout
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.ui.view.RingView
import cn.vove7.energy_ring.util.Config
import cn.vove7.energy_ring.util.getColorByRange
import cn.vove7.energy_ring.util.weakLazy

/**
 * # RingStyle
 *
 * @author Vove
 * 2020/5/11
 */
class RingStyle : EnergyStyle, RotateAnimatorSupporter() {

    private val ringViewDelegate = weakLazy {
        RingView(App.INS).apply {
            layoutParams = FrameLayout.LayoutParams(Config.size, Config.size)
        }
    }

    override val displayView: View by ringViewDelegate

    override fun onAnimatorUpdate(rotateValue: Float) {
        displayView.rotation = rotateValue
    }

    override fun setColor(color: Int) {
        (displayView as RingView).apply {
            mainColor = color
        }
    }

    override fun update(progress: Int?) {
        (displayView as RingView).apply {
            strokeWidthF = Config.strokeWidthF
            if (progress != null) {
                this.progress = progress
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

    override fun onRemove() {
        super.onRemove()
        ringViewDelegate.clearWeakValue()
    }
}