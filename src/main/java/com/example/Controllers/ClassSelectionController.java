package com.example.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import com.example.MongoConnection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class ClassSelectionController {

    @FXML
    private Button viewCoursesButton;

    @FXML
    private Label coursesLabel;

    private MongoDatabase database; // Declare the database variable at the class level

    // This method handles the "View Courses" button action
    @FXML
    void handleViewCourses(ActionEvent event) {
        try {
            System.out.println("Button clicked - Fetching courses...");

            // Initialize the MongoDB connection if it's not already initialized
            if (database == null) {
                System.out.println("Establishing MongoDB connection...");
                database = MongoConnection.getDatabase();
            }

            MongoCollection<Document> coursesCollection = database.getCollection("Courses");

            // Perform a query to get available courses
            FindIterable<Document> availableCourses = coursesCollection.find();

            // Process the availableCourses and format them to display in the UI
            StringBuilder coursesInfo = new StringBuilder();
            for (Document course : availableCourses) {
                // Assuming the document fields in MongoDB are 'course_code', 'title',
                // 'description', 'instructor', 'schedule'
                String courseCode = course.getString("course_code");
                String title = course.getString("title");
                String description = course.getString("description");
                String instructor = course.getString("instructor");
                String schedule = course.getString("schedule");
                String seats = course.getString("seats_available");

                // Append course information to coursesInfo StringBuilder
                coursesInfo.append("Course Code: ").append(courseCode).append("\n");
                coursesInfo.append("Title: ").append(title).append("\n");
                coursesInfo.append("Description: ").append(description).append("\n");
                coursesInfo.append("Instructor: ").append(instructor).append("\n");
                coursesInfo.append("Schedule: ").append(schedule).append("\n");
                coursesInfo.append("Seats: ").append(seats).append("\n");
                coursesInfo.append("\n");
            }

            // Update the label to display the courses information
            coursesLabel.setText(coursesInfo.toString());

            System.out.println("Courses fetched successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to fetch courses: " + e.getMessage());
            // Handle exceptions or display an error message in your UI
        }
    }

    // Close MongoDB connection when needed (e.g., when the application is closed)
    public void closeMongoDBConnection() {
        MongoConnection.close();
    }
}
