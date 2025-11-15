package com.studia.kasamate.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.studia.kasamate.data.Transaction
import java.util.Calendar

@Composable
fun MonthlySummaryChart(transactions: List<Transaction>) {
    val dailyTotals = transactions.filter { isThisMonth(it.date) }
        .groupBy { getDayOfMonth(it.date) }
        .mapValues { (_, transactions) -> transactions.sumOf { it.price } }

    val entries = dailyTotals.map { (day, total) ->
        BarEntry(day.toFloat(), total.toFloat())
    }

    val dataSet = BarDataSet(entries, "Daily Expenses")
    val barData = BarData(dataSet)
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        factory = { context ->
            BarChart(context).apply {
                data = barData
                description.isEnabled = false
                legend.isEnabled = false
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.textColor = textColor
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt().toString()
                    }
                }
                axisLeft.setDrawGridLines(false)
                axisLeft.textColor = textColor
                axisRight.isEnabled = false
                invalidate()
            }
        },
        update = {
            it.data = barData
            it.xAxis.textColor = textColor
            it.axisLeft.textColor = textColor
            it.invalidate()
        }
    )
}

private fun isThisMonth(dateString: String): Boolean {
    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)

    val transactionCalendar = Calendar.getInstance()
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    try {
        transactionCalendar.time = sdf.parse(dateString)!!
        val transactionMonth = transactionCalendar.get(Calendar.MONTH)
        val transactionYear = transactionCalendar.get(Calendar.YEAR)
        return currentMonth == transactionMonth && currentYear == transactionYear
    } catch (e: Exception) {
        return false
    }
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
