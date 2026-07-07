package com.hiddenrole.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.compose.rememberNavController
import com.hiddenrole.app.data.HistoryStorage
import com.hiddenrole.app.data.PresetStorage
import com.hiddenrole.app.data.RosterStorage
import com.hiddenrole.app.data.SettingsStorage
import com.hiddenrole.app.navigation.AppNavHost
import com.hiddenrole.app.state.GameStateHolder
import com.hiddenrole.app.ui.theme.HiddenRoleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val presetStorage = remember { PresetStorage(applicationContext) }
            val historyStorage = remember { HistoryStorage(applicationContext) }
            val rosterStorage = remember { RosterStorage(applicationContext) }
            val settingsStorage = remember { SettingsStorage(applicationContext) }
            val stateHolder = remember {
                GameStateHolder(presetStorage, historyStorage, rosterStorage, settingsStorage)
            }
            val navController = rememberNavController()

            HiddenRoleTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppNavHost(navController = navController, state = stateHolder)
                    }
                }
            }
        }
    }
}
