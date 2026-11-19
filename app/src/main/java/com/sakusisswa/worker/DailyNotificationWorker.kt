package com.sakusisswa.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sakusisswa.R
import com.sakusisswa.database.AppDatabase
import com.sakusisswa.util.formatRupiah
import java.util.Calendar

/**
 * Worker yang mengirim notifikasi ringkasan harian setiap malam.
 * Berisi: total uang, saldo cash, saldo e-wallet, pengeluaran hari ini.
 */
class DailyNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val CHANNEL_ID = "daily_summary"
        private const val NOTIFICATION_ID = 1001
    }

    override suspend fun doWork(): Result {
        val dao = AppDatabase.getDatabase(applicationContext).transactionDao()

        // Hitung awal dan akhir hari ini
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis
        val endOfDay = startOfDay + 24 * 60 * 60 * 1000 - 1

        val cashBalance = dao.getCashBalanceSync()
        val ewalletBalance = dao.getEwalletBalanceSync()
        val totalBalance = cashBalance + ewalletBalance
        val todayExpenses = dao.getExpensesInRange(startOfDay, endOfDay)

        // Buat notification channel (Android 8+)
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Ringkasan Harian",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifikasi ringkasan keuangan harian SakuSiswa"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val bigText = buildString {
            appendLine("💰 Total uang: ${formatRupiah(totalBalance)}")
            appendLine("💵 Saldo cash: ${formatRupiah(cashBalance)}")
            appendLine("📱 Saldo e-wallet: ${formatRupiah(ewalletBalance)}")
            append("📉 Pengeluaran hari ini: ${formatRupiah(todayExpenses)}")
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Ringkasan Harian SakuSiswa")
            .setContentText("Total: ${formatRupiah(totalBalance)} | Keluar hari ini: ${formatRupiah(todayExpenses)}")
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)

        return Result.success()
    }
}
