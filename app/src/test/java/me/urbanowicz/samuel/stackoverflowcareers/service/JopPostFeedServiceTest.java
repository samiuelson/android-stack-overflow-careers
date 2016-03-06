package me.urbanowicz.samuel.stackoverflowcareers.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import me.urbanowicz.samuel.stackoverflowcareers.domain.JobPostsFeed;
import retrofit.Call;
import retrofit.Response;
import rx.Observable;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JopPostFeedServiceTest {

    JobPostService jobPostService;

    @Before
    void setup() {
        jobPostService = new JobPostService();
    }

    @Test
    public void
    shouldDownloadSampleJsonFeed() throws IOException {
        Call<JsonElement> jobPostsFeedCall = jobPostService.getJobPostsFeedCall();

        assertNotNull(jobPostsFeedCall);

        Response response = jobPostsFeedCall.execute();

        boolean isSuccess = response.isSuccess();
        assertTrue(isSuccess);

        JsonObject jsonObject = (JsonObject) response.body();

        assertNotNull(jsonObject);
    }

    @Test
    public void
    shouldDownloadSampleJobPostFeed() throws IOException {
        Call<JobPostsFeed> jobPostsFeedCall = jobPostService.getJobPostFeedCall();

        assertNotNull(jobPostsFeedCall);

        Response response = jobPostsFeedCall.execute();

        boolean isSuccess = response.isSuccess();
        assertTrue(isSuccess);

        JobPostsFeed feed = (JobPostsFeed) response.body();
        assertNotNull(feed);
    }

    @Test
    public void
    shouldDownloadSampleJobPostFeedWithObservable() {
        Observable<JobPostsFeed> jobPostsFeedObservable = jobPostService.getJobPostFeedObservable();

        assertNotNull(jobPostsFeedObservable);
        // todo
    }
}
