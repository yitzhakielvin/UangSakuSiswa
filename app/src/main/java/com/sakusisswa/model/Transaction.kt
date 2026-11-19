package com.sakusisswa.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Jenis transaksi: uang masuk atau uang keluar.
 */
enum class TransactionType {
    INCOME,   // Uang Masuk
    EXPENSE   // Uang Keluar
}

/**
 * Sumber uang: cash atau e-wallet.
 * Struktur ini disiapkan agar integrasi resmi e-wallet
 * dapat ditambahkan di versi mendatang.
 */
enum class MoneySource {
    CASH,     // Uang tunai
    EWALLET   // E-Wallet (input manual pada versi awal)
}

/**
 * Entity transaksi yang disimpan di Room database.
 * Jumlah disimpan dalam satuan Rupiah (Long).
 */
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: TransactionType,
    val source: MoneySource,
    val amount: Long,
    val category: String,
    val note: String = "",
    val dateTime: Long // epoch milliseconds
)
