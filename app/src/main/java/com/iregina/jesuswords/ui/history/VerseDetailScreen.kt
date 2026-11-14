package com.iregina.jesuswords.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iregina.jesuswords.R
import com.iregina.jesuswords.data.JesusWord
import com.iregina.jesuswords.ui.UiState
import com.iregina.jesuswords.ui.components.CapsuleShape
import com.iregina.jesuswords.ui.components.ThemeBackground
import com.iregina.jesuswords.ui.components.blockTouchesBelow
import com.iregina.jesuswords.ui.components.plainClickable
import com.iregina.jesuswords.ui.today.VerseCard
import com.iregina.jesuswords.ui.today.VerseCardSizes

/**
 * Full-screen verse page opened from History or Favourites (VerseDetailView): close button,
 * the large verse card that always fits the screen, and "Share as Image".
 */
@Composable
fun VerseDetailScreen(
    detail: VerseDetail,
    state: UiState,
    onClose: () -> Unit,
    onToggleFavorite: (JesusWord) -> Unit,
    onShare: (JesusWord) -> Unit,
    modifier: Modifier = Modifier,
) {
    val language = state.language
    val word = detail.word
    Box(modifier.fillMaxSize().blockTouchesBelow()) {
        ThemeBackground(state.theme)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
        ) {
            Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.End) {
                Icon(
                    painterResource(R.drawable.ic_cancel),
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier
                        .size(32.dp)
                        .semantics {
                            role = Role.Button
                            contentDescription = language.pick("Close", "மூடு")
                        }
                        .plainClickable(onClick = onClose),
                )
            }

            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                VerseCard(
                    word = word,
                    theme = state.theme,
                    language = language,
                    isFavorite = state.isFavorite(word),
                    onToggleFavorite = { onToggleFavorite(word) },
                    animateIn = false,
                    sizes = VerseCardSizes.Detail,
                    footer = detail.shownAtMillis?.let {
                        val formatted = longDate(it)
                        language.pick("Shown on $formatted", "$formatted அன்று காட்டப்பட்டது")
                    },
                )
            }

            Spacer(Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.2f), CapsuleShape)
                    .semantics { role = Role.Button }
                    .plainClickable { onShare(word) }
                    .padding(horizontal = 28.dp, vertical = 12.dp),
            ) {
                Icon(painterResource(R.drawable.ic_image), contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Text(
                    language.pick("Share as Image", "படமாகப் பகிரு"),
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White),
                )
            }
            Spacer(Modifier.height(28.dp))
        }
    }
}
