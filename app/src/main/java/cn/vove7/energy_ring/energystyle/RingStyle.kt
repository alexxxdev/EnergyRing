package cn.vove7.energy_ring.energystyle

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.LinearInterpolator
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.listener.PowerEventReceiver
import cn.vove7.energy_ring.util.Config
import cn.vove7.energy_ring.view.RingView

/**
 * # RingStyle
 *
 * @author Vove
 * 2020/5/11
 */
class RingStyle : EnergyStyle {

    override val displayView: View by lazy { RingView(App.INS) }


    private lateinit var rotateAnimator: Animator

    private fun buildAnimator(
            start: Float = 0f,
            dur: Int
    ): Animator {
        val end: Float = 360 + start

        return ValueAnimator.ofFloat(start, end).apply {
            repeatCount = -1
            interpolator = LinearInterpolator()
            duration = dur.toLong()
            addUpdateListener {
                if (!App.powerManager.isInteractive) {
                    return@addUpdateListener
                }
                if (PowerEventReceiver.isCharging) {
                    displayView.rotation = it.animatedValue as Float
                } else {
                    displayView.rotation = it.animatedValue as Float
                }
            }
        }
    }

    override fun resumeAnimator() {
        if (!rotateAnimator.isPaused) {
            rotateAnimator.start()
        } else {
            rotateAnimator.resume()
        }
    }

    override fun onHide() {
        rotateAnimator.pause()
    }

    override fun reloadAnimation() {
        if (::rotateAnimator.isInitialized) {
            rotateAnimator.cancel()
        }
        rotateAnimator = buildAnimator(
                displayView.rotation,
                if (PowerEventReceiver.isCharging) Config.chargingRotateDuration
                else Config.defaultRotateDuration
        )
        if (!FloatRingWindow.isShowing) {
            return
        }
        rotateAnimator.start()
    }

    override fun update(progress: Int?) {
        (displayView as RingView).apply {
            strokeWidthF = Config.strokeWidthF
            if (progress != null) {
                this.progress = progress
            }
            doughnutColors = Config.colors
            bgColor = Config.ringBgColor
        }
    }

    override fun onRemove() {

    }
}