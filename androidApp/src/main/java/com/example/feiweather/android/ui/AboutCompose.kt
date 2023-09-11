package com.example.feiweather.android.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity

@Composable
fun AboutCompose(onTapMenu: () -> Unit) {
    val context = LocalContext.current
    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onTapMenu() }) {
                Icon(Icons.Default.Menu, "back")
            }
            Text("Thông tin ứng dụng", modifier = Modifier.fillMaxWidth())
        }
        Column {
            Text("Ứng dụng FeiWeather")
            Text("Phiên bản 1.0.0")
            Text("Đây là phiên bản đầu tiên của ứng dụng FeiWeather trên Android. Đội ngũ phát triển vẫn đang nỗ lực để cải thiện ứng dụng theo từng ngày.")
            Row (verticalAlignment = Alignment.CenterVertically){
                Text("Mọi thông báo về lỗi có thể gửi qua email ")
                TextButton(onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "message/rfc822"
                            putExtra(Intent.EXTRA_EMAIL, arrayOf("ntd271102@gmail.com"))
                            putExtra(Intent.EXTRA_SUBJECT, "[FeiWeather] Bug Report")
                        }
                        startActivity(context, intent, null)
                    } catch (e: ActivityNotFoundException) {
                        // TODO: Handle case where no email app is available
                    } catch (t: Throwable) {
                        // TODO: Handle potential other type of exceptions
                    }
                }, contentPadding = PaddingValues(0.dp)) {
                    Text(
                        "tại đây",
                        color = Color.Blue,
                        style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            textDecoration = TextDecoration.Underline
                        )
                    )
                }
            }
        }
    }
}