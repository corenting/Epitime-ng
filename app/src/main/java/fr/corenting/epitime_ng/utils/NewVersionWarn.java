package fr.corenting.epitime_ng.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.preference.PreferenceManager;

import fr.corenting.epitime_ng.R;

public class NewVersionWarn {


    public static void Run(Context c) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        int currentVersionNumber = 0;

        int savedVersionNumber = sharedPref.getInt("version_number", 0);

        try {
            PackageInfo pi = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
            currentVersionNumber = pi.versionCode;
        } catch (Exception e) {
        }

        if (currentVersionNumber > savedVersionNumber) {
            showWhatsNewDialog(c);

            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putInt("version_number", currentVersionNumber);
            editor.apply();
        }
    }

    private static void showWhatsNewDialog(Context c) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                c);
        alertDialogBuilder
                .setTitle(c.getString(R.string.new_version_warn_title))
                .setMessage(c.getString(R.string.new_version_warn))
                .setCancelable(false)
                .setPositiveButton(c.getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }
                );
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
