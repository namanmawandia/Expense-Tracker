package com.example.expensetracker

import java.time.LocalDate

data class DayTransactions(
    val date: Long,
    val transactions: List<Transaction>
)
