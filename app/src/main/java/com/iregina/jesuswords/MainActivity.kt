package com.iregina.jesuswords

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.iregina.jesuswords.ui.JesusWordsRoot
import com.iregina.jesuswords.ui.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // White status/navigation icons over the always-colourful theme backgrounds.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        setContent { JesusWordsRoot(viewModel) }
    }

    override fun onStart() {
        super.onStart()
        viewModel.onAppForeground()
    }
}
