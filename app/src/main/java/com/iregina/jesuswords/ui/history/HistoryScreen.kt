package com.iregina.jesuswords.ui.history

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iregina.jesuswords.R
import com.iregina.jesuswords.data.AppLanguage
import com.iregina.jesuswords.data.JesusWord
import com.iregina.jesuswords.data.VerseCategory
import com.iregina.jesuswords.ui.UiState
import com.iregina.jesuswords.ui.components.CapsuleShape
import com.iregina.jesuswords.ui.components.FavoriteHeartButton
import com.iregina.jesuswords.ui.components.PageHeader
import com.iregina.jesuswords.ui.components.TabBarReservedHeight
import com.iregina.jesuswords.ui.components.ThemeBackground
import com.iregina.jesuswords.ui.components.contentCard
import com.iregina.jesuswords.ui.components.overhang
import com.iregina.jesuswords.ui.components.plainClickable
import com.iregina.jesuswords.ui.theme.lineHeightWithSpacing
import com.iregina.jesuswords.ui.theme.textShadow
import com.iregina.jesuswords.ui.theme.themeFontFamily
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/** A verse opened from History (with the day it was shown) or from Favourites (no date). */
data class VerseDetail(val word: JesusWord, val shownAtMillis: Long?)

/** The History page with its History | Favourites switch (HistoryView.swift). */
@Composable
fun HistoryScreen(
    state: UiState,
    onToggleFavorite: (JesusWord) -> Unit,
    onOpen: (VerseDetail) -> Unit,
    modifier: Modifier = Modifier,
) {
    val language = state.language
    var showingFavorites by rememberSaveable { mutableStateOf(false) }
    val family = themeFontFamily(state.theme, language)

    Box(modifier.fillMaxSize()) {
        ThemeBackground(state.theme)

        Column(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 16.dp),
            ) {
                PageHeader(
                    title = if (showingFavorites) language.pick("My Favourites", "பிடித்தவை")
                    else language.pick("History", "வரலாறு"),
                    subtitle = if (showingFavorites) language.pick("Verses you saved, by topic", "நீங்கள் சேமித்த வசனங்கள்")
                    else language.pick("Your Daily Blessings", "உங்கள் தினசரி ஆசீர்வாதங்கள்"),
                    titleFamily = family,
                )
                SectionSwitch(showingFavorites, language) { showingFavorites = it }
            }

            if (showingFavorites) {
                FavoritesList(state, onToggleFavorite, onOpen = { onOpen(VerseDetail(it, null)) })
            } else if (state.history.isEmpty()) {
                EmptyState(
                    icon = R.drawable.ic_menu_book,
                    title = language.pick("No history yet", "இதுவரை வரலாறு இல்லை"),
                    body = language.pick("Your daily words will appear here", "உங்கள் தினசரி வார்த்தைகள் இங்கே தோன்றும்"),
                    iconAlpha = 0.5f, titleAlpha = 0.7f, bodyAlpha = 0.5f,
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = listBottomPadding(),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(state.history, key = { "${it.shownAtMillis}-${it.word.id}" }) { shown ->
                        VerseRowCard(
                            word = shown.word,
                            state = state,
                            shownAtMillis = shown.shownAtMillis,
                            onToggleFavorite = { onToggleFavorite(shown.word) },
                            onClick = { onOpen(VerseDetail(shown.word, shown.shownAtMillis)) },
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }
                }
            }
        }
    }
}

/** List padding so the last row clears the floating tab bar and the navigation bar. */
@Composable
internal fun listBottomPadding() = PaddingValues(
    bottom = TabBarReservedHeight + 24.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
)

/** [🕒 History | ♥ Favourites] capsule: selected segment white 90% with dark text. */
@Composable
private fun SectionSwitch(showingFavorites: Boolean, language: AppLanguage, onChange: (Boolean) -> Unit) {
    Row(
        Modifier
            .background(Color.Black.copy(alpha = 0.35f), CapsuleShape)
            .border(1.dp, Color.White.copy(alpha = 0.5f), CapsuleShape)
            .padding(3.dp),
    ) {
        SwitchSegment(language.pick("History", "வரலாறு"), R.drawable.ic_schedule_outline, !showingFavorites) { onChange(false) }
        SwitchSegment(language.pick("Favourites", "பிடித்தவை"), R.drawable.ic_favorite, showingFavorites) { onChange(true) }
    }
}

@Composable
private fun SwitchSegment(title: String, icon: Int, on: Boolean, onClick: () -> Unit) {
    val fill by animateColorAsState(if (on) Color.White.copy(alpha = 0.9f) else Color.Transparent, tween(200), label = "segFill")
    val content by animateColorAsState(if (on) Color.Black.copy(alpha = 0.8f) else Color.White, tween(200), label = "segText")
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(fill, CapsuleShape)
            .semantics {
                role = Role.Tab
                selected = on
            }
            .plainClickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Icon(painterResource(icon), contentDescription = null, tint = content, modifier = Modifier.size(16.dp))
        Text(title, style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = content), maxLines = 1)
    }
}

/** Medium date in the phone's own locale, e.g. "14 Nov 2026". */
fun mediumDate(millis: Long): String =
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        .format(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()))

/** Long date in the phone's own locale, e.g. "14 November 2026". */
fun longDate(millis: Long): String =
    DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
        .format(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()))

/**
 * One verse in History or Favourites (VerseRowCard): emoji, category, date and heart on
 * top; then theme, the quote (3 lines max) and the reference.
 */
@Composable
fun VerseRowCard(
    word: JesusWord,
    state: UiState,
    shownAtMillis: Long?,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val language = state.language
    val family = themeFontFamily(state.theme, language)
    val shadow = textShadow(0.5f, 2.dp)
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .contentCard(cornerRadius = 16.dp, strokeAlpha = 0.22f)
            .plainClickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(word.categoryEmoji, style = TextStyle(fontSize = 14.sp))
            Text(
                word.categoryName(language),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(fontFamily = family, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = 0.8f), shadow = shadow),
                modifier = Modifier.padding(start = 8.dp).weight(1f),
            )
            shownAtMillis?.let {
                Text(mediumDate(it), style = TextStyle(fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f), shadow = shadow), maxLines = 1)
            }
            FavoriteHeartButton(
                isFavorite = state.isFavorite(word),
                onToggle = onToggleFavorite,
                size = 17.dp,
                modifier = Modifier.overhang(horizontal = 10.dp, vertical = 10.dp),
            )
        }
        Text(
            word.theme(language),
            style = TextStyle(fontFamily = family, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White, shadow = shadow),
        )
        Text(
            "“${word.quote(language)}”",
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                fontFamily = family, fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f),
                lineHeight = lineHeightWithSpacing(family, 14f, 3f), shadow = shadow,
            ),
        )
        Text(
            "— ${word.reference(language)}",
            style = TextStyle(
                fontFamily = family, fontSize = 13.sp, fontWeight = FontWeight.Medium, fontStyle = FontStyle.Italic,
                color = Color.White.copy(alpha = 0.75f), shadow = shadow,
            ),
        )
    }
}

/** Favourites organised by topic, with filter chips (FavoritesListView). */
@Composable
private fun FavoritesList(state: UiState, onToggleFavorite: (JesusWord) -> Unit, onOpen: (JesusWord) -> Unit) {
    val language = state.language
    val favorites = state.favoriteWords
    if (favorites.isEmpty()) {
        EmptyState(
            icon = R.drawable.ic_favorite_outline,
            title = language.pick("No favourites yet", "இதுவரை பிடித்த வசனங்கள் இல்லை"),
            body = language.pick(
                "Tap the ♡ on any verse to save it here, ready whenever you need encouragement.",
                "எந்த வசனத்திலும் ♡ ஐத் தட்டி இங்கே சேமியுங்கள்",
            ),
            iconAlpha = 0.7f, titleAlpha = 0.9f, bodyAlpha = 0.75f,
        )
        return
    }

    var selectedTopic by rememberSaveable { mutableStateOf<String?>(null) }
    val byTopic = remember(favorites) { favorites.groupBy { it.category } }
    val topics = remember(byTopic) {
        VerseCategory.favouritesOrder.map { it.key }.filter { it in byTopic } +
            byTopic.keys.filter { key -> VerseCategory.favouritesOrder.none { it.key == key } }.sorted()
    }
    val visible = selectedTopic?.takeIf { it in topics }?.let(::listOf) ?: topics
    val family = themeFontFamily(state.theme, language)

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = listBottomPadding(),
        modifier = Modifier.fillMaxSize(),
    ) {
        item(key = "chips") {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.padding(bottom = 8.dp),
            ) {
                item {
                    TopicChip(language.pick("All", "அனைத்தும்"), favorites.size, selectedTopic == null || selectedTopic !in topics) {
                        selectedTopic = null
                    }
                }
                items(topics) { topic ->
                    val sample = byTopic.getValue(topic).first()
                    TopicChip("${sample.categoryEmoji} ${sample.categoryName(language)}", byTopic.getValue(topic).size, selectedTopic == topic) {
                        selectedTopic = topic
                    }
                }
            }
        }
        visible.forEach { topic ->
            val words = byTopic.getValue(topic)
            item(key = "header-$topic") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.animateItem().padding(start = 20.dp, end = 16.dp, top = 8.dp),
                ) {
                    val shadow = textShadow(0.5f, 2.dp)
                    Text(words.first().categoryEmoji, style = TextStyle(fontSize = 17.sp))
                    Text(
                        words.first().categoryName(language),
                        style = TextStyle(fontFamily = family, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White, shadow = shadow),
                    )
                    Text(
                        "${words.size}",
                        style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White),
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.25f), CapsuleShape)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    )
                }
            }
            items(words, key = { "fav-${it.id}" }) { word ->
                VerseRowCard(
                    word = word,
                    state = state,
                    shownAtMillis = null,
                    onToggleFavorite = { onToggleFavorite(word) },
                    onClick = { onOpen(word) },
                    modifier = Modifier.animateItem().padding(horizontal = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun TopicChip(title: String, count: Int, isOn: Boolean, onClick: () -> Unit) {
    Text(
        "$title · $count",
        maxLines = 1,
        style = TextStyle(
            fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
            color = if (isOn) Color.Black.copy(alpha = 0.8f) else Color.White,
        ),
        modifier = Modifier
            .background(if (isOn) Color.White.copy(alpha = 0.9f) else Color.Black.copy(alpha = 0.3f), CapsuleShape)
            .border(1.dp, Color.White.copy(alpha = 0.5f), CapsuleShape)
            .semantics {
                role = Role.Tab
                selected = isOn
            }
            .plainClickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    )
}

@Composable
private fun EmptyState(icon: Int, title: String, body: String, iconAlpha: Float, titleAlpha: Float, bodyAlpha: Float) {
    val shadow = textShadow(0.5f, 2.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        modifier = Modifier.fillMaxSize().padding(horizontal = 40.dp).padding(bottom = TabBarReservedHeight),
    ) {
        Icon(painterResource(icon), contentDescription = null, tint = Color.White.copy(alpha = iconAlpha), modifier = Modifier.size(60.dp))
        Text(title, style = TextStyle(fontSize = 22.sp, color = Color.White.copy(alpha = titleAlpha), textAlign = TextAlign.Center, shadow = shadow))
        Text(body, style = TextStyle(fontSize = 17.sp, color = Color.White.copy(alpha = bodyAlpha), textAlign = TextAlign.Center, shadow = shadow))
    }
}
