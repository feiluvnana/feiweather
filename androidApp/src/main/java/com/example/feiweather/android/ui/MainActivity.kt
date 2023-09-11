package com.example.feiweather.android.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.feiweather.clients.WeatherAPI
import com.example.feiweather.data.Settings
import com.example.feiweather.data.Location
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

lateinit var dataStore: DataStore<Preferences>
val api = WeatherAPI.create()

class MainActivity : ComponentActivity() {
    @SuppressLint("CoroutineCreationDuringComposition")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStore = createDataStore(name = "WeatherDataStorage")
        val qflow = dataStore.data.map {
            it[preferencesKey<Int>("qIndex")] ?: 0
        }
        val qlistflow = dataStore.data.map {
            Json.decodeFromString<List<Location>>(it[preferencesKey("qlist")] ?: "[]")
        }
        val settingsflow = dataStore.data.map {
            Json.decodeFromString<Settings>(it[preferencesKey("settings")] ?: Json.encodeToString(Settings(days = 7, isC = true)))
        }

        setContent {
            val navController = rememberNavController()
            val scaffoldState = rememberScaffoldState()
            val scope = rememberCoroutineScope()
            val settingsState = settingsflow.collectAsState(initial = Settings(days = 7, isC = true))
            val qIndexState: State<Int> = qflow.collectAsState(initial = 0)
            val qListState: State<List<Location>> = qlistflow.collectAsState(initial = listOf())
            MyApplicationTheme {
                fun openDrawer() {
                    scope.launch { scaffoldState.drawerState.open() }
                }

                fun openTextField() {
                    navController.navigate("input")
                }

                fun returnHome() {
                    navController.navigate("home")
                }

                Scaffold(scaffoldState = scaffoldState,
                    drawerElevation = Dp(0F),
                    drawerScrimColor = Color.Transparent,
                    drawerBackgroundColor = Color.Black.copy(alpha = 0.5F),
                    drawerContent = {
                        LazyColumn(modifier = Modifier.height(LocalConfiguration.current.screenHeightDp.dp / 3)) {
                            items(qListState.value.size) { index ->
                                Row(
                                    modifier = Modifier
                                        .padding(20.dp)
                                        .fillMaxWidth()
                                        .clickable {
                                            scope.launch {
                                                dataStore.edit {
                                                    it[preferencesKey<Int>("qIndex")] = index
                                                }
                                            }
                                        },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(qListState.value[index].name, color = Color.White)
                                    if (index != qIndexState.value) IconButton(
                                        onClick = {
                                            scope.launch {
                                                dataStore.edit {
                                                    it[preferencesKey<String>("qlist")] =
                                                        Json.encodeToString(Json.decodeFromString<List<Location>>(
                                                            it[preferencesKey("qlist")]
                                                                ?: "[]"
                                                        ).filterIndexed { idx, _ ->
                                                            idx != index
                                                        })
                                                }
                                            }
                                        },
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "delete",
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }
                        Divider()
                        Button(
                            onClick = { navController.navigate("home") },
                            elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                            colors = ButtonDefaults.buttonColors(Color.Transparent),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Home, "home", tint = Color.White)
                            Text("Trang chủ", color = Color.White)
                        }
                        Button(
                            onClick = { navController.navigate("settings") },
                            elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                            colors = ButtonDefaults.buttonColors(Color.Transparent),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Settings, "settings", tint = Color.White)
                            Text("Cài đặt", color = Color.White)
                        }
                        Button(
                            onClick = { navController.navigate("about") },
                            elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                            colors = ButtonDefaults.buttonColors(Color.Transparent),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Info, "about", tint = Color.White)
                            Text("Thông tin ứng dụng", color = Color.White)
                        }
                    }) { contentPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(contentPadding)
                    ) {
                        composable("home") {
                            HomeCompose(
                                onTapMenu = ::openDrawer,
                                onTapAdd = ::openTextField,
                                q = if (qListState.value.isEmpty()) "" else qListState.value[qIndexState.value].name,
                                settings = settingsState.value
                            )
                        }
                        composable("input") { InputCompose(onTapBack = ::returnHome) }
                        composable("settings") { SettingsCompose(settings = settingsState.value, onTapMenu = ::openDrawer) }
                        composable("about") { AboutCompose(onTapMenu = ::openDrawer)}
                    }
                }
            }
        }
    }
}