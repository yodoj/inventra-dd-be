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
Jalankan beberapa komando berikut ini melalui aplikasi
terminal seperti Command Prompt, PowerShell, Windows Terminal, Bash, Zsh, atau aplikasi
sejenis.

**1. Clone Repository**
```
git clone https://gitlab.cs.ui.ac.id/propensi-2025-2026-genap/kelas-a/ibuprofen/ibuprofen-backend.git
```

**2. Masuk ke folder project**
```
cd ibuprofen-backend
```

**3. Menjalankan aplikasi**
```
./gradlew bootRun
```

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


## Penjelasan Fitur Raysha Reifika Ryzki - 2306208426

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


## Penjelasan Fitur Ridya Azizah Khayyira Mumtaz - 2306245895

### 1. Mengelola Aset (EPIC02):
**Tujuan Fitur:**
- Memungkinkan pengguna melihat daftar aset pada unit masing-masing untuk meningkatkan transparansi kondisi aset.

**Alur Proses:**
- Sistem menampilkan daftar aset sesuai hak akses:
  - Role unit (Guru/Siswa/Kepsek):  hanya melihat aset pada unit sendiri.
  - Sarpras: mengelola aset pada unit miliknya.
  - Yayasan: mengelola aset pada seluruh unit.
- Pengguna dapat melakukan pencarian dan filter berdasarkan kategori, status, dan unit (jika diizinkan).
- Pengguna dengan hak akses (Sarpras/Yayasan) dapat:
  - Menambahkan aset baru
  - Memperbarui detail aset
  - Menghapus aset tertentu
- Data aset diperbarui dalam sistem dan ditampilkan kembali pada daftar aset.

### 2. Mengajukan Peminjaman Aset (EPIC03):
**Tujuan Fitur:**
- Memfasilitasi proses pengajuan peminjaman aset baik dalam unit sendiri maupun lintas unit secara terstruktur dan terdokumentasi.

**Alur Proses:**
- Pengguna (Siswa/Guru/Sarpras) membuka halaman pengajuan peminjaman.
- Sistem menampilkan daftar aset yang tersedia dan dapat dipinjam sesuai unit dan role.
- Pengguna mengisi detail peminjaman (aset, waktu, tujuan, jumlah).
- Sistem melakukan validasi:
  - Aset aktif dan tersedia
  - Jumlah tidak melebihi stok
  - Waktu pengembalian lebih besar dari waktu peminjaman
  - Untuk lintas unit (Sarpras), unit tujuan harus berbeda dari unit asal
- Jika valid, sistem menyimpan data dan menetapkan status awal DIAJUKAN.
- Pengguna dapat melihat daftar pengajuan yang telah dibuat beserta statusnya.
- Selama status masih DIAJUKAN, pengguna dapat memperbarui atau menghapus pengajuan.
- Sistem memastikan setiap perubahan hanya dapat dilakukan oleh pemilik pengajuan dan sesuai unitnya

### 3. Dashboard Utilisasi Aset (EPC11):
**Tujuan Fitur:**
- Menyediakan ringkasan dan analisis data utilisasi aset untuk membantu pengambilan keputusan berdasarkan role.

**Komponen Dashboard:**
- Summary Card Inventori Aset
   - Menampilkan total jumlah seluruh aset & per kategori
   - Data ditampilkan sesuai hak akses role:
     - Yayasan: seluruh unit.
     - Sarpras/Kepsek: hanya unit masing-masing.

- Utilisasi Aset per Unit (Khusus Yayasan)
  - Menampilkan perbandingan tingkat utilisasi antar unit.
  - Hanya muncul jika role = Yayasan.

- Tren Utilisasi Aset
  - Menampilkan tren penggunaan aset berdasarkan periode (bulan/tahun).
  - Data menyesuaikan filter yang dipilih pengguna.

- Top 5 Aset Paling Sering Dipinjam
  - Menampilkan 5 aset dengan frekuensi peminjaman tertinggi. 

- Top 5 Aset Paling Sering Rusak/Hilang
  - Menampilkan 5 aset dengan tingkat kerusakan atau kehilangan tertinggi.

- Filter Dashboard
  - Filter akan memperbarui seluruh komponen dashboard secara dinamis sesuai parameter yang dipilih.
  - Komponen: 
    - Periode (bulan/tahun)
    - Kategori aset
    - Unit (khusus Yayasan)


## Penjelasan Fitur Indah Cahya Puspitahati - 2306245453

### 1. EPIC01 – Modul Manajemen Akun & Profil Pengguna

Modul Manajemen Akun & Profil Pengguna merupakan bagian dari sistem **INVENTRA DD (Inventory and Resource Asset System Sekolah Islam Dian Didaktika)** yang berfokus pada pengelolaan data akun pengguna secara terpusat dan aman dengan penerapan **Role-Based Access Control (RBAC)**.

Modul ini memastikan setiap pengguna memiliki akses sesuai peran dan unitnya, serta mendukung proses administrasi akun oleh pihak Sarpras.

---

## Role yang Terlibat
- **Sarpras** (Administrator)
- **Guru**
- **Siswa**
- **Kepala Sekolah (Kepsek)**
- **Yayasan**

> Catatan: Sebagian besar endpoint administrasi hanya dapat diakses oleh role **Sarpras**.

---

## Fitur Utama

### 1. Menambahkan Akun Pengguna
- **Endpoint:** `POST /api/users`
- **Aktor:** Sarpras
- **Deskripsi:**  
  Mendaftarkan akun pengguna baru ke dalam sistem sesuai dengan peran dan unit yang ditentukan.

- **Aturan & Validasi:**
  - Email wajib unik dan berformat valid.
  - Field wajib: Nama Lengkap, Email, Role, Password.
  - Untuk role **Siswa**, wajib mengisi Unit dan Kelas.
  - Unit akun otomatis mengikuti unit Sarpras yang membuat akun.
  - Password dienkripsi sebelum disimpan ke database.

- **Respons:**
  - Berhasil → akun tersimpan dan notifikasi sukses ditampilkan.
  - Gagal → pesan error ditampilkan sesuai jenis validasi.

---

### 2. Melihat Daftar Akun Pengguna dalam Satu Unit
- **Endpoint:** `GET /api/users/per-unit`
- **Aktor:** Sarpras
- **Deskripsi:**  
  Menampilkan daftar akun pengguna yang terdaftar dalam unit yang sama dengan Sarpras yang sedang login.

- **Fitur Pendukung:**
  - Pencarian berdasarkan nama.
  - Filter berdasarkan role.
  - Pagination (page & limit).

- **Keamanan:**
  - Data dibatasi hanya pada satu unit sesuai akun Sarpras.

---

### 3. Melihat Daftar Akun Sarpras Lintas Unit
- **Endpoint:** `GET /api/users/lintas-unit`
- **Aktor:** Sarpras
- **Deskripsi:**  
  Menampilkan daftar akun Sarpras dari seluruh unit untuk kebutuhan koordinasi lintas unit.

- **Fitur Pendukung:**
  - Pencarian berdasarkan nama, email, atau nomor telepon.
  - Filter berdasarkan unit.
  - Pagination.

- **Aturan Bisnis:**
  - Hanya akun dengan role **Sarpras** yang ditampilkan.

---

### 4. Melihat Detail Akun Pengguna
- **Endpoint:** `GET /api/users/{id}`
- **Aktor:** Sarpras
- **Deskripsi:**  
  Menampilkan detail lengkap informasi akun pengguna berdasarkan ID.

- **Perilaku Sistem:**
  - Data ditampilkan dalam mode **read-only**.
  - Jika ID tidak ditemukan, sistem mengembalikan error **404 Not Found**.

- **UI Pendukung:**
  - Tombol **Edit** untuk mengubah data akun.
  - Tombol **Kembali** untuk kembali ke daftar akun tanpa reload halaman.

---

### 5. Menghapus Akun Pengguna
- **Endpoint:** `DELETE /api/users/{id}`
- **Aktor:** Sarpras
- **Deskripsi:**  
  Menghapus akun pengguna yang sudah tidak diperlukan atau tidak aktif.

- **Aturan & Keamanan:**
  - Konfirmasi penghapusan wajib ditampilkan.
  - Sistem memverifikasi keberadaan akun sebelum penghapusan.
  - Penghapusan menggunakan mekanisme **soft delete**.

- **Respons:**
  - Berhasil → daftar akun diperbarui secara otomatis.
  - Gagal → pesan error ditampilkan.

---

## Keamanan
- Seluruh endpoint dilindungi dengan autentikasi berbasis token.
- Implementasi **RBAC** untuk membatasi akses berdasarkan role.
- Validasi data dilakukan di sisi client dan server.

---

## Tujuan Modul
- Menjaga konsistensi dan keamanan data akun pengguna.
- Mempermudah administrasi pengguna oleh Sarpras.
- Memastikan setiap pengguna hanya memiliki akses sesuai peran dan unitnya.

---


### 2. Laporan Utilisasi Aset (EPIC08)
**Tujuan Fitur:**
Menyediakan laporan terstruktur mengenai pemanfaatan aset sekolah dalam bentuk tabel dan dokumen PDF, sehingga pihak manajemen dapat memantau riwayat dan frekuensi peminjaman aset secara transparan serta mendukung evaluasi dan pengambilan keputusan berbasis data.

**Alur Proses:**
- Akses Fitur:
    - Fitur laporan hanya dapat diakses oleh pengguna dengan role Yayasan, Kepala Sekolah (Kepsek), dan Sarpras sesuai kebijakan RBAC.
    - Pengguna membuka halaman Laporan Utilisasi Aset dari menu utama sistem.

**Riwayat Peminjaman Aset**
- Sistem menampilkan tabel riwayat peminjaman aset yang berisi informasi:
    - Nama peminjam
    - Nama aset
    - Kategori aset
    - Unit
    - Waktu peminjaman dan pengembalian
    - Tujuan peminjaman
    - Status peminjaman
- Secara default, tab Riwayat Peminjaman ditampilkan sebagai tampilan awal.
- Pengguna dapat melakukan:
    - Filter berdasarkan unit, periode (bulan/tahun/rentang tanggal), kategori aset, dan kata kunci pencarian.
    - Sort dan pencarian untuk mempermudah analisis data.
- Sistem mendukung pagination untuk menampilkan data dalam jumlah besar secara efisien.
- Jika data kosong, sistem menampilkan empty state dengan pesan informatif.

**Riwayat Frekuensi Peminjaman Aset**
- Pengguna dapat berpindah ke tab Riwayat Frekuensi Peminjaman.
- Sistem menampilkan ringkasan pemanfaatan aset dalam periode tertentu, meliputi:
    - Frekuensi peminjaman per aset
    - Total durasi peminjaman
    - Status aset (Tersedia, Dipinjam, Rusak)
- Data dapat difilter berdasarkan:
    - Unit
    - Periode
    - Kategori aset
    - Status aset
- Informasi ini membantu mengidentifikasi aset yang paling sering digunakan, jarang digunakan, atau berpotensi mengalami kerusakan.

**Riwayat Frekuensi Peminjaman Aset**
- Pengguna dapat mengunduh laporan dalam bentuk PDF melalui tombol Unduh / Export PDF.
- File PDF dihasilkan berdasarkan filter aktif yang sedang diterapkan pada tabel.
- Sistem menampilkan loading state selama proses pembuatan file.
- Jika data tidak tersedia, sistem tetap menampilkan PDF dengan keterangan bahwa data tidak ditemukan.
- Riwayat unduhan dicatat oleh sistem untuk kebutuhan audit dan pelacakan.


## Penjelasan Fitur Nadira Aliya Nashwa - 2306165692

### 1. Mengajukan Pengadaan Aset (EPIC04)
**Tujuan Fitur:**
Mengatur proses pengajuan pengadaan aset agar:
- Setiap kebutuhan aset terdokumentasi dengan jelas.
- Pengadaan dilakukan berdasarkan kebutuhan yang terverifikasi.
- Memudahkan proses review dan persetujuan pengadaan.
- Mendukung perencanaan dan kontrol anggaran.
- Mengurangi risiko pengadaan yang tidak diperlukan.

**Alur Proses:**
- Guru/Sarpras mengajukan pengadaan aset.
- Status awal: DIAJUKAN.
- Kepala Sekolah melakukan review.
- Jika disetujui → Status berubah menjadi DISETUJUI OLEH KEPALA SEKOLAH.
- Jika ditolak → Status berubah menjadi DITOLAK.
- Jika disetujui oleh Kepsek → diteruskan ke Yayasan untuk review akhir.
- Jika disetujui oleh Yayasan → Status berubah menjadi DISETUJUI OLEH YAYASAN.
- Jika ditolak oleh Yayasan → Status berubah menjadi DITOLAK.

### 2. Mengelola Persetujuan Peminjaman Aset (EPIC05)
**Tujuan Fitur:**
Mengatur proses review dan persetujuan peminjaman aset agar:
- Tidak ada pengajuan peminjaman tanpa approval
- Proses terdokumentasi dengan baik
- Mengurangi risiko bentrok penggunaan aset.
- Menjaga kontrol dan ketersediaan aset di setiap unit.
Meningkatkan transparansi proses peminjaman.

**Alur Proses:**
- Guru/Sarpras (lintas unit)/Siswa mengajukan peminjaman aset
- Status awal: DIAJUKAN
- Sarpras melakukan review
- Jika disetujui → Status pengajuan berubah menjadi DISETUJUI
- Jika ditolak → Status pengajuan berubah menjadi DITOLAK

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


## Penjelasan Fitur Patricia Gloria Sujatmoko Silaban - 2306275172

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
