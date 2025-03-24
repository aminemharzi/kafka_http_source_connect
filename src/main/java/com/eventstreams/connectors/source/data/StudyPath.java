// ========================
// File: StudyPath.java
// ========================
package com.eventstreams.connectors.source.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StudyPath {
    @SerializedName("diploma")
    @Expose
    private String diploma;

    @SerializedName("graduation_year")
    @Expose
    private int graduationYear;

    public String getDiploma() { return diploma; }
    public int getGraduationYear() { return graduationYear; }
}