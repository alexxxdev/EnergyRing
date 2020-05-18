package cn.vove7.energy_ring.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.listener.PowerEventReceiver
import cn.vove7.energy_ring.ui.adapter.ColorsAdapter
import cn.vove7.energy_ring.util.Config
import cn.vove7.energy_ring.util.antiColor
import cn.vove7.energy_ring.util.pickColor
import kotlinx.android.synthetic.main.fragment_double_ring_style.*
import kotlinx.android.synthetic.main.fragment_double_ring_style.view.*

/**
 * # BaseStyleFragment
 *
 * @author Vove
 * 2020/5/14
 */
abstract class BaseStyleFragment : Fragment() {
    abstract val layoutRes: Int

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutRes, container, false)
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        color_list.adapter = ColorsAdapter()
        refreshData()
        listenSeekBar(view)
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    @CallSuper
    open fun refreshData() = view.run {
        bg_color_view?.setBackgroundColor(Config.ringBgColor)
        bg_color_view?.setTextColor(Config.ringBgColor.antiColor)
        color_list?.adapter?.notifyDataSetChanged()
        strokeWidth_seek_bar?.progress = Config.strokeWidthF.toInt()

        posx_seek_bar?.progress = Config.posXf
        posy_seek_bar?.progress = Config.posYf
        size_seek_bar?.progress = Config.size

        charging_rotateDuration_seek_bar?.progress = (charging_rotateDuration_seek_bar.maxVal + 1 - Config.chargingRotateDuration / 1000)
        default_rotateDuration_seek_bar?.progress = (default_rotateDuration_seek_bar.maxVal + default_rotateDuration_seek_bar.minVal -
                (Config.defaultRotateDuration) / 1000)

        spacing_seek_bar?.progress = Config.spacingWidthF
    }

    @CallSuper
    open fun listenSeekBar(view: View): Unit = view.run {

        bg_color_view.setOnClickListener {
            pickColor(context!!, initColor = Config.ringBgColor) { c ->
                bg_color_view.setBackgroundColor(c)
                bg_color_view.setTextColor(c.antiColor)
                Config.ringBgColor = c
                FloatRingWindow.onShapeTypeChanged()
            }
        }
        charging_rotateDuration_seek_bar?.onStop { progress ->
            Config.chargingRotateDuration = (charging_rotateDuration_seek_bar.maxVal + 1 - progress) * 1000
            if (PowerEventReceiver.isCharging) {
                FloatRingWindow.reloadAnimation()
            }
        }
        default_rotateDuration_seek_bar?.onStop { progress -> //[60,180]
            Config.defaultRotateDuration = (default_rotateDuration_seek_bar.maxVal - (progress - default_rotateDuration_seek_bar.minVal)) * 1000
            Log.d("Debug :", "listenSeekBar  ---->$progress ${Config.defaultRotateDuration}")
            if (!PowerEventReceiver.isCharging) {
                FloatRingWindow.reloadAnimation()
            }
        }
        strokeWidth_seek_bar?.onChange { progress, user ->
            if (!user) return@onChange
            Config.strokeWidthF = progress.toFloat()
            FloatRingWindow.update()
        }
        strokeWidth_seek_bar?.onStart {
            FloatRingWindow.forceRefresh()
        }
        posx_seek_bar?.onChange { progress, user ->
            if (!user) return@onChange
            Config.posXf = progress
            FloatRingWindow.update()
        }
        posy_seek_bar?.onChange { progress, user ->
            if (!user) return@onChange
            Config.posYf = progress
            FloatRingWindow.update()
        }
        size_seek_bar?.onStart {
            FloatRingWindow.forceRefresh()
        }
        size_seek_bar?.onChange { progress, user ->
            if (!user) return@onChange
            Config.size = progress
            FloatRingWindow.update()
        }
        spacing_seek_bar?.onStart {
            FloatRingWindow.forceRefresh()
        }
        spacing_seek_bar?.onChange { progress, user ->
            if (!user) return@onChange
            Config.spacingWidthF = progress
            FloatRingWindow.update()
        }
    } ?: Unit

}