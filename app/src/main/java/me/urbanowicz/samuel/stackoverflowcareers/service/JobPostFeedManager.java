package me.urbanowicz.samuel.stackoverflowcareers.service;

import java.util.Collection;
import java.util.LinkedList;

import me.urbanowicz.samuel.stackoverflowcareers.domain.JobPost;
import me.urbanowicz.samuel.stackoverflowcareers.domain.JobPostsFeed;

public class JobPostFeedManager {

    private static JobPostFeedManager INSTANCE;

    public static synchronized JobPostFeedManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JobPostFeedManager();
        }
        return INSTANCE;
    }

    private JobPostFeedManager() {}

    private JobPostsFeed jobPostsFeed = JobPostsFeed.EMPTY;
    private MoreOfeersPossibility moreOfeersPossibility = MoreOfeersPossibility.PROBABLE;
    private int currentPage = 0;


    public void downloadFeedForNewSearch(Search search, JobPostsFeedManagerCallback callback) {
        currentPage = 0;
        Collection<JobPost> jobPosts = new LinkedList<>(); // todo download new feed
    }

    public void downloadFeedNextPage(JobPostsFeedManagerCallback callback) {
        currentPage++;
        Collection<JobPost> jobPosts = new LinkedList<>(); // todo download next page and add to previous job posts
        callback.onFeedUpdated(jobPosts);

        if (jobPosts.size() == 0) {
            moreOfeersPossibility = MoreOfeersPossibility.IMPOSSIBLE;
        }
    }

    public enum MoreOfeersPossibility {
        PROBABLE, IMPOSSIBLE
    }

    /**
     * Interface providing callback method for a view
     */
    public interface JobPostsFeedManagerCallback {
        void onFeedUpdated(Collection<JobPost> jobPosts);
    }
}
