package com.iregina.jesuswords.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.text.format.DateFormat
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.iregina.jesuswords.BuildConfig
import com.iregina.jesuswords.R
import com.iregina.jesuswords.data.AppLanguage
import com.iregina.jesuswords.ui.UiState
import com.iregina.jesuswords.ui.components.CapsuleShape
import com.iregina.jesuswords.ui.components.PageHeader
import com.iregina.jesuswords.ui.components.TabBarSpacer
import com.iregina.jesuswords.ui.components.ThemeBackground
import com.iregina.jesuswords.ui.components.contentCard
import com.iregina.jesuswords.ui.components.plainClickable
import com.iregina.jesuswords.ui.theme.lineHeightWithSpacing
import com.iregina.jesuswords.ui.theme.themeFontFamily
import kotlinx.coroutines.delay

/** The Settings page (SettingsView.swift): reminder time, Show Tips Again, About. */
@Composable
fun SettingsScreen(
    state: UiState,
    onSaveReminder: (hour: Int, minute: Int) -> Unit,
    onShowTipsAgain: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val language = state.language
    val family = themeFontFamily(state.theme, language)

    Box(modifier.fillMaxSize()) {
        ThemeBackground(state.theme)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)),
        ) {
            PageHeader(
                title = language.pick("Settings", "அமைப்புகள்"),
                subtitle = language.pick("Customize your experience", "உங்கள் அனுபவத்தைத் தனிப்பயனாக்குங்கள்"),
                titleFamily = family,
                modifier = Modifier.padding(top = 20.dp),
            )
            ReminderCard(state, onSaveReminder)
            ShowTipsRow(language, onShowTipsAgain)
            AboutCard(language, family)
            TabBarSpacer()
        }
    }
}

@Composable
private fun CardTitle(icon: Int, title: String, family: androidx.compose.ui.text.font.FontFamily) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Icon(painterResource(icon), contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        Text(
            title,
            style = TextStyle(fontFamily = family, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White),
            modifier = Modifier.padding(start = 10.dp),
        )
    }
}

@Composable
private fun ReminderCard(state: UiState, onSaveReminder: (Int, Int) -> Unit) {
    val context = LocalContext.current
    val language = state.language
    var hour by rememberSaveable(state.settings.reminderHour) { mutableIntStateOf(state.settings.reminderHour) }
    var minute by rememberSaveable(state.settings.reminderMinute) { mutableIntStateOf(state.settings.reminderMinute) }
    var savedCount by remember { mutableIntStateOf(0) }
    var showingSaved by remember { mutableStateOf(false) }

    LaunchedEffect(savedCount) {
        if (savedCount > 0) {
            showingSaved = true
            delay(2000)
            showingSaved = false
        }
    }

    fun save(granted: Boolean) {
        onSaveReminder(hour, minute)
        savedCount++
        if (!granted) {
            Toast.makeText(
                context,
                language.pick(
                    "Notifications are off. Allow them in Settings to get your daily reminder.",
                    "அறிவிப்புகள் முடக்கப்பட்டுள்ளன. தினசரி நினைவூட்டலுக்கு அமைப்புகளில் அனுமதியுங்கள்.",
                ),
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    // Notification permission is asked only here, when the user chooses a reminder.
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> save(granted) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().contentCard().padding(20.dp),
    ) {
        CardTitle(R.drawable.ic_notifications, language.pick("Reminder Time", "நினைவூட்டல் நேரம்"), themeFontFamily(state.theme, language))

        TimeWheel(
            hour = hour,
            minute = minute,
            is24Hour = DateFormat.is24HourFormat(context),
            onChange = { h, m ->
                hour = h
                minute = m
            },
        )

        val fill by animateColorAsState(
            if (showingSaved) Color(0xFF34C759).copy(alpha = 0.4f) else Color.White.copy(alpha = 0.2f),
            label = "savedFill",
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(fill, CapsuleShape)
                .border(1.dp, Color.White.copy(alpha = 0.4f), CapsuleShape)
                .semantics { role = Role.Button }
                .plainClickable {
                    val needsAsk = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                    if (needsAsk) permission.launch(Manifest.permission.POST_NOTIFICATIONS) else save(granted = true)
                }
                .padding(vertical = 14.dp),
        ) {
            Icon(
                painterResource(if (showingSaved) R.drawable.ic_check_circle else R.drawable.ic_notifications_active),
                contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp),
            )
            Text(
                if (showingSaved) language.pick("Saved!", "சேமிக்கப்பட்டது!")
                else language.pick("Update Reminder", "நினைவூட்டலைப் புதுப்பி"),
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White),
            )
        }
    }
}

@Composable
private fun ShowTipsRow(language: AppLanguage, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .contentCard()
            .semantics { role = Role.Button }
            .plainClickable(onClick = onClick)
            .padding(18.dp),
    ) {
        Icon(painterResource(R.drawable.ic_lightbulb), contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        Text(
            language.pick("Show Tips Again", "குறிப்புகளை மீண்டும் காட்டு"),
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White),
            modifier = Modifier.padding(start = 10.dp).weight(1f),
        )
        Icon(painterResource(R.drawable.ic_chevron_right), contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun AboutCard(language: AppLanguage, family: androidx.compose.ui.text.font.FontFamily) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().contentCard().padding(20.dp),
    ) {
        CardTitle(R.drawable.ic_info, language.pick("About", "பற்றி"), family)
        Text(
            language.pick(
                "Jesus Words brings you 365 authentic spoken words of Jesus Christ from the New Testament. Start each day with His wisdom, comfort, and encouragement.",
                "இயேசுவின் வார்த்தைகள் புதிய ஏற்பாட்டிலிருந்து இயேசு கிறிஸ்துவின் 365 உண்மையான வார்த்தைகளை உங்களுக்குக் கொண்டுவருகிறது. ஒவ்வொரு நாளையும் அவரது ஞானம், ஆறுதல் மற்றும் ஊக்கத்துடன் தொடங்குங்கள்.",
            ),
            style = TextStyle(
                fontFamily = family, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f),
                lineHeight = lineHeightWithSpacing(family, 14f, 4f),
            ),
        )
        Box(Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.3f)))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AboutRow(language.pick("Total Words", "மொத்த வார்த்தைகள்"), "365")
            AboutRow(language.pick("Categories", "வகைகள்"), "8")
            AboutRow(language.pick("Source", "ஆதாரம்"), language.pick("New Testament (NIV)", "புதிய ஏற்பாடு (NIV)"))
            AboutRow(language.pick("Version", "பதிப்பு"), BuildConfig.VERSION_NAME)
        }
    }
}

@Composable
private fun AboutRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = TextStyle(fontSize = 14.sp, color = Color.White.copy(alpha = 0.6f)), modifier = Modifier.weight(1f))
        Spacer(Modifier.size(12.dp))
        Text(value, style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = 0.9f)))
    }
}
