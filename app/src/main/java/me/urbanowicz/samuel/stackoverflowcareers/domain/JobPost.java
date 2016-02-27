package me.urbanowicz.samuel.stackoverflowcareers.domain;

import com.google.gson.annotations.SerializedName;

public class JobPost {

    @SerializedName("job_link/_title")
    private String jobTitle;

    public String getJobTitle() {
        return jobTitle;
    }
}
