package fr.corenting.epitime_ng.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;


public class MiscUtils{

    public static int getTheme(Context c)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt("appTheme", R.style.Orange);
    }

    public static void makeToast(String message) {
        final Toast toast = Toast.makeText(EpiTime.getInstance().getCurrentActivity(),
                message, Toast.LENGTH_SHORT);
        toast.show();

    }
}
