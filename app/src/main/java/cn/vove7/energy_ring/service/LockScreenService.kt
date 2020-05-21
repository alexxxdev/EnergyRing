package cn.vove7.energy_ring.service

import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.view.accessibility.AccessibilityEvent

/**
 * # LockScreenService
 *
 * @author Vove
 * 2020/5/21
 */
class LockScreenService : AccessibilityService() {
    companion object {
        val actived get() = INS != null

        var INS: AccessibilityService? = null

        fun screenOff() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                INS?.performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
            }
        }
    }

    override fun onInterrupt() {
    }

    override fun onCreate() {
        super.onCreate()
        INS = this
    }

    override fun onDestroy() {
        super.onDestroy()
        INS = null
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    }
}