package cn.vove7.energy_ring.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.SeekBar
import cn.vove7.energy_ring.R
import kotlinx.android.synthetic.main.accurate_seek_bar.view.*

/**
 * # AccurateSeekBar
 *
 * @author Vove
 * 2020/5/9
 */
class AccurateSeekBar @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var title: CharSequence? = null
        set(value) {
            field = value
            title_view.text = value
        }
    var minVal: Int = 0
        set(value) {
            field = value
            seek_bar_view.min = value
        }

    var maxVal: Int = 100
        set(value) {
            field = value
            seek_bar_view.max = value
        }

    var progress: Int = 0
        set(value) {
            field = value
            seek_bar_view.progress = value
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.accurate_seek_bar, this)
        val ats = context.obtainStyledAttributes(attrs, R.styleable.AccurateSeekBar)
        title = ats.getString(R.styleable.AccurateSeekBar_title)
        minVal = ats.getInt(R.styleable.AccurateSeekBar_min, 0)
        maxVal = ats.getInt(R.styleable.AccurateSeekBar_max, 100)

        ats.recycle()
        plus_view.setOnClickListener {
            seek_bar_view.progress = seek_bar_view.progress + 1
        }
        minus_view.setOnClickListener {
            seek_bar_view.progress = seek_bar_view.progress - 1
        }
    }

    fun onChange(lis: (progress: Int, fromUser: Boolean) -> Unit) {
        seek_bar_view.onChange(lis)
    }

    private fun SeekBar.onChange(lis: (progress: Int, fromUser: Boolean) -> Unit) {
        setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                lis(progress, fromUser)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

    }
}