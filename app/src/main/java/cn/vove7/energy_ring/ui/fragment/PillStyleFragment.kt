package cn.vove7.energy_ring.ui.fragment

import android.view.View
import cn.vove7.energy_ring.R
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.listener.PowerEventReceiver
import cn.vove7.energy_ring.util.Config
import cn.vove7.energy_ring.util.antiColor
import cn.vove7.energy_ring.util.pickColor
import kotlinx.android.synthetic.main.fragment_pill_style.*

/**
 * # PillStyleFragment
 *
 * @author Vove
 * 2020/5/12
 */
class PillStyleFragment : BaseStyleFragment() {
    override val layoutRes: Int
        get() = R.layout.fragment_pill_style

    override fun refreshData() {
        super.refreshData()
        spacing_seek_bar?.progress = Config.spacingWidthF
    }

    override fun listenSeekBar(view: View) {
        super.listenSeekBar(view)
        view.run {
            spacing_seek_bar?.onChange { progress, user ->
                if (!user) return@onChange
                Config.spacingWidthF = progress
                FloatRingWindow.update()
            }
        } ?: Unit
    }

}