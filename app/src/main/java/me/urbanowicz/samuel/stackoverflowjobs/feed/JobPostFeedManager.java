package me.urbanowicz.samuel.stackoverflowjobs.feed;

import java.util.ArrayList;
import java.util.Collection;

import me.urbanowicz.samuel.stackoverflowjobs.data.JobPost;
import me.urbanowicz.samuel.stackoverflowjobs.data.JobPostsFeed;
import me.urbanowicz.samuel.stackoverflowjobs.data.Search;
import me.urbanowicz.samuel.stackoverflowjobs.service.CareersAPI;
import me.urbanowicz.samuel.stackoverflowjobs.service.ServiceGenerator;
import me.urbanowicz.samuel.stackoverflowjobs.service.ServiceUtils;
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

    private ArrayList<JobPost> jobPosts = new ArrayList<>();
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
        final CareersAPI careersAPI = ServiceGenerator.createService(CareersAPI.class);
        final Call<JobPostsFeed> jobPostsFeedCall = careersAPI.getJobPostFeedCall(searchUrl, ServiceUtils.getApiKey());
        jobPostsFeedCall.enqueue(new Callback<JobPostsFeed>() {
            @Override
            public void onResponse(Response<JobPostsFeed> response, Retrofit retrofit) {
                if (response.body() != null) {
                    JobPostsFeed feed = response.body();
                    Collection<JobPost> jobPosts = feed.getJobPosts().get();
                    if (jobPosts.size() == 0) {
                        moreOfeersPossibility = MoreOfeersPossibility.IMPOSSIBLE;
                    } else {
                        moreOfeersPossibility = MoreOfeersPossibility.PROBABLE;
                    }
                    JobPostFeedManager.this.jobPosts.addAll(jobPosts);
                    callback.onFeedUpdated(JobPostFeedManager.this.jobPosts, moreOfeersPossibility);
                } else {
                    callback.onFeedUpdateError("");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFeedUpdateError(t.getMessage());
            }
        });
    }

    public ArrayList<JobPost> getCurrentJobPosts() {
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
