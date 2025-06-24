package com.example.expenzio

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MonthYearViewModel : ViewModel(){
    val selectedMonth = MutableLiveData<Int>()
    val selectedYear = MutableLiveData<Int>()

    fun updateMonthYear(month: Int, year: Int) {
        selectedMonth.value = month
        selectedYear.value = year
    }
}