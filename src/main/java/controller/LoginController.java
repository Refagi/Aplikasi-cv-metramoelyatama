package controller;

import java.net.URL;
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
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class LoginController implements Initializable {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private TextField txtVisiblePassword;
    @FXML private CheckBox chkShowPassword;
   

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtVisiblePassword.textProperty().bindBidirectional(txtPassword.textProperty());

        txtPassword.setOnAction(event -> handleLogin());      
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
        String tableName = role.equals("Admin") ? "Admin" : "Karyawan";
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
    

    private void openMenuPage(String userId, String nama, String role) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/mycompany/metramoelyatama_apps/Dashboard.fxml")
            );
            Parent root = loader.load();
            DashboardController controller = loader.getController();
            controller.setUserData(userId, nama, role);

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = new Scene(root);

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