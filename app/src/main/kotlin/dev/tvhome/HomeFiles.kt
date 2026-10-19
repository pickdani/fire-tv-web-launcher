package dev.tvhome

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.webkit.WebResourceResponse
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * Serves https://tvhome.local/ to the WebView:
 *
 *  - `/icon/<package>`: the app's banner (or icon) as PNG
 *  - `/<path>`: a file from /sdcard/tvhome, else the same file from the page built into the app
 *
 * adb can write to /sdcard/tvhome, so you can change the page without a rebuild.
 * The app reads it with READ_EXTERNAL_STORAGE, which tools/push.sh grants.
 */
class HomeFiles(private val context: Context) {

    private val homeDir = File(Environment.getExternalStorageDirectory(), "tvhome")
    private val iconCache = mutableMapOf<String, ByteArray?>()

    /** If /sdcard/tvhome/dev-url.txt has a URL, load the page from there (see tools/dev.sh). */
    fun startUrl(): String {
        val devUrl = runCatching { File(homeDir, "dev-url.txt").readText().trim() }.getOrDefault("")
        return if (devUrl.startsWith("http://") || devUrl.startsWith("https://")) devUrl else "https://$HOST/index.html"
    }

    @Synchronized
    fun clearIconCache() = iconCache.clear()

    fun serve(uri: Uri): WebResourceResponse? {
        if (uri.host != HOST) return null // Not ours: let the WebView fetch it normally.
        val path = uri.path?.removePrefix("/")?.ifEmpty { null } ?: "index.html"

        if (path.startsWith("icon/")) {
            val png = icon(path.removePrefix("icon/")) ?: return notFound()
            return ok("image/png", png.inputStream())
        }
        return try {
            ok(mimeType(path), openPageFile(path))
        } catch (e: IOException) {
            notFound()
        }
    }

    private fun openPageFile(path: String): InputStream {
        val file = File(homeDir, path).canonicalFile
        // Serve only files inside the home folder ("../" must not reach other files).
        if (file.path.startsWith(homeDir.canonicalPath + File.separator) && file.isFile) {
            return file.inputStream()
        }
        if (".." in path) throw IOException("bad path")
        return context.assets.open("home/$path")
    }

    @Synchronized
    private fun icon(packageName: String): ByteArray? = iconCache.getOrPut(packageName) {
        val pm = context.packageManager
        try {
            val banner = TvBridge.tvActivity(pm, packageName)?.activityInfo?.loadBanner(pm)
                ?: pm.getApplicationBanner(packageName)
            if (banner != null) banner.toPng(320, 180) else pm.getApplicationIcon(packageName).toPng(192, 192)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    private fun Drawable.toPng(width: Int, height: Int): ByteArray {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        setBounds(0, 0, width, height)
        draw(Canvas(bitmap))
        return ByteArrayOutputStream().also {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            bitmap.recycle()
        }.toByteArray()
    }

    private fun ok(mime: String, data: InputStream) = WebResourceResponse(
        mime, "utf-8", 200, "OK",
        mapOf("Cache-Control" to "no-store", "Access-Control-Allow-Origin" to "*"),
        data,
    )

    private fun notFound() = WebResourceResponse("text/plain", "utf-8", 404, "Not Found", emptyMap(), "".byteInputStream())

    private fun mimeType(path: String) = when (path.substringAfterLast('.').lowercase()) {
        "html" -> "text/html"
        "css" -> "text/css"
        "js" -> "application/javascript"
        "json" -> "application/json"
        "png" -> "image/png"
        "jpg", "jpeg" -> "image/jpeg"
        "webp" -> "image/webp"
        "gif" -> "image/gif"
        "svg" -> "image/svg+xml"
        "mp4" -> "video/mp4"
        "woff2" -> "font/woff2"
        else -> "application/octet-stream"
    }

    companion object {
        const val HOST = "tvhome.local"
    }
}
