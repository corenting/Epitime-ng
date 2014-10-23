package fr.corenting.epitime_ng.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;

public final class DialogUtils {
	private static boolean hasWindow = false;
	private DialogUtils() {}
	
	public static void displaySimpleAlert(Activity activity, String title, String message) {
		if(!hasWindow) {

            DialogUtils.hasWindow = true;
            Activity context = (Activity)EpiTime.getInstance().getCurrentActivity();

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater     = context.getLayoutInflater();
            View v                      = inflater.inflate(R.layout.dialog_alert, null);

            ((TextView)v.findViewById(R.id.dialog_alert_title)).setText(title);
            ((TextView)v.findViewById(R.id.dialog_alert_text)) .setText(message);

            builder.setView(v);
            builder.setPositiveButton("Ok", null);

            AlertDialog dialog = builder.create();
            dialog.show();

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(Color.parseColor("#282828"));
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#ffffff"));

		}
	}

}
