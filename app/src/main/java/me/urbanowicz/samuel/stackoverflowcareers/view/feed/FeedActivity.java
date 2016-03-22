package me.urbanowicz.samuel.stackoverflowcareers.view.feed;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import android.view.View;
import android.widget.Toast;

import java.util.Collection;

import me.urbanowicz.samuel.stackoverflowcareers.R;
import me.urbanowicz.samuel.stackoverflowcareers.domain.JobPost;
import me.urbanowicz.samuel.stackoverflowcareers.service.JobPostFeedManager;
import me.urbanowicz.samuel.stackoverflowcareers.service.Search;
import me.urbanowicz.samuel.stackoverflowcareers.view.detail.DetailActivity;
import me.urbanowicz.samuel.stackoverflowcareers.view.search.SearchActivity;

public class FeedActivity extends AppCompatActivity implements
        FeedRecyclerAdapter.OnItemClickListener, FeedRecyclerAdapter.OnLastItemAppearedListener,
        JobPostFeedManager.JobPostsFeedManagerCallback {

    private static final String TAG = FeedActivity.class.getSimpleName();
    private static final String KEY_SEARCH = "lastSearchKey";
    private static final int KEY_SEARCH_RESULT = 23;

    private FeedRecyclerAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Search lastSearch;
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
        swipeRefreshLayout.setOnRefreshListener(
                () -> feedManager.downloadFeedForNewSearch(lastSearch, this)
        );

        feedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedRecyclerView.hasFixedSize();
        adapter = new FeedRecyclerAdapter(this, this);
        feedRecyclerView.setAdapter(adapter);

        feedManager = JobPostFeedManager.getInstance();

        if (savedInstanceState != null) {
            Search search = (Search) savedInstanceState.getSerializable(KEY_SEARCH);
            this.lastSearch = search == null ? Search.EMPTY : search;
        } else {
            lastSearch = Search.EMPTY;
        }

        refreshSubtitle();

        if (feedManager.getCurrentSearch() != null && feedManager.getCurrentJobPosts().size() > 0) {
            refreshAdapter();
        } else {
            swipeRefreshLayout.setRefreshing(true);
            feedManager.downloadFeedForNewSearch(lastSearch, this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_SEARCH, lastSearch);
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
                Search search = (Search) data.getSerializableExtra(SearchActivity.EXTRA_SEARCH);
                search = search == null ? Search.EMPTY : search;
                if (!this.lastSearch.equals(search)) {
                    swipeRefreshLayout.setRefreshing(true);
                    feedManager.downloadFeedForNewSearch(search, this);
                }
                this.lastSearch = search;
                refreshSubtitle();
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void refreshSubtitle() {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setSubtitle(!TextUtils.isEmpty(lastSearch.getJobTitle()) ?
                    lastSearch.getJobTitle() :
                    getString(R.string.activity_feed_latest_label));
        }
    }

    private void refreshAdapter() {
        adapter.setJobPosts(feedManager.getCurrentJobPosts());
    }

    private void setError(String info) {
        Toast.makeText(FeedActivity.this, info, Toast.LENGTH_LONG).show();
        Snackbar
                .make(findViewById(R.id.coordinator), R.string.feed_general_error, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.button_label_retry, (View v) -> feedManager.downloadFeedForNewSearch(lastSearch, this))
                .show();

    }

    private void actionShowSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SearchActivity.EXTRA_SEARCH, lastSearch);
        startActivityForResult(intent, KEY_SEARCH_RESULT);
    }

    // FeedRecyclerAdapter.OnItemClickListener
    @Override
    public void onClick(int position) {
        JobPost jobPostClicked = feedManager.getCurrentJobPosts().get(position);
        DetailActivity.startActivity(this, jobPostClicked);
    }

    // FeedRecyclerActivity.OnLastItemAppearedListener
    @Override
    public void onLastItemAppeared() {
        Log.d(TAG, "onLastItemAppeared() called");
        feedManager.downloadFeedNextPage(this);
    }

    // JobPostFeedManager.JobPostsFeedManagerCallback
    @Override
    public void onFeedUpdated(Collection<JobPost> jobPosts, JobPostFeedManager.MoreOfeersPossibility moreOfeersPossibility) {
        adapter.setJobPosts(jobPosts);
        if (JobPostFeedManager.MoreOfeersPossibility.PROBABLE.equals(moreOfeersPossibility)) {
            adapter.setShouldShowFooterSpinner(true);
        } else {
            adapter.setShouldShowFooterSpinner(false);
        }
        swipeRefreshLayout.setRefreshing(false);
    }
    @Override
    public void onFeedUpdateError(String errorMessage) {
        setError(errorMessage);
        swipeRefreshLayout.setRefreshing(false);
    }
}
