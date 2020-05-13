package cn.vove7.energy_ring.monitor

/**
 * # DeviceMonitor
 *
 * @author Vove
 * 2020/5/12
 */
interface DeviceMonitor {
    val monitorListener: MonitorListener

    fun onStart()

    fun onStop()

}

interface MonitorListener {
    fun onProgress(progress: Int)
}