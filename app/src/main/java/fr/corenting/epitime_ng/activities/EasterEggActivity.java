package fr.corenting.epitime_ng.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.utils.MiscUtils;

public class EasterEggActivity extends Activity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EpiTime.getInstance().setCurrentActivity(this);
        setContentView(R.layout.activity_easter_egg);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        MiscUtils.makeToast(getString(R.string.i_am_a_llama));
    }
}
