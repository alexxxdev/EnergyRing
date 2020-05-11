package cn.vove7.energy_ring.ui

import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.vove7.energy_ring.R
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.listener.PowerEventReceiver
import cn.vove7.energy_ring.listener.RotationListener
import cn.vove7.energy_ring.ui.adapter.ColorsAdapter
import cn.vove7.energy_ring.util.*
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.checkbox.checkBoxPrompt
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsMultiChoice
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

        color_list.adapter = ColorsAdapter()
        bg_color_view.setBackgroundColor(Config.ringBgColor)
        bg_color_view.setTextColor(Config.ringBgColor.antiColor)
        bg_color_view.setOnClickListener {
            pickColor(this, initColor = Config.ringBgColor) { c ->
                bg_color_view.setBackgroundColor(c)
                bg_color_view.setTextColor(c.antiColor)
                Config.ringBgColor = c
                FloatRingWindow.update()
            }
        }
    }

    private fun refreshData() {
        strokeWidth_seek_bar.progress = Config.strokeWidthF.toInt()
        posx_seek_bar.progress = Config.posXf
        posy_seek_bar.progress = Config.posYf
        size_seek_bar.progress = Config.size
        charging_rotateDuration_seek_bar.progress = (charging_rotateDuration_seek_bar.maxVal + 1 - Config.chargingRotateDuration / 1000)
        default_rotateDuration_seek_bar.progress = (default_rotateDuration_seek_bar.maxVal + default_rotateDuration_seek_bar.minVal -
                (Config.defaultRotateDuration) / 1000)

    }

    private fun listenSeekBar() {
        fullscreen_auto_hide.isChecked = Config.autoHideFullscreen
        rotate_auto_hide.isChecked = Config.autoHideRotate

        fullscreen_auto_hide.setOnCheckedChangeListener { _, isChecked ->
            Config.autoHideFullscreen = isChecked
        }
        rotate_auto_hide.setOnCheckedChangeListener { _, isChecked ->
            Config.autoHideRotate = isChecked
            if (isChecked && !RotationListener.enabled) {
                RotationListener.start()
            } else {
                RotationListener.stop()
            }
        }
        charging_rotateDuration_seek_bar.onStop { progress ->
            Config.chargingRotateDuration = (charging_rotateDuration_seek_bar.maxVal + 1 - progress) * 1000
            if (PowerEventReceiver.isCharging) {
                FloatRingWindow.reloadAnimation()
            }
        }
        default_rotateDuration_seek_bar.onStop { progress -> //[15,60]
            Config.defaultRotateDuration = (default_rotateDuration_seek_bar.maxVal - (progress - default_rotateDuration_seek_bar.minVal)) * 1000
            Log.d("Debug :", "listenSeekBar  ----> ${Config.defaultRotateDuration}")
            if (!PowerEventReceiver.isCharging) {
                FloatRingWindow.reloadAnimation()
            }
        }
        strokeWidth_seek_bar.onChange { progress, _ ->
            Config.strokeWidthF = progress.toFloat()
            FloatRingWindow.update()
        }
        posx_seek_bar.onChange { progress, _ ->
            Config.posXf = progress
            FloatRingWindow.update()
        }
        posy_seek_bar.onChange { progress, _ ->
            Config.posYf = progress
            FloatRingWindow.update()
        }
        size_seek_bar.onChange { progress, _ ->
            Config.size = progress
            FloatRingWindow.update()
        }
    }

    private var firstIn = true
    override fun onResume() {
        super.onResume()
        if (!firstIn && Config.tipOfRecent) {
            MaterialDialog(this).show {
                title(R.string.how_to_hide_in_recent)
                message(R.string.help_to_hide_in_recent)
                cancelable(false)
                cancelOnTouchOutside(false)
                noAutoDismiss()
                positiveButton(text = "10s")
                getActionButton(WhichButton.POSITIVE).isEnabled = false
                object : CountDownTimer(10000, 1000) {
                    override fun onFinish() {
                        getActionButton(WhichButton.POSITIVE).isEnabled = true
                        positiveButton(R.string.i_know) {
                            dismiss()
                            Config.tipOfRecent = false
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
            message(text = "$msg\n" + getString(R.string.welcome_to_share_on_comment_area))
            positiveButton(R.string.copy) {
                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("EnergyRing", msg))
            }
            negativeButton(R.string.save_current_config) {
                MaterialDialog(this@MainActivity).show {
                    title(R.string.config_title)
                    input(waitForPositiveButton = true) { _, s ->
                        info.name = s.toString()
                        info.save()
                    }
                    positiveButton()
                    negativeButton()
                }
            }
        }
    }

    override fun onBackPressed() {
        finishAndRemoveTask()
    }

    private fun pickPreSet(view: View) {
        MaterialDialog(this).show {
            val allDs = Config.presetDevices.toMutableList().also {
                it.addAll(Config.localConfig)
            }
            title(R.string.model_preset)
            message(R.string.hint_preset_share)
            var ds = allDs.filter { it.model == Build.MODEL }
            if (ds.isEmpty()) {
                ds = allDs
            }
            listItems(items = ds.map { it.name }, waitForPositiveButton = false) { _, i, _ ->
                dismiss()
                applyConfig(ds[i])
            }
            checkBoxPrompt(R.string.display_only_this_model, isCheckedDefault = ds.size != allDs.size) { c ->
                val dss = if (c) allDs.filter { it.model == Build.MODEL }
                else allDs
                listItems(items = dss.map { it.name }) { _, i, _ ->
                    applyConfig(dss[i])
                }
            }
            positiveButton(R.string.edit) { editLocalConfig() }
        }
    }

    private fun editLocalConfig() {
        MaterialDialog(this).show {
            title(R.string.edit_local_config)
            listItemsMultiChoice(items = Config.localConfig.map { it.name }, waitForPositiveButton = true) { _, indices, _ ->
                val cs = Config.localConfig
                val list = cs.toMutableList()
                list.removeAll(indices.map { cs[it] })
                Config.localConfig = list.toTypedArray()
            }
            positiveButton(R.string.delete_selected)
            negativeButton()
        }
    }

    private fun applyConfig(info: Config.Info) {
        Config.posXf = info.posxf
        Config.posYf = info.posyf
        Config.sizef = info.sizef
        Config.strokeWidthF = info.strokeWidth
        refreshData()
        if (info.energyType != Config.energyType) {
            Config.energyType = info.energyType
            FloatRingWindow.onChangeShapeType()
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
                        Config.strokeWidthF = strokeWidth
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
                    //todo ad donate
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

    override fun onDestroy() {
        super.onDestroy()
        Config.devicesWeakLazy.clearWeakValue()
    }
}
