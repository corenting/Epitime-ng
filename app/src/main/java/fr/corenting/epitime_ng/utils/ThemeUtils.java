package fr.corenting.epitime_ng.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Method;

import fr.corenting.epitime_ng.R;

public class ThemeUtils {

    public static int getTheme(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        String pref = sp.getString("appTheme", "Blue");
        return getThemeFromString(pref);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarColor(Activity activity) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        String themeName = sp.getString("appTheme", "Blue");
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(activity.getResources().getColor(getThemeColorDark(themeName)));
    }

    public static void checkTheme(Activity activity) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        int saved = ThemeUtils.getThemeFromString(sp.getString("appTheme", "Blue"));
        int current = ThemeUtils.getCurrentThemeId(activity);

        if (saved != current) {
            MiscUtils.reloadActivity(activity, activity.getClass());
        }
    }

    public static int getWidgetDateBackground(String theme) {
        if (theme.equals("Indigo")) {
            return R.color.indigo_primary;
        }
        if (theme.equals("Green")) {
            return R.color.green_primary;
        }
        if (theme.equals("Purple")) {
            return R.color.purple_primary;
        }
        if (theme.equals("BlueGray")) {
            return R.color.bluegray_primary;
        }
        if (theme.equals("Red")) {
            return R.color.red_primary;
        }
        if (theme.equals("Orange")) {
            return R.color.orange_primary;
        }
        return R.color.blue_primary;
    }

    public static int getThemeColorDark(String theme) {
        if (theme.equals("Indigo")) {
            return R.color.indigo_primary_dark;
        }
        if (theme.equals("Green")) {
            return R.color.green_primary_dark;
        }
        if (theme.equals("Purple")) {
            return R.color.purple_primary_dark;
        }
        if (theme.equals("BlueGray")) {
            return R.color.bluegray_primary_dark;
        }
        if (theme.equals("Red")) {
            return R.color.red_primary_dark;
        }
        if (theme.equals("Orange")) {
            return R.color.orange_primary_dark;
        }
        return R.color.blue_primary_dark;
    }

    public static int getThemeFromString(String theme) {
        if (theme.equals("Indigo")) {
            return R.style.Indigo;
        }
        if (theme.equals("Green")) {
            return R.style.Green;
        }
        if (theme.equals("Purple")) {
            return R.style.Purple;
        }
        if (theme.equals("BlueGray")) {
            return R.style.BlueGray;
        }
        if (theme.equals("Red")) {
            return R.style.Red;
        }
        if (theme.equals("Orange")) {
            return R.style.Orange;
        }
        return R.style.Blue;
    }

    public static int getCurrentThemeId(Context context) {
        //Find a proper way to do this
        try {
            Class<?> clazz = ContextThemeWrapper.class;
            Method method = clazz.getMethod("getThemeResId");
            method.setAccessible(true);
            return (Integer) method.invoke(context);
        } catch (Exception e) {
            Log.e("getCurrentThemeId", "Failed to get theme resource ID", e);
            return getTheme(context); //Else return the saved theme to avoid reloading if this doesn't work
        }
    }
}
