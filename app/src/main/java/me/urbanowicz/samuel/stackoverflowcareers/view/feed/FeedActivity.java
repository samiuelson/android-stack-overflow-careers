package me.urbanowicz.samuel.stackoverflowcareers.view.feed;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import me.urbanowicz.samuel.stackoverflowcareers.R;
import me.urbanowicz.samuel.stackoverflowcareers.domain.JobPost;
import me.urbanowicz.samuel.stackoverflowcareers.domain.JobPostsFeed;
import me.urbanowicz.samuel.stackoverflowcareers.service.JobPostFeedClient;
import me.urbanowicz.samuel.stackoverflowcareers.service.ServiceGenerator;
import me.urbanowicz.samuel.stackoverflowcareers.service.ServiceUtils;
import me.urbanowicz.samuel.stackoverflowcareers.view.detail.DetailActivity;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class FeedActivity extends AppCompatActivity implements FeedRecyclerAdapter.OnItemClickListener{
    private static final String TAG = FeedActivity.class.getSimpleName();
    private static final String KEY_JOBS_FEED = "jobs_feed";

    private FeedRecyclerAdapter adapter;
    private TextInputEditText titleEditText;
    private SwipeRefreshLayout swipeRefreshLayout;
    private JobPostsFeed jobPostsFeed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_feed);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setTitle("Stack Overflow Careers");

        RecyclerView feedRecyclerView = (RecyclerView) findViewById(R.id.feedItemsRecyclerView);
        titleEditText = (TextInputEditText) findViewById(R.id.titleEditText);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> updateFeed());

        findViewById(R.id.searchBtn).setOnClickListener((btn) -> updateFeed());

        feedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedRecyclerView.hasFixedSize();
        adapter = new FeedRecyclerAdapter(this);
        feedRecyclerView.setAdapter(adapter);

        if (savedInstanceState != null) {
            jobPostsFeed = (JobPostsFeed) savedInstanceState.getSerializable(KEY_JOBS_FEED);
        } else {
            jobPostsFeed = JobPostsFeed.EMPTY;
        }

        if (jobPostsFeed != null && jobPostsFeed.getJobPosts().isPresent() && jobPostsFeed.getJobPosts().get().size() > 0) {
            refreshAdapter();
        } else {
            updateFeed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_JOBS_FEED, jobPostsFeed);
    }

    private void updateFeed() {
        final String searchQuery = titleEditText.getText().toString();
        final String searchUrl = ServiceUtils.getUrlWithSearchQuery(searchQuery);

        final JobPostFeedClient jobPostFeedClient = ServiceGenerator.createService(JobPostFeedClient.class);
        final Call<JobPostsFeed> jobPostsFeedCall = jobPostFeedClient.getJobPostFeedCall(searchUrl, ServiceUtils.getApiKey());
        jobPostsFeedCall.enqueue(new Callback<JobPostsFeed>() {
            @Override
            public void onResponse(Response<JobPostsFeed> response, Retrofit retrofit) {
                if (response.body() != null) {
                    jobPostsFeed = response.body();
                } else {
                    setError("Feed is null for search term: " + searchUrl);
                }
                swipeRefreshLayout.setRefreshing(false);
                refreshAdapter();
            }

            @Override
            public void onFailure(Throwable t) {
                jobPostsFeed = JobPostsFeed.EMPTY;
                setError(t.getLocalizedMessage());
            }
        });

    }

    private void refreshAdapter() {
        adapter.setJobPosts(jobPostsFeed.getJobPosts().get());
    }

    private void setError(String info) {
        Toast.makeText(FeedActivity.this, info, Toast.LENGTH_LONG).show();
    }

    // FeedRecyclerAdapter.OnItemClickListener
    @Override
    public void onClick(int position) {
        JobPost jobPostClicked;
        if (jobPostsFeed.getJobPosts().isPresent()) {
            jobPostClicked = new ArrayList<>(jobPostsFeed.getJobPosts().get()).get(position);
        } else {
            jobPostClicked = JobPost.EMPTY;
        }
        DetailActivity.startActivity(this, jobPostClicked);
    }
}
