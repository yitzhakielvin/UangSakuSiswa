package com.sakusisswa.ui.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sakusisswa.model.Transaction
import com.sakusisswa.model.TransactionType
import com.sakusisswa.ui.theme.ExpenseRed
import com.sakusisswa.ui.theme.IncomeGreen
import com.sakusisswa.util.CategoryHelper
import com.sakusisswa.util.formatRupiah
import com.sakusisswa.util.formatRupiahCompact
import com.sakusisswa.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.max

data class ChartPoint(
    val label: String,
    val income: Long,
    val expense: Long
)

enum class StatPeriod(val label: String) {
    WEEK("Minggu Ini"),
    MONTH("Bulan Ini"),
    YEAR("Tahun Ini")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticScreen(
    viewModel: TransactionViewModel,
    onNavigateBack: () -> Unit
) {
    val allTransactions by viewModel.allTransactions.collectAsState()
    var selectedPeriodIndex by remember { mutableIntStateOf(0) } // Default Minggu Ini

    val period = when (selectedPeriodIndex) {
        0 -> StatPeriod.WEEK
        1 -> StatPeriod.MONTH
        else -> StatPeriod.YEAR
    }

    // Ambil rentang waktu
    val (startOfPeriod, endOfPeriod) = remember(period) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        val end = cal.timeInMillis

        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        when (period) {
            StatPeriod.WEEK -> {
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
            }
            StatPeriod.MONTH -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
            }
            StatPeriod.YEAR -> {
                cal.set(Calendar.DAY_OF_YEAR, 1)
            }
        }
        Pair(cal.timeInMillis, end)
    }

    val filteredTransactions = remember(allTransactions, startOfPeriod, endOfPeriod) {
        allTransactions.filter { it.dateTime in startOfPeriod..endOfPeriod }
    }

    // Hitung total
    val totalIncome = remember(filteredTransactions) {
        filteredTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    }
    val totalExpense = remember(filteredTransactions) {
        filteredTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }
    val netCashflow = totalIncome - totalExpense

    // Buat data titik grafik (Line Chart Data)
    val chartPoints = remember(filteredTransactions, period) {
        generateChartPoints(period, filteredTransactions)
    }

    // Breakdown per kategori pengeluaran
    val expenseCategories = remember(filteredTransactions, totalExpense) {
        filteredTransactions.filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .map { (catName, items) ->
                val amount = items.sumOf { it.amount }
                val percentage = if (totalExpense > 0) amount.toFloat() / totalExpense else 0f
                Pair(catName, Pair(amount, percentage))
            }
            .sortedByDescending { it.second.first }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistik & Tren Keuangan") },
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tab Pemilih Periode
            item {
                Spacer(modifier = Modifier.height(4.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = selectedPeriodIndex == 0,
                        onClick = { selectedPeriodIndex = 0 },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                    ) {
                        Text("Minggu")
                    }
                    SegmentedButton(
                        selected = selectedPeriodIndex == 1,
                        onClick = { selectedPeriodIndex = 1 },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                    ) {
                        Text("Bulan")
                    }
                    SegmentedButton(
                        selected = selectedPeriodIndex == 2,
                        onClick = { selectedPeriodIndex = 2 },
                        shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                    ) {
                        Text("Tahun")
                    }
                }
            }

            // === GRAFIK TREN KURVA DENGAN TITIK DATA (LINE CHART) ===
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.ShowChart,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Grafik Tren Arus Kas",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Legenda
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(IncomeGreen))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Masuk", style = MaterialTheme.typography.labelSmall)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ExpenseRed))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Keluar", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }

                        // Komponen Custom Line Chart
                        TrendLineChart(points = chartPoints)
                    }
                }
            }

            // Ringkasan Arus Kas
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Ringkasan ${period.label}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = IncomeGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Pemasukan", style = MaterialTheme.typography.bodySmall)
                                }
                                Text(
                                    formatRupiah(totalIncome),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = IncomeGreen
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = ExpenseRed, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Pengeluaran", style = MaterialTheme.typography.bodySmall)
                                }
                                Text(
                                    formatRupiah(totalExpense),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ExpenseRed
                                )
                            }
                        }

                        // Garis pemisah selisih
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Selisih Bersih (Cashflow)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                formatRupiah(netCashflow),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (netCashflow >= 0) IncomeGreen else ExpenseRed
                            )
                        }
                    }
                }
            }

            // Kategori Pengeluaran Terbesar
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.PieChart,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Kategori Pengeluaran Terbesar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (expenseCategories.isEmpty()) {
                item {
                    Text(
                        "Belum ada pengeluaran di periode ${period.label.lowercase()}.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(expenseCategories) { (catName, data) ->
                    val (amount, percentage) = data
                    val catInfo = CategoryHelper.getCategoryInfo(catName)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(catInfo.color.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        catInfo.icon,
                                        contentDescription = null,
                                        tint = catInfo.color,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = catName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${(percentage * 100).toInt()}% dari total pengeluaran",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = formatRupiah(amount),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ExpenseRed
                                )
                            }

                            LinearProgressIndicator(
                                progress = { percentage },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = catInfo.color,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

/**
 * Custom Line Chart Canvas dengan Titik-Titik Data (Dots), Grid Line, dan Tooltip Interaktif saat disentuh
 */
@Composable
fun TrendLineChart(
    points: List<ChartPoint>,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return

    var selectedIndex by remember { mutableIntStateOf(-1) }
    val maxVal = remember(points) {
        val highest = points.maxOfOrNull { max(it.income, it.expense) } ?: 0L
        if (highest == 0L) 100_000L else highest
    }

    val selectedPoint = if (selectedIndex in points.indices) points[selectedIndex] else null

    Column(modifier = modifier.fillMaxWidth()) {
        // Tampilkan info tooltip bila user menyentuh salah satu titik
        if (selectedPoint != null) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        selectedPoint.label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "Masuk: ${formatRupiahCompact(selectedPoint.income)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = IncomeGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Keluar: ${formatRupiahCompact(selectedPoint.expense)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = ExpenseRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            Text(
                "Sentuh titik pada kurva untuk melihat nominal",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        // Canvas Area
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .pointerInput(points) {
                    detectTapGestures { offset ->
                        val stepX = size.width / (points.size - 1).coerceAtLeast(1)
                        val clickedIndex = (offset.x / stepX + 0.5f).toInt().coerceIn(0, points.size - 1)
                        selectedIndex = if (selectedIndex == clickedIndex) -1 else clickedIndex
                    }
                }
        ) {
            val w = size.width
            val h = size.height - 35f // Beri ruang bawah untuk teks label sumbu X
            val stepX = if (points.size > 1) w / (points.size - 1) else w

            // 1. Gambar Grid Horizontal (3 garis bantu halus)
            for (i in 0..3) {
                val gridY = h * (i / 3f)
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.4f),
                    start = Offset(0f, gridY),
                    end = Offset(w, gridY),
                    strokeWidth = 1f
                )
            }

            // Fungsi pembantu menghitung koordinat Y
            fun getY(amount: Long): Float {
                val ratio = (amount.toFloat() / maxVal).coerceIn(0f, 1f)
                return h - (ratio * (h - 20f)) - 10f
            }

            // 2. Buat Path Kurva Pengeluaran (Expense / Merah)
            val expensePath = Path()
            val incomePath = Path()

            points.forEachIndexed { i, p ->
                val x = i * stepX
                val yExp = getY(p.expense)
                val yInc = getY(p.income)

                if (i == 0) {
                    expensePath.moveTo(x, yExp)
                    incomePath.moveTo(x, yInc)
                } else {
                    expensePath.lineTo(x, yExp)
                    incomePath.lineTo(x, yInc)
                }
            }

            // Gambar Garis Tren Pengeluaran & Pemasukan
            drawPath(
                path = incomePath,
                color = IncomeGreen,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
            drawPath(
                path = expensePath,
                color = ExpenseRed,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // 3. Gambar Titik-Titik Data (Dots) di setiap node
            points.forEachIndexed { i, p ->
                val x = i * stepX
                val yInc = getY(p.income)
                val yExp = getY(p.expense)

                val isSelected = (i == selectedIndex)
                val dotRadius = if (isSelected) 6.dp.toPx() else 4.dp.toPx()

                // Titik Pemasukan (Hijau)
                drawCircle(color = Color.White, radius = dotRadius + 2f, center = Offset(x, yInc))
                drawCircle(color = IncomeGreen, radius = dotRadius, center = Offset(x, yInc))

                // Titik Pengeluaran (Merah)
                drawCircle(color = Color.White, radius = dotRadius + 2f, center = Offset(x, yExp))
                drawCircle(color = ExpenseRed, radius = dotRadius, center = Offset(x, yExp))

                // Garis vertikal indikator jika dipilih
                if (isSelected) {
                    drawLine(
                        color = Color.DarkGray.copy(alpha = 0.5f),
                        start = Offset(x, 0f),
                        end = Offset(x, h),
                        strokeWidth = 2f
                    )
                }
            }
        }

        // Label Sumbu X (Waktu: Hari / Bulan)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            points.forEach { p ->
                Text(
                    text = p.label,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Generator titik-titik kurva data berdasarkan periode
 */
private fun generateChartPoints(period: StatPeriod, transactions: List<Transaction>): List<ChartPoint> {
    val cal = Calendar.getInstance()

    return when (period) {
        StatPeriod.WEEK -> {
            // 7 Hari (Sen, Sel, Rab, Kam, Jum, Sab, Min)
            val dayNames = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")
            val result = mutableListOf<ChartPoint>()

            cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            for (i in 0..6) {
                val dayStart = cal.apply {
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
                }.timeInMillis
                val dayEnd = cal.apply {
                    set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59)
                }.timeInMillis

                val dayTxs = transactions.filter { it.dateTime in dayStart..dayEnd }
                val inc = dayTxs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                val exp = dayTxs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

                val dayIndex = cal.get(Calendar.DAY_OF_WEEK) - 1
                result.add(ChartPoint(dayNames[dayIndex], inc, exp))

                cal.add(Calendar.DAY_OF_WEEK, 1)
            }
            result
        }
        StatPeriod.MONTH -> {
            // 4 Minggu dalam Bulan
            val result = mutableListOf<ChartPoint>()
            cal.set(Calendar.DAY_OF_MONTH, 1)
            val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

            val weekRanges = listOf(
                Pair(1, 7),
                Pair(8, 14),
                Pair(15, 21),
                Pair(22, maxDays)
            )

            weekRanges.forEachIndexed { idx, (startD, endD) ->
                val wStart = cal.apply { set(Calendar.DAY_OF_MONTH, startD); set(Calendar.HOUR_OF_DAY, 0) }.timeInMillis
                val wEnd = cal.apply { set(Calendar.DAY_OF_MONTH, endD); set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59) }.timeInMillis

                val wTxs = transactions.filter { it.dateTime in wStart..wEnd }
                val inc = wTxs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                val exp = wTxs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

                result.add(ChartPoint("M-${idx + 1}", inc, exp))
            }
            result
        }
        StatPeriod.YEAR -> {
            // 12 Bulan (Jan - Des)
            val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des")
            val currentYear = cal.get(Calendar.YEAR)
            val result = mutableListOf<ChartPoint>()

            for (m in 0..11) {
                cal.set(currentYear, m, 1, 0, 0, 0)
                val mStart = cal.timeInMillis
                cal.set(currentYear, m, cal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
                val mEnd = cal.timeInMillis

                val mTxs = transactions.filter { it.dateTime in mStart..mEnd }
                val inc = mTxs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                val exp = mTxs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

                result.add(ChartPoint(monthNames[m], inc, exp))
            }
            result
        }
    }
}
