package com.example.feiweather.android.utils

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt


class UIUtils {
    companion object {
        fun uvIndexToString(index: Double) : String {
            return when (index) {
                in 0.0..2.9 -> {
                    "Thấp";
                }
                in 3.0..5.9 -> {
                    "Trung bình";
                }
                in 6.0..7.9 -> {
                    "Cao";
                }
                in 8.0..10.9 -> {
                    "Rất cao";
                }
                else -> {
                    "Cực cao";
                }
            }
        }

        fun windspeedkphToBeaufort(spd: Double) : String {
            return when (spd.roundToInt()) {
                0 -> {
                    "Cấp 0"
                }
                in 1..5 -> {
                    "Cấp 1"
                }
                in 6..11 -> {
                    "Cấp 2"
                }
                in 12..19 -> {
                    "Cấp 3"
                }
                in 20..28 -> {
                    "Cấp 4"
                }
                in 29..38 -> {
                    "Cấp 5"
                }
                in 39..49 -> {
                    "Cấp 6"
                }
                in 50..61 -> {
                    "Cấp 7"
                }
                in 62..74 -> {
                    "Cấp 8"
                }
                in 75..88 -> {
                    "Cấp 9"
                }
                in 89..102 -> {
                    "Cấp 10"
                }
                in 103..117 -> {
                    "Cấp 11"
                }
                else -> {
                    "Cấp 12"
                }
            }
        }

        fun winddirToVietnamese(dir: String) : String {
            return when (dir) {
                "N" -> {
                    "Bắc"
                }
                "NNE" -> {
                    "Bắc Đông Bắc"
                }
                "NE" -> {
                    "Đông Bắc"
                }
                "ENE" -> {
                    "Đông Đông Bắc"
                }
                "E" -> {
                    "Đông"
                }
                "ESE" -> {
                    "Đông Đông Nam"
                }
                "SE" -> {
                    "Đông Nam"
                }
                "SSE" -> {
                    "Nam Đông Nam"
                }
                "S" -> {
                    "Nam"
                }
                "SSW" -> {
                    "Nam Tây Nam"
                }
                "SW" -> {
                    "Tây Nam"
                }
                "WSW" -> {
                    "Tây Tây Nam"
                }
                "W" -> {
                    "Tây"
                }
                "WNW" -> {
                    "Tây Tây Bắc"
                }
                "NW" -> {
                    "Tây Bắc"
                } else -> {
                    "Bắc Tây Bắc"
                }
            }
        }

        fun moonPhaseToVietnamese(phase: String) : String {
            return when(phase) {
                "New Moon" -> "Không trăng"
                "Waxing Crescent" -> "Trăng non"
                "First Quarter" -> "Trăng thượng huyền"
                "Waxing Gibbous" -> "Trăng trương huyền tròn dần"
                "Waning Crescent" -> "Trăng tàn"
                "Last Quarter" -> "Trăng hạ huyền"
                "Waning Gibbous" -> "Trăng trương huyền khuyết"
                else ->
                    "Trăng tròn"
            }
        }

        @Composable
        fun createShimmerBrush(): Brush {
            val colors: List<Color> = listOf(
                Color.LightGray.copy(0.9f), Color.LightGray.copy(0.1f), Color.LightGray.copy(0.5f)
            )

            val translateAni by rememberInfiniteTransition().animateFloat(
                initialValue = 0f,
                targetValue = 800f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )

            return Brush.linearGradient(
                colors = colors,
                start = Offset(10f,10f),
                end = Offset(translateAni, translateAni)
            )
        }
    }
}