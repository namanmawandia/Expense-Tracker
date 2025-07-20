package com.MStudios.expenzio

data class DayTransactions(
    val date: Long,
    val transactions: List<Transaction>
)
