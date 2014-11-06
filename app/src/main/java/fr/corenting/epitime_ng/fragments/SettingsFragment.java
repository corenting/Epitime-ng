package fr.corenting.epitime_ng.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.preference.PreferenceFragment;

import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.activities.AboutActivity;
import fr.corenting.epitime_ng.managers.BlacklistManager;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        Preference aboutPref = findPreference(getString(R.string.about));
        aboutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getActivity().startActivity(new Intent(getActivity(), AboutActivity.class));
                return false;
            }
        });
    }
}