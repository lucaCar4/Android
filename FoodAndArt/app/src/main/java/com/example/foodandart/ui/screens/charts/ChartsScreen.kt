package com.example.foodandart.ui.screens.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foodandart.R

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartsScreen(navController: NavController, viewModel: ChartsViewModel) {
    val data = remember { listOf(30f, 20f, 15f, 35f) }
    val colors = remember { listOf(Color.Blue, Color.Red, Color.Green, Color.Yellow) }
    val labels = remember { listOf("Alimentari", "Trasporti", "Intrattenimento", "Abbigliamento") }
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
        Column(modifier = Modifier.padding(contentPadding)) {
            PieChartWithLabels(data = data, colors = colors, labels = labels)
            val travelData = remember { listOf(10, 15, 20, 18, 25, 22, 28, 30, 27, 23, 19, 15) }

            MonthlyTravelChart(data = travelData)
        }
    }
}

@Composable
fun PieChartWithLabels(data: List<Float>, colors: List<Color>, labels: List<String>) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
        PieChart(data = data, colors = colors)
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            labels.forEachIndexed { index, label ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(colors[index])
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = label)
                }
            }
        }
    }
}

@Composable
fun PieChart(data: List<Float>, colors: List<Color>) {
    Canvas(modifier = Modifier.size(200.dp)) {
        var startAngle = 0f
        val total = data.sum()

        data.forEachIndexed { index, value ->
            val sweepAngle = 360 * (value / total)
            drawArc(
                color = colors.getOrElse(index) { Color.Blue },
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset.Zero,
                size = Size(size.width, size.height)
            )
            startAngle += sweepAngle
        }
    }
}

@Composable
fun MonthlyTravelChart(data: List<Int>) {
    val months = remember { listOf("Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre") }

    val maxY = data.maxOrNull()?.toFloat() ?: 0f
    val minY = 0f

    val linePath = remember { Path() }
    linePath.reset()

    Canvas(modifier = Modifier.fillMaxSize()) {
        val startX = 0f
        val endX = size.width
        val intervalX = endX / (months.size - 1)

        val scale = (size.height - 40) / (maxY - minY)
        val startY = size.height - 20 - (data.firstOrNull()?.toFloat()?.times(scale) ?: 0f)

        linePath.moveTo(startX, startY)

        months.forEachIndexed { index, _ ->
            val x = startX + (intervalX * index)
            val y = size.height - 20 - (data.getOrNull(index)?.toFloat()?.times(scale) ?: 0f)
            linePath.lineTo(x, y)
        }

        drawPath(path = linePath, color = Color.Blue, style = Stroke(width = 4.dp.toPx()))
    }
}