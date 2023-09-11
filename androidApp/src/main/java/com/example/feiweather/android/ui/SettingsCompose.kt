package com.example.feiweather.android.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import com.example.feiweather.data.Settings
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun SettingsCompose(settings: Settings, onTapMenu: () -> Unit) {
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onTapMenu() }) {
                Icon(Icons.Default.Menu, "back")
            }
            Text("Cài đặt", modifier = Modifier.fillMaxWidth())
        }
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isExpanded = remember { mutableStateOf(false) }
            Text("Số ngày dự báo:  ")
            Column {
                OutlinedTextField(value = settings.days.toString(),
                    readOnly = true,
                    onValueChange = {},
                    modifier = Modifier.width(100.dp),
                    trailingIcon = {
                        Icon(if (isExpanded.value) Icons.Filled.KeyboardArrowUp
                        else Icons.Filled.KeyboardArrowDown,
                            "contentDescription",
                            Modifier.clickable { isExpanded.value = !isExpanded.value })
                    })
                DropdownMenu(
                    expanded = isExpanded.value,
                    onDismissRequest = { isExpanded.value = false },
                    modifier = Modifier.width(100.dp)
                ) {
                    repeat(14) { index ->
                        DropdownMenuItem(onClick = {
                            scope.launch {
                                dataStore.edit {
                                    it[preferencesKey<String>("settings")] = Json.encodeToString(
                                        Settings(
                                            days = index + 1,
                                            isC = settings.isC
                                        )
                                    )
                                }
                            }
                            isExpanded.value = false
                        }) {
                            Text((index + 1).toString())
                        }
                    }
                }
            }
        }
        Box(
            Modifier.height(30.dp)
        )
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isExpanded = remember { mutableStateOf(false) }
            Text("Đơn vị nhiệt độ:  ")
            Column {
                OutlinedTextField(value = if (settings.isC) "C" else "F",
                    readOnly = true,
                    onValueChange = {},
                    modifier = Modifier.width(100.dp),
                    trailingIcon = {
                        Icon(if (isExpanded.value) Icons.Filled.KeyboardArrowUp
                        else Icons.Filled.KeyboardArrowDown,
                            "contentDescription",
                            Modifier.clickable { isExpanded.value = !isExpanded.value })
                    })
                DropdownMenu(
                    expanded = isExpanded.value,
                    onDismissRequest = { isExpanded.value = false },
                    modifier = Modifier.width(100.dp)
                ) {

                    DropdownMenuItem(onClick = {
                        scope.launch {
                            dataStore.edit {
                                it[preferencesKey<String>("settings")] =
                                    Json.encodeToString(Settings(days = settings.days, isC = true))
                            }
                        }
                        isExpanded.value = false
                    }) {
                        Text("C")
                    }
                    DropdownMenuItem(onClick = {
                        scope.launch {
                            dataStore.edit {
                                it[preferencesKey<String>("settings")] =
                                    Json.encodeToString(Settings(days = settings.days, isC = false))
                            }
                        }
                        isExpanded.value = false
                    }) {
                        Text("F")
                    }

                }
            }
        }
    }
}
