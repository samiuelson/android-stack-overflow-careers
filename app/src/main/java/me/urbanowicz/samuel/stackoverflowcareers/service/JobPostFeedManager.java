package me.urbanowicz.samuel.stackoverflowcareers.service;

import java.util.Collection;
import java.util.LinkedList;

import me.urbanowicz.samuel.stackoverflowcareers.domain.JobPost;
import me.urbanowicz.samuel.stackoverflowcareers.domain.JobPostsFeed;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Class responsible for fetching, collecting and synthesis of JobPostsFeed.
 * JobPostFeedManager instance is intended to live across activity life cycle changes /
 * destroying-creating cycles.
 */
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
        JobPostFeedManager.this.jobPosts.clear();

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
                    JobPostFeedManager.this.jobPosts.addAll(jobPosts);
                    callback.onFeedUpdated(JobPostFeedManager.this.jobPosts, moreOfeersPossibility);
                } else {
                    // todo notify about error
                    callback.onFeedUpdateError("");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                // todo notify about error
                callback.onFeedUpdateError("");
            }
        });
    }

    public Collection<JobPost> getCurrentJobPosts() {
        return this.jobPosts;
    }

    public Search getCurrentSearch() {
        return this.search;
    }

    public enum MoreOfeersPossibility {
        PROBABLE, IMPOSSIBLE
    }

    /**
     * Interface providing callback with a JobPost collection and some other info or error message
     * used to get response back to the view module.
     */
    public interface JobPostsFeedManagerCallback {
        void onFeedUpdated(Collection<JobPost> jobPosts, MoreOfeersPossibility moreOfeersPossibility);

        void onFeedUpdateError(String errorMessage);
    }
}
