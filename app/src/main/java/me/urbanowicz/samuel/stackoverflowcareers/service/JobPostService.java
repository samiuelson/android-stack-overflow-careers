package me.urbanowicz.samuel.stackoverflowcareers.service;

import com.google.gson.JsonElement;

import me.urbanowicz.samuel.stackoverflowcareers.domain.JobPostsFeed;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

@Deprecated
public class JobPostService {
    private static final String BASE_FEED_URL =
            "https://api.import.io/store/connector/6986fa32-b64c-47d5-a1f4-3035affdf45b/";
    private final String API_KEY ="a13becac060949f08d6eae26467ca0e595afcb544a7497c0f0ba97de1987897cec8d360b8e52f0738063ad9c7c8e1c180b8b2746e8a44f3a61185e532c4401a2c6798d70b2e0a0cdb663bf748db2f12d";

    private JobPostFeedClient jobPostFeedClient;

    public JobPostService() {
        jobPostFeedClient =
                new Retrofit.Builder()
                        .baseUrl(BASE_FEED_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(JobPostFeedClient.class);
    }

    public Call<JsonElement> getJobPostsFeedCall() {
        return jobPostFeedClient.getJobPostsJsonCall();
    }

    public Call<JsonElement> getJsonJobPostFeedCall(String searchTerm) {
        final String searchUrl = "http://careers.stackoverflow.com/jobs?searchTerm=" + searchTerm;
        return jobPostFeedClient.getJobPostsJsonCall(searchUrl, API_KEY);
    }
    public Call<JobPostsFeed> getJobPostFeedCall() {
        return jobPostFeedClient.getJobPostFeedCall();
    }

    public Call<JobPostsFeed> getJobPostFeedCall(String searchTerm) {
        return jobPostFeedClient.getJobPostFeedCall(getSearchUrl(searchTerm), API_KEY);
    }


    private static String getSearchUrl(String searchTerm) {
        final String searchBaseUrl = "http://careers.stackoverflow.com/jobs?searchTerm=";

        return searchBaseUrl + searchTerm;
    }
}
