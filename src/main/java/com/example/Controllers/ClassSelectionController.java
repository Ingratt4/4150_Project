package com.example.Controllers;

import com.example.MongoConnection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class ClassSelectionController {

    @FXML
    private ComboBox<String> courseComboBox;

    @FXML
    private Label courseInfoLabel;

    private List<Document> courses;

    @FXML
    public void initialize() {
        // Load course codes into the ComboBox on initialization
        loadCourseCodes();
    }

    private void loadCourseCodes() {
        try {
            MongoDatabase database = MongoConnection.getDatabase();
            MongoCollection<Document> coursesCollection = database.getCollection("Courses");

            // Fetch course codes
            FindIterable<Document> availableCourses = coursesCollection.find();
            courses = new ArrayList<>();
            List<String> courseCodes = new ArrayList<>();
            for (Document course : availableCourses) {
                String courseCode = course.getString("course_code");
                courseCodes.add(courseCode);
                courses.add(course); // Store course information for reference
            }

            // Populate ComboBox with course codes
            courseComboBox.setItems(FXCollections.observableArrayList(courseCodes));

            // Close MongoDB connection after fetching course codes
            MongoConnection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleCourseSelection(ActionEvent event) {
        // Get the selected course code
        String selectedCourseCode = courseComboBox.getValue();

        // Find the corresponding course information
        for (Document course : courses) {
            if (course.getString("course_code").equals(selectedCourseCode)) {
                // Display course details in the label
                String details = String.format("Title: %s\nDescription: %s\nInstructor: %s\nSchedule: %s\nSeats: %s",
                        course.getString("title"),
                        course.getString("description"),
                        course.getString("instructor"),
                        course.getString("schedule"),
                        course.getString("seats_available"));
                courseInfoLabel.setText(details);
                break;
            }
        }
    }
}
