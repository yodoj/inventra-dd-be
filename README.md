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
