package me.urbanowicz.samuel.stackoverflowcareers.data;

import com.annimon.stream.Optional;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class JobPostsFeed implements Serializable {
    @SerializedName("results")
    private Collection<JobPost> jobPosts;

    private JobPostsFeed(Collection<JobPost> jobPosts) {
        this.jobPosts = new ArrayList<>(jobPosts);
    }

    public Optional<Collection<JobPost>> getJobPosts() {
        return Optional.ofNullable(jobPosts);
    }

    public static final JobPostsFeed EMPTY = new JobPostsFeed(Collections.EMPTY_LIST);
}
