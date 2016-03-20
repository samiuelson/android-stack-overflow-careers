package me.urbanowicz.samuel.stackoverflowcareers.service;

import java.util.Collection;
import java.util.LinkedList;

import me.urbanowicz.samuel.stackoverflowcareers.domain.JobPost;
import me.urbanowicz.samuel.stackoverflowcareers.domain.JobPostsFeed;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class JobPostFeedManager {

    private static JobPostFeedManager INSTANCE;

    public static synchronized JobPostFeedManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JobPostFeedManager();
        }
        return INSTANCE;
    }

    private JobPostFeedManager() {}

    private Collection<JobPost> jobPosts = new LinkedList<>();
    private MoreOfeersPossibility moreOfeersPossibility = MoreOfeersPossibility.PROBABLE;
    private int currentPage = 0;
    private Search search = Search.EMPTY;


    public void downloadFeedForNewSearch(Search search, JobPostsFeedManagerCallback callback) {
        this.currentPage = 0;
        this.search = search;

        fetchJobPosts(callback);
    }

    public void downloadFeedNextPage(JobPostsFeedManagerCallback callback) {
        currentPage++;

        fetchJobPosts(callback);
    }

    private void fetchJobPosts(JobPostsFeedManagerCallback callback) {
        final String searchUrl = ServiceUtils.getUrlSearchQuery(search, currentPage);
        final JobPostFeedClient jobPostFeedClient = ServiceGenerator.createService(JobPostFeedClient.class);
        final Call<JobPostsFeed> jobPostsFeedCall = jobPostFeedClient.getJobPostFeedCall(searchUrl, ServiceUtils.getApiKey());
        jobPostsFeedCall.enqueue(new Callback<JobPostsFeed>() {
            @Override
            public void onResponse(Response<JobPostsFeed> response, Retrofit retrofit) {
                if (response.body() != null) {
                    JobPostsFeed feed = response.body();
                    Collection<JobPost> jobPosts = feed.getJobPosts().get();
                    if (jobPosts.size() == 0) {
                        moreOfeersPossibility = MoreOfeersPossibility.IMPOSSIBLE;
                    }
                    JobPostFeedManager.this.jobPosts.clear();
                    JobPostFeedManager.this.jobPosts.addAll(jobPosts);
                    callback.onFeedUpdated(jobPosts, moreOfeersPossibility);
                } else {
                    // todo notify about error
                }
            }

            @Override
            public void onFailure(Throwable t) {
                // todo notify about error
            }
        });
    }

    public enum MoreOfeersPossibility {
        PROBABLE, IMPOSSIBLE
    }

    /**
     * Interface providing callback method for a view
     */
    public interface JobPostsFeedManagerCallback {
        void onFeedUpdated(Collection<JobPost> jobPosts, MoreOfeersPossibility moreOfeersPossibility);
    }
}
