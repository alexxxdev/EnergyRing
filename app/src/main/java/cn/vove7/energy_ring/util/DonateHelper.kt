package cn.vove7.energy_ring.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.URI_INTENT_SCHEME
import android.content.pm.PackageManager
import java.net.URISyntaxException

/**
 * # DonateHelper
 *
 * @author Vove
 * 2019/11/27
 */
object DonateHelper {

    private const val payCode = "FKX07237LYKEFIVIY8MSE9"

    fun isInstallAlipay(context: Context): Boolean {
        val pm = context.packageManager
        return try {
            val info = pm.getPackageInfo("com.eg.android.AlipayGphone", 0)
            info != null
        } catch (var3: PackageManager.NameNotFoundException) {
            var3.printStackTrace()
            false
        }

    }

    fun openAliPay(ctx: Context): Boolean {
        return startIntentUrl(ctx)
    }

    private fun startIntentUrl(context: Context): Boolean {
        val uri = "intent://platformapi/startapp?saId=10000007" +
                "&clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2F${payCode}%3F_s%3Dweb-other&_t=1472443966571#Intent;" +
                "scheme=alipayqr;package=com.eg.android.AlipayGphone;end"
        return try {
            val intent = Intent.parseUri(uri, URI_INTENT_SCHEME)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (var3: URISyntaxException) {
            var3.printStackTrace()
            false
        } catch (var4: ActivityNotFoundException) {
            var4.printStackTrace()
            false
        }

    }


}