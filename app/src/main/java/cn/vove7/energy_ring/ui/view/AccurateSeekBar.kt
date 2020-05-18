package cn.vove7.energy_ring.ui.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.annotation.RequiresApi
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

    val aboveO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    var minVal: Int = 0
        @RequiresApi(Build.VERSION_CODES.O)
        set(value) {
            field = value
            if (aboveO) {
                seek_bar_view.min = value
            } else {
                maxVal = maxVal
            }
        }

    var maxVal: Int = 100
        set(value) {
            field = value
            seek_bar_view.max = if (aboveO) {
                value
            } else {
                value - minVal
            }
        }

    var progress: Int = 0
        set(value) {
            field = value
            seek_bar_view.progress = if (aboveO) value else value - minVal
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.accurate_seek_bar, this)
        val ats = context.obtainStyledAttributes(attrs, R.styleable.AccurateSeekBar)
        title = ats.getString(R.styleable.AccurateSeekBar_title)
        maxVal = ats.getInt(R.styleable.AccurateSeekBar_max, 100)
        minVal = ats.getInt(R.styleable.AccurateSeekBar_min, 0)

        ats.recycle()
        plus_view.setOnClickListener {
            var p = seek_bar_view.progress + 1
            seek_bar_view.progress = p
            p = if (aboveO) p else p + minVal
            onChangeAction?.invoke(p, true)
            onStopAction?.invoke(p)
        }
        minus_view.setOnClickListener {
            var p = seek_bar_view.progress - 1
            seek_bar_view.progress = p
            p = if (aboveO) p else p + minVal
            onChangeAction?.invoke(p, true)
            onStopAction?.invoke(p)
        }
        seek_bar_view.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val p = if (aboveO) progress else progress + minVal
                onChangeAction?.invoke(p, fromUser)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                onStartAction?.invoke()
            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val p = if (aboveO) seekBar.progress else seekBar.progress + minVal
                onStopAction?.invoke(p)
            }
        })
    }

    fun onChange(lis: (progress: Int, fromUser: Boolean) -> Unit) {
        onChangeAction = lis
    }

    private var onStopAction: ((progress: Int) -> Unit)? = null
    private var onStartAction: (() -> Unit)? = null

    private var onChangeAction: ((progress: Int, fromUser: Boolean) -> Unit)? = null

    fun onStart(startAction: () -> Unit) {
        onStartAction = startAction
    }

    fun onStop(stopAction: ((progress: Int) -> Unit)) {
        onStopAction = stopAction
    }

}