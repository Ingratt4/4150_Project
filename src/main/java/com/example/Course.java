package com.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Course {
    private final StringProperty courseCode;
    private final StringProperty instructor;
    private final StringProperty schedule;
    private final StringProperty description;

    public Course(String courseCode, String instructor, String schedule, String description) {
        this.courseCode = new SimpleStringProperty(courseCode);
        this.instructor = new SimpleStringProperty(instructor);
        this.schedule = new SimpleStringProperty(schedule);
        this.description = new SimpleStringProperty(description);
    }

    public String getCourseCode() {
        return courseCode.get(); // Assuming courseCode is a StringProperty
    }

    // Getters for JavaFX properties
    public StringProperty courseCodeProperty() {
        return courseCode;
    }

    public StringProperty instructorProperty() {
        return instructor;
    }

    public StringProperty scheduleProperty() {
        return schedule;
    }

    public StringProperty descriptionProperty() {
        return description;
    }
}
