package com.pemmob.schedulyy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pemmob.schedulyy.ui.navigation.MainScaffold
import com.pemmob.schedulyy.ui.theme.SchedulyyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SchedulyyTheme(darkTheme = false) {
                MainScaffold()
            }
        }
    }
}
