package com.iregina.jesuswords.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iregina.jesuswords.ui.main.MainScreen

/** Root of the UI (JesusWordsApp.swift). */
@Composable
fun JesusWordsRoot(viewModel: MainViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val current = state
    if (current == null) {
        // Content and settings load in a few milliseconds; match the splash colour meanwhile.
        Box(Modifier.fillMaxSize().background(SplashBackground))
        return
    }
    MainScreen(current, viewModel)
}

/** Same as @color/splash_background. */
val SplashBackground = Color(0xFF3B2050)
