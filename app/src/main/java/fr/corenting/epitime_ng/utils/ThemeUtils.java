package fr.corenting.epitime_ng.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextThemeWrapper;

import java.lang.reflect.Method;

import fr.corenting.epitime_ng.R;

public class ThemeUtils {

    public static int getTheme(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        String pref = sp.getString("appTheme", "Blue");
        return getThemeFromString(pref);
    }

    public static int getWidgetDateBackground(String theme) {
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

    public static int getWidgetGroupBackground(String theme) {
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

    public static int getCurrentThemeId(Context context)
    {
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
