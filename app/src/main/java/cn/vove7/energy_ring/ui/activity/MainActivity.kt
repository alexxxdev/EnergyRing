package cn.vove7.energy_ring.ui.activity

import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.ActionMenuView
import android.widget.ImageView
import android.widget.Toast
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.BuildConfig
import cn.vove7.energy_ring.R
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.listener.NotificationListener
import cn.vove7.energy_ring.listener.RotationListener
import cn.vove7.energy_ring.model.ShapeType
import cn.vove7.energy_ring.ui.adapter.StylePagerAdapter
import cn.vove7.energy_ring.util.Config
import cn.vove7.energy_ring.util.ConfigInfo
import cn.vove7.energy_ring.util.DonateHelper
import cn.vove7.energy_ring.util.openNotificationService
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

class MainActivity : BaseActivity(), ActionMenuView.OnMenuItemClickListener {

    private val pageAdapter by lazy {
        StylePagerAdapter(supportFragmentManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        style_view_pager.adapter = pageAdapter

        if (BuildConfig.DEBUG) {
            view_info_view.setOnLongClickListener {
                startActivity(Intent(this, MessageHintActivity::class.java))
                true
            }
        }
        view_info_view.setOnClickListener(::outConfig)
        import_view.setOnClickListener(::importFromClip)
        initRadioStylesView()

        styleButtons[Config.energyType.ordinal].callOnClick()

        menuInflater.inflate(R.menu.main, menu_view.menu)
        menu_view.setOnMenuItemClickListener(this)
        menu_view.overflowIcon = getDrawable(R.drawable.ic_settings)
        menu_view.menu.findItem(R.id.rotate_auto_hide).isChecked = Config.autoHideRotate
        menu_view.menu.findItem(R.id.fullscreen_auto_hide).isChecked = Config.autoHideFullscreen
        refreshMenu()
        checkNotificationService()
    }

    private fun checkNotificationService() {
        Handler().postDelayed({
            if (isFinishing) return@postDelayed

            if (Config.notificationListenerEnabled && !NotificationListener.isConnect) {
                openNotificationService()
            }
        }, 3000)
    }

    private fun initRadioStylesView() {
        button_style_ring.setOnClickListener(::onStyleButtonClick)
        button_style_double_ring.setOnClickListener(::onStyleButtonClick)
        button_style_pill.setOnClickListener(::onStyleButtonClick)
    }

    private val styleButtons by lazy {
        arrayOf(button_style_ring, button_style_double_ring, button_style_pill)
    }

    private fun onStyleButtonClick(v: View) {
        val i = styleButtons.indexOf(v)
        styleButtons.forEach { it.isSelected = (it == v) }
        val newStyle = ShapeType.values()[i]
        if (Config.energyType != newStyle) {
            Config.energyType = newStyle
            FloatRingWindow.onShapeTypeChanged()
        }
        style_view_pager.currentItem = i
    }


    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_about -> showAbout()
            R.id.menu_color_mode -> pickColorMode()
            R.id.menu_model_preset -> pickPreSet()
            R.id.menu_screen_off_remind -> startActivity(Intent(this, MessageHintSettingActivity::class.java))
            R.id.menu_force_refresh -> FloatRingWindow.onShapeTypeChanged()
            R.id.fullscreen_auto_hide -> {
                Config.autoHideFullscreen = !Config.autoHideFullscreen
                item.isChecked = Config.autoHideFullscreen
            }
            R.id.rotate_auto_hide -> {
                Config.autoHideRotate = !Config.autoHideRotate
                item.isChecked = Config.autoHideRotate
                if (item.isChecked && !RotationListener.enabled) {
                    RotationListener.start()
                } else {
                    RotationListener.stop()
                }
            }
        }
        return true
    }

    private fun pickColorMode() {
        if (Config.energyType == ShapeType.PILL) {
            Toast.makeText(this, R.string.not_support_current_mode, Toast.LENGTH_SHORT).show()
            return
        }
        MaterialDialog(this).show {
            title(R.string.color_mode)
            listItems(R.array.modes_of_color) { _, i, _ ->
                Config.colorMode = i
                refreshMenu()
                FloatRingWindow.update()
            }
        }
    }

    private fun refreshMenu() {
        menu_view.menu.findItem(R.id.menu_color_mode).title = getString(R.string.color_mode) + ": " +
                resources.getStringArray(R.array.modes_of_color)[Config.colorMode]
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
                            showAbout()
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
        val info = ConfigInfo.fromConfig(Build.MODEL)
        val msg = GsonBuilder().setPrettyPrinting().create().toJson(info)
        MaterialDialog(this).show {
            title(R.string.config_data)
            message(text = "$msg\n" + getString(R.string.welcome_to_share_on_comment_area))
            positiveButton(R.string.copy) {
                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("EnergyRing", msg))
            }
            negativeButton(R.string.save_current_config) {
                saveConfig(info)
            }
        }
    }

    private fun saveConfig(info: ConfigInfo, name: CharSequence? = null) {
        MaterialDialog(this@MainActivity).show {
            title(R.string.config_title)
            input(waitForPositiveButton = true, prefill = name) { _, s ->
                info.name = s.toString()
                info.save()
            }
            positiveButton()
            negativeButton()
        }
    }

    override fun onBackPressed() {
        finishAndRemoveTask()
    }

    private fun pickPreSet() {
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
                val dss = if (c) allDs.filter { it.model.equals(Build.MODEL, ignoreCase = true) }
                else allDs
                listItems(items = dss.map { it.name }, waitForPositiveButton = false) { _, i, _ ->
                    applyConfig(dss[i])
                }
            }
            positiveButton(R.string.edit) { editLocalConfig() }
            negativeButton(R.string.close)
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

    private fun applyConfig(info: ConfigInfo) {
        info.applyConfig()
        if (info.energyType != Config.energyType) {
            Config.energyType = info.energyType ?: ShapeType.RING
            FloatRingWindow.onShapeTypeChanged()
        } else {
            FloatRingWindow.update()
        }
        refreshData()
    }

    private fun refreshData() {
        pageAdapter.getItem(style_view_pager.currentItem).onResume()
        styleButtons[Config.energyType.ordinal].callOnClick()
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
                importConfig(content.toString(), false)
            }
            negativeButton(R.string.config_import_and_save) {
                importConfig(content.toString(), true)
            }
        }
    }

    private fun importConfig(content: String, save: Boolean) {
        kotlin.runCatching {
            Gson().fromJson(content, ConfigInfo::class.java)
        }.onSuccess {
            applyConfig(it)
            if (save) {
                saveConfig(it, it.name)
            }
        }.onFailure {
            App.toast(it.message ?: "导入失败")
        }
    }

    private fun showAbout() {
        MaterialDialog(this).show {
            title(R.string.about)
            message(
                    text = """电量指示环
                    |- 全屏自动隐藏（跟随状态栏）
                    |- 横屏自动隐藏
                    |- 充电动画
                    |- 熄屏通知提醒
                    |- 不受分辨率影响(2k/1080p)
                    |
                    |某些时候会出现“冻结”情况，更改属性 圆环不会变化，可以在菜单强制刷新或切换形状。
                    |【此应用完全免费】
                    |""".trimMargin())
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
            positiveButton(R.string.support, click = ::donate)
        }
    }

    private fun donate(d: MaterialDialog) {
        MaterialDialog(this).show {
            title(R.string.way_support)
            listItems(R.array.way_of_support) { _, i, c ->
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
                    2 -> {
                        starGithubRepo()
                    }
                    //todo ad donate
                }
            }
        }
    }

    private fun starGithubRepo() {
        MaterialDialog(this).show {
            title(text = "Star Github 仓库以支持作者")
            message(text = "此方式您需要一个Github账号，若没有可使用邮箱注册；点击下面打开链接，点击Star按钮即可。")
            positiveButton(R.string.open_link) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.data = Uri.parse("https://github.com/Vove7/yyets_flutter")
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(this@MainActivity,
                            R.string.no_browser_available, Toast.LENGTH_SHORT).show()
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
