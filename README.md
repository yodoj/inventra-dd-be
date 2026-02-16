# INVENTRA DD - Inventory and Resource Asset System Sekolah Islam Dian Didaktika

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