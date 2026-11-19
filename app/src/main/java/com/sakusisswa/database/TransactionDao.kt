package com.sakusisswa.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.sakusisswa.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert
    suspend fun insert(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions ORDER BY dateTime DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    /**
     * Saldo cash = total uang masuk cash - total uang keluar cash.
     */
    @Query(
        """
        SELECT COALESCE(
            SUM(CASE WHEN type = 'INCOME' THEN amount ELSE -amount END), 0
        ) FROM transactions WHERE source = 'CASH'
        """
    )
    fun getCashBalance(): Flow<Long>

    /**
     * Saldo e-wallet = total uang masuk ewallet - total uang keluar ewallet.
     */
    @Query(
        """
        SELECT COALESCE(
            SUM(CASE WHEN type = 'INCOME' THEN amount ELSE -amount END), 0
        ) FROM transactions WHERE source = 'EWALLET'
        """
    )
    fun getEwalletBalance(): Flow<Long>

    /**
     * Versi synchronous dari getCashBalance untuk Worker / validasi.
     */
    @Query(
        """
        SELECT COALESCE(
            SUM(CASE WHEN type = 'INCOME' THEN amount ELSE -amount END), 0
        ) FROM transactions WHERE source = 'CASH'
        """
    )
    suspend fun getCashBalanceSync(): Long

    /**
     * Versi synchronous dari getEwalletBalance untuk Worker / validasi.
     */
    @Query(
        """
        SELECT COALESCE(
            SUM(CASE WHEN type = 'INCOME' THEN amount ELSE -amount END), 0
        ) FROM transactions WHERE source = 'EWALLET'
        """
    )
    suspend fun getEwalletBalanceSync(): Long

    /**
     * Total pengeluaran pada rentang waktu tertentu (untuk ringkasan harian).
     */
    @Query(
        """
        SELECT COALESCE(SUM(amount), 0) FROM transactions
        WHERE type = 'EXPENSE' AND dateTime BETWEEN :startOfDay AND :endOfDay
        """
    )
    suspend fun getExpensesInRange(startOfDay: Long, endOfDay: Long): Long
}
