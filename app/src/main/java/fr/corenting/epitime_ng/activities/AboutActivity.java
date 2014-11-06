package fr.corenting.epitime_ng.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import fr.corenting.epitime_ng.BuildConfig;
import fr.corenting.epitime_ng.R;

public class AboutActivity extends ActionBarActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        TextView about = (TextView) findViewById(R.id.about_text);
        about.setText(about.getText() + "\n" + "Version : " + BuildConfig.VERSION_NAME);
    }
}
