package com.tstreet.onhand

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tstreet.onhand.nav.setupNavigation
import com.tstreet.onhand.ui.theme.OnHandTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Example of how to retrieve appComponent, unused for now
        //val onHandAppComponent = (application as OnHandApplication).appComponent

        setContent {
            OnHandTheme {
                setupNavigation(this.application)
            }
        }
    }
}