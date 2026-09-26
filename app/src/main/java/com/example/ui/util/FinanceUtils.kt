package com.example.ui.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

object FinanceUtils {

    private val numberFormat = DecimalFormat("#,##0")
    private val decimalNumberFormat = DecimalFormat("#,##0.##")

    fun formatNumber(value: Double): String {
        return if (value % 1.0 == 0.0) {
            numberFormat.format(value)
        } else {
            decimalNumberFormat.format(value)
        }
    }

    fun formatCurrency(amount: Double, forcePlus: Boolean = false): String {
        return when {
            amount == 0.0 -> "Rs. 0"
            amount < 0.0 -> "- Rs. ${formatNumber(abs(amount))}"
            forcePlus -> "+ Rs. ${formatNumber(amount)}"
            else -> "Rs. ${formatNumber(amount)}"
        }
    }

    fun getMonthYearKey(cal: Calendar): String {
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        return String.format(Locale.US, "%04d-%02d", year, month)
    }

    fun getMonthYearDisplay(cal: Calendar): String {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.US)
        return sdf.format(cal.time)
    }

    fun getMonthShortDisplay(cal: Calendar): String {
        val sdf = SimpleDateFormat("MMM yyyy", Locale.US)
        return sdf.format(cal.time)
    }

    fun formatDate(epochMillis: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.US)
        return sdf.format(Date(epochMillis))
    }

    fun formatTime(epochMillis: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.US)
        return sdf.format(Date(epochMillis))
    }

    fun isToday(epochMillis: Long): Boolean {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = epochMillis }
        return now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)
    }

    fun isSameMonth(epochMillis: Long, monthYearKey: String): Boolean {
        val target = Calendar.getInstance().apply { timeInMillis = epochMillis }
        return getMonthYearKey(target) == monthYearKey
    }
}
