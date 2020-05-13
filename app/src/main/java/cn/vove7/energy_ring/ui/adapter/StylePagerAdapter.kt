package cn.vove7.energy_ring.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import cn.vove7.energy_ring.ui.fragment.DoubleRingStyleFragment
import cn.vove7.energy_ring.ui.fragment.PillStyleFragment
import cn.vove7.energy_ring.ui.fragment.RingStyleFragment

/**
 * # StylePagerAdapter
 *
 * @author Vove
 * 2020/5/11
 */
class StylePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fs =
        listOf(
                RingStyleFragment(),
                DoubleRingStyleFragment(),
                PillStyleFragment()
        )

    override fun getItem(position: Int): Fragment = fs[position]

    override fun getCount(): Int = fs.size
}