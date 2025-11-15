package com.studia.kasamate.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.studia.kasamate.data.AppDatabase
import com.studia.kasamate.data.SettingsRepository
import com.studia.kasamate.data.Transaction
import com.studia.kasamate.data.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

enum class SortType {
    NONE,
    NAME_ASC,
    NAME_DESC,
    PRICE_ASC,
    PRICE_DESC,
    DATE_ASC,
    DATE_DESC
}

class TransactionViewModel(application: Application, private val username: String) : AndroidViewModel(application) {

    private val transactionRepository: TransactionRepository = TransactionRepository(AppDatabase.getDatabase(application).transactionDao())
    private val settingsRepository: SettingsRepository = SettingsRepository(application)
    private val _sortType = MutableStateFlow(SortType.NONE)

    val allTransactions = transactionRepository.getAllTransactions(username).combine(_sortType) { transactions, sortType ->
        when (sortType) {
            SortType.NAME_ASC -> transactions.sortedBy { it.description }
            SortType.NAME_DESC -> transactions.sortedByDescending { it.description }
            SortType.PRICE_ASC -> transactions.sortedBy { it.price }
            SortType.PRICE_DESC -> transactions.sortedByDescending { it.price }
            SortType.DATE_ASC -> transactions.sortedBy { it.date }
            SortType.DATE_DESC -> transactions.sortedByDescending { it.date }
            else -> transactions
        }
    }.asLiveData()

    fun setSortType(sortType: SortType) {
        _sortType.value = sortType
    }

    fun addTransaction(transaction: Transaction) = viewModelScope.launch(Dispatchers.IO) {
        val currency = settingsRepository.getCurrency()
        transactionRepository.insert(transaction.copy(currency = currency, username = username))
    }

    fun updateTransaction(transaction: Transaction) = viewModelScope.launch(Dispatchers.IO) {
        transactionRepository.update(transaction)
    }

    fun deleteTransaction(transaction: Transaction) = viewModelScope.launch(Dispatchers.IO) {
        transactionRepository.delete(transaction)
    }
}

class TransactionViewModelFactory(private val application: Application, private val username: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(application, username) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
