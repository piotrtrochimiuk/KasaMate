package com.studia.kasamate.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.studia.kasamate.R
import com.studia.kasamate.data.Transaction
import java.util.Calendar

@Composable
fun MonthlySummaryChart(transactions: List<Transaction>) {
    val dailyData = transactions
        .groupBy { getDayOfMonth(it.date) }
        .mapValues { (_, dayTransactions) ->
            val income = dayTransactions.filter { it.isIncome }.sumOf { it.price }
            val expense = dayTransactions.filter { !it.isIncome }.sumOf { it.price }
            income to expense
        }

    val incomeEntries = dailyData.map { (day, data) ->
        BarEntry(day.toFloat(), data.first.toFloat())
    }.sortedBy { it.x }

    val expenseEntries = dailyData.map { (day, data) ->
        BarEntry(day.toFloat(), -data.second.toFloat())
    }.sortedBy { it.x }

    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val incomeLabel = stringResource(R.string.chart_income)
    val expenseLabel = stringResource(R.string.chart_expense)

    val incomeDataSet = BarDataSet(incomeEntries, incomeLabel).apply {
        color = android.graphics.Color.parseColor("#4CAF50") // Green
        valueTextColor = textColor
        setDrawValues(false)
    }

    val expenseDataSet = BarDataSet(expenseEntries, expenseLabel).apply {
        color = android.graphics.Color.parseColor("#F44336") // Red
        valueTextColor = textColor
        setDrawValues(false)
    }

    val barData = BarData(incomeDataSet, expenseDataSet)

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        factory = { context ->
            BarChart(context).apply {
                data = barData
                description.isEnabled = false
                legend.isEnabled = true
                legend.textColor = textColor
                
                // Disable zooming and scaling
                setScaleEnabled(false)
                setPinchZoom(false)
                isDoubleTapToZoomEnabled = false
                
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    this.textColor = textColor
                    granularity = 1f
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return value.toInt().toString()
                        }
                    }
                }

                axisLeft.apply {
                    setDrawGridLines(true)
                    this.textColor = textColor
                    // Let it automatically handle negative (expenses) and positive (incomes)
                }
                axisRight.isEnabled = false
                
                setFitBars(true)
                animateY(500)
                invalidate()
            }
        },
        update = { chart ->
            chart.data = barData
            chart.xAxis.textColor = textColor
            chart.axisLeft.textColor = textColor
            chart.legend.textColor = textColor
            chart.invalidate()
        }
    )
}

private fun getDayOfMonth(dateString: String): Int {
    val transactionCalendar = Calendar.getInstance()
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    try {
        transactionCalendar.time = sdf.parse(dateString)!!
        return transactionCalendar.get(Calendar.DAY_OF_MONTH)
    } catch (e: Exception) {
        return 0
    }
}
