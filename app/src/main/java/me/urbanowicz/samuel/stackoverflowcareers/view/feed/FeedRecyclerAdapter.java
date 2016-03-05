package me.urbanowicz.samuel.stackoverflowcareers.view.feed;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import me.urbanowicz.samuel.stackoverflowcareers.R;
import me.urbanowicz.samuel.stackoverflowcareers.domain.JobPost;


class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.ViewHolder> {
    private List<JobPost> jobPosts;

    public FeedRecyclerAdapter() {
        this(Collections.EMPTY_LIST);
    }

    public FeedRecyclerAdapter(Collection<JobPost> jobPosts) {
        this.jobPosts = new ArrayList<>(jobPosts);
    }

    public void setJobPosts(Collection<JobPost> jobPosts) {
        this.jobPosts = new ArrayList<>(jobPosts);
        notifyDataSetChanged();
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
        holder.jobTitleTextView.setText(jobPosts.get(position).getJobTitle());
    }

    @Override
    public int getItemCount() {
        return jobPosts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView jobTitleTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            jobTitleTextView = (TextView) itemView.findViewById(R.id.jobTitleTextView);
        }
    }
}