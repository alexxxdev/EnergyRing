package cn.vove7.energy_ring.energystyle

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.SystemClock
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.annotation.CallSuper
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.listener.PowerEventReceiver
import cn.vove7.energy_ring.listener.ScreenListener
import cn.vove7.energy_ring.util.Config

/**
 * # RotateAnimatorSupporter
 *
 * @author Vove
 * 2020/5/12
 */
abstract class RotateAnimatorSupporter : EnergyStyle {

    val TAG = this::class.java.simpleName

    companion object {
        var lastRotation = 0f
    }

    var rotateAnimator: Animator? = null

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
                lastRotation = it.animatedValue as Float
                FloatRingWindow.checkValid() ?: return@addUpdateListener
                onAnimatorUpdate(lastRotation)
            }
        }
    }

    abstract fun onAnimatorUpdate(rotateValue: Float)

    @CallSuper
    override fun reloadAnimation() {
        rotateAnimator?.cancel()
        //未充电 未自动旋转
        if (!PowerEventReceiver.isCharging && !Config.autoRotateDisCharging) {
            Log.d(TAG, "未充电不自动旋转  ----> ")
            lastRotation = 0f
            onAnimatorUpdate(0f)
            return
        }
        val dur = if (PowerEventReceiver.isCharging) Config.chargingRotateDuration
        else Config.defaultRotateDuration

        Log.d(TAG, "reloadAnimation  ----> dur: $dur")

        rotateAnimator = buildAnimator(
                lastRotation.let { if (it > 360) it - 360 else it }, dur)
        if (!FloatRingWindow.isShowing) {
            return
        }
        rotateAnimator?.start()
    }


    @CallSuper
    override fun resumeAnimator() {
        if (rotateAnimator?.isPaused != true) {
            rotateAnimator?.start()
        } else {
            rotateAnimator?.resume()
        }
    }

    @CallSuper
    override fun onHide() {
        Log.d(TAG, "onHide  ----> $TAG")
        pauseAnimator()
    }

    override fun pauseAnimator() {
        Log.d(TAG, "pauseAnimator  ----> $TAG")
        rotateAnimator?.pause()
    }

    @CallSuper
    override fun onRemove() {
        Log.d(TAG, "onRemove  ----> $TAG")
        rotateAnimator?.cancel()
    }
}