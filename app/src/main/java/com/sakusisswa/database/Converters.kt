package com.sakusisswa.database

import androidx.room.TypeConverter
import com.sakusisswa.model.MoneySource
import com.sakusisswa.model.TransactionType

/**
 * Type converters untuk Room agar bisa menyimpan enum sebagai String.
 */
class Converters {
    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = TransactionType.valueOf(value)

    @TypeConverter
    fun fromMoneySource(value: MoneySource): String = value.name

    @TypeConverter
    fun toMoneySource(value: String): MoneySource = MoneySource.valueOf(value)
}
