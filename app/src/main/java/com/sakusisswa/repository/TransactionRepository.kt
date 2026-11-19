package com.sakusisswa.repository

import com.sakusisswa.database.TransactionDao
import com.sakusisswa.model.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Repository sebagai abstraksi sumber data.
 * Di versi mendatang, bisa ditambahkan sumber data lain
 * (misalnya API e-wallet resmi).
 */
class TransactionRepository(private val dao: TransactionDao) {

    val allTransactions: Flow<List<Transaction>> = dao.getAllTransactions()
    val cashBalance: Flow<Long> = dao.getCashBalance()
    val ewalletBalance: Flow<Long> = dao.getEwalletBalance()

    suspend fun insert(transaction: Transaction) {
        dao.insert(transaction)
    }

    suspend fun delete(transaction: Transaction) {
        dao.delete(transaction)
    }

    suspend fun getCashBalanceSync(): Long = dao.getCashBalanceSync()

    suspend fun getEwalletBalanceSync(): Long = dao.getEwalletBalanceSync()

    suspend fun getExpensesInRange(startOfDay: Long, endOfDay: Long): Long {
        return dao.getExpensesInRange(startOfDay, endOfDay)
    }
}
