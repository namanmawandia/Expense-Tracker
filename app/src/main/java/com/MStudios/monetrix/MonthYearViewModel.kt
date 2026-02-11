package com.MStudios.monetrix

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

val months = listOf(
    "January", "February", "March", "April",
    "May", "June", "July", "August",
    "September", "October", "November", "December"
    )

fun yearsInRange(start: Int, end: Int): List<String> {
    return (start..end).map { it.toString() }
}

val years = yearsInRange(2000, 2045)

class MonthYearViewModel : ViewModel(){
    val selectedMonth = MutableLiveData<Int>()
    val selectedYear = MutableLiveData<Int>()

    fun updateMonthYear(month: Int, year: Int) {
        selectedMonth.value = month
        selectedYear.value = year
    }
}