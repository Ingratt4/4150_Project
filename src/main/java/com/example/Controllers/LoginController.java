package com.example.Controllers;

import com.example.MongoConnection;
import com.example.Session;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    void handleLogin(ActionEvent event) {
        String enteredUsername = usernameField.getText();
        String enteredPassword = passwordField.getText();

        try {
            // Perform login verification using MongoConnection class
            boolean isValidLogin = MongoConnection.verifyLogin(enteredUsername, enteredPassword);

            if (isValidLogin) {
                Session.getInstance().setStudentID(enteredUsername);
                openClassSelectionWindow();
                closeLoginWindow();

                System.out.println("Login successful!");
            } else {
                // Invalid credentials
                System.out.println("Invalid username or password!");
            }
        } catch (Exception e) {
            // Handle exceptions, log or show an error message to the user
            e.printStackTrace();
        }
    }

    private void closeLoginWindow() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    private void openClassSelectionWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ClassSelection.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
