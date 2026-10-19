package dev.tvhome

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

/**
 * Keeps Fire TV Web Launcher in front of the Amazon home screen.
 *
 * Fire OS 8 handles the Home button before apps can see it, so catching the key alone does not work.
 * This service watches for the Amazon home screen to open and opens Fire TV Web Launcher on top of it.
 *
 * It gets only window-change events (package names) and key events. It cannot read the screen.
 * See res/xml/home_guard.xml. It runs in its own small process (":guard" in the manifest),
 * so it stays alive when Android closes the WebView process to free memory.
 */
class HomeGuardService : AccessibilityService() {

    /** True while the user is on the Amazon home screen on purpose. */
    private var stockAllowed = false

    private val openStockHome = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Set the flag in this process before the Amazon window opens, so there is no race.
            stockAllowed = true
            startActivity(
                Intent(Intent.ACTION_MAIN)
                    .addCategory(Intent.CATEGORY_HOME)
                    .setPackage(TvBridge.STOCK_LAUNCHER)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
        }
    }

    override fun onServiceConnected() {
        registerReceiver(openStockHome, IntentFilter(ACTION_OPEN_STOCK_HOME))
    }

    override fun onDestroy() {
        unregisterReceiver(openStockHome)
        super.onDestroy()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        when (event.packageName?.toString()) {
            TvBridge.STOCK_LAUNCHER -> if (!stockAllowed) openHome()
            SYSTEM_UI -> Unit // Overlays such as the volume bar do not end a visit to the Amazon home screen.
            else -> stockAllowed = false
        }
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        // On systems that do give the Home key to accessibility services, handle it here.
        if (event.keyCode != KeyEvent.KEYCODE_HOME) return false
        if (event.action == KeyEvent.ACTION_UP) openHome()
        return true
    }

    override fun onInterrupt() = Unit

    private fun openHome() {
        startActivity(
            Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT),
        )
    }

    companion object {
        /** Sent by the page (through [TvBridge]) to open the Amazon home screen on purpose. */
        const val ACTION_OPEN_STOCK_HOME = "dev.tvhome.OPEN_STOCK_HOME"
        private const val SYSTEM_UI = "com.android.systemui"
    }
}
