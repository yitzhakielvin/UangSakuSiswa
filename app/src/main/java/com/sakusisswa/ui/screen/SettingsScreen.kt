package com.sakusisswa.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.sakusisswa.util.NotificationHelper
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// DataStore untuk pengaturan
val android.content.Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "settings"
)

val NOTIFICATION_ENABLED_KEY = booleanPreferencesKey("notification_enabled")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val notificationEnabled by context.settingsDataStore.data
        .map { prefs -> prefs[NOTIFICATION_ENABLED_KEY] ?: true }
        .collectAsState(initial = true)

    // Permission launcher untuk Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            scope.launch {
                context.settingsDataStore.edit { prefs ->
                    prefs[NOTIFICATION_ENABLED_KEY] = true
                }
            }
            NotificationHelper.scheduleDailyNotification(context)
            Toast.makeText(context, "Notifikasi harian diaktifkan", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                context,
                "Izin notifikasi diperlukan untuk fitur ini",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Kartu pengaturan notifikasi
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (notificationEnabled) Icons.Default.Notifications
                        else Icons.Default.NotificationsOff,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            "Notifikasi Harian",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Kirim ringkasan keuangan setiap jam 21:00",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = notificationEnabled,
                        onCheckedChange = { enabled ->
                            if (enabled) {
                                // Periksa izin notifikasi (Android 13+)
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val hasPermission = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (hasPermission) {
                                        scope.launch {
                                            context.settingsDataStore.edit { prefs ->
                                                prefs[NOTIFICATION_ENABLED_KEY] = true
                                            }
                                        }
                                        NotificationHelper.scheduleDailyNotification(context)
                                        Toast.makeText(
                                            context,
                                            "Notifikasi harian diaktifkan",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        notificationPermissionLauncher.launch(
                                            Manifest.permission.POST_NOTIFICATIONS
                                        )
                                    }
                                } else {
                                    scope.launch {
                                        context.settingsDataStore.edit { prefs ->
                                            prefs[NOTIFICATION_ENABLED_KEY] = true
                                        }
                                    }
                                    NotificationHelper.scheduleDailyNotification(context)
                                    Toast.makeText(
                                        context,
                                        "Notifikasi harian diaktifkan",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                scope.launch {
                                    context.settingsDataStore.edit { prefs ->
                                        prefs[NOTIFICATION_ENABLED_KEY] = false
                                    }
                                }
                                NotificationHelper.cancelDailyNotification(context)
                                Toast.makeText(
                                    context,
                                    "Notifikasi harian dinonaktifkan",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }
            }

            // Info tambahan
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "ℹ️ Tentang SakuSiswa",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Aplikasi pencatatan keuangan sederhana untuk pelajar SMK. " +
                        "Catat pemasukan dan pengeluaran dari uang cash maupun e-wallet. " +
                        "Data disimpan secara lokal di perangkatmu.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Versi 1.0",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                    )
                }
            }

            // Catatan e-wallet
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "📱 Catatan E-Wallet",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Saat ini transaksi e-wallet dicatat secara manual. " +
                        "Integrasi otomatis dengan aplikasi e-wallet akan tersedia " +
                        "di versi mendatang jika API resmi sudah didukung.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
