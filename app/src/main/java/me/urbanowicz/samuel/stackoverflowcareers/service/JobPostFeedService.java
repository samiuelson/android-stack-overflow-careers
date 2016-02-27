package me.urbanowicz.samuel.stackoverflowcareers.service;

import me.urbanowicz.samuel.stackoverflowcareers.domain.JobPostsFeed;
import retrofit2.http.GET;
import rx.Observable;

public interface JobPostFeedService {
    @GET
    Observable<JobPostsFeed> getJobPosts();
}
