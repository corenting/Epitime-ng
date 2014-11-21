package fr.corenting.epitime_ng.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.preference.PreferenceFragment;

import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.utils.FileUtils;
import fr.corenting.epitime_ng.utils.MiscUtils;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        Preference deletePref = findPreference(getString(R.string.settings_delete_cache_title));
        deletePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(getActivity())
                        .setMessage(getString(R.string.settings_delete_cache_dialog))
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (FileUtils.deleteAllFiles()) {
                                    MiscUtils.makeToast(getString(R.string.settings_cache_success));
                                } else {
                                    MiscUtils.makeToast(getString(R.string.settings_cache_fail));
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), null)
                        .show();
                return false;
            }
        });
    }
}