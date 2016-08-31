package me.urbanowicz.samuel.stackoverflowjobs.search;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;

import me.urbanowicz.samuel.stackoverflowjobs.R;
import me.urbanowicz.samuel.stackoverflowjobs.data.Search;

public class SearchActivity extends AppCompatActivity {

    public static final String EXTRA_SEARCH = "extra_search";

    private EditText jobTitleEditText;
    private EditText locationEditText;
    private CheckBox allowsRemoteCheckBox;
    private CheckBox providesRelocationCheckBox;
    private CheckBox providesVisaSponsorshipCheckBox;
    private Search search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        findViewById(R.id.scrim).setOnClickListener((View v) -> dismiss());

        jobTitleEditText = (EditText) findViewById(R.id.search_view);
        locationEditText = (EditText) findViewById(R.id.location);
        allowsRemoteCheckBox = (CheckBox) findViewById(R.id.allows_remote);
        providesRelocationCheckBox = (CheckBox) findViewById(R.id.offers_relocation);
        providesVisaSponsorshipCheckBox = (CheckBox) findViewById(R.id.offers_visa_sponsorship);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Drawable back = DrawableCompat.wrap(ContextCompat.getDrawable(this, R.drawable.ic_back));
        toolbar.setNavigationIcon(back);
        toolbar.setNavigationOnClickListener((View v) -> dismiss());
        toolbar.inflateMenu(R.menu.menu_activity_search);
        toolbar.setOnMenuItemClickListener((MenuItem item) -> {
            if (item.getItemId() == R.id.action_search) {
                finishWithResult();
                return true;
            }
            return false;
        });

        Search search = (Search) getIntent().getSerializableExtra(SearchActivity.EXTRA_SEARCH);
        this.search = search == null? Search.EMPTY : search;
        setupSearchView();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            doEnterAnim();
        }

        overridePendingTransition(0, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    private void setupSearchView() {
        jobTitleEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                finishWithResult();
                return true;
            }
            return false;
        });
        if (!TextUtils.isEmpty(search.getJobTitle())) {
            jobTitleEditText.setText(search.getJobTitle());
        }
        if (!TextUtils.isEmpty(search.getLocation())) {
            locationEditText.setText(search.getLocation());
        }
        allowsRemoteCheckBox.setChecked(search.isAllowsRemote());
        providesRelocationCheckBox.setChecked(search.isProvidesRelocation());
        providesVisaSponsorshipCheckBox.setChecked(search.isProvidesVisaSponsorship());
    }

    private void dismiss() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            doExitAnim();
        } else {
            ActivityCompat.finishAfterTransition(this);
        }
    }

    private void finishWithResult() {
        final String jobTitle = jobTitleEditText.getText().toString();
        final String location = locationEditText.getText().toString();
        final boolean allowsRemote = allowsRemoteCheckBox.isChecked();
        final boolean offersVisaSponsorship = providesVisaSponsorshipCheckBox.isChecked();
        final boolean offersRelocation = providesRelocationCheckBox.isChecked();

        final Search search = new Search(
                jobTitle,
                location,
                100,
                "",
                allowsRemote,
                offersRelocation,
                offersVisaSponsorship);

        Intent result = new Intent();
        result.putExtra(EXTRA_SEARCH, search);
        setResult(RESULT_OK, result);

        dismiss();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void doEnterAnim() {
        // Fade in a background scrim as this is a floating window. We could have used a
        // translucent window background but this approach allows us to turn off window animation &
        // overlap the fade with the reveal animation – making it feel snappier.
        View scrim = findViewById(R.id.scrim);
        scrim.animate()
                .alpha(1f)
                .setDuration(500L)
                .setInterpolator(
                        AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in))
                .start();

        // Next perform the circular reveal on the search panel
        final View searchPanel = findViewById(R.id.search_panel);
        if (searchPanel != null) {
            // We use a view tree observer to set this up once the view is measured & laid out
            searchPanel.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            searchPanel.getViewTreeObserver().removeOnPreDrawListener(this);
                            // As the height will change once the initial suggestions are delivered by the
                            // loader, we can't use the search panels height to calculate the final radius
                            // so we fall back to it's parent to be safe
                            int revealRadius = ((ViewGroup) searchPanel.getParent()).getHeight();
                            // Center the animation on the top right of the panel i.e. near to the
                            // search button which launched this screen.
                            Animator show = ViewAnimationUtils.createCircularReveal(searchPanel,
                                    searchPanel.getRight(), searchPanel.getTop(), 0f, revealRadius);
                            show.setDuration(250L);
                            show.setInterpolator(AnimationUtils.loadInterpolator(SearchActivity.this,
                                    android.R.interpolator.fast_out_slow_in));
                            show.start();
                            return false;
                        }
                    });
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void doExitAnim() {
        final View searchPanel = findViewById(R.id.search_panel);
        // Center the animation on the top right of the panel i.e. near to the search button which
        // launched this screen. The starting radius therefore is the diagonal distance from the top
        // right to the bottom left
        int revealRadius = (int) Math.sqrt(Math.pow(searchPanel.getWidth(), 2)
                + Math.pow(searchPanel.getHeight(), 2));
        // Animating the radius to 0 produces the contracting effect
        Animator shrink = ViewAnimationUtils.createCircularReveal(searchPanel,
                searchPanel.getRight(), searchPanel.getTop(), revealRadius, 0f);
        shrink.setDuration(200L);
        shrink.setInterpolator(AnimationUtils.loadInterpolator(SearchActivity.this,
                android.R.interpolator.fast_out_slow_in));
        shrink.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                searchPanel.setVisibility(View.GONE);
                ActivityCompat.finishAfterTransition(SearchActivity.this);
            }
        });
        shrink.start();

        // We also animate out the translucent background at the same time.
        findViewById(R.id.scrim).animate()
                .alpha(0f)
                .setDuration(200L)
                .setInterpolator(
                        AnimationUtils.loadInterpolator(SearchActivity.this,
                                android.R.interpolator.fast_out_slow_in))
                .start();
    }

}
