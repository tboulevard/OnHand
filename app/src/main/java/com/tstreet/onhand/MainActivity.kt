package com.tstreet.onhand

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.content.ContextCompat
import com.tstreet.onhand.core.common.LocalCommonProvider
import com.tstreet.onhand.core.data.api.di.LocalDataProvider
import com.tstreet.onhand.core.ui.theming.OnHandTheme
import com.tstreet.onhand.nav.Navigation

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // To make status bar color match app background color...
            window.statusBarColor = ContextCompat.getColor(this, R.color.Grey10)
            OnHandTheme {
                CompositionLocalProvider(
                    LocalDataProvider provides application.appComponent.dataComponent,
                    LocalCommonProvider provides application.appComponent.commonComponent
                ) {
                    Navigation()
                }
            }
        }
    }
}