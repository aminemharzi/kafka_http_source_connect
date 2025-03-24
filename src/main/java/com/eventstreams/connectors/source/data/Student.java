// ========================
// File: Student.java
// ========================
package com.eventstreams.connectors.source.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Objects;

public class Student implements Comparable<Student> {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("age")
    @Expose
    private int age;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("major")
    @Expose
    private String major;

    @SerializedName("gpa")
    @Expose
    private double gpa;

    @SerializedName("study_path")
    @Expose
    private StudyPath studyPath;

    @SerializedName("time")
    @Expose
    private String time;

    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getEmail() { return email; }
    public String getMajor() { return major; }
    public double getGpa() { return gpa; }
    public StudyPath getStudyPath() { return studyPath; }
    public String getTime() { return time; }
    public void setMajor(String major) { this.major = major; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student student = (Student) o;
        return id == student.id && Objects.equals(email, student.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public int compareTo(Student other) {
        return Integer.compare(this.id, other.id);
    }
}
