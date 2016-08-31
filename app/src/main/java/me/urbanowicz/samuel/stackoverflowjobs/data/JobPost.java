package me.urbanowicz.samuel.stackoverflowjobs.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.net.URL;

public class JobPost implements Serializable {

    @SerializedName("job_link/_title")
    private String jobTitle;
    @SerializedName("job_link")
    private URL jobLink;
    @SerializedName("employer_value")
    private String companyName;
    @SerializedName("location_value")
    private String location;

    public String getJobTitle() {
        return jobTitle;
    }

    public URL getJobLink() {
        return jobLink;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getLocation() {
        return location;
    }

    public static final JobPost EMPTY = new JobPost();
}
