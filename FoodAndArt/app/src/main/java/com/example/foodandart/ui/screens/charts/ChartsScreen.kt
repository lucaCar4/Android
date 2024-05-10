package com.example.foodandart.ui.screens.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.PlotType
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import co.yml.charts.ui.barchart.models.SelectionHighlightData
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.example.foodandart.R
import com.example.foodandart.cardTypes
import com.example.foodandart.data.firestore.cloud_database.getCards
import com.example.foodandart.data.firestore.cloud_database.getPurchases
import java.text.DateFormatSymbols
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


val selectedYear by mutableStateOf(LocalDate.now().year.toString())
val cards = getCards()
var purchases = getPurchases()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.charts)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { contentPadding ->
        Column(modifier = Modifier
            .padding(contentPadding)
            .padding(16.dp, 0.dp)) {
            Text(text = stringResource(id = R.string.bar_description), fontSize = 30.sp)
            Box(modifier = Modifier.padding(16.dp)) {
                BarChartScreen()
            }
            Spacer(modifier = Modifier.padding(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                PurchasesTypeComposition()
                Spacer(modifier = Modifier.padding(8.dp))
                ColorLegend()
            }
        }
    }
}

@Composable
fun BarChartScreen() {

    val barsData = mutableListOf<BarData>()
    val moths = DateFormatSymbols(Locale.getDefault()).shortMonths
    val events = getMonthsEvents()
    val maxN = events.maxOf { it } + 0.2f

    moths.forEachIndexed { index, monthName ->
        if ((LocalDate.now().year.toString() == selectedYear && index + 1 <= LocalDate.now().month.value) || LocalDate.now().year.toString() < selectedYear) {
            val barData = BarData(
                point = Point(index.toFloat(), events[index].toFloat() + 0.2f),
                label = monthName,
                gradientColorList = listOf(Color.Cyan, Color.White),
                description = "${events[index]} ${stringResource(id = R.string.voyages)}"
            )
            barsData.add(barData)
        }
    }

    val barStyle = BarStyle(
        paddingBetweenBars = 4.dp,
        barWidth = 60.dp,
        cornerRadius = 0.dp,
        isGradientEnabled = true,
        selectionHighlightData = SelectionHighlightData(
            highlightTextBackgroundColor = Color.Transparent,
            highlightBarColor = Color.Transparent,
            highlightTextColor = MaterialTheme.colorScheme.onBackground,
            popUpLabel = { x, _ -> barsData[x.toInt()].description },
        )
    )

    val xAxisData = AxisData.Builder()
        .bottomPadding(16.dp)
        .axisLineColor(Color.Transparent)
        .axisLabelAngle(0f)
        .backgroundColor(Color.Transparent)
        .steps(barsData.size - 1)
        .labelData { i -> barsData[i].label }
        .labelAndAxisLinePadding(5.dp)
        .axisLabelColor(MaterialTheme.colorScheme.onBackground)
        .startDrawPadding(30.dp)
        .build()

    val yAxisData = AxisData.Builder().steps(maxN.toInt() + 1).build()

    val barChartData = BarChartData(
        chartData = barsData,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        barStyle = barStyle,
        horizontalExtraSpace = 30.dp,
        backgroundColor = MaterialTheme.colorScheme.surface
    )

    BarChart(
        barChartData = barChartData,
        modifier = Modifier
            .height(200.dp)
            .fillMaxSize()
        //.padding(16.dp)
    )
}

private fun getMonthsEvents(): MutableList<Int> {
    val events = mutableListOf<Int>()
    val moths = DateFormatSymbols(Locale.getDefault()).shortMonths
    val selectedPurchases = purchases.filter {
        val date = LocalDate.parse(
            it["purchase_date"].toString(),
        )
        date.year.toString() == selectedYear && date.month <= LocalDate.now().month
    }.toMutableList()
    moths.forEachIndexed { index, monthName ->
        val count = selectedPurchases.count {
            val date = LocalDate.parse(
                it["date"].toString(),
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
            )
            date.month.value - 1 == index
        }
        events.add(count)
    }

    return events
}

@Composable
fun PurchasesTypeComposition(){
    val counts = getTypesCount()
    val slices = mutableListOf<PieChartData.Slice>()
    counts.forEach { (type, count) ->
        val data = PieChartData.Slice(
            label = type,
            value = count.toFloat(),
            color = cardTypes[type] ?: MaterialTheme.colorScheme.onBackground
        )
        slices.add(data)
    }
    val pieData = PieChartData(
        slices = slices,
        plotType = PlotType.Donut
    )
    val pieChartConfig = PieChartConfig(
        isAnimationEnable = true,
        showSliceLabels = true,
        sliceLabelTextColor = MaterialTheme.colorScheme.onBackground,
        activeSliceAlpha = 0.5f,
        isEllipsizeEnabled = true,
        animationDuration = 600,
        backgroundColor = Color.Transparent,
        labelColor = MaterialTheme.colorScheme.onBackground,
        labelVisible = true,
        labelType = PieChartConfig.LabelType.VALUE,
    )

    DonutPieChart(modifier = Modifier.size(200.dp), pieChartData = pieData, pieChartConfig = pieChartConfig)
}

fun getTypesCount() : MutableMap<String, Int>  {
    val voyages = mutableMapOf<String, Int>()
    cardTypes.forEach {(type, _) ->
        val count = purchases.count { cards[it["card"]]?.get("type").toString() == type }
        voyages[type] = count
    }
    return voyages
}
@Composable
fun ColorLegend(){
    Column {
        cardTypes.forEach { (type, color) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = type)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}