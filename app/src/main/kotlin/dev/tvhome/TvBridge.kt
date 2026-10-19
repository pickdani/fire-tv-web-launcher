package dev.tvhome

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.provider.Settings
import android.webkit.JavascriptInterface
import org.json.JSONArray
import org.json.JSONObject

/** The functions the home page can call as `window.TV.*`. See the README for the list. */
class TvBridge(private val activity: MainActivity) {

    /** JSON array of {pkg, label, tv}. `tv` is true for apps made for TV (leanback). */
    @JavascriptInterface
    fun apps(): String {
        val pm = activity.packageManager
        val seen = mutableSetOf(activity.packageName) // Do not list Fire TV Web Launcher itself.
        val result = JSONArray()
        for ((category, isTv) in listOf(Intent.CATEGORY_LEANBACK_LAUNCHER to true, Intent.CATEGORY_LAUNCHER to false)) {
            val intent = Intent(Intent.ACTION_MAIN).addCategory(category)
            for (info in pm.queryIntentActivities(intent, 0)) {
                val pkg = info.activityInfo.packageName
                if (!seen.add(pkg)) continue
                result.put(JSONObject().put("pkg", pkg).put("label", info.loadLabel(pm).toString()).put("tv", isTv))
            }
        }
        return result.toString()
    }

    @JavascriptInterface
    fun launch(pkg: String): Boolean {
        val pm = activity.packageManager
        val intent = pm.getLeanbackLaunchIntentForPackage(pkg) ?: pm.getLaunchIntentForPackage(pkg) ?: return false
        activity.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        return true
    }

    @JavascriptInterface
    fun openSettings() {
        activity.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /**
     * Opens the Amazon home screen and lets it stay until the user opens another app.
     * [HomeGuardService] opens it, so the guard knows to leave it. Does nothing if the guard is off.
     */
    @JavascriptInterface
    fun openStockHome() {
        activity.sendBroadcast(Intent(HomeGuardService.ACTION_OPEN_STOCK_HOME).setPackage(activity.packageName))
    }

    /** True if the Home button service is on. */
    @JavascriptInterface
    fun guardEnabled(): Boolean {
        val enabled = Settings.Secure.getString(activity.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            ?: return false
        val guard = ComponentName(activity, HomeGuardService::class.java)
        return guard.flattenToString() in enabled || guard.flattenToShortString() in enabled
    }

    @JavascriptInterface
    fun reload() = activity.reloadPage()

    @JavascriptInterface
    fun version(): String = activity.packageManager.getPackageInfo(activity.packageName, 0).versionName ?: ""

    companion object {
        const val STOCK_LAUNCHER = "com.amazon.tv.launcher"

        /** The app's TV activity, which has the TV banner. Null for apps not made for TV. */
        fun tvActivity(pm: PackageManager, pkg: String): ResolveInfo? = pm.queryIntentActivities(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER).setPackage(pkg), 0,
        ).firstOrNull()
    }
}
