package com.example.feiweather.android.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.feiweather.android.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .paint(
                        painter = painterResource(
                            id = R.drawable.loading
                        ), contentScale = ContentScale.FillBounds
                    )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.title),
                    contentDescription = "title",
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )
                Text("A product created by Fei",modifier = Modifier.padding(10.dp).align(Alignment.BottomCenter))
            }
        }
        val a = Intent(this, MainActivity::class.java)
        a.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            startActivity(a)
            delay(1000)
            finish()
        }
    }
}