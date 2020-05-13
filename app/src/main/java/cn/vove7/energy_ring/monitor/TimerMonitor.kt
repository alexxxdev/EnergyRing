package cn.vove7.energy_ring.monitor

import android.os.Handler
import android.os.Looper
import cn.vove7.energy_ring.util.weakLazy
import java.util.*

/**
 * # TimerMonitor
 *
 * @author Vove
 * 2020/5/12
 */
abstract class TimerMonitor : DeviceMonitor, TimerTask() {
    val TAG = this::class.java.simpleName

    companion object {
        val mainHandler by weakLazy {
            Handler(Looper.getMainLooper())
        }
    }

    abstract fun getProgress(): Int

    abstract val period: Long

    private val timer by lazy { Timer() }

    override fun onStart() {
        timer.schedule(this, 0, period)
    }

    override fun run() {
        val p = getProgress()
        mainHandler.post {
            monitorListener.onProgress(p)
        }
    }

    override fun onStop() {
        timer.cancel()
    }
}