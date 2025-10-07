package com.MStudios.monetrix

data class DayTransactions(
    val date: Long,
    val transactions: List<Transaction>
)
