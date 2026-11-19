package com.sakusisswa.ui.screen

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sakusisswa.util.formatNumberWithDots
import com.sakusisswa.util.formatRupiah
import com.sakusisswa.viewmodel.TransactionViewModel

data class EwalletApp(
    val name: String,
    val packageName: String,
    val color: Color,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EwalletConnectScreen(
    viewModel: TransactionViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val ewalletBalance by viewModel.ewalletBalance.collectAsState()

    var showSyncDialogFor by remember { mutableStateOf<EwalletApp?>(null) }
    var targetBalanceInput by remember { mutableStateOf("") }

    val ewalletList = remember {
        listOf(
            EwalletApp("DANA", "id.dana", Color(0xFF108EE9), "Dompet digital DANA Indonesia"),
            EwalletApp("GoPay", "com.gojek.app", Color(0xFF00AA13), "Gojek / GoPay"),
            EwalletApp("OVO", "ovo.id", Color(0xFF4C3494), "OVO Payment & Points"),
            EwalletApp("ShopeePay", "com.shopee.id", Color(0xFFEE4D2D), "Shopee & ShopeePay")
        )
    }

    fun openEwalletApp(wallet: EwalletApp) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(wallet.packageName)
        if (launchIntent != null) {
            context.startActivity(launchIntent)
        } else {
            try {
                val marketIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=${wallet.packageName}")
                )
                context.startActivity(marketIntent)
            } catch (e: Exception) {
                val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=${wallet.packageName}")
                )
                context.startActivity(webIntent)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Koneksi & Sinkron E-Wallet") },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Kartu Total Saldo E-Wallet saat ini di SakuSiswa
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.PhoneAndroid,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Total Saldo E-Wallet di SakuSiswa",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = formatRupiah(ewalletBalance),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Penjelasan Keamanan Sistem E-Wallet Android
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "Kenapa tidak bisa auto-baca saldo sendiri?",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Karena sistem keamanan Android (Banking Sandbox) melarang keras aplikasi apapun mengintip saldo dompet digital kamu tanpa izin BI SNAP resmi.\n\n" +
                                "Solusi praktis: Tekan ikon ↗ untuk buka DANA/GoPay/OVO/ShopeePay dan cek saldo aslimu, lalu tekan tombol 'Sinkronkan' di bawah untuk update otomatis!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    "Pilih Dompet Digital Kamu:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Daftar E-Wallet dengan tombol Sinkron yang jelas
            items(ewalletList) { wallet ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(wallet.color),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = wallet.name.take(2),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = wallet.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = wallet.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Tombol Buka Aplikasi
                            IconButton(onClick = { openEwalletApp(wallet) }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = "Buka ${wallet.name}",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Tombol Aksi Sinkronkan Saldo
                        FilledTonalButton(
                            onClick = {
                                showSyncDialogFor = wallet
                                targetBalanceInput = if (ewalletBalance > 0) ewalletBalance.toString() else ""
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sinkronkan Saldo ${wallet.name}")
                        }
                    }
                }
            }
        }
    }

    // Dialog Sinkronisasi Saldo
    showSyncDialogFor?.let { wallet ->
        AlertDialog(
            onDismissRequest = { showSyncDialogFor = null },
            title = { Text("Sinkronkan Saldo ${wallet.name}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Ketikkan nominal saldo yang ada di aplikasi ${wallet.name} kamu saat ini. SakuSiswa akan langsung menyesuaikan saldo e-wallet:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = targetBalanceInput,
                        onValueChange = { newValue ->
                            val rawDigits = newValue.filter { it.isDigit() }
                            targetBalanceInput = if (rawDigits.isNotBlank()) {
                                formatNumberWithDots(rawDigits)
                            } else {
                                ""
                            }
                        },
                        label = { Text("Saldo Baru di ${wallet.name}") },
                        prefix = { Text("Rp ", fontWeight = FontWeight.Bold) },
                        placeholder = { Text("0") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val rawDigits = targetBalanceInput.replace(".", "").trim()
                    val targetAmount = rawDigits.toLongOrNull() ?: 0L
                    viewModel.syncEwalletBalance(targetAmount, wallet.name)
                    showSyncDialogFor = null
                    Toast.makeText(context, "Saldo E-Wallet berhasil disinkronkan!", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Terapkan Saldo")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSyncDialogFor = null }) {
                    Text("Batal")
                }
            }
        )
    }
}
