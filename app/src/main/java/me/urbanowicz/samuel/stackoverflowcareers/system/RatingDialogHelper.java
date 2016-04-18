package me.urbanowicz.samuel.stackoverflowcareers.system;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import me.urbanowicz.samuel.stackoverflowcareers.R;

public class RatingDialogHelper {
    private final static int APP_RUNNS_INTERVAL_BETWEEN_DIALOG = 20;

    public static void showRatingBeggingDialogIfNeeded(Activity activity) {
        if (PreferencesUtils.isDialogEnabled(activity)
                & PreferencesUtils.getAppStartCount(activity) % APP_RUNNS_INTERVAL_BETWEEN_DIALOG == 0) {

            new AlertDialog.Builder(activity)
                    .setIcon(R.drawable.ic_rate)
                    .setMessage(R.string.rating_begging_message)
                    .setTitle(R.string.rating_begging_title)
                    .setNeutralButton(activity.getString(R.string.rating_begging_not_now_btn), null)
                    .setPositiveButton(activity.getString(R.string.rating_begging_sure_btn), (dialog, which) -> {
                        goToGooglePlayStore(activity);
                    })
                    .setNegativeButton(R.string.rating_begging_no_btn, ((dialog, which) -> setNotToAskAgain(activity)))
                    .setCancelable(false)
                    .show();
        }
    }

    private static void goToGooglePlayStore(Context context) {
        final String packageName = context.getPackageName();

        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + packageName)));
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
    }

    private static void setNotToAskAgain(Context context) {
        PreferencesUtils.disableDialog(context);
    }

}
