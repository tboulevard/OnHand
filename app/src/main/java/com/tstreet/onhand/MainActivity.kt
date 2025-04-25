package com.tstreet.onhand

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import com.tstreet.onhand.core.data.impl.di.DaggerDataComponent
import com.tstreet.onhand.core.ui.theming.OnHandTheme
import com.tstreet.onhand.nav.Navigation

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("[OnHand]", "MainActivity onCreate")
        val commonComponent = application.appComponent.commonComponent
        val dataComponent = DaggerDataComponent.builder().commonComponent(commonComponent).build()
        setContent {
            // To make status bar color match app background color...
            window.statusBarColor = ContextCompat.getColor(this, R.color.Grey10)
            OnHandTheme {
                Navigation(
                    commonComponent,
                    dataComponent
                )
            }
        }
    }
}