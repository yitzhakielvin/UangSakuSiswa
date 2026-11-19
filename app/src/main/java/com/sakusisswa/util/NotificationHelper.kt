package com.sakusisswa.util

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.sakusisswa.worker.DailyNotificationWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Helper untuk menjadwalkan dan membatalkan notifikasi harian.
 */
object NotificationHelper {

    private const val WORK_NAME = "daily_notification"

    /**
     * Jadwalkan notifikasi harian setiap jam 21:00.
     * Jika jam 21:00 sudah lewat hari ini, akan dimulai besok.
     */
    fun scheduleDailyNotification(context: Context) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 21)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) add(Calendar.DAY_OF_MONTH, 1)
        }

        val initialDelay = target.timeInMillis - now.timeInMillis

        val workRequest = PeriodicWorkRequestBuilder<DailyNotificationWorker>(
            1, TimeUnit.DAYS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
    }

    /**
     * Batalkan notifikasi harian.
     */
    fun cancelDailyNotification(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}
