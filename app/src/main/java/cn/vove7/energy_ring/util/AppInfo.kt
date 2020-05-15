package cn.vove7.energy_ring.util

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

/**
 * App信息类
 */
data class AppInfo(val packageName: String) {

    private var _name: String? = null

    fun getName(pm: PackageManager): String? = _name
        ?: (packageInfo(pm)?.applicationInfo?.loadLabel(pm)?.toString()).also {
            _name = it
        }

    var _icon: Drawable? = null

    fun getIcon(pm: PackageManager): Drawable? = _icon
        ?: (packageInfo(pm)?.applicationInfo?.loadIcon(pm))?.also {
            _icon = it
        }

    private fun packageInfo(pm: PackageManager): PackageInfo? = pm.getPackageInfo(packageName, 0)

}