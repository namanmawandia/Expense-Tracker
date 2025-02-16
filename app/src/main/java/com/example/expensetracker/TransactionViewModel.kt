package com.example.expensetracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val db = TransactionDatabase.getDatabase(application)
    private val transactionDao = db.transactinoDao()

    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionDao.insert(transaction)
        }
    }

    fun getAllTransactions() {
        viewModelScope.launch {
            val transactions = transactionDao.getAllTransactions()

        }
    }
}