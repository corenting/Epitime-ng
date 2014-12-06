package fr.corenting.epitime_ng.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;


public class MiscUtils {

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

    public static void makeToast(String message) {
        final Toast toast = Toast.makeText(EpiTime.getInstance().getCurrentActivity(),
                message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private static int getThemeFromString(String theme) {
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
}
