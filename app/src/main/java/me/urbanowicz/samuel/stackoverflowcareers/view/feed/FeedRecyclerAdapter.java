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


public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.ViewHolder> {
    private List<JobPost> jobPosts;

    private OnItemClickListener onItemClickListener;

    public FeedRecyclerAdapter(OnItemClickListener listener) {
        this(Collections.EMPTY_LIST, listener);
    }

    public FeedRecyclerAdapter(Collection<JobPost> jobPosts, OnItemClickListener listener) {
        this.jobPosts = new ArrayList<>(jobPosts);
        this.onItemClickListener = listener;
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
        final JobPost jobPost = jobPosts.get(position);
        holder.job.setText(jobPost.getJobTitle());
        holder.company.setText(jobPost.getCompanyName());
        holder.location.setText(jobPost.getLocation());
        holder.itemView.setOnClickListener((view) -> onItemClickListener.onClick(position));
    }

    @Override
    public int getItemCount() {
        return jobPosts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView job;
        final TextView company;
        final TextView location;

        public ViewHolder(View itemView) {
            super(itemView);
            job = (TextView) itemView.findViewById(R.id.job_title);
            company = (TextView) itemView.findViewById(R.id.company_name);
            location = (TextView) itemView.findViewById(R.id.location);
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }


}