-- CREATE DATABASE
CREATE DATABASE IF NOT EXISTS db_metramoelyatama
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE db_metramoelyatama;

-- DROP TABLES
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `Detail_penjualan`;
DROP TABLE IF EXISTS `Penjualan`;
DROP TABLE IF EXISTS `Detail_pembelian`;
DROP TABLE IF EXISTS `Pembelian`;
DROP TABLE IF EXISTS `Barang`;
DROP TABLE IF EXISTS `Invoice`;
DROP TABLE IF EXISTS `Pengerjaan`;
DROP TABLE IF EXISTS `Detail_order`;
DROP TABLE IF EXISTS `Orders`;
DROP TABLE IF EXISTS `Jenis_layanan`;
DROP TABLE IF EXISTS `Supplier`;
DROP TABLE IF EXISTS `Admin`;
DROP TABLE IF EXISTS `Karyawan`;
DROP TABLE IF EXISTS `Client`;
DROP TABLE IF EXISTS `Users`;

SET FOREIGN_KEY_CHECKS = 1;

-- CREATE TABLE
CREATE TABLE `Users` (
  id VARCHAR(36) PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role ENUM('Karyawan', 'Admin') NOT NULL,
  status ENUM('online', 'offline', 'blacklisted') DEFAULT 'offline',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Client` (
  id VARCHAR(36) PRIMARY KEY,
  nama VARCHAR(255) NOT NULL,
  jenis_client ENUM('Perusahaan', 'Perorangan', 'Event Organizer') DEFAULT 'Perorangan',
  npwp VARCHAR(20) UNIQUE,
  alamat TEXT,
  no_telp VARCHAR(15),
  email VARCHAR(100),
  tgl_daftar DATE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Karyawan` (
  id VARCHAR(36) PRIMARY KEY,
  userId VARCHAR(36),
  nama VARCHAR(255) NOT NULL,
  jabatan VARCHAR(50),
  no_telp VARCHAR(15) UNIQUE,
  tgl_masuk DATE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (userId) REFERENCES `Users`(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Admin` (
  id VARCHAR(36) PRIMARY KEY,
  userId VARCHAR(36),
  nama VARCHAR(255) NOT NULL,
  no_telp VARCHAR(15) UNIQUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (userId) REFERENCES `Users`(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Supplier` (
  id VARCHAR(36) PRIMARY KEY,
  nama VARCHAR(255) NOT NULL,
  email VARCHAR(100),
  alamat TEXT,
  no_telp VARCHAR(15) UNIQUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Jenis_layanan` (
  id VARCHAR(36) PRIMARY KEY,
  nama VARCHAR(255) NOT NULL,
  deskripsi TEXT,
  tarif DECIMAL(15,2),
  satuan VARCHAR(30),
  kategori ENUM('Catering', 'Pernikahan', 'Bangunan', 'Dekorasi', 'Lainnya') DEFAULT 'Lainnya',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Orders` (
  id VARCHAR(36) PRIMARY KEY,
  clientId VARCHAR(36),
  karyawanId VARCHAR(36),
  tgl_order DATE,
  batas_waktu DATE,
  status_order ENUM('Pending', 'Proses', 'Selesai', 'Batal') DEFAULT 'Pending',
  keterangan TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (clientId) REFERENCES Client(id) ON DELETE CASCADE,
  FOREIGN KEY (karyawanId) REFERENCES Karyawan(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Detail_order` (
  id VARCHAR(36) PRIMARY KEY,
  orderId VARCHAR(36),
  layananId VARCHAR(36),
  jumlah INT DEFAULT 1,
  tarif DECIMAL(15,2),
  subtotal DECIMAL(15,2),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (orderId) REFERENCES `Orders`(id) ON DELETE CASCADE,
  FOREIGN KEY (layananId) REFERENCES Jenis_layanan(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Pengerjaan` (
  id VARCHAR(36) PRIMARY KEY,
  orderId VARCHAR(36),
  karyawanId VARCHAR(36),
  tgl_mulai DATE,
  tgl_selesai DATE,
  catatan TEXT,
  status ENUM('Belum Mulai', 'On Progress', 'Selesai') DEFAULT 'Belum Mulai',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (orderId) REFERENCES `Orders`(id) ON DELETE CASCADE,
  FOREIGN KEY (karyawanId) REFERENCES Karyawan(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Invoice` (
  id VARCHAR(36) PRIMARY KEY,
  orderId VARCHAR(36) UNIQUE,
  tgl_invoice DATE,
  total_bayar DECIMAL(15,2),
  status_bayar ENUM('Belum Lunas', 'Lunas') DEFAULT 'Belum Lunas',
  tgl_bayar DATE,
  metode_bayar VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (orderId) REFERENCES `Orders`(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Barang` (
  id VARCHAR(36) PRIMARY KEY,
  kode_barang VARCHAR(20) UNIQUE NOT NULL,
  nama_barang VARCHAR(255) NOT NULL,
  deskripsi TEXT,
  harga_beli DECIMAL(15,2),
  harga_jual DECIMAL(15,2),
  stok INT DEFAULT 0,
  satuan ENUM('Pcs', 'Box', 'Kg', 'Liter', 'Unit', 'Sak', 'm3', 'm2', 'Paket', 'Set') DEFAULT 'Unit',
  supplierId VARCHAR(36),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (supplierId) REFERENCES Supplier(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Pembelian` (
  id VARCHAR(36) PRIMARY KEY,
  no_faktur VARCHAR(50) NOT NULL UNIQUE,
  tanggal DATETIME,
  supplierId VARCHAR(36),
  karyawanId VARCHAR(36),
  total DECIMAL(15,2),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (supplierId) REFERENCES Supplier(id) ON DELETE SET NULL,
  FOREIGN KEY (karyawanId) REFERENCES Karyawan(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Detail_pembelian` (
  id VARCHAR(36) PRIMARY KEY,
  pembelianId VARCHAR(36),
  barangId VARCHAR(36),
  qty INT,
  harga DECIMAL(15,2),
  subtotal DECIMAL(15,2),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (pembelianId) REFERENCES Pembelian(id) ON DELETE CASCADE,
  FOREIGN KEY (barangId) REFERENCES Barang(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Penjualan` (
  id VARCHAR(36) PRIMARY KEY,
  no_faktur VARCHAR(50) UNIQUE,
  tanggal DATE,
  clientId VARCHAR(36),
  karyawanId VARCHAR(36),
  total DECIMAL(15,2),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (clientId) REFERENCES Client(id) ON DELETE SET NULL,
  FOREIGN KEY (karyawanId) REFERENCES Karyawan(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Detail_penjualan` (
  id VARCHAR(36) PRIMARY KEY,
  penjualanId VARCHAR(36),
  barangId VARCHAR(36),
  qty INT,
  harga DECIMAL(15,2),
  subtotal DECIMAL(15,2),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (penjualanId) REFERENCES Penjualan(id) ON DELETE CASCADE,
  FOREIGN KEY (barangId) REFERENCES Barang(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- SEED DATA
INSERT INTO `Users` (id, email, password, role, status) VALUES
('usr-admin-001', 'admin@metramoelyatama.com', 'admin123', 'Admin', 'online'),
('usr-kary-001', 'budi@metramoelyatama.com', 'budi123', 'Karyawan', 'offline'),
('usr-kary-002', 'siti@metramoelyatama.com', 'siti123', 'Karyawan', 'offline'),
('usr-kary-003', 'ahmad@metramoelyatama.com', 'ahmad123', 'Karyawan', 'offline');

INSERT INTO `Admin` (id, userId, nama, no_telp) VALUES
('admin-001', 'usr-admin-001', 'Administrator', '081234567890');

INSERT INTO `Karyawan` (id, userId, nama, jabatan, no_telp, tgl_masuk) VALUES
('kary-001', 'usr-kary-001', 'Budi Santoso', 'Event Coordinator', '081234567891', '2023-01-15'),
('kary-002', 'usr-kary-002', 'Siti Nurhaliza', 'Admin & Keuangan', '081234567892', '2023-02-10'),
('kary-003', 'usr-kary-003', 'Ahmad Rizki', 'Supervisor Lapangan', '081234567893', '2023-03-01');

INSERT INTO `Supplier` (id, nama, email, alamat, no_telp) VALUES
('sup-001', 'PT Kertas & Percetakan Jaya', 'percetakanjaya@gmail.com', 'Jl. Industri No.10, Jakarta', '021-12345678'),
('sup-002', 'CV Alat Tenda & Dekorasi Makmur', 'tendamakmur@gmail.com', 'Jl. Raya Bogor Km 25, Depok', '0251-987654'),
('sup-003', 'UD Bahan Bangunan Sejahtera', 'bahansejahtera@yahoo.com', 'Jl. Veteran No.45, Bandung', '022-87654321');

INSERT INTO `Client` (id, nama, jenis_client, npwp, alamat, no_telp, email, tgl_daftar) VALUES
('cln-001', 'PT Maju Jaya Abadi', 'Perusahaan', '01.234.567.8-901.000', 'Jl. Sudirman No.123, Jakarta', '02112345678', 'info@majujaya.com', '2024-01-10'),
('cln-002', 'CV Berkah Event', 'Event Organizer', NULL, 'Jl. Gatot Subroto No.45, Bandung', '02298765432', 'berkahevent@gmail.com', '2024-02-05'),
('cln-003', 'Andi Setiawan', 'Perorangan', NULL, 'Jl. Diponegoro No.67, Surabaya', '08123456789', 'andi.setiawan@email.com', '2024-02-20'),
('cln-004', 'Siti Wedding Organizer', 'Event Organizer', '02.345.678.9-012.000', 'Jl. Ahmad Yani No.88, Yogyakarta', '02741234567', 'sitiwedding@gmail.com', '2024-03-01');

INSERT INTO `Jenis_layanan` (id, nama, deskripsi, tarif, satuan, kategori) VALUES
('lyn-001', 'Catering Prasmanan Pernikahan', 'Paket catering untuk 200-500 tamu', 45000.00, 'per pax', 'Catering'),
('lyn-002', 'Catering Makanan Harian Kantor', 'Catering nasi box untuk karyawan', 25000.00, 'per pax', 'Catering'),
('lyn-003', 'Sewa Tenda Pernikahan Premium', 'Tenda dekorasi lengkap dengan lampu', 3500000.00, 'per paket', 'Pernikahan'),
('lyn-004', 'Dekorasi Panggung Pernikahan', 'Dekorasi full flower & lighting', 7500000.00, 'per paket', 'Dekorasi'),
('lyn-005', 'Sewa Kursi & Meja VIP', 'Kursi dan meja premium', 85000.00, 'per set', 'Pernikahan'),
('lyn-006', 'Pembuatan Gapura & Aksesoris Pernikahan', 'Gapura custom + standing flower', 2800000.00, 'per paket', 'Pernikahan'),
('lyn-007', 'Pasokan Material Bangunan', 'Pasir, semen, bata, keramik', 0.00, 'per m3 / pcs', 'Bangunan'),
('lyn-008', 'Jasa Pengecatan & Renovasi', 'Pengecatan interior & eksterior', 85000.00, 'per m2', 'Bangunan');

INSERT INTO `Orders` (id, clientId, karyawanId, tgl_order, batas_waktu, status_order, keterangan) VALUES
('ord-2024-001', 'cln-001', 'kary-001', '2024-04-01', '2024-04-20', 'Selesai', 'Catering + Tenda untuk pernikahan anak direktur'),
('ord-2024-002', 'cln-002', 'kary-002', '2024-04-05', '2024-04-25', 'Proses', 'Full dekorasi dan tenda untuk event 300 tamu'),
('ord-2024-003', 'cln-003', 'kary-003', '2024-04-10', '2024-04-15', 'Pending', 'Sewa tenda sedang & kursi untuk syukuran'),
('ord-2024-004', 'cln-004', 'kary-001', '2024-04-15', '2024-05-05', 'Proses', 'Paket lengkap wedding organizer');

INSERT INTO `Detail_order` (id, orderId, layananId, jumlah, tarif, subtotal) VALUES
('dord-001', 'ord-2024-001', 'lyn-001', 350, 45000.00, 15750000.00),
('dord-002', 'ord-2024-001', 'lyn-003', 1, 3500000.00, 3500000.00),
('dord-003', 'ord-2024-002', 'lyn-004', 1, 7500000.00, 7500000.00),
('dord-004', 'ord-2024-002', 'lyn-005', 150, 85000.00, 12750000.00),
('dord-005', 'ord-2024-003', 'lyn-003', 1, 2800000.00, 2800000.00),
('dord-006', 'ord-2024-004', 'lyn-001', 250, 45000.00, 11250000.00);

INSERT INTO `Pengerjaan` (id, orderId, karyawanId, tgl_mulai, tgl_selesai, catatan, status) VALUES
('pgr-001', 'ord-2024-001', 'kary-001', '2024-04-05', '2024-04-18', 'Semua berjalan lancar', 'Selesai'),
('pgr-002', 'ord-2024-002', 'kary-003', '2024-04-10', NULL, 'Masih proses dekorasi', 'On Progress'),
('pgr-003', 'ord-2024-004', 'kary-001', '2024-04-20', NULL, 'Persiapan venue', 'Belum Mulai');

INSERT INTO `Invoice` (id, orderId, tgl_invoice, total_bayar, status_bayar, tgl_bayar, metode_bayar) VALUES
('inv-2024-001', 'ord-2024-001', '2024-04-19', 19250000.00, 'Lunas', '2024-04-20', 'Transfer Bank'),
('inv-2024-002', 'ord-2024-002', '2024-04-25', 20250000.00, 'Belum Lunas', NULL, NULL),
('inv-2024-003', 'ord-2024-004', '2024-05-01', 14500000.00, 'Lunas', '2024-05-02', 'Cash');

INSERT INTO `Barang` (id, kode_barang, nama_barang, deskripsi, harga_beli, harga_jual, stok, satuan, supplierId) VALUES
('brg-001', 'TD-001', 'Tenda Dekorasi Premium 6x8m', 'Tenda pernikahan dengan aksesoris', 2800000.00, 3500000.00, 8, 'Unit', 'sup-002'),
('brg-002', 'KR-001', 'Kursi VIP Putih', 'Kursi tamu VIP', 75000.00, 85000.00, 120, 'Pcs', 'sup-002'),
('brg-003', 'FL-001', 'Standing Flower Tower', 'Dekorasi bunga standing', 450000.00, 650000.00, 25, 'Unit', 'sup-002'),
('brg-004', 'SM-001', 'Semen Portland', 'Semen berkualitas', 65000.00, 75000.00, 450, 'Sak', 'sup-003'),
('brg-005', 'PS-001', 'Pasir Halus', 'Pasir untuk plesteran', 120000.00, 150000.00, 120, 'm3', 'sup-003'),
('brg-006', 'KT-001', 'Keramik 60x60', 'Keramik lantai premium', 85000.00, 110000.00, 800, 'm2', 'sup-003'),
('brg-007', 'SP-001', 'Sound System Active', 'Speaker aktif 1000W', 4500000.00, 5500000.00, 5, 'Unit', 'sup-001');

INSERT INTO `Pembelian` (id, no_faktur, tanggal, supplierId, karyawanId, total) VALUES
('pmb-001', 'PBL/2024/001', '2024-03-15 10:30:00', 'sup-002', 'kary-002', 18500000.00),
('pmb-002', 'PBL/2024/002', '2024-04-05 14:00:00', 'sup-003', 'kary-003', 42500000.00);

INSERT INTO `Detail_pembelian` (id, pembelianId, barangId, qty, harga, subtotal) VALUES
('dpmb-001', 'pmb-001', 'brg-001', 3, 2800000.00, 8400000.00),
('dpmb-002', 'pmb-001', 'brg-002', 100, 75000.00, 7500000.00),
('dpmb-003', 'pmb-002', 'brg-004', 200, 65000.00, 13000000.00),
('dpmb-004', 'pmb-002', 'brg-005', 50, 120000.00, 6000000.00);

INSERT INTO `Penjualan` (id, no_faktur, tanggal, clientId, karyawanId, total) VALUES
('pnj-001', 'PNJ/2024/001', '2024-04-10', 'cln-001', 'kary-002', 12500000.00);

INSERT INTO `Detail_penjualan` (id, penjualanId, barangId, qty, harga, subtotal) VALUES
('dpnj-001', 'pnj-001', 'brg-002', 50, 85000.00, 4250000.00),
('dpnj-002', 'pnj-001', 'brg-003', 10, 650000.00, 6500000.00);



-- Indexes
CREATE INDEX idx_user_email ON `Users`(email);
CREATE INDEX idx_user_role ON `Users`(role);
CREATE INDEX idx_client_nama ON `Client`(nama);
CREATE INDEX idx_order_status ON `Orders`(status_order);
CREATE INDEX idx_order_tanggal ON `Orders`(tgl_order);
CREATE INDEX idx_invoice_status ON `Invoice`(status_bayar);
CREATE INDEX idx_barang_kode ON `Barang`(kode_barang);