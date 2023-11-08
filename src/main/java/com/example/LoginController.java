package com.example;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    public void loginButtonAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Authenticate the user
        if (isUserAuthenticated(username, password)) {
            // Navigate to the main application screen (you can replace this with your code)
            System.out.println("Login successful!");
        } else {
            // Show an error message for failed login
            System.out.println("Login failed. Please check your credentials.");
        }
    }

    private boolean isUserAuthenticated(String username, String password) {
        // Connect to the MongoDB database
        MongoDatabase database = MongoConnection.getDatabase();
        System.out.println("Database Name: " + database.getName());
    
        // Get the collection containing user credentials
        MongoCollection<Document> collection = database.getCollection("Login-Info");
        System.out.println("Collection Name: " + collection.getNamespace().getCollectionName()); 
    
        // Create a query to find a document matching the provided username and password
        Document query = new Document("UserName", username).append("Password", password);
        System.out.println("Query: " + query);
    
        // Check if a matching document was found
        Document userDocument = collection.find(query).first();
    
        return userDocument != null;
    }
    
}
