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