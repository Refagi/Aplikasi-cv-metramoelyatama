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
  role ENUM('Karyawan', 'Admin', 'Supplier') NOT NULL,
  status ENUM('online', 'offline', 'blacklisted') DEFAULT 'offline',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Client` (
  id VARCHAR(36) PRIMARY KEY,
  nama VARCHAR(255) NOT NULL,
  jenis_wajib_pajak ENUM('Pribadi', 'Badan'),
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
  status ENUM('aktif', 'nonaktif') DEFAULT 'aktif',
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
  userId VARCHAR(36),
  nama VARCHAR(255) NOT NULL,
  alamat TEXT,
  no_telp VARCHAR(15) UNIQUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (userId) REFERENCES `Users`(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Jenis_layanan` (
  id VARCHAR(36) PRIMARY KEY,
  nama VARCHAR(255) NOT NULL,
  deskripsi TEXT,
  tarif DECIMAL(15,2),
  satuan VARCHAR(30),
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
  FOREIGN KEY (karyawanId) REFERENCES Karyawan(userId) ON DELETE SET NULL
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
  FOREIGN KEY (karyawanId) REFERENCES Karyawan(userId) ON DELETE SET NULL
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
  satuan ENUM('Pcs', 'Box', 'Kg', 'Liter', 'Unit') DEFAULT 'Pcs',
  supplierId VARCHAR(36),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (supplierId) REFERENCES Supplier(userId) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Pembelian` (
  id VARCHAR(36) PRIMARY KEY,
  no_faktur VARCHAR(50) NOT NULL UNIQUE,
  tanggal DATETIME,
  supplierId VARCHAR(36),
  karyawanId VARCHAR(36),
  total DECIMAL(15,2),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (supplierId) REFERENCES Supplier(userId) ON DELETE SET NULL,
  FOREIGN KEY (karyawanId) REFERENCES Karyawan(userId) ON DELETE SET NULL
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
  FOREIGN KEY (karyawanId) REFERENCES Karyawan(userId) ON DELETE SET NULL
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
-- SEED: User (Admin, Karyawan, Supplier)
INSERT INTO `Users` (id, email, password, role, status) VALUES
('usr-admin-001', 'admin@metramoelyatama.com', 'admin123', 'Admin', 'online'),
('usr-kary-001', 'budi@metramoelyatama.com', 'budi123', 'Karyawan', 'offline'),
('usr-kary-002', 'siti@metramoelyatama.com', 'siti123', 'Karyawan', 'offline'),
('usr-kary-003', 'ahmad@metramoelyatama.com', 'ahmad123', 'Karyawan', 'offline'),
('usr-supp-001', 'supplier1@gmail.com', 'supp123', 'Supplier', 'offline'),
('usr-supp-002', 'supplier2@gmail.com', 'supp456', 'Supplier', 'offline');

-- SEED: Admin
INSERT INTO `Admin` (id, userId, nama, no_telp) VALUES
('admin-021', 'usr-admin-001', 'Administrator', '081234567890');

-- SEED: Karyawan
INSERT INTO `Karyawan` (id, userId, nama, jabatan, no_telp, tgl_masuk, status) VALUES
('kary-012', 'usr-kary-001', 'Budi Santoso', 'Konsultan Pajak Senior', '081234567891', '2020-01-15', 'aktif'),
('kary-052', 'usr-kary-002', 'Siti Nurhaliza', 'Staff Administrasi', '081234567892', '2021-03-10', 'aktif'),
('kary-017', 'usr-kary-003', 'Ahmad Rizki', 'Konsultan Pajak Junior', '081234567893', '2022-06-01', 'aktif');

-- SEED: Supplier
INSERT INTO `Supplier` (id, userId, nama, alamat, no_telp) VALUES
('sup-026', 'usr-supp-001', 'PT Kertas Jaya', 'Jl. Industri No. 10, Jakarta', '021-12345678'),
('supp-014', 'usr-supp-002', 'CV Alat Tulis Makmur', 'Jl. Perdagangan No. 25, Bandung', '022-87654321');

-- SEED: Clien
INSERT INTO `Client` (id, nama, jenis_wajib_pajak, npwp, alamat, no_telp, email, tgl_daftar) VALUES
('cln-001', 'PT Maju Jaya', 'Badan', '01.234.567.8-901.000', 'Jl. Sudirman No. 123, Jakarta', '02112345678', 'info@majujaya.com', '2023-01-10'),
('cln-002', 'CV Berkah Selalu', 'Badan', '02.345.678.9-012.000', 'Jl. Gatot Subroto No. 45, Bandung', '02298765432', 'cv.berkah@gmail.com', '2023-02-15'),
('cln-003', 'Andi Setiawan', 'Pribadi', '03.456.789.0-123.000', 'Jl. Diponegoro No. 67, Surabaya', '08123456789', 'andi.setiawan@email.com', '2023-03-20'),
('cln-004', 'PT Digital Solusi', 'Badan', '04.567.890.1-234.000', 'Jl. Teknologi No. 88, Yogyakarta', '02743218765', 'cs@digitalsolusi.id', '2023-04-01');

-- SEED: Jenis Layanan
INSERT INTO `Jenis_layanan` (id, nama, deskripsi, tarif, satuan) VALUES
('lyn-001', 'SPT Tahunan Pribadi', 'Pembuatan SPT Tahunan untuk Wajib Pajak Pribadi', 500000.00, 'per laporan'),
('lyn-002', 'SPT Tahunan Badan', 'Pembuatan SPT Tahunan untuk Wajib Pajak Badan', 2500000.00, 'per laporan'),
('lyn-003', 'PPN Bulanan', 'Pelaporan PPN setiap bulan', 750000.00, 'per bulan'),
('lyn-004', 'PPh 21 Bulanan', 'Pelaporan PPh 21 karyawan per bulan', 1000000.00, 'per bulan'),
('lyn-005', 'Pembuatan NPWP', 'Pengurusan NPWP baru', 300000.00, 'per dokumen'),
('lyn-006', 'Konsultasi Pajak', 'Konsultasi perpajakan umum', 200000.00, 'per jam');

-- SEED: Order
INSERT INTO `Orders` (id, clientId, karyawanId, tgl_order, batas_waktu, status_order, keterangan) VALUES
('ord-2024-001', 'cln-001', 'usr-kary-001', '2024-01-05', '2024-01-31', 'Selesai', 'SPT Tahunan 2023'),
('ord-2024-002', 'cln-002', 'usr-kary-001', '2024-02-10', '2024-03-15', 'Proses', 'Pelaporan PPN Januari-Februari'),
('ord-2024-003', 'cln-003', 'usr-kary-003', '2024-03-01', '2024-03-31', 'Pending', 'SPT Tahunan Pribadi 2023'),
('ord-2024-004', 'cln-004', 'usr-kary-002', '2024-04-01', '2024-04-30', 'Pending', 'Konsultasi pajak bulanan');

-- SEED: Detail Order
INSERT INTO `Detail_order` (id, orderId, layananId, jumlah, tarif, subtotal) VALUES
('dord-001', 'ord-2024-001', 'lyn-002', 1, 2500000.00, 2500000.00),
('dord-002', 'ord-2024-002', 'lyn-003', 2, 750000.00, 1500000.00),
('dord-003', 'ord-2024-003', 'lyn-001', 1, 500000.00, 500000.00),
('dord-004', 'ord-2024-004', 'lyn-006', 3, 200000.00, 600000.00);

-- SEED: Pengerjaan
INSERT INTO `Pengerjaan` (id, orderId, karyawanId, tgl_mulai, tgl_selesai, catatan, status) VALUES
('pgr-001', 'ord-2024-001', 'usr-kary-001', '2024-01-06', '2024-01-20', 'Selesai tepat waktu', 'Selesai'),
('pgr-002', 'ord-2024-002', 'usr-kary-001', '2024-02-11', NULL, 'Sedang dikerjakan', 'On Progress');

-- SEED: Invoice
INSERT INTO `Invoice` (id, orderId, tgl_invoice, total_bayar, status_bayar, tgl_bayar, metode_bayar) VALUES
('inv-2024-001', 'ord-2024-001', '2024-01-25', 2500000.00, 'Lunas', '2024-01-30', 'Transfer Bank'),
('inv-2024-002', 'ord-2024-002', '2024-02-28', 1500000.00, 'Belum Lunas', NULL, NULL);

-- SEED: Barang
INSERT INTO Barang (id, kode_barang, nama_barang, deskripsi, harga_beli, harga_jual, stok, satuan, supplierId) VALUES
('brg-001', 'ATK-001', 'Kertas A4 70gsm', 'Kertas A4 ukuran 70gsm isi 500 lembar', 35000.00, 45000.00, 50, 'Box', 'usr-supp-001'),
('brg-002', 'ATK-002', 'Pulpen Hitam', 'Pulpen tinta hitam standar', 2000.00, 3000.00, 200, 'Pcs', 'usr-supp-002'),
('brg-003', 'ATK-003', 'Stabilo Warna', 'Highlighter warna warni set 4', 15000.00, 20000.00, 30, 'Box', 'usr-supp-002'),
('brg-004', 'ATK-004', 'Map Plastik', 'Map plastik transparan A4', 5000.00, 8000.00, 100, 'Pcs', 'usr-supp-001');

INSERT INTO Pembelian (id, no_faktur, tanggal, supplierId, karyawanId, total) VALUES
('pmb-001', 'PBL/2024/001', '2024-01-15 10:30:00', 'usr-supp-001', 'usr-kary-002', 1750000.00),
('pmb-002', 'PBL/2024/002', '2024-02-20 14:00:00', 'usr-supp-002', 'usr-kary-002', 850000.00);

-- SEED: Detail Pembelian
INSERT INTO Detail_pembelian (id, pembelianId, barangId, qty, harga, subtotal) VALUES
('dpmb-001', 'pmb-001', 'brg-001', 50, 35000.00, 1750000.00),
('dpmb-002', 'pmb-002', 'brg-002', 200, 2000.00, 400000.00),
('dpmb-003', 'pmb-002', 'brg-003', 30, 15000.00, 450000.00);

-- SEED: Penjualan
INSERT INTO Penjualan (id, no_faktur, tanggal, clientId, karyawanId, total) VALUES
('pnj-001', 'PNJ/2024/001', '2024-03-10', 'cln-001', 'usr-kary-002', 225000.00),
('pnj-002', 'PNJ/2024/002', '2024-03-25', 'cln-002', 'usr-kary-002', 180000.00);

-- SEED: Detail Penjualan
INSERT INTO Detail_penjualan (id, penjualanId, barangId, qty, harga, subtotal) VALUES
('dpnj-001', 'pnj-001', 'brg-001', 5, 45000.00, 225000.00),
('dpnj-002', 'pnj-002', 'brg-002', 20, 3000.00, 60000.00),
('dpnj-003', 'pnj-002', 'brg-003', 6, 20000.00, 120000.00);

-- Indexes
CREATE INDEX idx_user_email ON `Users`(email);
CREATE INDEX idx_user_role ON `Users`(role);
CREATE INDEX idx_clien_nama ON Client(nama);
CREATE INDEX idx_order_status ON `Orders`(status_order);
CREATE INDEX idx_order_tanggal ON `Orders`(tgl_order);
CREATE INDEX idx_invoice_status ON Invoice(status_bayar);
CREATE INDEX idx_barang_kode ON Barang(kode_barang);
