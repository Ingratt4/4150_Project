package com.example;

import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoConnection {
    private static final String CONNECTION_STRING = "mongodb+srv://student:university@cluster0.h3m1ty3.mongodb.net/?retryWrites=true&w=majority\r\n"
            + //
            "";
    private static final String DATABASE_NAME = "University-Database"; // Replace with your actual database name

    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static MongoDatabase getDatabase() {
        if (database == null) {
            mongoClient = MongoClients.create(CONNECTION_STRING);
            database = mongoClient.getDatabase(DATABASE_NAME);
        }
        return database;
    }

    public static boolean verifyLogin(String username, String password) {
        MongoDatabase database = getDatabase();

        // Access the collection where user information is stored
        MongoCollection<org.bson.Document> usersCollection = database.getCollection("Login-Info");

        // Query MongoDB for the entered username
        Document query = new Document("UserName", username);
        Document user = usersCollection.find(query).first();

        if (user != null) {
            // If the username exists, check the password
            String storedPassword = user.getString("Password");

            // Verify the entered password against the stored password
            return password.equals(storedPassword);
        }

        return false; // Username not found
    }

    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
