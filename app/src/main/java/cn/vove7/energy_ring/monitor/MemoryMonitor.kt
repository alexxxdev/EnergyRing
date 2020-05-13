package cn.vove7.energy_ring.monitor

import android.util.Log
import java.io.File

/**
 * # MemoryMonitor
 *
 * @author Vove
 * 2020/5/12
 */
class MemoryMonitor(override val monitorListener: MonitorListener) : TimerMonitor() {

    override fun getProgress(): Int {
        Log.d(TAG, "getProgress  ----> $TAG")
        val ms = File("/proc/meminfo").readLines()
        val map = ms.mapNotNull { line ->
            line.split(":").let { ss ->
                if (ss.size == 2) {
                    val (k, v) = ss
                    k.trim() to v.trim(' ', 'k', 'b', 'B')
                } else null
            }
        }.toMap()
        val total = (map["MemTotal"] ?: "1000").toDouble()
        val free = (map["MemAvailable"] ?: "0").toDouble()

        val p = ((total - free) / total * 1000).toInt()
        Log.d(TAG, "getProgress  ----> $p/1000")
        return p
    }

    override val period: Long get() = 6000L
}