package com.iregina.jesuswords.ui.share

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iregina.jesuswords.R
import com.iregina.jesuswords.data.AppLanguage
import com.iregina.jesuswords.data.AppTheme
import com.iregina.jesuswords.data.JesusWord
import com.iregina.jesuswords.ui.components.AutoFitText
import com.iregina.jesuswords.ui.components.CapsuleShape
import com.iregina.jesuswords.ui.components.DropShadow
import com.iregina.jesuswords.ui.components.LatinCrossIcon
import com.iregina.jesuswords.ui.components.diagonalGradient
import com.iregina.jesuswords.ui.components.drawOverlays
import com.iregina.jesuswords.ui.components.rememberOverlayPainters
import com.iregina.jesuswords.ui.components.softShadow
import com.iregina.jesuswords.ui.theme.NotoSerif
import com.iregina.jesuswords.ui.theme.lineHeightWithSpacing
import com.iregina.jesuswords.ui.theme.serifFontFamily
import com.iregina.jesuswords.ui.theme.textShadow
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/** Side of the shared image, in pixels (and in the layout units below). */
const val SHARE_CANVAS = 1080

/**
 * The 1080×1080 share image (ShareImageView.swift). Every size is in canvas units: wrap it
 * in a `LocalDensity` where 1.dp = one canvas pixel (export) or previewPx / 1080 (live
 * preview), so the preview is the very same layout, only scaled.
 *
 * Fixed top band (18%) and bottom branding (13%) never move; only the quote shrinks,
 * down to 30%, so long Tamil verses fit. The branding is always English.
 */
@Composable
fun ShareImage(
    word: JesusWord,
    theme: AppTheme,
    language: AppLanguage,
    wallpaper: ImageBitmap?,
    modifier: Modifier = Modifier,
    waitingForWallpaper: Boolean = false,
) {
    val canvas = SHARE_CANVAS.toFloat()
    val top = canvas * 0.18f
    val bottom = canvas * 0.13f
    val vertical = canvas * 0.05f
    val gap = canvas * 0.02f
    val cardHeight = canvas - top - bottom - vertical * 2 - gap * 2
    val serif = serifFontFamily(language)

    Box(modifier.requiredSize(SHARE_CANVAS.dp)) {
        if (wallpaper != null) {
            Image(wallpaper, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(Color.Black.copy(0.15f), Color.Black.copy(0.35f), Color.Black.copy(0.15f))),
                ),
            )
        } else if (waitingForWallpaper) {
            // The picture is still decoding (a few milliseconds): plain dark, not the theme.
            Box(Modifier.fillMaxSize().background(Color(0xFF141414)))
        } else {
            ThemeShareBackground(theme)
        }

        Column(Modifier.fillMaxSize().padding(horizontal = 52.dp, vertical = vertical.dp)) {
            // Top band: cross + title, aligned to the bottom of the band.
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Bottom),
                modifier = Modifier.fillMaxWidth().height(top.dp),
            ) {
                LatinCrossIcon(72.dp, color = Color.White.copy(alpha = 0.92f), shadows = listOf(DropShadow(0.25f, 6.dp)))
                BasicText(
                    language.pick("Jesus Words", "இயேசுவின் வார்த்தைகள்"),
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased(minFontSize = 39.sp, maxFontSize = 56.sp, stepSize = 1.sp),
                    style = TextStyle(
                        fontFamily = serif, fontWeight = FontWeight.Bold, fontSize = 56.sp,
                        color = Color.White, textAlign = TextAlign.Center, shadow = textShadow(0.2f, 4.dp),
                    ),
                )
            }

            Spacer(Modifier.height(gap.dp))
            QuoteCard(word, language, Modifier.fillMaxWidth().height(cardHeight.dp))
            Spacer(Modifier.height(gap.dp))

            Branding(Modifier.fillMaxWidth().height(bottom.dp))
        }
    }
}

@Composable
private fun QuoteCard(word: JesusWord, language: AppLanguage, modifier: Modifier) {
    val serif = serifFontFamily(language)
    val shape = RoundedCornerShape(28.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.22f), shape)
            .border(1.dp, Color.White.copy(alpha = 0.08f), shape)
            .padding(horizontal = 40.dp, vertical = 16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 4.dp)
                .background(Color.White.copy(alpha = 0.15f), CapsuleShape)
                .padding(horizontal = 24.dp, vertical = 10.dp),
        ) {
            BasicText(word.categoryEmoji, style = TextStyle(fontSize = 30.sp))
            BasicText(
                word.categoryName(language),
                maxLines = 1,
                autoSize = TextAutoSize.StepBased(minFontSize = 17.sp, maxFontSize = 28.sp, stepSize = 1.sp),
                style = TextStyle(fontFamily = serif, fontWeight = FontWeight.SemiBold, fontSize = 28.sp, color = Color.White),
            )
        }

        AutoFitText(
            word.theme(language),
            maxLines = 2,
            minFontSize = 24.sp,
            style = TextStyle(
                fontFamily = serif, fontWeight = FontWeight.Bold, fontSize = 48.sp, color = Color.White,
                textAlign = TextAlign.Center, shadow = textShadow(0.15f, 3.dp),
            ),
            modifier = Modifier.padding(vertical = 2.dp),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(70.dp).height(2.dp).background(Color.White.copy(alpha = 0.25f)))
            Icon(painterResource(R.drawable.ic_auto_awesome), contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(18.dp))
            Box(Modifier.width(70.dp).height(2.dp).background(Color.White.copy(alpha = 0.25f)))
        }

        AutoFitText(
            "“${word.quote(language)}”",
            minFontSize = 15.5.sp,
            style = TextStyle(
                fontFamily = serif, fontWeight = FontWeight.Medium, fontStyle = FontStyle.Italic, fontSize = 52.sp,
                lineHeight = lineHeightWithSpacing(serif, 52f, 10f), color = Color.White,
                textAlign = TextAlign.Center, shadow = textShadow(0.1f, 2.dp),
            ),
            modifier = Modifier.weight(1f, fill = false).padding(vertical = 4.dp),
        )

        AutoFitText(
            "— ${word.reference(language)}",
            maxLines = 1,
            minFontSize = 18.sp,
            style = TextStyle(
                fontFamily = serif, fontWeight = FontWeight.SemiBold, fontStyle = FontStyle.Italic, fontSize = 36.sp,
                color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center,
            ),
        )
    }
}

/** App icon + "Jesus Words / Daily Blessings" — always in English. */
@Composable
private fun Branding(modifier: Modifier) {
    val iconShape = RoundedCornerShape(24.dp)
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Image(
            painterResource(R.drawable.share_app_icon),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(100.dp).softShadow(iconShape, alpha = 0.3f, radius = 6.dp, y = 3.dp).clip(iconShape),
        )
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            BasicText("Jesus Words", maxLines = 1, style = TextStyle(fontFamily = NotoSerif, fontWeight = FontWeight.Bold, fontSize = 34.sp, color = Color.White))
            BasicText("Daily Blessings", maxLines = 1, style = TextStyle(fontFamily = NotoSerif, fontSize = 22.sp, color = Color.White.copy(alpha = 0.55f)))
        }
    }
}

/** Fallback without a wallpaper: theme gradient, overlays at 2× and light rays. */
@Composable
private fun ThemeShareBackground(theme: AppTheme) {
    val painters = rememberOverlayPainters(theme)
    Canvas(Modifier.fillMaxSize()) {
        drawRect(diagonalGradient(theme.gradient, size))
        drawOverlays(theme, painters, scale = 2f)
        val origin = Offset(size.width * 0.8f, -30.dp.toPx())
        val length = max(size.width, size.height) * 1.5f
        val rays = Path()
        for (deg in 0 until 360 step 30) {
            val a = Math.toRadians(deg.toDouble())
            rays.moveTo(origin.x, origin.y)
            rays.lineTo(origin.x + (cos(a) * length).toFloat(), origin.y + (sin(a) * length).toFloat())
        }
        drawPath(rays, Color.White.copy(alpha = 0.04f), style = Stroke(width = 50.dp.toPx()))
    }
}
