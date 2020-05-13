package cn.vove7.energy_ring.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.vove7.energy_ring.R
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.listener.PowerEventReceiver
import cn.vove7.energy_ring.listener.RotationListener
import cn.vove7.energy_ring.ui.adapter.ColorsAdapter
import cn.vove7.energy_ring.util.Config
import cn.vove7.energy_ring.util.antiColor
import cn.vove7.energy_ring.util.batteryLevel
import cn.vove7.energy_ring.util.pickColor
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import kotlinx.android.synthetic.main.fragment_double_ring_style.*
import kotlinx.android.synthetic.main.fragment_double_ring_style.bg_color_view
import kotlinx.android.synthetic.main.fragment_double_ring_style.charging_rotateDuration_seek_bar
import kotlinx.android.synthetic.main.fragment_double_ring_style.default_rotateDuration_seek_bar
import kotlinx.android.synthetic.main.fragment_double_ring_style.pick_battery_direction_view
import kotlinx.android.synthetic.main.fragment_double_ring_style.pick_secondary_ring_func_view
import kotlinx.android.synthetic.main.fragment_double_ring_style.posx_seek_bar
import kotlinx.android.synthetic.main.fragment_double_ring_style.posy_seek_bar
import kotlinx.android.synthetic.main.fragment_double_ring_style.size_seek_bar
import kotlinx.android.synthetic.main.fragment_double_ring_style.spacing_seek_bar
import kotlinx.android.synthetic.main.fragment_double_ring_style.strokeWidth_seek_bar
import kotlinx.android.synthetic.main.fragment_double_ring_style.view.*

/**
 * # DoubleRingStyleFragment
 *
 * @author Vove
 * 2020/5/12
 */
class DoubleRingStyleFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_double_ring_style, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        color_list.adapter = ColorsAdapter()

        refreshData()
        listenSeekBar(view)
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun refreshData() {
        bg_color_view?.setBackgroundColor(Config.ringBgColor)
        bg_color_view?.setTextColor(Config.ringBgColor.antiColor)

        strokeWidth_seek_bar?.progress = Config.strokeWidthF.toInt()
        posx_seek_bar?.progress = Config.posXf
        posy_seek_bar?.progress = Config.posYf
        size_seek_bar?.progress = Config.size
        charging_rotateDuration_seek_bar?.progress = (charging_rotateDuration_seek_bar.maxVal + 1 - Config.chargingRotateDuration / 1000)
        default_rotateDuration_seek_bar?.progress = (default_rotateDuration_seek_bar.maxVal + default_rotateDuration_seek_bar.minVal -
                (Config.defaultRotateDuration) / 1000)
        spacing_seek_bar?.progress = Config.spacingWidthF

        val fc by lazy { resources.getStringArray(R.array.ring_features)[Config.secondaryRingFeature] }
        pick_secondary_ring_func_view?.text = getString(R.string.feature_of_secondary_ring, fc)

        val dir by lazy { resources.getStringArray(R.array.double_ring_battery_direction)[Config.doubleRingChargingIndex] }
        pick_battery_direction_view?.text = getString(R.string.battery_direction_format, dir)
        color_list?.adapter?.notifyDataSetChanged()
    }

    private fun listenSeekBar(view: View): Unit = view.run {
        pick_secondary_ring_func_view.setOnClickListener {
            MaterialDialog(context).show {
                listItems(R.array.ring_features, waitForPositiveButton = false) { _, i, _ ->
                    if (Config.secondaryRingFeature != i) {
                        Config.secondaryRingFeature = i
                        val fc = resources.getStringArray(R.array.ring_features)[i]
                        view.pick_secondary_ring_func_view.text = getString(R.string.feature_of_secondary_ring, fc)
                        FloatRingWindow.update()
                    }
                }
            }
        }
        pick_battery_direction_view.setOnClickListener {
            MaterialDialog(context).show {
                listItems(R.array.double_ring_battery_direction, waitForPositiveButton = false) { _, i, _ ->
                    if (Config.doubleRingChargingIndex != i) {
                        Config.doubleRingChargingIndex = i
                        val dir = resources.getStringArray(R.array.double_ring_battery_direction)[i]
                        view.pick_battery_direction_view?.text = getString(R.string.battery_direction_format, dir)
                        FloatRingWindow.update(batteryLevel)
                    }
                }
            }
        }
        bg_color_view.setOnClickListener {
            pickColor(context!!, initColor = Config.ringBgColor) { c ->
                bg_color_view.setBackgroundColor(c)
                bg_color_view.setTextColor(c.antiColor)
                Config.ringBgColor = c
                FloatRingWindow.update()
            }
        }
        charging_rotateDuration_seek_bar?.onStop { progress ->
            Config.chargingRotateDuration = (charging_rotateDuration_seek_bar.maxVal + 1 - progress) * 1000
            if (PowerEventReceiver.isCharging) {
                FloatRingWindow.reloadAnimation()
            }
        }
        default_rotateDuration_seek_bar?.onStop { progress -> //[15,60]
            Config.defaultRotateDuration = (default_rotateDuration_seek_bar.maxVal - (progress - default_rotateDuration_seek_bar.minVal)) * 1000
            if (!PowerEventReceiver.isCharging) {
                FloatRingWindow.reloadAnimation()
            }
        }
        strokeWidth_seek_bar?.onChange { progress, user ->
            if (!user) return@onChange
            Config.strokeWidthF = progress.toFloat()
            FloatRingWindow.update()
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
        size_seek_bar?.onChange { progress, user ->
            if (!user) return@onChange
            Config.size = progress
            FloatRingWindow.update()
        }
        spacing_seek_bar?.onChange { progress, user ->
            if (!user) return@onChange
            Config.spacingWidthF = progress
            FloatRingWindow.update()
        }
    } ?: Unit
}