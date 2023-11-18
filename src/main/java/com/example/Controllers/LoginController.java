package com.example.Controllers;

import com.example.MongoConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

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
                // Successful login - Perform your actions here (e.g., navigate to another
                // scene)
                System.out.println("Login successful!");
            } else {
                // Invalid credentials
                System.out.println("Invalid username or password!");
            }
        } catch (Exception e) {
            // Handle exceptions, log or show an error message to the user
            e.printStackTrace();
        } finally {
            // Close connections after use
            MongoConnection.close();
        }
    }
}
