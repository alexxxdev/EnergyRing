package cn.vove7.energy_ring.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import cn.vove7.energy_ring.R
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.listener.NotificationListener
import cn.vove7.energy_ring.service.LockScreenService
import cn.vove7.energy_ring.util.Config
import cn.vove7.energy_ring.util.goAccessibilityService
import cn.vove7.energy_ring.util.openNotificationService
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import kotlinx.android.synthetic.main.activity_message_hint_setting.*
import kotlin.math.ceil

/**
 * # MessageHintSettingActivity
 *
 * @author Vove
 * 2020/5/14
 */
class MessageHintSettingActivity : BaseActivity() {

    var checkOpen = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_hint_setting)

        service_status_button.setOnClickListener {
            if (service_status_button.isSelected) {
                NotificationListener.stop()
                refreshStatusButton()
            } else if (!NotificationListener.isConnect) {
                checkOpen = true
                openNotificationService()
            } else if (!LockScreenService.actived) {
                checkOpen = true
                goAccessibilityService()
            } else {
                NotificationListener.resume()
                refreshStatusButton()
            }
        }
        preview_button.setOnClickListener {
            FloatRingWindow.hide()
            startActivityForResult(Intent(this, MessageHintActivity::class.java), 10)
        }
        showTips()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10) {
            FloatRingWindow.show()
        }
    }

    private fun showTips() {
        if (!Config["tips_of_notification_hint", true, false]) {
            return
        }
        MaterialDialog(this).show {
            title(R.string.prompt)
            cancelable(false)
            cancelOnTouchOutside(false)
            message(text = """
                |熄屏闪光提醒。
                
                |需要通知权限和无障碍
                |呼吸灯闪烁时，双击亮屏。
                |双击亮屏时人脸解锁可能无法使用。
                """.trimMargin())

            noAutoDismiss()
            positiveButton(text = "10s")
            getActionButton(WhichButton.POSITIVE).isEnabled = false
            object : CountDownTimer(10000, 1000) {
                override fun onFinish() {
                    getActionButton(WhichButton.POSITIVE).isEnabled = true
                    positiveButton(R.string.i_know) {
                        Config["tips_of_notification_hint"] = false
                        dismiss()
                    }
                }

                override fun onTick(millis: Long) {
                    positiveButton(text = "${ceil(millis / 1000.0).toInt()}s")
                }
            }.start()
        }

    }

    override fun onResume() {
        super.onResume()
        if (checkOpen) {
            if (NotificationListener.isConnect && LockScreenService.actived) {
                NotificationListener.resume()
                checkOpen = false
            }
        }
        refreshStatusButton()
    }

    private fun refreshStatusButton() {
        service_status_button.isSelected = NotificationListener.isOpen
        service_status_button.text = if (service_status_button.isSelected) "停止服务" else "开启服务"
    }

//    fun getAllApps() = thread {
//        val man = packageManager
//        val list = man.getInstalledPackages(0)
//        val appList = mutableListOf<AppInfo>()
//        for (app in list) {
//            try {
//                appList.add(AppInfo(app.packageName))
//            } catch (e: Exception) {//NameNotFoundException
//                e.printStackTrace()
//            }
//        }
//
//        val (a, b) = appList.spliteBy {
//            it.packageName in Config.notifyApps
//        }
//
//        val adapter = MergeAdapter(AppListAdapter(a, "生效", man), AppListAdapter(b, "未生效", man))
//
//        if (isDestroyed) {
//            return@thread
//        }
//        runOnUiThread {
//            loading_bar.visibility = View.GONE
//            list_view.adapter = adapter
//        }
//    }
}