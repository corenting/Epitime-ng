package fr.corenting.epitime_ng.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.activities.SettingsActivity;
import fr.corenting.epitime_ng.fragments.SettingsFragment;


public class MiscUtils {

    public static void makeToast(String message) {
        final Toast toast = Toast.makeText(EpiTime.getInstance().getCurrentActivity(),
                message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void reloadActivity(Activity a, Class classToStart)
    {
        a.finish();
        Intent intent = new Intent(a, classToStart);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        a.startActivity(intent);
    }
}
