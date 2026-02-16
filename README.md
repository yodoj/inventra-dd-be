# INVENTRA DD - Inventory and Resource Asset System Sekolah Islam Dian Didaktika

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