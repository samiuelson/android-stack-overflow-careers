package me.urbanowicz.samuel.stackoverflowcareers.domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.net.URL;

public class JobPost implements Serializable {

    @SerializedName("job_link/_title")
    private String jobTitle;
    @SerializedName("job_link")
    private URL jobLink;

    public String getJobTitle() {
        return jobTitle;
    }

    public URL getJobLink() {
        return jobLink;
    }

    public static final JobPost EMPTY = new JobPost();
}
