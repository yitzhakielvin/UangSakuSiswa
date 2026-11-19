package com.sakusisswa

import android.app.Application
import com.sakusisswa.ui.screen.NOTIFICATION_ENABLED_KEY
import com.sakusisswa.ui.screen.settingsDataStore
import com.sakusisswa.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Application class utama SakuSiswa.
 * Menjadwalkan notifikasi harian saat aplikasi pertama kali dibuka
 * jika pengaturan notifikasi aktif.
 */
class SakuSiswaApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Cek pengaturan notifikasi dan jadwalkan jika aktif
        CoroutineScope(Dispatchers.IO).launch {
            val isEnabled = settingsDataStore.data
                .map { prefs -> prefs[NOTIFICATION_ENABLED_KEY] ?: true }
                .first()

            if (isEnabled) {
                NotificationHelper.scheduleDailyNotification(this@SakuSiswaApp)
            }
        }
    }
}
