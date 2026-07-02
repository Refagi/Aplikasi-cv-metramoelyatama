CREATE DATABASE IF NOT EXISTS db_metramoelyatama
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE db_metramoelyatama;

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
  no_telp VARCHAR(30),
  email VARCHAR(100),
  tgl_daftar DATE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Karyawan` (
  id VARCHAR(36) PRIMARY KEY,
  userId VARCHAR(36),
  nama VARCHAR(255) NOT NULL,
  jabatan VARCHAR(50),
  no_telp VARCHAR(30) UNIQUE,
  tgl_masuk DATE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (userId) REFERENCES `Users`(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Admin` (
  id VARCHAR(36) PRIMARY KEY,
  userId VARCHAR(36),
  nama VARCHAR(255) NOT NULL,
  no_telp VARCHAR(30) UNIQUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (userId) REFERENCES `Users`(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Supplier` (
  id VARCHAR(36) PRIMARY KEY,
  nama VARCHAR(255) NOT NULL,
  email VARCHAR(100),
  alamat TEXT,
  no_telp VARCHAR(30) UNIQUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Jenis_layanan` (
  id VARCHAR(36) PRIMARY KEY,
  nama VARCHAR(255) NOT NULL,
  deskripsi TEXT,
  kategori ENUM('Catering', 'Pernikahan', 'Bangunan', 'Dekorasi', 'Lainnya') DEFAULT 'Lainnya',
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
  status_bayar ENUM('Belum Bayar', 'Cicilan', 'Lunas') DEFAULT 'Belum Bayar',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (orderId) REFERENCES `Orders`(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Pembayaran` (
  id VARCHAR(36) PRIMARY KEY,
  invoiceId VARCHAR(36) NOT NULL,
  tgl_bayar DATE NOT NULL,
  jumlah_bayar DECIMAL(15,2) NOT NULL,
  metode_bayar ENUM('Transfer', 'Cash') DEFAULT 'Cash',
  catatan VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (invoiceId) REFERENCES `Invoice`(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Barang` (
  id VARCHAR(36) PRIMARY KEY,
  kode_barang VARCHAR(20) UNIQUE NOT NULL,
  nama_barang VARCHAR(255) NOT NULL,
  deskripsi TEXT,
  harga_beli DECIMAL(15,2),
  harga_jual DECIMAL(15,2),
  stok INT DEFAULT 0,
  satuan ENUM('Pax', 'Pcs', 'Box', 'Kg', 'Liter', 'Unit', 'Sak', 'm3', 'm2', 'Paket', 'Set') DEFAULT 'Unit',
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

--SEED DATA
INSERT INTO `Users` (id, email, password, role, status) VALUES
(UUID(), 'admin@gmail.com', 'admin123', 'Admin', 'online'),
(UUID(), 'budi@gmail.com', 'budi123', 'Karyawan', 'offline'),
(UUID(), 'siti@gmail.com', 'siti123', 'Karyawan', 'offline'),
(UUID(), 'ahmad@gmail.com', 'ahmad123', 'Karyawan', 'offline'),
(UUID(), 'rina@gmail.com', 'rina123', 'Karyawan', 'offline');

--variable mysql untuk fk id
SET @admin1  = (SELECT id FROM `Users` WHERE email = 'admin@gmail.com' LIMIT 1);
SET @kary1   = (SELECT id FROM `Users` WHERE email = 'budi@gmail.com' LIMIT 1);
SET @kary2   = (SELECT id FROM `Users` WHERE email = 'siti@gmail.com' LIMIT 1);
SET @kary3   = (SELECT id FROM `Users` WHERE email = 'ahmad@gmail.com' LIMIT 1);
SET @kary4   = (SELECT id FROM `Users` WHERE email = 'rina@gmail.com' LIMIT 1);

INSERT INTO `Admin` (id, userId, nama, no_telp) VALUES
(UUID(), @admin1, 'Administrator Utama', '081234567890');

INSERT INTO `Karyawan` (id, userId, nama, jabatan, no_telp, tgl_masuk) VALUES
(UUID(), @kary1, 'Budi Santoso', 'Event Coordinator', '081234567891', '2023-01-15'),
(UUID(), @kary2, 'Siti Nurhaliza', 'Admin & Keuangan', '081234567892', '2023-02-10'),
(UUID(), @kary3, 'Ahmad Rizki', 'Supervisor Lapangan', '081234567893', '2023-03-01'),
(UUID(), @kary4, 'Rina Wijaya', 'Marketing & Sales', '081234567894', '2023-04-01');

SET @adm_id   = (SELECT id FROM `Admin` LIMIT 1);
SET @kar1_id  = (SELECT id FROM `Karyawan` WHERE nama LIKE '%Budi%' LIMIT 1);
SET @kar2_id  = (SELECT id FROM `Karyawan` WHERE nama LIKE '%Siti%' LIMIT 1);
SET @kar3_id  = (SELECT id FROM `Karyawan` WHERE nama LIKE '%Ahmad%' LIMIT 1);
SET @kar4_id  = (SELECT id FROM `Karyawan` WHERE nama LIKE '%Rina%' LIMIT 1);

INSERT INTO `Supplier` (id, nama, email, alamat, no_telp) VALUES
(UUID(), 'PT Kertas & Percetakan Jaya', 'percetakanjaya@gmail.com', 'Jl. Industri No.10, Jakarta', '021-12345678'),
(UUID(), 'CV Alat Tenda & Dekorasi Makmur', 'tendamakmur@gmail.com', 'Jl. Raya Bogor Km 25, Depok', '0251-987654'),
(UUID(), 'UD Bahan Bangunan Sejahtera', 'bahansejahtera@gmail.com', 'Jl. Veteran No.45, Bandung', '022-87654321'),
(UUID(), 'CV Catering Premium Nusantara', 'cateringnusantara@gmail.com', 'Jl. Raya Cikarang No.88, Bekasi', '021-4567890');

SET @splr1  = (SELECT id FROM `Supplier` WHERE email = 'percetakanjaya@gmail.com' LIMIT 1);
SET @splr2  = (SELECT id FROM `Supplier` WHERE email = 'tendamakmur@gmail.com' LIMIT 1);
SET @splr3  = (SELECT id FROM `Supplier` WHERE email = 'bahansejahtera@gmail.com' LIMIT 1);
SET @splr4  = (SELECT id FROM `Supplier` WHERE email = 'cateringnusantara@gmail.com' LIMIT 1);

INSERT INTO `Client` (id, nama, jenis_client, npwp, alamat, no_telp, email, tgl_daftar) VALUES
(UUID(), 'PT Maju Jaya Abadi', 'Perusahaan', '01.234.567.8-901.000', 'Jl. Sudirman No.123, Jakarta', '02112345678', 'info@majujaya.com', '2024-01-10'),
(UUID(), 'CV Berkah Event Organizer', 'Event Organizer', NULL, 'Jl. Gatot Subroto No.45, Bandung', '02298765432', 'berkahevent@gmail.com', '2024-02-05'),
(UUID(), 'Andi Setiawan', 'Perorangan', NULL, 'Jl. Diponegoro No.67, Surabaya', '08123456789', 'andi.setiawan@email.com', '2024-02-20'),
(UUID(), 'Siti Wedding Organizer', 'Event Organizer', '02.345.678.9-012.000', 'Jl. Ahmad Yani No.88, Yogyakarta', '02741234567', 'sitiwedding@gmail.com', '2024-03-01'),
(UUID(), 'PT Graha Mulia Sejahtera', 'Perusahaan', '03.456.789.0-123.000', 'Jl. Thamrin No.99, Jakarta', '0217654321', 'gmsejahtera@company.com', '2024-04-05');

SET @client1  = (SELECT id FROM `Client` WHERE nama = 'PT Maju Jaya Abadi' LIMIT 1);
SET @client2  = (SELECT id FROM `Client` WHERE nama = 'CV Berkah Event Organizer' LIMIT 1);
SET @client3  = (SELECT id FROM `Client` WHERE nama = 'Andi Setiawan' LIMIT 1);
SET @client4  = (SELECT id FROM `Client` WHERE nama = 'Siti Wedding Organizer' LIMIT 1);
SET @client5  = (SELECT id FROM `Client` WHERE nama = 'PT Graha Mulia Sejahtera' LIMIT 1);

INSERT INTO `Jenis_layanan` (id, nama, deskripsi, kategori, tarif, satuan) VALUES
(UUID(), 'Catering Prasmanan Pernikahan', 'Paket catering premium 200-500 pax', 'Catering', 45000.00, 'per pax'),
(UUID(), 'Catering Nasi Box Kantor', 'Nasi box untuk karyawan kantor', 'Catering', 25000.00, 'per pax'),
(UUID(), 'Sewa Tenda Pernikahan Premium', 'Tenda dekorasi lengkap + lampu', 'Pernikahan', 3500000.00, 'per paket'),
(UUID(), 'Dekorasi Panggung & Pelaminan', 'Dekorasi full flower & lighting',  'Dekorasi', 7500000.00, 'per paket'),
(UUID(), 'Sewa Kursi & Meja VIP', 'Kursi dan meja premium', 'Pernikahan', 85000.00, 'per set'),
(UUID(), 'Pembuatan Gapura Custom', 'Gapura + standing flower', 'Pernikahan', 2800000.00, 'per paket'),
(UUID(), 'Material Bangunan (Pasir & Semen)', 'Pasir halus dan semen berkualitas', 'Bangunan', 400000.00, 'per m3/sak'),
(UUID(), 'Jasa Pengecatan Interior', 'Cat tembok interior & eksterior', 'Bangunan', 85000.00, 'per m2');

SET @lyn1 = (SELECT id FROM `Jenis_layanan` LIMIT 1);
SET @lyn2 = (SELECT id FROM `Jenis_layanan` WHERE nama LIKE '%Tenda%' LIMIT 1);
SET @lyn3 = (SELECT id FROM `Jenis_layanan` WHERE nama LIKE '%Dekorasi%' LIMIT 1);


SET @ord1 = UUID();
INSERT INTO `Orders` (id, clientId, karyawanId, tgl_order, batas_waktu, status_order, keterangan) 
VALUES (@ord1, @client1, @kar1_id, '2025-04-01', '2025-04-20', 'Selesai', 'Pernikahan anak direktur');

INSERT INTO `Detail_order` (id, orderId, layananId, jumlah, tarif, subtotal) VALUES
(UUID(), @ord1, @lyn1, 350, 45000.00, 15750000.00),
(UUID(), @ord1, @lyn2, 1, 3500000.00, 3500000.00);

INSERT INTO `Pengerjaan` (id, orderId, karyawanId, tgl_mulai, tgl_selesai, catatan, status) 
VALUES (UUID(), @ord1, @kar1_id, '2025-04-05', '2025-04-18', 'Semua berjalan lancar', 'Selesai');

INSERT INTO `Invoice` (id, orderId, tgl_invoice, total_bayar, status_bayar, tgl_bayar, metode_bayar) 
VALUES (UUID(), @ord1, '2025-04-19', 19250000.00, 'Lunas', '2025-04-20', 'Transfer Bank');

SET @ord2 = UUID();
INSERT INTO `Orders` (id, clientId, karyawanId, tgl_order, batas_waktu, status_order, keterangan) 
VALUES (@ord2, @client2, @kar2_id, '2025-04-10', '2025-05-05', 'Proses', 'Event perusahaan');

INSERT INTO `Detail_order` (id, orderId, layananId, jumlah, tarif, subtotal) VALUES
(UUID(), @ord2, @lyn3, 1, 7500000.00, 7500000.00);

INSERT INTO `Pengerjaan` (id, orderId, karyawanId, tgl_mulai, tgl_selesai, catatan, status) 
VALUES (UUID(), @ord2, @kar3_id, '2025-04-12', NULL, 'Masih dalam proses', 'On Progress');


INSERT INTO `Barang` (id, kode_barang, nama_barang, deskripsi, harga_beli, harga_jual, stok, satuan, supplierId) VALUES
(UUID(), 'TD-001', 'Tenda Dekorasi Premium 6x8m', 'Tenda pernikahan premium', 2800000.00, 3500000.00, 12, 'Unit', @splr1),
(UUID(), 'KR-001', 'Kursi VIP Putih Gold', 'Kursi tamu VIP', 75000.00, 85000.00, 200, 'Pcs', @splr2),
(UUID(), 'FL-001', 'Standing Flower Tower', 'Dekorasi bunga standing', 450000.00, 650000.00, 35, 'Unit', @splr3),
(UUID(), 'SM-001', 'Semen Portland', 'Semen berkualitas', 65000.00, 75000.00, 600, 'Sak', @splr4);

-- Indexes
CREATE INDEX idx_user_email ON `Users`(email);
CREATE INDEX idx_user_role ON `Users`(role);
CREATE INDEX idx_client_nama ON `Client`(nama);
CREATE INDEX idx_order_status ON `Orders`(status_order);
CREATE INDEX idx_order_tanggal ON `Orders`(tgl_order);
CREATE INDEX idx_invoice_status ON `Invoice`(status_bayar);
CREATE INDEX idx_barang_kode ON `Barang`(kode_barang);

CREATE INDEX idx_pembayaran_invoice ON `Pembayaran`(invoiceId);
CREATE INDEX idx_pembayaran_tanggal ON `Pembayaran`(tgl_bayar);