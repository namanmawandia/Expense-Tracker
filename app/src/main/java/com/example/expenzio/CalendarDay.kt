package com.example.expenzio

data class CalendarDay(
    val date: String?,
    val income: Double?=null,
    val expense: Double?=null,
    val total: Double?=null
)
