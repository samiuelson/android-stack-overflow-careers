package me.urbanowicz.samuel.stackoverflowcareers.view.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import me.urbanowicz.samuel.stackoverflowcareers.R;
import me.urbanowicz.samuel.stackoverflowcareers.domain.JobPost;

public class DetailActivity extends AppCompatActivity {
    private static final String EXTRA_JOB_POST = "extra_job";

    private JobPost jobPost;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.jobPost = (JobPost) getIntent().getSerializableExtra(EXTRA_JOB_POST);

        setContentView(R.layout.detail_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ProgressBar progress = (ProgressBar) findViewById(R.id.progress);
        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
//        webView.setScrollbarFadingEnabled(false);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress >= 99) {
                    progress.setVisibility(View.GONE);
                }
            }
        });
        findViewById(R.id.fab).setOnClickListener((v) -> actionShare());

        setupAppBarValues();
        loadContent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupAppBarValues() {
        setTitle(jobPost.getJobTitle());
    }

    private void loadContent() {
        webView.loadUrl(jobPost.getJobLink().toString());
    }

    private void actionShare() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, jobPost.getJobLink().toString());
        intent.putExtra(Intent.EXTRA_SUBJECT, jobPost.getJobTitle());
        startActivity(Intent.createChooser(intent, getString(R.string.share_job_title)));
    }

    public static void startActivity(Context context, JobPost jobPost) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_JOB_POST, jobPost);
        context.startActivity(intent);
    }

}
