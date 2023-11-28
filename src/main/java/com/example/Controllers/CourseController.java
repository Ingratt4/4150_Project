package com.example.Controllers;

import com.example.Course;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CourseController {
    @FXML
    private Label courseCodeLabel;

    @FXML
    private Label instructorLabel;

    @FXML
    private Label scheduleLabel;

    @FXML
    private Label descriptionLabel;

    public void displayCourseDetails(Course course) {
        // Bind the course details to the UI elements
        courseCodeLabel.textProperty().bind(course.courseCodeProperty());
        instructorLabel.textProperty().bind(course.instructorProperty());
        scheduleLabel.textProperty().bind(course.scheduleProperty());
        descriptionLabel.textProperty().bind(course.descriptionProperty());
    }
}
