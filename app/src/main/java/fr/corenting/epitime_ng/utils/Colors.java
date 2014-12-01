package fr.corenting.epitime_ng.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import fr.corenting.epitime_ng.R;

public class Colors {

    public static int getBackgroundMainColor(Context c)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt("appBackgroundMainColor", c.getResources().getColor(android.support.v7.appcompat.R.color.background_material_dark));
    }

    public static int getBackgroundVariantColor(Context c)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt("appBackgroundMVariantColor", c.getResources().getColor(android.R.color.black));
    }
}
