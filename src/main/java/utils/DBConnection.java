package utils;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnection {

    private static DBConnection instance;
    private Connection connection;

    private final String host;
    private final String port;
    private final String name;
    private final String user;
    private final String pass;
    private final String timezone;

    private DBConnection() {
        Dotenv dotenv = Dotenv.configure()
                .directory("src/main/resources")
                .ignoreIfMissing()
                .load();

        this.host     = dotenv.get("DB_HOST",     "localhost");
        this.port     = dotenv.get("DB_PORT",     "3306");
        this.name     = dotenv.get("DB_NAME",     "");
        this.user     = dotenv.get("DB_USER",     "root");
        this.pass     = dotenv.get("DB_PASS",     "");
        this.timezone = dotenv.get("DB_TIMEZONE", "Asia/Jakarta");

        if (this.name.isBlank()) {
            throw new RuntimeException("DB_NAME di .env kosong! Isi dulu nama database.");
        }
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                String url = String.format(
                    "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=%s&allowPublicKeyRetrieval=true",
                    host, port, name, timezone
                );

                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(url, user, pass);

                System.out.println("✅ Koneksi DB berhasil → " + name + " (" + host + ":" + port + ")");
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL tidak ditemukan. Cek dependency pom.xml!", e);
        } catch (SQLException e) {
            throw new RuntimeException(
                "Koneksi DB gagal!\n" +
                "   Pastikan XAMPP MySQL sudah aktif\n" +
                "   Cek nilai DB_HOST, DB_PORT, DB_USER, DB_PASS di .env\n" +
                "   Detail: " + e.getMessage(), e
            );
        }
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Koneksi DB ditutup.");
            }
        } catch (SQLException e) {
            System.err.println("Gagal tutup koneksi: " + e.getMessage());
        }
    }

    public String getInfo() {
        return String.format("DB: %s | Host: %s:%s | User: %s", name, host, port, user);
    }
}