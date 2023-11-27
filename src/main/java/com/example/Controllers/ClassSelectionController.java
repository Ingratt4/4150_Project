package com.example.Controllers;

import com.example.MongoConnection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    private Button enrollButton;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleCourseSelection(ActionEvent event) {
        // Get the selected course code
        String selectedCourse = courseComboBox.getValue();
        if (selectedCourse != null && !selectedCourse.isEmpty()) {
            enrollButton.setVisible(true);
        } else {
            enrollButton.setVisible(false);
        }

        // Find the corresponding course information
        for (Document course : courses) {
            if (course.getString("course_code").equals(selectedCourse)) {
                // Display course details in the label
                String details = String.format("Title: %s\nDescription: %s\nInstructor: %s\nSchedule: %s\nSeats: %s",
                        course.getString("title"),
                        course.getString("description"),
                        course.getString("instructor"),
                        course.getString("schedule"),
                        course.getInteger("seats_available"));
                courseInfoLabel.setText(details);
                break;
            }
        }
    }

    @FXML
    void handleEnroll(ActionEvent event) {
        String selectedCourseCode = courseComboBox.getValue();

        if (selectedCourseCode != null && !selectedCourseCode.isEmpty()) {
            try {
                MongoDatabase database = MongoConnection.getDatabase();
                MongoCollection<Document> enrollmentCollection = database.getCollection("Enrollment");
                MongoCollection<Document> coursesCollection = database.getCollection("Courses");

                // Query for the selected course
                Document courseQuery = new Document("course_code", selectedCourseCode);
                Document selectedCourse = coursesCollection.find(courseQuery).first();

                if (selectedCourse != null) {
                    // Check if seats are available
                    Long availableSeatsLong = selectedCourse.getLong("seats_available");
                    int availableSeats = availableSeatsLong != null ? availableSeatsLong.intValue() : 0;

                    if (availableSeats > 0) {
                        // Get student information (you may need to retrieve the logged-in student's ID)
                        String studentID = "test_student_id"; // Replace with the actual student ID
                        String studentName = "Test Student"; // Replace with the actual student name

                        // Create an enrollment document
                        Document enrollmentDocument = new Document();
                        enrollmentDocument.append("student_id", studentID);
                        enrollmentDocument.append("student_name", studentName);
                        enrollmentDocument.append("course_code", selectedCourseCode);

                        // Insert the enrollment document into the Enrollment collection
                        enrollmentCollection.insertOne(enrollmentDocument);

                        // Decrease the available seats for the selected course by one
                        int newAvailableSeats = availableSeats - 1;
                        coursesCollection.updateOne(courseQuery,
                                new Document("$set", new Document("seats_available", newAvailableSeats)));

                        // Display enrollment confirmation
                        courseInfoLabel.setText("Enrolled in: " + selectedCourseCode);
                    } else {
                        courseInfoLabel.setText("No available seats for this course.");
                    }
                } else {
                    courseInfoLabel.setText("Course not found.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            courseInfoLabel.setText("Please select a course to enroll.");
        }
    }

    // ... other methods ...
}
