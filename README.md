# Ibuprofen Back-End - Dokumentasi API & Fitur

Dokumen ini berisi spesifikasi teknis untuk API yang dikembangkan oleh **Patricia Gloria Sujatmoko Silaban** pada proyek Sistem Informasi Manajemen Aset, Pinjaman, dan Pengadaan Sekolah Islam Dian Didaktika.

## 🛠 Fitur & Endpoint API

### 1. Modul Manajemen Akun (User Management)
Fokus pada pengelolaan data profil pengguna dengan keamanan berbasis token dan role.

* **Melihat Profil Mandiri**
    * **Endpoint:** `GET /api/profile`
    * **Fungsi:** Mengambil data profil lengkap berdasarkan token sesi pengguna yang aktif.
    * **Keamanan:** Memastikan pengguna hanya dapat mengakses data miliknya sendiri.
* **Mengedit Profil Mandiri**
    * **Endpoint:** `PUT /api/profile`
    * **Fungsi:** Memperbarui informasi profil atau mengubah kata sandi.
    * **Validasi:** Sistem wajib melakukan validasi agar field `Role`, `Email`, dan `Unit` tidak dapat diubah oleh pemilik akun sendiri untuk menjaga konsistensi organisasi.
* **Update Akun oleh Admin (Sarpras)**
    * **Endpoint:** `PUT /api/users/{id}`
    * **Fungsi:** Memperbarui informasi atau hak akses pengguna lain.
    * **Otorisasi:** Mengimplementasikan Role-Based Access Control (RBAC) sehingga hanya role **Sarpras** yang dapat mengakses endpoint ini.

### 2. Modul Laporan Pengadaan Aset (Reporting)
Fokus pada pengolahan data agregat dan ekspor dokumen.

* **Get Laporan Pengadaan**
    * **Endpoint:** `GET /api/laporan/pengadaan`
    * **Parameter:** Menerima `period_type` (daily, monthly, yearly) serta `start_date` dan `end_date`.
    * **Fitur:** Mendukung filter (Unit, Kategori, Status), pencarian (nama aset), dan pengurutan (estimated_price).
    * **Otorisasi:** Hanya dapat diakses oleh role **Yayasan, Kepsek, dan Sarpras**.
* **Export Laporan PDF**
    * **Endpoint:** `GET /api/laporan/pengadaan/export/pdf`
    * **Fungsi:** Menghasilkan dokumen PDF berdasarkan dataset yang sesuai dengan filter yang dikirimkan.
    * **Output:** Mengembalikan file PDF valid (`Content-Type: application/pdf`).

## 🔒 Keamanan & Validasi Global
* **RBAC:** Setiap endpoint dilindungi oleh pengecekan role sesuai spesifikasi backlog.
* **Validasi Input:** Mengembalikan error `400 Bad Request` jika format tanggal atau parameter tidak sesuai.
* **Autentikasi:** Mengembalikan `401/403` untuk token atau role yang tidak valid.

# INVENTRA DD - Inventory and Resource Asset System Sekolah Islam Dian Didaktika


Aplikasi backend berbasis Spring Boot untuk manajemen aset sekolah, mencakup:
  
  - **TPS**: Kelola profil, kelola aset, peminjaman aset, pengadaan aset,
  persetujuan peminjaman, persetujuan pengadaan aset, penggantian barang rusak

  - **MIS**: Laporan utilisasi aset & pengadaan

  - **EIS**: Dashboard pengadaan & peminjaman

Ringkasan kebutuhan fitur & role akses mengacu pada dokumen README pada tiap branch anggota.

## Tech Stack

- Java 11+
- Spring Boot
- Gradle
- Database (PostgreSQL)
- Docker 
 
## Instalasi dan Menjalankan Aplikasi

1. Clone Repository:

    git clone https://gitlab.cs.ui.ac.id/propensi-2025-2026-genap/kelas-a/ibuprofen/ibuprofen-backend.git

    cd ibuprofen-backend

2. Menjalankan aplikasi:
    ./gradlew bootRun

## Struktur Direktori
```
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
```

## Penjelasan Struktur Direktori:

| Folder            | Fungsi                                                 |
| ----------------- | ------------------------------------------------------ |
| `config/`         | Konfigurasi khusus module (bean, config tambahan, dll) |
| `security/`       | Pengaturan keamanan (JWT filter, role access, dsb)     |
| `model/`          | Entity / representasi tabel database                   |
| `repository/`     | Interface JPA untuk akses database                     |
| `service/`        | Business logic aplikasi                                |
| `restcontroller/` | Endpoint API (Controller REST)                         |
| `dto/request/`    | Object untuk menerima request dari client              |
| `dto/response/`   | Object untuk response ke client                        |
| `util/`           | Helper / utility khusus module                         |
