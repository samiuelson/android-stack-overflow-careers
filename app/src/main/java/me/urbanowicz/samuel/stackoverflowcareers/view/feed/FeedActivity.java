package me.urbanowicz.samuel.stackoverflowcareers.view.feed;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import me.urbanowicz.samuel.stackoverflowcareers.R;
import me.urbanowicz.samuel.stackoverflowcareers.domain.JobPost;
import me.urbanowicz.samuel.stackoverflowcareers.domain.JobPostsFeed;
import me.urbanowicz.samuel.stackoverflowcareers.service.JobPostFeedManager;
import me.urbanowicz.samuel.stackoverflowcareers.service.Search;
import me.urbanowicz.samuel.stackoverflowcareers.service.JobPostFeedClient;
import me.urbanowicz.samuel.stackoverflowcareers.service.ServiceGenerator;
import me.urbanowicz.samuel.stackoverflowcareers.service.ServiceUtils;
import me.urbanowicz.samuel.stackoverflowcareers.view.detail.DetailActivity;
import me.urbanowicz.samuel.stackoverflowcareers.view.search.SearchActivity;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class FeedActivity extends AppCompatActivity implements
        FeedRecyclerAdapter.OnItemClickListener, FeedRecyclerAdapter.OnLastItemAppearedListener {
    private static final String TAG = FeedActivity.class.getSimpleName();
    private static final String KEY_JOBS_FEED = "jobs_feed";
    private static final String KEY_SEARCH = "search";
    private static final String KEY_PAGES_FETCHED_COUNT = "fetched_pages_count";
    private static final int KEY_SEARCH_RESULT = 23;

    private FeedRecyclerAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    // todo extract jobPostFeed, search and pagesFetchedCount to external class {@link JobPostFeedDownloadManager}
    private int pagesFetchedCount = 0;
    private JobPostsFeed jobPostsFeed;
    private Search search;
    private JobPostFeedManager feedManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_feed);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setTitle(getString(R.string.app_name_title));

        RecyclerView feedRecyclerView = (RecyclerView) findViewById(R.id.feedItemsRecyclerView);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setOnRefreshListener(this::updateFeed);

        feedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedRecyclerView.hasFixedSize();
        adapter = new FeedRecyclerAdapter(this, this);
        feedRecyclerView.setAdapter(adapter);

        feedManager = JobPostFeedManager.getInstance();

        if (savedInstanceState != null) {
            this.jobPostsFeed = (JobPostsFeed) savedInstanceState.getSerializable(KEY_JOBS_FEED);
            this.pagesFetchedCount = savedInstanceState.getInt(KEY_PAGES_FETCHED_COUNT);

            Search search = (Search) savedInstanceState.getSerializable(KEY_SEARCH);
            this.search = search == null? Search.EMPTY : search;
        } else {
            jobPostsFeed = JobPostsFeed.EMPTY;
            search = Search.EMPTY;
        }

        refreshSubtitle();

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
        outState.putSerializable(KEY_SEARCH, search);
        outState.putInt(KEY_PAGES_FETCHED_COUNT, pagesFetchedCount);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_activity_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                actionShowSearch();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == KEY_SEARCH_RESULT) {
                final Search search = (Search) data.getSerializableExtra(SearchActivity.EXTRA_SEARCH);
                this.search = search == null? Search.EMPTY : search;
                refreshSubtitle();
                updateFeed();
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void updateFeed() {
        final String searchUrl = ServiceUtils.getUrlSearchQuery(search, pagesFetchedCount);
        final JobPostFeedClient jobPostFeedClient = ServiceGenerator.createService(JobPostFeedClient.class);
        final Call<JobPostsFeed> jobPostsFeedCall = jobPostFeedClient.getJobPostFeedCall(searchUrl, ServiceUtils.getApiKey());
        jobPostsFeedCall.enqueue(new Callback<JobPostsFeed>() {
            @Override
            public void onResponse(Response<JobPostsFeed> response, Retrofit retrofit) {
                if (response.body() != null) {
                    if (JobPostsFeed.EMPTY.equals(jobPostsFeed)) {
                        jobPostsFeed = response.body();
                    } else {
                        JobPostsFeed temp = response.body();
                        if (temp.getJobPosts().get().size() == 0 ) {
                            adapter.setShouldShowFooterSpinner(false);
                        } else {
                            jobPostsFeed.getJobPosts().get().addAll(temp.getJobPosts().get());
                        }
                    }
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

    private void refreshSubtitle() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setSubtitle(!TextUtils.isEmpty(search.getJobTitle()) ?
                    search.getJobTitle() :
                    getString(R.string.activity_feed_latest_label));
        }
    }

    private void refreshAdapter() {
        adapter.setJobPosts(jobPostsFeed.getJobPosts().get());
    }

    private void setError(String info) {
        Toast.makeText(FeedActivity.this, info, Toast.LENGTH_LONG).show();
    }

    private void actionShowSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SearchActivity.EXTRA_SEARCH, search);
        startActivityForResult(intent, KEY_SEARCH_RESULT);
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

    // FeedRecyclerActivity.OnLastItemAppearedListener
    @Override
    public void onLastItemAppeared() {
        pagesFetchedCount++;
        Log.d(TAG, "onLastItemAppeared() called");
//        new Handler().postDelayed((Runnable) () -> adapter.setShouldShowFooterSpinner(false), 1000);
        updateFeed();
    }
}
