package com.sakusisswa.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sakusisswa.database.AppDatabase
import com.sakusisswa.model.MoneySource
import com.sakusisswa.model.Transaction
import com.sakusisswa.model.TransactionType
import com.sakusisswa.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel utama untuk mengelola transaksi, saldo, dan statistik.
 */
class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository

    val allTransactions: StateFlow<List<Transaction>>
    val cashBalance: StateFlow<Long>
    val ewalletBalance: StateFlow<Long>

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    sealed class UiEvent {
        data object TransactionAdded : UiEvent()
        data class Error(val message: String) : UiEvent()
    }

    init {
        val dao = AppDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepository(dao)

        allTransactions = repository.allTransactions
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        cashBalance = repository.cashBalance
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

        ewalletBalance = repository.ewalletBalance
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)
    }

    /**
     * Tambah transaksi baru:
     * - Jumlah harus > 0
     * - Kategori tidak boleh kosong
     * - Saldo diperbolehkan bernilai minus (misal saat berutang/pinjam uang jajan)
     */
    fun addTransaction(
        type: TransactionType,
        source: MoneySource,
        amount: Long,
        category: String,
        note: String,
        dateTime: Long
    ) {
        viewModelScope.launch {
            if (amount <= 0) {
                _uiEvent.emit(UiEvent.Error("Jumlah harus lebih dari 0"))
                return@launch
            }

            if (category.isBlank()) {
                _uiEvent.emit(UiEvent.Error("Pilih kategori terlebih dahulu"))
                return@launch
            }

            // Saldo diperbolehkan bernilai minus sesuai kebutuhan pengguna

            repository.insert(
                Transaction(
                    type = type,
                    source = source,
                    amount = amount,
                    category = category,
                    note = note,
                    dateTime = dateTime
                )
            )
            _uiEvent.emit(UiEvent.TransactionAdded)
        }
    }

    /**
     * Sinkronisasi cepat e-wallet (Top up saldo e-wallet atau penyesuaian nominal)
     */
    fun syncEwalletBalance(targetBalance: Long, ewalletName: String) {
        viewModelScope.launch {
            val currentBalance = repository.getEwalletBalanceSync()
            val diff = targetBalance - currentBalance
            if (diff == 0L) {
                _uiEvent.emit(UiEvent.Error("Saldo e-wallet sudah sesuai ($targetBalance)"))
                return@launch
            }

            val type = if (diff > 0) TransactionType.INCOME else TransactionType.EXPENSE
            val amount = kotlin.math.abs(diff)
            val note = if (diff > 0) "Sinkronisasi saldo masuk $ewalletName" else "Penyesuaian saldo $ewalletName"

            repository.insert(
                Transaction(
                    type = type,
                    source = MoneySource.EWALLET,
                    amount = amount,
                    category = "Tabungan",
                    note = note,
                    dateTime = System.currentTimeMillis()
                )
            )
            _uiEvent.emit(UiEvent.TransactionAdded)
        }
    }

    /**
     * Hapus transaksi dari database.
     */
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.delete(transaction)
        }
    }
}
