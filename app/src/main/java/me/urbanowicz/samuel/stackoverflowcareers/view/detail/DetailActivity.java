package me.urbanowicz.samuel.stackoverflowcareers.view.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import me.urbanowicz.samuel.stackoverflowcareers.R;

public class DetailActivity extends AppCompatActivity {
    private static final String EXTRA_URL = "extra_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    public static void startActivity(Context context, String jobUrl) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_URL, jobUrl);
        context.startActivity(intent);
    }

}
