package com.iregina.jesuswords.ui.components

import android.content.Context
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Decodes large background WebPs off the main thread, down-sampled to the size they
 * are shown at, and keeps them in a memory-bounded LRU cache shared by every screen.
 */
object ImageCache {
    private val maxKb = (Runtime.getRuntime().maxMemory() / 1024 / 6).toInt()

    private val bitmaps = object : LruCache<String, ImageBitmap>(maxKb) {
        override fun sizeOf(key: String, value: ImageBitmap): Int = value.width * value.height * 4 / 1024
    }

    /** Original pixel size of each drawable, read once from the file header. */
    private val bounds = HashMap<Int, Pair<Int, Int>>()

    fun peek(@DrawableRes resId: Int, reqWidth: Int, reqHeight: Int): ImageBitmap? {
        val size = synchronized(bounds) { bounds[resId] } ?: return null
        return bitmaps.get(key(resId, sampleSize(size, reqWidth, reqHeight)))
    }

    suspend fun load(context: Context, @DrawableRes resId: Int, reqWidth: Int, reqHeight: Int): ImageBitmap =
        withContext(Dispatchers.IO) { loadBlocking(context, resId, reqWidth, reqHeight) }

    fun loadBlocking(context: Context, @DrawableRes resId: Int, reqWidth: Int, reqHeight: Int): ImageBitmap {
        val res = context.resources
        val size = synchronized(bounds) { bounds[resId] } ?: run {
            val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeResource(res, resId, opts)
            (opts.outWidth to opts.outHeight).also { synchronized(bounds) { bounds[resId] = it } }
        }
        val sample = sampleSize(size, reqWidth, reqHeight)
        val key = key(resId, sample)
        bitmaps.get(key)?.let { return it }
        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        val bitmap = BitmapFactory.decodeResource(res, resId, opts).asImageBitmap()
        bitmaps.put(key, bitmap)
        return bitmap
    }

    private fun key(resId: Int, sample: Int) = "$resId@$sample"

    /** Largest power of two that still leaves both sides at least the requested size. */
    private fun sampleSize(size: Pair<Int, Int>, reqWidth: Int, reqHeight: Int): Int {
        val (w, h) = size
        if (reqWidth <= 0 || reqHeight <= 0) return 1
        var sample = 1
        while (w / (sample * 2) >= reqWidth && h / (sample * 2) >= reqHeight) sample *= 2
        return sample
    }
}

/** The drawable as an [ImageBitmap] sized for a [reqWidth]×[reqHeight] px box; null while decoding. */
@Composable
fun rememberDrawableBitmap(@DrawableRes resId: Int, reqWidth: Int, reqHeight: Int): ImageBitmap? {
    val context = LocalContext.current
    val cached = remember(resId, reqWidth, reqHeight) { ImageCache.peek(resId, reqWidth, reqHeight) }
    val bitmap by produceState(cached, resId, reqWidth, reqHeight) {
        if (value == null) value = ImageCache.load(context, resId, reqWidth, reqHeight)
    }
    return bitmap
}
