package me.urbanowicz.samuel.stackoverflowcareers.view.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
        webView = (WebView) findViewById(R.id.web_view);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);

        findViewById(R.id.fab).setOnClickListener((v) -> actionShare());

        setupAppBarValues();
        loadContent();
    }

    private void loadContent() {
        webView.loadUrl(jobPost.getJobLink().toString());
    }

    private void setupAppBarValues() {
        setTitle(jobPost.getJobTitle());
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
