package me.urbanowicz.samuel.stackoverflowcareers.service;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import me.urbanowicz.samuel.stackoverflowcareers.domain.JobPostsFeed;
import retrofit.Call;
import retrofit.Response;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class JobPostClientTest {
    JobPostFeedClient client;

    @Before public void
    setup() {
        client = ServiceGenerator.createService(JobPostFeedClient.class);
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
                "http://careers.stackoverflow.com/jobs?searchTerm=android",
                ServiceUtils.getApiKey()
        );
        assertNotNull(call);

        Response<JobPostsFeed> response = call.execute();
        assertNotNull(response);

        assertTrue(response.isSuccess());

        assertNotNull((JobPostsFeed) response.body());
    }

}
