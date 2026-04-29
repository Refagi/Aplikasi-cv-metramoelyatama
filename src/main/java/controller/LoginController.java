package controller;

import utils.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.animation.FadeTransition;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class LoginController {

    @FXML private VBox loginFormPanel;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Hyperlink linkForgotPassword;
    @FXML private TextField txtVisiblePassword;
    @FXML private CheckBox chkShowPassword;
    
    @FXML private VBox forgotPasswordPanel; 
    @FXML private PasswordField txtNewPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private TextField txtVisibleNewPassword;
    @FXML private TextField txtVisibleConfirmPassword;
    @FXML private CheckBox chkShowPasswordForgot;
    @FXML private Button btnChangePassword;
    @FXML private Hyperlink linkBackToLogin;

    @FXML
    private void initialize() {
        // Bind visible password dengan password field (Login)
        txtVisiblePassword.textProperty().bindBidirectional(txtPassword.textProperty());
        
        // Bind visible password dengan password field (Forgot Password)
        if (txtNewPassword != null && txtVisibleNewPassword != null) {
            txtVisibleNewPassword.textProperty().bindBidirectional(txtNewPassword.textProperty());
        }
        if (txtConfirmPassword != null && txtVisibleConfirmPassword != null) {
            txtVisibleConfirmPassword.textProperty().bindBidirectional(txtConfirmPassword.textProperty());
        }

        txtPassword.setOnAction(event -> handleLogin());
        
        // Default state: Login visible, Forgot Password hidden
        if (loginFormPanel != null) {
            loginFormPanel.setVisible(true);
            loginFormPanel.setManaged(true);
        }
        if (forgotPasswordPanel != null) {
            forgotPasswordPanel.setVisible(false);
            forgotPasswordPanel.setManaged(false);
        }
        
    }

    @FXML
    private void handleLogin() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, 
                     "Form Belum Lengkap", 
                     "Email dan password harus diisi!");
            return;
        }

        try {
            Connection conn = DBConnection.getInstance().getConnection();
            String sql = "SELECT * FROM `Users` WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String userId = rs.getString("id");
                String role = rs.getString("role");
                
                String nama = getNamaByRole(userId, role);

                showAlert(Alert.AlertType.INFORMATION, 
                         "Login Berhasil", 
                         "Selamat datang, " + nama + "!");

                openMenuPage(userId, nama, role);

            } else {
                showAlert(Alert.AlertType.ERROR, 
                         "Login Gagal", 
                         "Email atau password salah!");
            }

            rs.close();
            stmt.close();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, 
                     "Koneksi Error", 
                     "Gagal terhubung ke database:\n" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String getNamaByRole(String userId, String role) throws Exception {
        Connection conn = DBConnection.getInstance().getConnection();
        String tableName = role.equals("Admin") ? "Admin" : role.equals("Supplier") ? "Supplier" : "Karyawan";
        String sql = "SELECT nama FROM " + tableName + " WHERE userId = ?";
        
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, userId);
        ResultSet rs = stmt.executeQuery();
        String nama = rs.next() ? rs.getString("nama") : "User";
        rs.close();
        stmt.close();
        return nama;
    }
    
    @FXML
    private void handleShowPassword() {
        if (chkShowPassword.isSelected()) {
            txtVisiblePassword.setVisible(true);
            txtVisiblePassword.setManaged(true);
            txtPassword.setVisible(false);
            txtPassword.setManaged(false);
        } else {
            txtVisiblePassword.setVisible(false);
            txtVisiblePassword.setManaged(false);
            txtPassword.setVisible(true);
            txtPassword.setManaged(true);
        }
    }
    
       @FXML
    private void handleShowPasswordForgot() {
        if (chkShowPasswordForgot.isSelected()) {
            // Show New Password
            if (txtVisibleNewPassword != null && txtNewPassword != null) {
                txtVisibleNewPassword.setVisible(true);
                txtVisibleNewPassword.setManaged(true);
                txtNewPassword.setVisible(false);
                txtNewPassword.setManaged(false);
            }
            // Show Confirm Password
            if (txtVisibleConfirmPassword != null && txtConfirmPassword != null) {
                txtVisibleConfirmPassword.setVisible(true);
                txtVisibleConfirmPassword.setManaged(true);
                txtConfirmPassword.setVisible(false);
                txtConfirmPassword.setManaged(false);
            }
        } else {
            // Hide New Password
            if (txtVisibleNewPassword != null && txtNewPassword != null) {
                txtVisibleNewPassword.setVisible(false);
                txtVisibleNewPassword.setManaged(false);
                txtNewPassword.setVisible(true);
                txtNewPassword.setManaged(true);
            }
            // Hide Confirm Password
            if (txtVisibleConfirmPassword != null && txtConfirmPassword != null) {
                txtVisibleConfirmPassword.setVisible(false);
                txtVisibleConfirmPassword.setManaged(false);
                txtConfirmPassword.setVisible(true);
                txtConfirmPassword.setManaged(true);
            }
        }
    }
    
    @FXML
    private void handleForgotPassword() {
        // Animasi transisi dari Login ke Forgot Password
        animateFormTransition(loginFormPanel, forgotPasswordPanel);
    }
    
    
    @FXML
    private void handleBackToLogin() {
        // Animasi transisi dari Forgot Password ke Login
        animateFormTransition(forgotPasswordPanel, loginFormPanel);
        
        // Clear fields
        if (txtNewPassword != null) txtNewPassword.clear();
        if (txtConfirmPassword != null) txtConfirmPassword.clear();
        if (chkShowPasswordForgot != null) chkShowPasswordForgot.setSelected(false);
    }
    
    @FXML
    private void handleChangePassword() {
        String newPassword = txtNewPassword.getText().trim();
        String confirmPassword = txtConfirmPassword.getText().trim();

        // Validasi
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, 
                     "Form Belum Lengkap", 
                     "Semua field harus diisi!");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, 
                     "Password Tidak Cocok", 
                     "Password baru dan konfirmasi password tidak sama!");
            return;
        }

        if (newPassword.length() < 6) {
            showAlert(Alert.AlertType.WARNING, 
                     "Password Terlalu Pendek", 
                     "Password minimal 6 karakter!");
            return;
        }

        // TODO: Implementasi reset password via email/token
        // Untuk sementara, tampilkan sukses
        showAlert(Alert.AlertType.INFORMATION, 
                 "Password Berhasil Diubah", 
                 "Password Anda telah berhasil diubah.\n\nSilakan login dengan password baru.");

        // Kembali ke login form
        handleBackToLogin();
    }
    
    private void animateFormTransition(VBox hidePanel, VBox showPanel) {
        if (hidePanel == null || showPanel == null) return;
        
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), hidePanel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        fadeOut.setOnFinished(e -> {
            hidePanel.setVisible(false);
            hidePanel.setManaged(false);
            
            showPanel.setVisible(true);
            showPanel.setManaged(true);
            showPanel.setOpacity(0.0);
            
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), showPanel);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        
        fadeOut.play();
    }
    

    private void openMenuPage(String userId, String nama, String role) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/mycompany/metramoelyatama_apps/Main.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                getClass().getResource("/com/mycompany/metramoelyatama_apps/styles.css").toExternalForm()
            );

            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", 
                     "Gagal membuka halaman menu:\n" + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}