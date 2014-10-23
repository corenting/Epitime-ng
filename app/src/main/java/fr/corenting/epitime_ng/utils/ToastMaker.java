package fr.corenting.epitime_ng.utils;

import android.os.Handler;
import android.widget.Toast;

import fr.corenting.epitime_ng.EpiTime;

/**
 * Created by KingGreed on 08/06/2014.
 */
public class ToastMaker {

    public static void makeToast(String message, int time) {
        final Toast toast = Toast.makeText(EpiTime.getInstance().getCurrentActivity(),
                message, Toast.LENGTH_SHORT);
        toast.show();

        new Handler().postDelayed(new Runnable() { @Override public void run() { toast.cancel(); } }, time);

    }
}
