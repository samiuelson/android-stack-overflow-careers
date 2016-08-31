package me.urbanowicz.samuel.stackoverflowjobs.service;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import me.urbanowicz.samuel.stackoverflowjobs.data.JobPostsFeed;
import retrofit.Call;
import retrofit.Response;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class JobPostClientTest {
    CareersAPI client;

    @Before public void
    setup() {
        client = ServiceGenerator.createService(CareersAPI.class);
    }

    @Test public void
    shouldDownloadJobPostFeed() throws IOException {
        Call<JobPostsFeed> call = client.getJobPostFeedCall();
        assertNotNull(call);

        Response<JobPostsFeed> response = call.execute();
        assertNotNull(response);

        assertTrue(response.isSuccess());

        assertNotNull((JobPostsFeed) response.body());
    }

    @Test public void
    shouldDownloadJobPostFeedForTerm() throws IOException {
        Call<JobPostsFeed> call = client.getJobPostFeedCall(
                new ServiceUtils.SearchQueryUrlBuilder()
                        .addJobTitle("android")
                        .toString(),
                ServiceUtils.getApiKey()
        );
        assertNotNull(call);

        Response<JobPostsFeed> response = call.execute();
        assertNotNull(response);

        assertTrue(response.isSuccess());

        JobPostsFeed feed = response.body();
        assertNotNull(feed);

        assertNotNull(feed.getJobPosts());

        assertTrue(feed.getJobPosts().isPresent());
    }

}
