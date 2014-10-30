package fr.corenting.epitime_ng.utils;

import android.widget.Toast;

import fr.corenting.epitime_ng.EpiTime;


public class ToastMaker {

    public static void makeToast(String message) {
        final Toast toast = Toast.makeText(EpiTime.getInstance().getCurrentActivity(),
                message, Toast.LENGTH_SHORT);
        toast.show();

    }
}
