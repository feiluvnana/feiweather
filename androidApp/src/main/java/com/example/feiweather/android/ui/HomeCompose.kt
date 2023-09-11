package com.example.feiweather.android.ui

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import coil.compose.rememberAsyncImagePainter
import com.example.feiweather.android.R
import com.example.feiweather.android.utils.UIUtils
import com.example.feiweather.data.CurrentWeather
import com.example.feiweather.data.ForecastWeather
import com.example.feiweather.data.Settings
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.util.*
import kotlin.math.*

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeCompose(
    onTapMenu: () -> Unit, q: String, onTapAdd: () -> Unit, settings: Settings
) {
    val instant = Clock.System.now()
    val hour: Int = instant.toLocalDateTime(TimeZone.currentSystemDefault()).hour
    val isDay: Boolean = hour in 6..18
    if (q.isEmpty()) {
        Box(
            modifier = Modifier
                .paint(
                    painter = painterResource(
                        id = (if (!isDay) R.drawable.bg_night else R.drawable.bg_day)
                    ), contentScale = ContentScale.FillBounds
                )
                .fillMaxSize()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .height(Dp(60F))
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        modifier = Modifier, onClick = onTapMenu
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Menu"
                        )
                    }
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Chưa có địa điểm nào",
                            fontSize = 12.sp
                        )
                    }
                    IconButton(
                        onClick = { onTapAdd() }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    } else {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val forecastWeather = remember { mutableStateOf<ForecastWeather?>(null) }
        val isRefresh = remember { mutableStateOf(false) }
        fun refresh() = scope.launch {
            isRefresh.value = true
            forecastWeather.value = null
            forecastWeather.value = api.getForecastWeather(days = settings.days, q = q)
            if (forecastWeather.value == null) {
                Toast.makeText(
                    context,
                    "Không thể lấy dữ liệu thời tiết. Hãy thử lại sau.",
                    Toast.LENGTH_LONG
                ).show()
            }
            isRefresh.value = false
        }

        val pullRefreshState =
            rememberPullRefreshState(refreshing = isRefresh.value, onRefresh = ::refresh)
        LaunchedEffect(key1 = q) {
            forecastWeather.value = null
            forecastWeather.value = api.getForecastWeather(days = settings.days, q = q)
            if (forecastWeather.value == null) {
                Toast.makeText(
                    context,
                    "Không thể lấy dữ liệu thời tiết. Hãy thử lại sau.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        Box(
            modifier = Modifier
                .paint(
                    painter = painterResource(
                        id = (if (!isDay) R.drawable.bg_night else R.drawable.bg_day)
                    ), contentScale = ContentScale.FillBounds
                )
                .pullRefresh(pullRefreshState, enabled = true)
        ) {
            Column {
                if (forecastWeather.value == null) {
                    Row(
                        Modifier
                            .height(Dp(60F))
                            .fillMaxWidth()
                            .padding(10.dp)
                            .background(brush = UIUtils.createShimmerBrush())
                    ) {
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .height(Dp(60F))
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            modifier = Modifier, onClick = onTapMenu
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Menu"
                            )
                        }
                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(forecastWeather.value?.location?.name ?: "", fontSize = 12.sp)
                            Text(
                                if (forecastWeather.value?.location?.localtime == null) "" else "Thời gian: ${forecastWeather.value?.location?.localtime}",
                                fontSize = 12.sp
                            )
                        }
                        IconButton(
                            onClick = { onTapAdd() }) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                LazyColumn(
                    modifier = Modifier.padding(bottom = 20.dp),
                ) {


                    item {
                        if (forecastWeather.value != null) {
                            FirstHomeCompose(currentWeather = forecastWeather.value!!.currentWeather, settings = settings)
                        } else {
                            LoadingHomeCompose()
                        }
                    }

                    item {
                        if (forecastWeather.value != null) SecondHomeCompose(forecastWeather = forecastWeather.value!!, settings = settings)
                    }

                    item {
                        if (forecastWeather.value != null) Box(modifier = Modifier.height(30.dp))
                    }

                    item {
                        if (forecastWeather.value != null) ThirdHomeCompose(currentWeather = forecastWeather.value!!.currentWeather, settings = settings)
                    }

                    item {
                        if (forecastWeather.value != null) Box(modifier = Modifier.height(30.dp))
                    }

                    item {
                        if (forecastWeather.value != null) FourthHomeCompose(currentWeather = forecastWeather.value!!.currentWeather)
                    }

                    item {
                        if (forecastWeather.value != null) Box(modifier = Modifier.height(30.dp))
                    }

                    item {
                        if (forecastWeather.value != null) FifthHomeCompose(forecastWeather = forecastWeather.value!!)
                    }

                    item {
                        if (forecastWeather.value != null) Box(modifier = Modifier.height(30.dp))
                    }

                    item {
                        if (forecastWeather.value != null) SixthHomeCompose(forecastWeather = forecastWeather.value!!)
                    }
                }
            }
            PullRefreshIndicator(
                refreshing = isRefresh.value,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }


}

@Composable
fun LoadingHomeCompose() {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .height(LocalConfiguration.current.screenHeightDp.dp - 60.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(110.dp)
                    .width(150.dp)
                    .background(brush = UIUtils.createShimmerBrush())
            ) {

            }
            Box(
                modifier = Modifier
                    .height(170.dp)
                    .padding(bottom = 20.dp, top = 10.dp)
                    .width(200.dp)
                    .background(brush = UIUtils.createShimmerBrush())
            ) {}
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(100.dp)
                .then(
                    if (LocalConfiguration.current.orientation == ORIENTATION_PORTRAIT) Modifier.background(
                        brush = UIUtils.createShimmerBrush()
                    ) else Modifier
                )
        ) {}

    }
}

@Composable
fun FirstHomeCompose(currentWeather: CurrentWeather, settings: Settings) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .height(LocalConfiguration.current.screenHeightDp.dp - 60.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                currentWeather.location.name,
                fontSize = 40.sp,
                fontWeight = FontWeight.W200,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    currentWeather.current.condition.text, modifier = Modifier.weight(2F, false)
                )
                Image(
                    painter = rememberAsyncImagePainter("https:${currentWeather.current.condition.icon}"),
                    contentDescription = "image",
                    modifier = Modifier
                        .size(40.dp)
                        .weight(1F, false)
                )
            }
            Row {
                Text(
                    "${(if(settings.isC)currentWeather.current.temp_c else currentWeather.current.temp_f).toInt()}°",
                    fontSize = 100.sp,
                    fontWeight = FontWeight.W600,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
        if (LocalConfiguration.current.orientation == ORIENTATION_PORTRAIT) Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .padding(20.dp)
                .border(
                    width = Dp(2F),
                    color = Color.White.copy(alpha = 0.7F),
                    shape = RoundedCornerShape(Dp(30F))
                )
                .height(Dp(100F))
                .fillMaxWidth()
                .background(
                    color = Color.White.copy(alpha = 0.3F), shape = RoundedCornerShape(Dp(30F))
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(Dp(5F))
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .weight(1F)
            ) {
                Text("${currentWeather.current.humidity}%")
                Text("Độ ẩm")
            }
            Divider(
                modifier = Modifier
                    .padding(vertical = Dp(10F))
                    .fillMaxHeight()
                    .width(Dp(2F)),
                color = Color.White.copy(alpha = 0.7F)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(Dp(5F))
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .weight(1F)
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = null,
                        modifier = Modifier.rotate(currentWeather.current.wind_degree.toFloat())
                    )
                    Text("${currentWeather.current.wind_kph.roundToInt()}km/h")
                }
                Text("Gió")
            }
            Divider(
                modifier = Modifier
                    .padding(vertical = Dp(10F))
                    .fillMaxHeight()
                    .width(Dp(2F)),
                color = Color.White.copy(alpha = 0.7F)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(Dp(5F))
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .weight(1F)
            ) {
                Text("${currentWeather.current.uv}")
                Text("Chỉ số UV")
            }
        } else Box(modifier = Modifier.fillMaxWidth())
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SecondHomeCompose(forecastWeather: ForecastWeather, settings: Settings) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .background(color = Color.Black.copy(alpha = 0.5F))
            .padding(10.dp),
    ) {
        Text("Dự báo")
        Divider()
        LazyRow {
            val temp: List<Int> = forecastWeather.forecast.forecastday[0].hour.map {
                (if(settings.isC)it.temp_c else it.temp_f).roundToInt()
            }
            val coldest: Int = temp.min()
            val hottest: Int = temp.max()
            items(forecastWeather.forecast.forecastday[0].hour.size) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val isColdest: Boolean = temp[it] == coldest
                    val isHottest: Boolean = temp[it] == hottest
                    Text(forecastWeather.forecast.forecastday[0].hour[it].time.substring(startIndex = forecastWeather.forecast.forecastday[0].hour[it].time.length - 5))
                    Box(
                        modifier = Modifier.size(40.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter("https:${forecastWeather.forecast.forecastday[0].hour[it].condition.icon}"),
                            contentDescription = "image",
                            modifier = Modifier.size(30.dp)
                        )
                        Text(
                            if (forecastWeather.forecast.forecastday[0].hour[it].chance_of_rain > 0) "${forecastWeather.forecast.forecastday[0].hour[it].chance_of_rain}" else "",
                            modifier = Modifier.align(
                                Alignment.TopEnd
                            ),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.W500
                        )
                    }
                    Text(
                        "${temp[it]}°",
                        color = if (isColdest) Color(0xff799fcb) else if (isHottest) Color(
                            0xfff9665e
                        ) else Color.White
                    )
                }
            }
        }
        Column {
            repeat(forecastWeather.forecast.forecastday.size) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dp.Hairline)
                ) {
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    drawLine(
                        color = Color.White,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        pathEffect = pathEffect
                    )
                }
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        LocalDate.parse(forecastWeather.forecast.forecastday[it].date).dayOfWeek.getDisplayName(
                            java.time.format.TextStyle.FULL, Locale("vi_VN")
                        )
                    )
                    Row {
                        Box(
                            modifier = Modifier.size(40.dp)
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter("https:${forecastWeather.forecast.forecastday[it].day.condition.icon}"),
                                contentDescription = "image",
                                modifier = Modifier.size(30.dp)
                            )
                            Text(
                                if (forecastWeather.forecast.forecastday[it].day.daily_chance_of_rain > 0) "${forecastWeather.forecast.forecastday[it].day.daily_chance_of_rain}" else "",
                                modifier = Modifier.align(
                                    Alignment.TopEnd
                                ),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.W500
                            )
                        }
                        Box(modifier = Modifier.width(60.dp)) {

                        }
                        Text(
                            "${(if(settings.isC)forecastWeather.forecast.forecastday[it].day.mintemp_c else forecastWeather.forecast.forecastday[it].day.mintemp_f).roundToInt()}°",
                            color = Color(0xff799fcb)
                        )
                        Box(modifier = Modifier.width(20.dp)) {

                        }
                        Text(
                            "${(if(settings.isC)forecastWeather.forecast.forecastday[it].day.maxtemp_c else forecastWeather.forecast.forecastday[it].day.maxtemp_f).roundToInt()}°",
                            color = Color(0xfff9665e)
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ThirdHomeCompose(currentWeather: CurrentWeather, settings: Settings) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .background(color = Color.Black.copy(alpha = 0.5F))
            .padding(10.dp),
    ) {
        Text("Chi tiết")
        Divider()
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberAsyncImagePainter("https:${currentWeather.current.condition.icon}"),
                contentDescription = "image",
                modifier = Modifier
                    .size(100.dp)
                    .weight(1F)
            )
            Column(modifier = Modifier.weight(2F)) {
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Cảm thấy như:")
                    Text("${(if(settings.isC)currentWeather.current.feelslike_c else currentWeather.current.feelslike_f).roundToInt()}°")
                }
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dp.Hairline)
                ) {
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    drawLine(
                        color = Color.White,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        pathEffect = pathEffect
                    )
                }
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Độ ẩm:")
                    Text("${currentWeather.current.humidity}%")
                }
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dp.Hairline)
                ) {
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    drawLine(
                        color = Color.White,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        pathEffect = pathEffect
                    )
                }
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tầm nhìn:")
                    Text("${currentWeather.current.vis_km}km")
                }
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dp.Hairline)
                ) {
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    drawLine(
                        color = Color.White,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        pathEffect = pathEffect
                    )
                }
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Chỉ số UV:")
                    Text("${UIUtils.uvIndexToString(currentWeather.current.uv)} (${currentWeather.current.uv})")
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FourthHomeCompose(currentWeather: CurrentWeather) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .background(color = Color.Black.copy(alpha = 0.5F))
            .padding(10.dp),
    ) {
        Text("Gió và khí áp")
        Divider()
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.wind),
                contentDescription = "wind",
                modifier = Modifier
                    .size(100.dp)
                    .weight(1F)
            )
            Column(modifier = Modifier.weight(2F)) {
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Gió:")
                    Text("${currentWeather.current.wind_kph.roundToInt()}km/h")
                }
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dp.Hairline)
                ) {
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    drawLine(
                        color = Color.White,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        pathEffect = pathEffect
                    )
                }
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Cấp gió:")
                    Text(
                        UIUtils.windspeedkphToBeaufort(currentWeather.current.wind_kph),
                        style = TextStyle(
                            fontStyle = if (UIUtils.windspeedkphToBeaufort(currentWeather.current.wind_kph).length == 6) FontStyle.Italic else FontStyle.Normal, fontWeight = FontWeight.W500
                        )
                    )
                }
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dp.Hairline)
                ) {
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    drawLine(
                        color = Color.White,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        pathEffect = pathEffect
                    )
                }
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Hướng gió:")
                    Text(UIUtils.winddirToVietnamese(currentWeather.current.wind_dir))
                }
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dp.Hairline)
                ) {
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    drawLine(
                        color = Color.White,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        pathEffect = pathEffect
                    )
                }
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Khí áp:")
                    Text("${currentWeather.current.pressure_mb.toInt()}mBar")
                }

            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FifthHomeCompose(forecastWeather: ForecastWeather) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .background(color = Color.Black.copy(alpha = 0.5F))
            .padding(10.dp),
    ) {
        val chatEntryModel =
            entryModelOf(forecastWeather.forecast.forecastday[0].hour.mapIndexed { index, it ->
                entryOf(index, it.precip_mm)
            })
        Text("Lượng mưa")
        Divider()
        Chart(
            chart = lineChart(spacing = 60.dp), model = chatEntryModel,
            startAxis = rememberStartAxis(valueFormatter = { value, _ ->
                "${(value * 100.0).roundToLong() / 100.0}mm"
            }),
            bottomAxis = rememberBottomAxis(valueFormatter = { value, _ ->
                "${value.toInt()}h"
            }),
        )
    }
}

@OptIn(ExperimentalTextApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SixthHomeCompose(forecastWeather: ForecastWeather) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .background(color = Color.Black.copy(alpha = 0.5F))
            .padding(10.dp),
    ) {
        fun timeToSecond(str: String): Int {
            var h = (str.substring(0..1)).toInt()
            val m = (str.substring(3..4)).toInt()
            if (str.length == 5) {
                return h * 3600 + m * 60
            }
            if (str[6] == 'A') {
                if (h == 12) h = 0
            } else {
                if (h != 12) h += 12
            }
            return h * 3600 + m * 60
        }

        val config = LocalConfiguration.current
        val astro = forecastWeather.forecast.forecastday[0].astro
        val textMeasure = rememberTextMeasurer()
        //Sun
        val ratioSun = (timeToSecond(astro.sunset) - timeToSecond(astro.sunrise)) / 86400F
        val ratioSunset = timeToSecond(astro.sunset) / 86400F
        val ratioSunrise = timeToSecond(astro.sunrise) / 86400F
        var ratioCurrent =
            timeToSecond(forecastWeather.current.last_updated.substring(11..15)) / 86400F
        if (ratioCurrent >= ratioSunset) ratioCurrent = ratioSunset
        if (ratioCurrent <= ratioSunrise) ratioCurrent = ratioSunrise
        val currentAngle = acos(
            ((ratioSunset + ratioSunrise) / 2F - ratioCurrent) / (ratioSun / 2F)
        ) * 180 / PI.toFloat()
        val sunPainter =
            rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.sun))
        //Moon
        val moonImage = ImageBitmap.imageResource(
            id = when (astro.moon_phase) {
                "New Moon" -> R.drawable.moon_new_moon
                "Waxing Crescent" -> R.drawable.moon_waxing_crescent
                "First Quarter" -> R.drawable.moon_first_quarter
                "Waxing Gibbous" -> R.drawable.moon_waxing_gibbous
                "Waning Crescent" -> R.drawable.moon_waning_crescent
                "Last Quarter" -> R.drawable.moon_last_quarter
                "Waning Gibbous" -> R.drawable.moon_waning_gibbous
                else -> R.drawable.moon_full_moon
            }
        )

        Text("Mặt trời và mặt trăng")
        Divider()
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height((config.screenWidthDp - 60).dp / 2F + 30.dp)
        ) {
            val drawWidth = (config.screenWidthDp - 60).dp.toPx()
            val sunZone = Path().apply {
                moveTo(drawWidth * ratioSunrise, drawWidth / 2)
                arcTo(
                    rect = Rect(
                        offset = Offset(
                            drawWidth * ratioSunrise, drawWidth / 2 - drawWidth * ratioSun / 2
                        ), size = Size(drawWidth * ratioSun, drawWidth * ratioSun)
                    ),
                    startAngleDegrees = -180F,
                    sweepAngleDegrees = currentAngle,
                    forceMoveTo = true
                )
                lineTo(drawWidth * ratioCurrent, drawWidth / 2)
                lineTo(drawWidth * ratioSunrise, drawWidth / 2)
                close()
            }
            val sunIconHeight =
                sqrt((ratioSun / 2F) * (ratioSun / 2F) - ((ratioSunset + ratioSunrise) / 2F - ratioCurrent) * ((ratioSunset + ratioSunrise) / 2F - ratioCurrent)) * drawWidth

            translate(
                left = drawWidth * ratioCurrent - 7.5.dp.toPx(),
                top = (drawWidth / 2 - sunIconHeight) - 7.5.dp.toPx()
            ) {
                with(sunPainter) {
                    draw(
                        size = Size(15.dp.toPx(), 15.dp.toPx()),
                        colorFilter = ColorFilter.tint(color = Color.Yellow)
                    )
                }
            }

            scale(0.5F, Offset(5.dp.toPx(), 5.dp.toPx())) {
                drawImage(
                    image = moonImage,
                    topLeft = Offset(5.dp.toPx(), 5.dp.toPx()),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
            drawText(
                textMeasurer = textMeasure,
                text = UIUtils.moonPhaseToVietnamese(astro.moon_phase),
                style = TextStyle(
                    color = Color.White,
                ),
                topLeft = Offset(30.sp.toPx(), 5.sp.toPx())
            )
            drawText(
                textMeasurer = textMeasure,
                text = "Trăng mọc lúc ${astro.moonrise}, lặn lúc ${astro.moonset}",
                style = TextStyle(
                    color = Color.White,
                ),
                topLeft = Offset(5.sp.toPx(), 25.sp.toPx())
            )


            drawPath(
                path = sunZone, color = Color.Yellow.copy(alpha = 0.3F)
            )

            drawArc(
                color = Color.White,
                startAngle = -180F,
                sweepAngle = 180F,
                useCenter = false,
                topLeft = Offset(
                    drawWidth * ratioSunrise, drawWidth / 2 - drawWidth * ratioSun / 2
                ),
                size = Size(drawWidth * ratioSun, drawWidth * ratioSun),
                style = Stroke(width = 1F),
                alpha = 0.8F
            )
            drawPoints(
                points = listOf(
                    Offset(drawWidth * ratioSunset, drawWidth / 2),
                    Offset(drawWidth * ratioSunrise, drawWidth / 2)
                ),
                pointMode = PointMode.Points,
                color = Color.Yellow,
                strokeWidth = 15F,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Color.White,
                start = Offset(0.dp.toPx(), drawWidth / 2),
                end = Offset(drawWidth, drawWidth / 2),
                strokeWidth = 1F,
                alpha = 0.8F
            )
            drawText(
                textMeasurer = textMeasure, text = astro.sunrise, style = TextStyle(
                    color = Color.White, fontWeight = FontWeight.W200, fontSize = 9.sp
                ), topLeft = Offset(drawWidth * ratioSunrise - 9.sp.toPx() * 2, drawWidth / 2)
            )
            drawText(
                textMeasurer = textMeasure, text = astro.sunset, style = TextStyle(
                    color = Color.White, fontWeight = FontWeight.W200, fontSize = 9.sp
                ), topLeft = Offset(drawWidth * ratioSunset - 9.sp.toPx() * 2, drawWidth / 2)
            )
        }
    }
}
