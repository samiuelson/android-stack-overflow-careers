package me.urbanowicz.samuel.stackoverflowcareers.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.junit.Test;

import java.io.IOException;

import retrofit.Call;
import retrofit.Response;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JopPostFeedServiceTest {

    @Test
    public void
    shouldDownloadSampleFeed() throws IOException {
        JobPostService jobPostService = new JobPostService();
        Call<JsonElement> jobPostsFeedCall = jobPostService.getJobPostsFeed();

        assertNotNull(jobPostsFeedCall);

        Response response = jobPostsFeedCall.execute();

        boolean isSuccess = response.isSuccess();
        assertTrue(isSuccess);

        JsonObject jsonObject = (JsonObject) response.body();

        assertNotNull(jsonObject);
    }
}
