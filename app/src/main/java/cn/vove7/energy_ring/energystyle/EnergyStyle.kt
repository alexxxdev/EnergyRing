package cn.vove7.energy_ring.energystyle

import android.view.View

interface EnergyStyle {
    val displayView: View

    fun reloadAnimation()

    fun resumeAnimator()

    fun update(progress: Int? = null)

    fun onRemove()

    fun onHide()

    fun pauseAnimator()

    fun setColor(color: Int) {}
}