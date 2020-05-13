package cn.vove7.energy_ring.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * # NoScrollableViewPager
 *
 * @author Vove
 * 2020/5/11
 */
class NoScrollableViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }
}