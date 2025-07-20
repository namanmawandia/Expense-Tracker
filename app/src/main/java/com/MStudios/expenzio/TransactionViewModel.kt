package com.MStudios.expenzio

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val db = TransactionDatabase.getDatabase(application)
    private val transactionDao = db.transactionDao()

    val transactions: LiveData<List<Transaction>> = transactionDao.getAllTransactions()

    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch { transactionDao.insert(transaction) }
    }

    fun delWithID(id: Int){
        viewModelScope.launch { transactionDao.deleteById(id)}
    }

    suspend fun getTransactionById(id: Int): Transaction {
        return transactionDao.getTransactionById(id)
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch { transactionDao.update(transaction) }
    }
}