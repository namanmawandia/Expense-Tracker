package com.example.expensetracker

data class DayTransactions(
    val date: Long,
    val transactions: List<Transaction>
)
