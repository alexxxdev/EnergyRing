package cn.vove7.energy_ring.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import cn.vove7.energy_ring.util.isDarkMode

/**
 * # BaseActivity
 *
 * @author Vove
 * 2020/5/14
 */
abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isDarkMode) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                        0x00000010
        }
    }
}