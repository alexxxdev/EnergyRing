package cn.vove7.energy_ring.util

import android.content.res.Configuration
import cn.vove7.energy_ring.App

/**
 * # utils
 *
 * @author Vove
 * 2020/5/9
 */

val isDarkMode: Boolean
    get() = (App.INS.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES