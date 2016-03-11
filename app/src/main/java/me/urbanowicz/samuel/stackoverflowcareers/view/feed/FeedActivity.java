package me.urbanowicz.samuel.stackoverflowcareers.view.feed;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import me.urbanowicz.samuel.materialsearchview.SearchActivity;
import me.urbanowicz.samuel.stackoverflowcareers.R;
import me.urbanowicz.samuel.stackoverflowcareers.domain.JobPostsFeed;
import me.urbanowicz.samuel.stackoverflowcareers.service.JobPostFeedClient;
import me.urbanowicz.samuel.stackoverflowcareers.service.ServiceGenerator;
import me.urbanowicz.samuel.stackoverflowcareers.service.ServiceUtils;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class FeedActivity extends AppCompatActivity {
    private static final String TAG = FeedActivity.class.getSimpleName();
    private static final String KEY_JOBS_FEED = "jobs_feed";

    private FeedRecyclerAdapter adapter;
    private RecyclerView feedRecyclerView;
    private TextInputEditText titleEditText;
    private SwipeRefreshLayout swipeRefreshLayout;
    private JobPostsFeed jobPostsFeed;
    private CoordinatorLayout contentView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_feed);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setTitle("Stack Overflow Careers");

        feedRecyclerView = (RecyclerView) findViewById(R.id.feedItemsRecyclerView);
        titleEditText = (TextInputEditText) findViewById(R.id.titleEditText);
        contentView = (CoordinatorLayout) findViewById(R.id.contentView);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> updateFeed());

        feedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedRecyclerView.hasFixedSize();
        adapter = new FeedRecyclerAdapter();
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_feed_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                //todo start search activity
                startActivity(new Intent(FeedActivity.this, SearchActivity.class));
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private void updateFeed() {
        final String searchUrl =
                "http://careers.stackoverflow.com/jobs/searchTerm%3D"
                        + titleEditText.getText().toString();

        JobPostFeedClient jobPostFeedClient = ServiceGenerator.createService(JobPostFeedClient.class);
        Call<JobPostsFeed> jobPostsFeedCall = jobPostFeedClient.getJobPostFeedCall(searchUrl, ServiceUtils.getApiKey());
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
}
