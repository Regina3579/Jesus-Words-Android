package com.iregina.jesuswords

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iregina.jesuswords.data.AppContent
import com.iregina.jesuswords.data.AppLanguage
import com.iregina.jesuswords.data.ContentRepository
import com.iregina.jesuswords.ui.components.DropShadow
import com.iregina.jesuswords.ui.components.LatinCrossIcon
import com.iregina.jesuswords.ui.components.ThemeBackground
import com.iregina.jesuswords.ui.theme.textShadow
import com.iregina.jesuswords.ui.theme.themeFontFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        setContent { BackgroundPreview() }
    }
}

/** Milestone 1 preview: the default theme background with the cross and title. */
@Composable
private fun BackgroundPreview() {
    val context = LocalContext.current
    val content by produceState<AppContent?>(null) {
        value = withContext(Dispatchers.Default) { ContentRepository.get(context) }
    }
    val theme = content?.theme(null) ?: return
    Box(Modifier.fillMaxSize()) {
        ThemeBackground(theme)
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            LatinCrossIcon(
                height = 36.dp,
                shadows = listOf(DropShadow(0.55f, 2.dp), DropShadow(0.35f, 8.dp)),
            )
            Text(
                text = "Jesus Words",
                color = Color.White,
                style = TextStyle(
                    fontFamily = themeFontFamily(theme, AppLanguage.ENGLISH),
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    shadow = textShadow(0.6f, 2.dp),
                ),
            )
        }
    }
}
