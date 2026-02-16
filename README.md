# INVENTRA DD - Inventory and Resource Asset System Sekolah Islam Dian Didaktika


Aplikasi backend berbasis Spring Boot untuk manajemen aset sekolah, mencakup:
    - **TPS**: Kelola profil, kelola aset, peminjaman aset, pengadaan aset, persetujuan peminjaman, persetujuan pengadaan aset, pengantian barang rusak

    - **MIS**: laporan utilisasi aset & pengadaan

    - **EIS**: dashboard pengadaan & peminjaman

Ringkasan kebutuhan fitur & role akses mengacu pada dokumen README pada tiap branch anggota.

## Tech Stack
- Java 11+
- Spring Boot
- Gradle
- Database (PostgreSQL)
- Docker 
 
## Instalasi dan Menjalankan Aplikasi
1. Clone Repository:
    git clone <repository-url>
    cd <repository-folder>

2. Menjalankan aplikasi:
    ./gradlew bootRun

## Struktur Direktori
ibuprofen-backend/
├── src/
│   ├── main/
│   │   ├── java/io/ibuprofen/inventra_dd_be/
│   │   │   ├── InventraDdBeApplication.java
│   │   │   ├── Aset
│   │   │   │   ├── config/
│   │   │   │   ├── security/
│   │   │   │   ├── model/
│   │   │   │   ├── repository/
│   │   │   │   ├── service/
│   │   │   │   ├── restcontroller/
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/
│   │   │   │   │   └── response/
│   │   │   │   └── util/
│   │   │   ├── Profile
│   │   │   │   ├── config/
│   │   │   │   ├── security/
│   │   │   │   ├── model/
│   │   │   │   ├── repository/
│   │   │   │   ├── service/
│   │   │   │   ├── restcontroller/
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/
│   │   │   │   │   └── response/
│   │   │   │   └── util/
│   │   │   ├── PeminjamanAset
│   │   │   │   ├── config/
│   │   │   │   ├── security/
│   │   │   │   ├── model/
│   │   │   │   ├── repository/
│   │   │   │   ├── service/
│   │   │   │   ├── restcontroller/
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/
│   │   │   │   │   └── response/
│   │   │   │   └── util/
│   │   │   ├── PengadaanAset
│   │   │   │   ├── config/
│   │   │   │   ├── security/
│   │   │   │   ├── model/
│   │   │   │   ├── repository/
│   │   │   │   ├── service/
│   │   │   │   ├── restcontroller/
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/
│   │   │   │   │   └── response/
│   │   │   │   └── util/
│   │   │   ├── PersetujuanPeminjamanAset
│   │   │   │   ├── config/
│   │   │   │   ├── security/
│   │   │   │   ├── model/
│   │   │   │   ├── repository/
│   │   │   │   ├── service/
│   │   │   │   ├── restcontroller/
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/
│   │   │   │   │   └── response/
│   │   │   │   └── util/
│   │   │   ├── PersetujuanPengadaanAset
│   │   │   │   ├── config/
│   │   │   │   ├── security/
│   │   │   │   ├── model/
│   │   │   │   ├── repository/
│   │   │   │   ├── service/
│   │   │   │   ├── restcontroller/
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/
│   │   │   │   │   └── response/
│   │   │   │   └── util/
│   │   │   ├── Penggantian Barang Rusak
│   │   │   │   ├── config/
│   │   │   │   ├── security/
│   │   │   │   ├── model/
│   │   │   │   ├── repository/
│   │   │   │   ├── service/
│   │   │   │   ├── restcontroller/
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/
│   │   │   │   │   └── response/
│   │   │   │   └── util/
│   │   └── resources/
│   │
│   └── test/
│   │   ├── java/io/ibuprofen/inventra_dd_be/
│   │       └── InventraDdBeApplicationTests.java
│
├── .gitignore
└── README.md

## Penjelasan Fitur Raysha Reifika Ryzki
### 1. Persetujuan Pengadaan Aset (EPIC06):
**Tujuan Fitur:**
- Mengatur proses review dan persetujuan pengadaan aset agar:
- Tidak ada pengajuan pengadaan tanpa approval
- Proses terdokumentasi dengan baik
- Biaya pengadaan dapat dikontrol
- Bukti pembelian tersimpan dalam sistem

**Alur Proses:**
- Guru/Sarpras mengajukan pengadaan aset
- Status awal: DIAJUKAN
- Kepala Sekolah dan Yayasan melakukan review
- Jika disetujui → Yayasan melakukan pembelian
- Yayasan mengunggah bukti pembelian
- Status berubah menjadi TELAH DIBELI dan jumlah aset di manajemen aset berubah.

### 2. Penggantian Barang Rusak (EPIC07):
**Tujuan Fitur:**
- Mempermudah dan mendokumentasikan penggantian barang rusak akibat peminjaman agar operasional sekolah tetap berjalan dengan baik.

**Alur Proses:**
- Guru/Siswa mengajukan penggantian barang rusak
- Status awal: DIAJUKAN
- Sarana Prasarana melakukan review

### 3. Dashboard Pengadaan Aset (EPIC10):
**Tujuan Fitur:**
Memberikan gambaran visual dan analisis terkait pengadaan aset untuk membantu pengambilan keputusan.

**Komponen Dashboard:**
- Summary Card:
    Menampilkan total pengadaan, total biaya, perbandingan antar unit (Khusus yayasan)

- Grafik Pengadaan per Tahun:
    Menampilkan total biaya pengadaan dan jumlah aset pengadaan tiap tahun

- Top 5 Aset Paling Cepat Habis
    Menampilkan aset yang paling cepat habis / paling sering dibeli ulang.

- Top 5 Pengadaan dengan Biaya Terbesar
    Menampilkan 5 transaksi pengadaan dengan nilai tertinggi.