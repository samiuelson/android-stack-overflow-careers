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


public class FeedRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static int VIEW_TYPE_REGULAR = 23;
    private final static int VIEW_TYPE_SPINNER = 24;

    private List<JobPost> jobPosts;

    private OnItemClickListener onItemClickListener;
    private OnLastItemAppearedListener onLastItemAppearedListener;

    private boolean shouldShowFooterSpinner = true;

    public FeedRecyclerAdapter(
            OnItemClickListener listener,
            OnLastItemAppearedListener lastItemAppearedListener
    ) {
        this(Collections.EMPTY_LIST, listener, lastItemAppearedListener);
    }

    public FeedRecyclerAdapter(
            Collection<JobPost> jobPosts,
            OnItemClickListener onItemClickListener,
            OnLastItemAppearedListener onLastItemAppearedListener
    ) {
        this.jobPosts = new ArrayList<>(jobPosts);
        this.onItemClickListener = onItemClickListener;
        this.onLastItemAppearedListener = onLastItemAppearedListener;
    }

    public void setJobPosts(Collection<JobPost> jobPosts) {
        this.jobPosts = new ArrayList<>(jobPosts);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (getIsLastPosition(position)) {
            return VIEW_TYPE_SPINNER;
        }
        return VIEW_TYPE_REGULAR;
    }

    private boolean getIsLastPosition(int position) {
        return position == jobPosts.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            default:
            case VIEW_TYPE_REGULAR:
            return new RegularViewHolder(
                    LayoutInflater
                            .from(parent.getContext())
                            .inflate(R.layout.feed_item_view, parent, false)
            );
            case VIEW_TYPE_SPINNER:
                return new SpinnerViewHolder(
                        LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.feed_list_spinner, parent, false)
                );
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);

        if (VIEW_TYPE_REGULAR == itemViewType) {
            RegularViewHolder viewHolder = (RegularViewHolder) holder;
            final JobPost jobPost = jobPosts.get(position);
            viewHolder.job.setText(jobPost.getJobTitle());
            viewHolder.company.setText(jobPost.getCompanyName());
            viewHolder.location.setText(jobPost.getLocation());
            viewHolder.cardContentContainer.setOnClickListener((view) -> onItemClickListener.onClick(position));
        } else if (VIEW_TYPE_SPINNER == itemViewType) {
            SpinnerViewHolder viewHolder = (SpinnerViewHolder) holder;
            onLastItemAppearedListener.onLastItemAppeared();
        }

    }

    @Override
    public int getItemCount() {
        if (jobPosts == null) {
            return 0;
        }
        if (jobPosts.size() == 0) {
            return 0;
        }
        if (shouldShowFooterSpinner) {
            return jobPosts.size() + 1;//adds 1 for spinner
        } else {
            return jobPosts.size();
        }

    }

    public void setShouldShowFooterSpinner(boolean shouldShowFooterSpinner) {
        this.shouldShowFooterSpinner = shouldShowFooterSpinner;
        notifyDataSetChanged();
    }

    static class RegularViewHolder extends RecyclerView.ViewHolder {
        final TextView job;
        final TextView company;
        final TextView location;
        final ViewGroup cardContentContainer;

        public RegularViewHolder(View itemView) {
            super(itemView);
            job = (TextView) itemView.findViewById(R.id.job_title);
            company = (TextView) itemView.findViewById(R.id.company_name);
            location = (TextView) itemView.findViewById(R.id.location);
            cardContentContainer = (ViewGroup) itemView.findViewById(R.id.card_content);
        }
    }

    static class SpinnerViewHolder extends RecyclerView.ViewHolder {
        public SpinnerViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public interface OnLastItemAppearedListener {
        void onLastItemAppeared();
    }


}