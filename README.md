# 💰 UangSakuSiswa (SakuSiswa)

Aplikasi pencatatan keuangan pribadi modern berbasis Android yang dirancang khusus untuk membantu pelajar dan siswa memantau uang saku harian, pemasukan, dan pengeluaran secara terstruktur, mandiri, dan 100% luring (*offline-first*).

Dibangun menggunakan **Kotlin**, **Jetpack Compose (Material 3)**, arsitektur modern **MVVM (Model-View-ViewModel)**, **Room Database (KSP)**, dan **Jetpack DataStore Preferences**.

---

## 📱 Fitur Utama

- **Ringkasan Saldo Interaktif (Multi-Wallet)**:
  - Menampilkan 3 kartu saldo utama: **Total Uang**, **Uang Cash**, dan **E-Wallet**.
  - Penyingkatan nominal otomatis untuk angka besar (`rb`, `jt`, `M`, `T`) agar kartu tetap rapi dan seimbang.
  - Fitur interaktif: sentuh/klik pada kartu saldo untuk melihat atau menyembunyikan nominal angka asli lengkap (`Rp...`).
  - Mendukung pencatatan saldo bernilai negatif (misal saat berutang/talangan).

- **Pencatatan Transaksi Masuk & Keluar**:
  - Pilihan jenis transaksi: **Uang Masuk** atau **Uang Keluar**.
  - Pilihan sumber dana terpisah: **Cash** atau **E-Wallet**.
  - Pemisah ribuan otomatis saat mengetik nominal (contoh: `1000000` menjadi `1.000.000`).
  - Tombol pintas template nominal cepat: `+000 (Ribu)`, `+000.000 (Juta)`, `10 rb`, `50 rb`, `100 rb`, hingga `1 Juta`.
  - Pemilihan kategori pengeluaran dan pemasukan dengan ikon visual dan warna tematik.
  - Catatan tambahan opsional dan pemilih tanggal (*DatePicker*) serta waktu (*TimePicker*).

- **Validasi Input Terintegrasi**:
  - Validasi nominal transaksi harus lebih dari 0.
  - Validasi kategori wajib dipilih sebelum menyimpan.
  - Umpan balik langsung kepada pengguna melalui *Toast alert*.

- **Riwayat Transaksi & Konfirmasi Hapus**:
  - Transaksi dikelompokkan secara otomatis berdasarkan tanggal (*Hari Ini*, *Kemarin*, atau tanggal spesifik).
  - Badge sumber dana yang jelas (*Cash* / *E-Wallet*) beserta waktu transaksi.
  - Dialog konfirmasi (*AlertDialog*) sebelum data transaksi dihapus permanen untuk mencegah ketidaksengajaan.

- **Grafik & Statistik Tren Keuangan**:
  - Visualisasi tren arus kas pemasukan dan pengeluaran menggunakan kurva garis (*Line Chart Canvas*) yang dilengkapi titik data (*dots*) dan garis bantu kisi (*gridlines*).
  - Pilihan periode analisis fleksibel: **Mingguan**, **Bulanan**, dan **Tahunan**.
  - Fitur interaktif: sentuh titik data pada kurva untuk memunculkan *tooltip* nominal tepat di waktu tersebut.
  - Analisis kategori pengeluaran terbesar dilengkapi persentase dan indikator bar visual (*LinearProgressIndicator*).

- **Koneksi & Sinkronisasi E-Wallet**:
  - Antarmuka pendukung dompet digital populer (DANA, GoPay, OVO, ShopeePay).
  - Pintasan pembuka aplikasi resmi e-wallet secara langsung melalui *Android Intent*.
  - Dialog penyesuaian/sinkronisasi cepat saldo e-wallet untuk mencocokkan sisa saldo riil.

- **Pengaturan & Notifikasi Harian**:
  - Penjadwalan pengingat ringkasan saldo harian otomatis setiap malam (21:00) menggunakan **WorkManager**.
  - Sakelar aktif/nonaktif notifikasi yang tersimpan permanen menggunakan **Jetpack DataStore Preferences**.
  - Penanganan izin notifikasi dinamis untuk Android 13+ (*POST_NOTIFICATIONS*).

- **Penyimpanan Lokal Penuh (Offline First)**:
  - Seluruh data transaksi dan pengaturan tersimpan secara lokal di perangkat menggunakan **Room Database**, tanpa membutuhkan koneksi internet dan tanpa akun pengguna (*privacy-focused*).

---

## 🛠️ Arsitektur & Teknologi

Proyek ini dibangun mengikuti pedoman resmi *Modern Android Development* (MAD):

- **Bahasa Pemrograman**: [Kotlin](https://kotlinlang.org/) (v2.1.0)
- **UI Toolkit**: [Jetpack Compose](https://developer.android.com/jetpack/compose) dengan [Material Design 3 (M3)](https://m3.material.io/)
- **Pola Desain Arsitektur**: **MVVM (Model-View-ViewModel)** dengan *Unidirectional Data Flow (UDF)*
- **Manajemen State**: `StateFlow` & `SharedFlow` berbasis Kotlin Coroutines
- **Navigasi**: [Jetpack Navigation Compose](https://developer.android.com/guide/navigation/navigation-compose)
- **Penyimpanan Database**: [Room Database](https://developer.android.com/training/data-storage/room) dengan [Kotlin Symbol Processing (KSP)](https://kotlinlang.org/docs/ksp-overview.html)
- **Penyimpanan Pengaturan**: [Jetpack DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore)
- **Tugas Latar Belakang**: [AndroidX WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
- **Target SDK**: Min SDK 26 (Android 8.0) | Target & Compile SDK 35 (Android 15)
- **Java Compatibility**: JDK 17

---

## 📂 Struktur Direktori Proyek

```
SakuSiswa/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/sakusisswa/
│   │   │   ├── MainActivity.kt               # Single-activity container
│   │   │   ├── SakuSiswaApp.kt               # Application entry point & WorkManager scheduler
│   │   │   ├── database/
│   │   │   │   ├── AppDatabase.kt            # Room Database definition
│   │   │   │   ├── Converters.kt             # Room TypeConverters (Enum to String)
│   │   │   │   └── TransactionDao.kt         # Data Access Object (Query & Flow)
│   │   │   ├── model/
│   │   │   │   └── Transaction.kt            # Entity, TransactionType & MoneySource enums
│   │   │   ├── repository/
│   │   │   │   └── TransactionRepository.kt  # Single source of truth data abstraction
│   │   │   ├── ui/
│   │   │   │   ├── navigation/
│   │   │   │   │   └── NavGraph.kt           # NavHost & rute navigasi aplikasi
│   │   │   │   ├── screen/
│   │   │   │   │   ├── HomeScreen.kt          # Layar ringkasan saldo & menu navigasi
│   │   │   │   │   ├── AddTransactionScreen.kt# Formulir penambahan transaksi & template nominal
│   │   │   │   │   ├── HistoryScreen.kt       # Daftar riwayat transaksi & konfirmasi hapus
│   │   │   │   │   ├── StatisticScreen.kt     # Grafik kurva tren (Line Chart) & proporsi pengeluaran
│   │   │   │   │   ├── EwalletConnectScreen.kt# Antarmuka launcher & sinkronisasi saldo e-wallet
│   │   │   │   │   └── SettingsScreen.kt      # Pengaturan preferensi notifikasi harian
│   │   │   │   └── theme/
│   │   │   │       ├── Color.kt
│   │   │   │       ├── Theme.kt
│   │   │   │       └── Type.kt
│   │   │   ├── util/
│   │   │   │   ├── CategoryHelper.kt          # Ikon dan warna tema tiap kategori
│   │   │   │   ├── CurrencyFormatter.kt       # Format mata uang Rupiah & compact notation
│   │   │   │   └── NotificationHelper.kt      # Penjadwalan PeriodicWorkRequest
│   │   │   ├── viewmodel/
│   │   │   │   └── TransactionViewModel.kt   # Business logic, validasi input & state holder
│   │   │   └── worker/
│   │   │       └── DailyNotificationWorker.kt # Worker pengirim notifikasi ringkasan saldo
│   │   └── res/                               # Drawable, launcher icon, vector assets, and values
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

---

## 🚀 Panduan Menjalankan Proyek

### Prasyarat
1. **Android Studio**: Android Studio Ladybug (2024.2.1) atau versi lebih baru disarankan.
2. **JDK**: JDK 17 atau yang terintegrasi di Android Studio (JBR 17/21).
3. **Perangkat Uji**: Emulator Android (API Level 26+) atau perangkat fisik dengan fitur *USB Debugging* aktif.

### Langkah-langkah
1. **Klon atau Buka Repositori**:
   - Di Android Studio, pilih menu **File** > **Open...**
   - Arahkan ke folder repositori `SakuSiswa` lalu klik **OK**.
2. **Sinkronisasi Gradle**:
   - Biarkan Android Studio mengunduh dependensi dan melakukan **Gradle Sync** secara otomatis hingga selesai.
3. **Jalankan Aplikasi**:
   - Pilih modul `app` dan tentukan target emulator / ponsel Anda di toolbar atas.
   - Tekan tombol **Run ▶** (atau gunakan pintasan `Shift + F10`).
   - Aplikasi akan terpasang dan siap digunakan.

---

## 👥 Anggota Kelompok

Proyek ini disusun dan diselesaikan bersama oleh:

1. **Ebenhaezer Yitzhakielvin Hikmatjipta Kanaan**
2. **Obed Obadiah**
3. **Muhammad Mikail Handriansyahputra**
4. **Muhammad Alif Amirullah**
5. **Sefira Tri Wahyuni**
6. **Zacky Ahmad Arrasyid**

---
*Proyek ini dikembangkan untuk penyelesaian Modul 10 (Materi 10.4) Aplikasi Android Pembelajaran.*
