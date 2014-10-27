package fr.corenting.epitime_ng.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;

public final class DialogUtils {

    private static boolean hasWindow = false;

    public static void displaySimpleAlert(String title, String message) {
        if (!hasWindow) {
            DialogUtils.hasWindow = true;
            Activity context = (Activity) EpiTime.getInstance().getCurrentActivity();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setPositiveButton(context.getResources().getString(R.string.ok), null);
            AlertDialog dialog = builder.create();
            dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            dialog.setTitle(title);
            dialog.setMessage(message);
            dialog.show();

        }
    }
}
