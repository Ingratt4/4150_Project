package com.example.Controllers;

import com.example.Course;
import com.example.MongoConnection;
import com.example.Session;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class ClassSelectionController {

    @FXML
    private ComboBox<String> courseComboBox;

    @FXML
    private Label courseInfoLabel;
    @FXML
    private List<Document> courses;
    @FXML
    private TableView<Course> courseTableView;
    @FXML
    private TableColumn<Course, String> courseCodeColumn;
    @FXML
    private TableColumn<Course, String> instructorColumn;
    @FXML
    private TableColumn<Course, String> scheduleColumn;
    @FXML
    private TableColumn<Course, String> descriptionColumn;

    @FXML
    private Button enrollButton;

    @FXML
    public void initialize() {
        // Load course codes into the ComboBox on initialization
        loadCourseCodes();
        displayEnrolledCourses();

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
    void displayEnrolledCourses() {

        String studentID = Session.getInstance().getStudentID();

        try {
            MongoDatabase database = MongoConnection.getDatabase();
            MongoCollection<Document> enrollmentCollection = database.getCollection("Enrollment");
            MongoCollection<Document> coursesCollection = database.getCollection("Courses");

            // Query for enrolled courses of the logged-in student
            Document query = new Document("student_id", studentID);
            FindIterable<Document> enrolledCourses = enrollmentCollection.find(query);

            // Prepare data for TableView
            ObservableList<Course> enrolledCourseList = FXCollections.observableArrayList();
            for (Document enrolledCourse : enrolledCourses) {
                String courseCode = enrolledCourse.getString("course_code");
                // Fetch course details from the Courses collection using the course code
                Document courseQuery = new Document("course_code", courseCode);
                Document courseDetails = coursesCollection.find(courseQuery).first();

                // Create Course object or extract details as needed and add it to the list
                Course course = new Course(
                        courseDetails.getString("course_code"),
                        courseDetails.getString("instructor"),
                        courseDetails.getString("schedule"),
                        courseDetails.getString("description"));
                enrolledCourseList.add(course);
            }

            // Set the enrolled courses into the TableView
            courseTableView.setItems(enrolledCourseList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateTableView(ObservableList<Course> enrolledCourses) {
        courseTableView.setItems(enrolledCourses);
    }

    @FXML
    void handleEnroll(ActionEvent event) {
        String selectedCourseCode = courseComboBox.getValue();

        if (selectedCourseCode != null && !selectedCourseCode.isEmpty()) {
            try {
                MongoDatabase database = MongoConnection.getDatabase();
                MongoCollection<Document> enrollmentCollection = database.getCollection("Enrollment");
                MongoCollection<Document> coursesCollection = database.getCollection("Courses");
                MongoCollection<Document> studentInfo = database.getCollection("Login-Info");

                String studentID = Session.getInstance().getStudentID(); // Replace with the actual student ID

                // Query for the selected course
                Document courseQuery = new Document("course_code", selectedCourseCode);
                Document selectedCourse = coursesCollection.find(courseQuery).first();

                // Query for student ID
                Document studentQuery = new Document("student_id", studentID);
                Document selectedID = studentInfo.find(studentQuery).first();

                if (selectedCourse != null && selectedID != null) {
                    // Extract student information
                    String studentName = selectedID.getString("name");
                    int totalClasses = selectedID.getInteger("total_classes", 0);

                    // Check if the student can enroll in another class (limit of 5)
                    if (totalClasses < 5) {
                        // Check if seats are available
                        int availableSeats = selectedCourse.containsKey("seats_available")
                                ? selectedCourse.getInteger("seats_available")
                                : 0;
                        if (availableSeats > 0) {
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

                            // Increment the total_classes for the student in the Login_Info collection
                            studentInfo.updateOne(studentQuery,
                                    new Document("$inc", new Document("total_classes", 1)));

                            // Update the totalClasses variable locally
                            totalClasses++;

                            // Display enrollment confirmation
                            courseInfoLabel.setText("Enrolled in: " + selectedCourseCode);
                        } else {
                            courseInfoLabel.setText("No available seats for this course.");
                        }
                    } else {
                        courseInfoLabel.setText("You have reached the maximum limit of classes.");
                    }
                } else {
                    courseInfoLabel.setText("Course not found or student information not found.");
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
