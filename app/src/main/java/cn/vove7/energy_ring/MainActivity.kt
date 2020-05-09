package cn.vove7.energy_ring

import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.util.Config
import cn.vove7.energy_ring.util.DonateHelper
import cn.vove7.energy_ring.util.isDarkMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.list.listItems
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.ceil

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isDarkMode) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        setContentView(R.layout.activity_main)

        refreshData()
        Handler().postDelayed(::listenSeekBar, 500)

        about_view.setOnClickListener(::showAbout)
        pick_preset_view.setOnClickListener(::pickPreSet)
        view_info_view.setOnClickListener(::outConfig)
        import_view.setOnClickListener(::importFromClip)
    }

    private fun refreshData() {

        strokeWidth_seek_bar.progress = Config.strokeWidth.toInt()
        posx_seek_bar.progress = Config.posXf
        posy_seek_bar.progress = Config.posYf
        size_seek_bar.progress = Config.size
        rotateDuration_seek_bar.progress = (rotateDuration_seek_bar.maxVal + 1 - Config.rotateDuration / 1000)
    }

    private fun listenSeekBar() {
        rotateDuration_seek_bar.onChange { progress, fromUser ->
            Config.rotateDuration = (rotateDuration_seek_bar.maxVal + 1 - progress) * 1000
            if (FloatRingWindow.isCharging) {
                FloatRingWindow.reloadAnimation()
            }
        }
        strokeWidth_seek_bar.onChange { progress, fromUser ->
            Config.strokeWidth = progress.toFloat()
            FloatRingWindow.update()
        }
        posx_seek_bar.onChange { progress, fromUser ->
            Config.posXf = progress
            FloatRingWindow.update()
        }
        posy_seek_bar.onChange { progress, fromUser ->
            Config.posYf = progress
            FloatRingWindow.update()
        }
        size_seek_bar.onChange { progress, fromUser ->
            Config.size = progress
            FloatRingWindow.update()
        }
    }

    private var firstIn = true
    override fun onResume() {
        super.onResume()
        if (!firstIn && Config.tipOfRecent) {
            Config.tipOfRecent = false

            MaterialDialog(this).show {
                title(R.string.how_to_hide_in_recent)
                message(R.string.help_to_hide_in_recent)
                cancelable(false)
                cancelOnTouchOutside(false)
                noAutoDismiss()
                positiveButton(text = "5s")
                getActionButton(WhichButton.POSITIVE).isEnabled = false
                object : CountDownTimer(5000, 1000) {
                    override fun onFinish() {
                        getActionButton(WhichButton.POSITIVE).isEnabled = true
                        positiveButton(R.string.i_know) {
                            dismiss()
                            showAbout(null)
                        }
                    }

                    override fun onTick(millis: Long) {
                        positiveButton(text = "${ceil(millis / 1000.0).toInt()}s")
                    }
                }.start()
            }
        }
        firstIn = false
    }

    private fun outConfig(view: View) {
        val info = Config.Info.fromConfig(Build.MODEL)
        val msg = GsonBuilder().setPrettyPrinting().create().toJson(info)
        MaterialDialog(this).show {
            title(R.string.config_data)
            message(text = "$msg\n欢迎分享至评论区")
            positiveButton(R.string.copy) {
                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("EnergyRing", msg))
            }
        }
    }

    override fun onBackPressed() {
        finishAndRemoveTask()
    }

    private fun pickPreSet(view: View) {
        MaterialDialog(this).show {
            title(R.string.model_preset)
            message(R.string.hint_preset_share)
            listItems(items = Config.presetDevices.map { it.name }) { _, i, _ ->
                Config.presetDevices[i].apply {
                    Config.posXf = posxf
                    Config.posYf = posyf
                    Config.sizef = sizef
                    Config.strokeWidth = strokeWith
                    refreshData()
                }
            }
        }
    }

    private fun importFromClip(view: View) {
        val content = getSystemService(ClipboardManager::class.java)!!.primaryClip?.let {
            it.getItemAt(it.itemCount - 1).text
        }
        if (content == null) {
            Toast.makeText(content, R.string.empty_in_clipboard, Toast.LENGTH_SHORT).show()
            return
        }
        MaterialDialog(this).show {
            title(R.string.clipboard_content)
            message(text = content)
            positiveButton(R.string.text_import) {
                kotlin.runCatching {
                    Gson().fromJson(content.toString(), Config.Info::class.java)
                }.onSuccess {
                    it.apply {
                        Config.posXf = posxf
                        Config.posYf = posyf
                        Config.sizef = sizef
                        Config.strokeWidth = strokeWith
                        refreshData()
                    }
                }.onFailure {
                    Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showAbout(view: View?) {
        MaterialDialog(this).show {
            title(R.string.about)
            message(
                    text = """电量指示环
                    |- 全屏自动隐藏（跟随状态栏）
                    |- 横屏自动隐藏
                    |- 充电动画
                    |- 不受分辨率影响(2k/1080p)""".trimMargin())
            negativeButton(R.string.author) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.data = Uri.parse("https://coolapk.com/u/1090701")
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(this@MainActivity, R.string.no_browser_available, Toast.LENGTH_SHORT).show()
                }
            }
            positiveButton(R.string.donate, click = ::donate)
        }
    }

    private fun donate(d: MaterialDialog) {
        MaterialDialog(this).show {
            title(R.string.way_donate)
            listItems(R.array.way_of_donate) { _, i, c ->
                when (i) {
                    0 -> {
                        if (DonateHelper.isInstallAlipay(this@MainActivity)) {
                            DonateHelper.openAliPay(this@MainActivity)
                        } else {
                            Toast.makeText(context, R.string.alipay_is_not_installed, Toast.LENGTH_SHORT).show()
                        }
                    }
                    1 -> {
                        showWxQr()
                    }
                }
            }
        }

    }

    private fun showWxQr() {
        MaterialDialog(this@MainActivity).show {
            title(R.string.hint_wx_donate)
            customView(view = ImageView(this@MainActivity).apply {
                adjustViewBounds = true
                setImageResource(R.drawable.qr_wx)
            })
        }
    }
}
