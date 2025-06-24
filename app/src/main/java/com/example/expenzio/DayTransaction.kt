package com.example.expenzio

data class DayTransactions(
    val date: Long,
    val transactions: List<Transaction>
)
