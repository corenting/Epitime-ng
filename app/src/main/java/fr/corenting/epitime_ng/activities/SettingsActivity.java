package fr.corenting.epitime_ng.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.utils.ThemeUtils;
import fr.corenting.epitime_ng.fragments.SettingsFragment;

public class SettingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setTheme(ThemeUtils.getTheme(this));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            ThemeUtils.setStatusBarColor(this);
        }
        setContentView(R.layout.blank);
        // Display the fragment as the main content.
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}