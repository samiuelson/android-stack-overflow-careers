package me.urbanowicz.samuel.stackoverflowcareers.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import me.urbanowicz.samuel.stackoverflowcareers.R;
import me.urbanowicz.samuel.stackoverflowcareers.domain.JobPost;
import me.urbanowicz.samuel.stackoverflowcareers.domain.JobPostsFeed;
import me.urbanowicz.samuel.stackoverflowcareers.service.JobPostService;

public class FeedActivity extends AppCompatActivity {
    private static final String TAG = FeedActivity.class.getSimpleName();

    private FeedRecyclerAdapter adapter;
    private RecyclerView feedRecyclerView;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        updateFeed();
    }

    private void updateFeed() {
        //todo
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setJobPosts(finalJobPostsFeed.getJobPosts());
                        adapter.notifyDataSetChanged();
                        feedRecyclerView.invalidate();
                    }
                });
            }
        }).start();


    }

    private static class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.ViewHolder> {
        private List<JobPost> jobPosts;

        public FeedRecyclerAdapter() {
            this(Collections.EMPTY_LIST);
        }

        public FeedRecyclerAdapter(Collection<JobPost> jobPosts) {
            this.jobPosts = new ArrayList<>(jobPosts);
        }

        public void setJobPosts(Collection<JobPost> jobPosts) {
            this.jobPosts = new ArrayList<>(jobPosts);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(
                    LayoutInflater
                            .from(parent.getContext())
                            .inflate(R.layout.feed_item_view, parent, false)
            );
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ((TextView) holder.itemView)
                    .setText(jobPosts.get(position).getJobTitle());

        }

        @Override
        public int getItemCount() {
            return 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
