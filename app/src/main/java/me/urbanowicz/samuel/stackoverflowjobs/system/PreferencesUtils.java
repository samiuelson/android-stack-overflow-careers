package me.urbanowicz.samuel.stackoverflowjobs.system;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtils {
    private static final String PREFS_NAME = "shared_preferences_name";
    private static final String KEY_APP_START_COUNT = "app_start_count";
    private static final String KEY_IS_DIALOG_ENABLED = "is_dialog_enabled";

    public static void incrementAppStartCount(Context context) {
        int appStartCount = getAppStartCount(context);
        getSharedPrefs(context).edit().putInt(KEY_APP_START_COUNT, ++appStartCount).commit();
    }

    public static int getAppStartCount(Context context) {
        return getSharedPrefs(context).getInt(KEY_APP_START_COUNT, 0);
    }

    public static boolean isDialogEnabled(Context context) {
        return getSharedPrefs(context).getBoolean(KEY_IS_DIALOG_ENABLED, true);
    }

    public static void disableDialog(Context context) {
        getSharedPrefs(context).edit().putBoolean(KEY_IS_DIALOG_ENABLED, false).apply();
    }

    private static SharedPreferences getSharedPrefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
