package me.urbanowicz.samuel.stackoverflowjobs.feed;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
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
import java.util.Collections;

import me.urbanowicz.samuel.stackoverflowjobs.R;
import me.urbanowicz.samuel.stackoverflowjobs.data.JobPost;
import me.urbanowicz.samuel.stackoverflowjobs.data.Search;
import me.urbanowicz.samuel.stackoverflowjobs.search.SearchActivity;
import me.urbanowicz.samuel.stackoverflowjobs.system.PreferencesUtils;
import me.urbanowicz.samuel.stackoverflowjobs.system.RatingDialogHelper;

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

        PreferencesUtils.incrementAppStartCount(this);
        RatingDialogHelper.showRatingBeggingDialogIfNeeded(this);

        setContentView(R.layout.activity_feed);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setTitle(getString(R.string.app_name_title));

        RecyclerView feedRecyclerView = (RecyclerView) findViewById(R.id.feedItemsRecyclerView);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setOnRefreshListener(
                () -> {
                    feedManager.downloadFeedForNewSearch(lastSearch, this);
                    adapter.setJobPosts(Collections.EMPTY_LIST);
                }
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
            swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(true));
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
                    swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(true));
                    adapter.setJobPosts(Collections.EMPTY_LIST);
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
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, jobPostClicked.getJobLink().toString());
        intent.putExtra(Intent.EXTRA_SUBJECT, jobPostClicked.getJobTitle());
        final Bitmap shareIcon = BitmapFactory.decodeResource(getResources(), R.id.action_search);
//        builder.setActionButton(shareIcon, "Share", PendingIntent.getActivity(this, 0, Intent.createChooser(intent, getString(R.string.share_job_title)), 0));
//        builder.addMenuItem("Share", PendingIntent.getActivity(this, 0, Intent.createChooser(intent, getString(R.string.share_job_title)), 0));
        builder.setStartAnimations(this, android.R.anim.fade_in, android.R.anim.fade_out);
        builder.setExitAnimations(this, android.R.anim.fade_in, android.R.anim.fade_out);
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(jobPostClicked.getJobLink().toString()));
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
