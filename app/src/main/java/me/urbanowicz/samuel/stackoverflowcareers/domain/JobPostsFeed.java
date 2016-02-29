package me.urbanowicz.samuel.stackoverflowcareers.domain;

import com.google.gson.annotations.SerializedName;

import java.util.Collection;

public class JobPostsFeed {
    @SerializedName("results")
    Collection<JobPost> jobPosts;

    public Collection<JobPost> getJobPosts() {
        return jobPosts;
    }
}
