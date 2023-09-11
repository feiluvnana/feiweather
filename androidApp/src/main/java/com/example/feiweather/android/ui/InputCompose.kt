package com.example.feiweather.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import com.example.feiweather.data.Location
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun InputCompose(onTapBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val text = remember { mutableStateOf("") }
    val result : MutableState<List<Location>?> = remember { mutableStateOf(listOf<Location>())}
    DisposableEffect(text.value) {
        val job = scope.launch {
            delay(500)
            result.value = api.getAutoComplete(text.value)
        }
        onDispose {
            job.cancel()
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 10.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onTapBack() }) {
                Icon(Icons.Default.ArrowBack, "back")
            }
            Text("Tìm kiếm thành phố", modifier = Modifier.fillMaxWidth())
        }
        OutlinedTextField(modifier = Modifier.fillMaxWidth(),
            label = { Text("Nhập thông tin tìm kiếm*") },
            value = text.value,
            onValueChange = {
                text.value = it
            })
        repeat(result.value?.size ?: 0) { index ->
            Box(modifier = Modifier
                .fillMaxWidth()
                .zIndex(1F)
                .background(color = Color.Gray)
                .padding(20.dp)
                .clickable {
                    scope.launch {
                        dataStore.edit {
                            if (Json
                                    .decodeFromString<List<Location>>(
                                        it[preferencesKey<String>("qlist")] ?: "[]"
                                    )
                                    .map { location ->
                                        location.id
                                    }
                                    .contains(result.value!![index].id)
                            ) return@edit
                            it[preferencesKey<String>("qlist")] = Json.encodeToString(
                                Json
                                    .decodeFromString<List<Location>>(
                                        it[preferencesKey("qlist")] ?: "[]"
                                    )
                                    .plus(result.value!![index])
                            )
                        }
                    }
                }) {
                Text(result.value!![index].name)
            }
            Divider()
        }
        if(result.value == null) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .zIndex(1F)
                .background(color = Color.Gray)
                .padding(20.dp)
                ) {
                Text("Không có thông tin tìm kiếm")
            }
        }
    }
}