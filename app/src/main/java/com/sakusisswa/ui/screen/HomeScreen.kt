package com.sakusisswa.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakusisswa.ui.theme.AmberSecondary
import com.sakusisswa.ui.theme.ExpenseRed
import com.sakusisswa.ui.theme.GreenLight
import com.sakusisswa.ui.theme.GreenPrimary
import com.sakusisswa.util.formatRupiah
import com.sakusisswa.util.formatRupiahCompact
import com.sakusisswa.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TransactionViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToStatistic: () -> Unit,
    onNavigateToEwallet: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val cashBalance by viewModel.cashBalance.collectAsState()
    val ewalletBalance by viewModel.ewalletBalance.collectAsState()
    val totalBalance = cashBalance + ewalletBalance

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "💰 SakuSiswa",
                        style = MaterialTheme.typography.headlineMedium
                    )
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Kartu Total Uang (Bisa ditekan untuk toggle nominal lengkap / disingkat)
            ClickableBalanceCard(
                title = "Total Uang",
                amount = totalBalance,
                icon = {
                    Icon(
                        Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = GreenPrimary
                    )
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )

            // Dua kartu saldo berdampingan dengan tinggi identik rata (IntrinsicSize.Height)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ClickableBalanceCard(
                    title = "Uang Cash",
                    amount = cashBalance,
                    icon = {
                        Icon(
                            Icons.Default.Money,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = GreenLight
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )

                ClickableBalanceCard(
                    title = "E-Wallet",
                    amount = ewalletBalance,
                    icon = {
                        Icon(
                            Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = AmberSecondary
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Tombol Aksi Utama: Tambah Transaksi
            ElevatedButton(
                onClick = onNavigateToAdd,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tambah Transaksi Baru", style = MaterialTheme.typography.titleMedium)
            }

            // Menu & Fitur
            Text(
                "Menu & Fitur",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FilledTonalButton(
                    onClick = onNavigateToStatistic,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PieChart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Grafik & Statistik Tren Keuangan", style = MaterialTheme.typography.labelLarge)
                }

                FilledTonalButton(
                    onClick = onNavigateToEwallet,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PhoneAndroid, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Koneksi & Sinkronisasi E-Wallet", style = MaterialTheme.typography.labelLarge)
                }

                FilledTonalButton(
                    onClick = onNavigateToHistory,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.History, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Riwayat Transaksi", style = MaterialTheme.typography.labelLarge)
                }

                FilledTonalButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pengaturan Notifikasi", style = MaterialTheme.typography.labelLarge)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * Kartu saldo yang otomatis menyingkat nominal besar (jt / M / T)
 * dan bila diklik/dipencet akan langsung menampilkan nominal asli lengkapnya!
 */
@Composable
private fun ClickableBalanceCard(
    title: String,
    amount: Long,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    var showFullAmount by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .defaultMinSize(minHeight = 105.dp)
            .clickable { showFullAmount = !showFullAmount }
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                icon()
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Teks Nominal: jika dipencet menampilkan nominal lengkap, jika tidak disingkat (jt/M/T)
            val displayText = if (showFullAmount) {
                formatRupiah(amount)
            } else {
                formatRupiahCompact(amount)
            }

            Text(
                text = displayText,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = if (showFullAmount && displayText.length > 14) 18.sp else 22.sp
                ),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = if (amount < 0) ExpenseRed else MaterialTheme.colorScheme.onSurface
            )

            // Indikator kecil di bawah bahwa kartu bisa dipencet
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.TouchApp,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = if (showFullAmount) "Klik utk ringkas" else "Klik lihat lengkap",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}
