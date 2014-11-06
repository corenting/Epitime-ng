package fr.corenting.epitime_ng.fragments;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.support.v4.preference.PreferenceFragment;

import fr.corenting.epitime_ng.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);
    }
}