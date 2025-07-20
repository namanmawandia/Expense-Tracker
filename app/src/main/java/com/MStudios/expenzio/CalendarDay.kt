package com.MStudios.expenzio

data class CalendarDay(
    val date: String?,
    val income: Double?=null,
    val expense: Double?=null,
    val total: Double?=null
)
