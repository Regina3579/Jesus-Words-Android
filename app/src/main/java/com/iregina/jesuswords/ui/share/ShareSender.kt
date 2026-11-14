package com.iregina.jesuswords.ui.share

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/** Saves the rendered image to the cache and opens the system share sheet (image/png). */
object ShareSender {
    private const val DIR = "shared"

    suspend fun share(context: Context, bitmap: Bitmap, verseId: Int, chooserTitle: String) {
        val file = withContext(Dispatchers.IO) {
            val dir = File(context.cacheDir, DIR).apply { mkdirs() }
            // Keep the cache small: only the latest shared image is kept.
            dir.listFiles()?.forEach { it.delete() }
            File(dir, "jesus_words_$verseId.png").also { out ->
                out.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            }
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val send = Intent(Intent.ACTION_SEND)
            .setType("image/png")
            .putExtra(Intent.EXTRA_STREAM, uri)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        // ClipData lets the share sheet show a preview of the image.
        send.clipData = ClipData.newRawUri(null, uri)
        context.startActivity(Intent.createChooser(send, chooserTitle))
    }
}
