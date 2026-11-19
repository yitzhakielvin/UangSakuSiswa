package com.sakusisswa.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs

/**
 * Format angka ke Rupiah dengan pemisah ribuan titik.
 * Contoh: Rp25.000 atau -Rp25.000
 */
fun formatRupiah(amount: Long): String {
    val symbols = DecimalFormatSymbols(Locale("id", "ID")).apply {
        groupingSeparator = '.'
    }
    val formatter = DecimalFormat("#,###", symbols)
    val formatted = formatter.format(abs(amount))
    return if (amount < 0) "-Rp$formatted" else "Rp$formatted"
}

/**
 * Format ringkas Rupiah agar pas di box/layar kecil (contoh: 15 jt, 1.5 M, 2.5 T).
 * Mencegah teks kepotong atau turun ke bawah.
 */
fun formatRupiahCompact(amount: Long): String {
    val isNegative = amount < 0
    val absVal = abs(amount).toDouble()
    val prefix = if (isNegative) "-Rp" else "Rp"

    val (value, suffix) = when {
        absVal >= 1_000_000_000_000.0 -> Pair(absVal / 1_000_000_000_000.0, " T")
        absVal >= 1_000_000_000.0 -> Pair(absVal / 1_000_000_000.0, " M")
        absVal >= 1_000_000.0 -> Pair(absVal / 1_000_000.0, " jt")
        absVal >= 10_000.0 -> Pair(absVal / 1_000.0, " rb")
        else -> return formatRupiah(amount)
    }

    // Buat maksimal 1 desimal jika bukan bilangan bulat
    val formattedNum = if (value % 1.0 == 0.0) {
        String.format(Locale("id", "ID"), "%.0f", value)
    } else {
        String.format(Locale("id", "ID"), "%.1f", value)
    }

    return "$prefix$formattedNum$suffix"
}

/**
 * Format string angka mentah menjadi format bertitik saat diketik (misal "1000000" jadi "1.000.000")
 */
fun formatNumberWithDots(rawInput: String): String {
    val cleanString = rawInput.replace(".", "").replace(",", "").trim()
    val number = cleanString.toLongOrNull() ?: return cleanString
    val symbols = DecimalFormatSymbols(Locale("id", "ID")).apply {
        groupingSeparator = '.'
    }
    return DecimalFormat("#,###", symbols).format(number)
}
