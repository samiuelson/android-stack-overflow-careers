package me.urbanowicz.samuel.stackoverflowcareers.service;

import com.google.gson.JsonElement;

import me.urbanowicz.samuel.stackoverflowcareers.domain.JobPost;
import me.urbanowicz.samuel.stackoverflowcareers.domain.JobPostsFeed;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import rx.Observable;

public class JobPostService {
    private static final String BASE_FEED_URL =
            "https://api.import.io/store/connector/6986fa32-b64c-47d5-a1f4-3035affdf45b/";

    private JobPostFeedApi jobPostFeedApi;

    public JobPostService() {
        jobPostFeedApi =
                new Retrofit.Builder()
                        .baseUrl(BASE_FEED_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(JobPostFeedApi.class);
    }

    public Call<JsonElement> getJobPostsFeedCall() {
        return jobPostFeedApi.getJobPostsJsonCall();
    }
    public Call<JobPostsFeed> getJobPostFeedCall() {
        return jobPostFeedApi.getJobPostFeedCall();
    }
    public Observable<JobPostsFeed> getJobPostFeedObservable() {
        return jobPostFeedApi.getJobPostFeedObservable();
    }
}
