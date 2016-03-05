package me.urbanowicz.samuel.stackoverflowcareers.view.feed;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.IOException;

import me.urbanowicz.samuel.stackoverflowcareers.R;
import me.urbanowicz.samuel.stackoverflowcareers.domain.JobPostsFeed;
import me.urbanowicz.samuel.stackoverflowcareers.service.JobPostService;

public class FeedActivity extends AppCompatActivity {
    private static final String TAG = FeedActivity.class.getSimpleName();

    private FeedRecyclerAdapter adapter;
    private RecyclerView feedRecyclerView;
    private Handler refreshFeedHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_feed);
        feedRecyclerView = (RecyclerView) findViewById(R.id.feedItemsRecyclerView);
        feedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedRecyclerView.hasFixedSize();
        adapter = new FeedRecyclerAdapter();
        feedRecyclerView.setAdapter(adapter);
        refreshFeedHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        updateFeed();
    }

    private void updateFeed() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JobPostService jobPostService = new JobPostService();
                JobPostsFeed jobPostsFeed = null;
                try {
                    jobPostsFeed = jobPostService
                            .getJobPostFeed()
                            .execute()
                            .body();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                final JobPostsFeed finalJobPostsFeed = jobPostsFeed;
                refreshFeedHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setJobPosts(finalJobPostsFeed.getJobPosts());
                    }
                });
            }
        }).start();


    }
}
