package fr.corenting.epitime_ng.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.managers.ScheduleManager;

public class StartActivity extends Activity {

    private Intent destination = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean fromWidget = getIntent().getBooleanExtra("fromWidget", false);
        setContentView(R.layout.activity_splash_screen);
        EpiTime.getInstance().setCurrentActivity(this);
        ScheduleManager manager = EpiTime.getInstance().getScheduleManager();

        EpiTime.getInstance().getGroupManager().getGroups();

        this.destination = new Intent(this, GroupListActivity.class);

        Bundle b = new Bundle();
        b.putBoolean("NoGroup", true);
        b.putString("School", "EPITA");

        this.destination.putExtras(b);

        if(!manager.getGroup().equals(ScheduleManager.defaultGroup)) {
            this.destination = new Intent(this, DayListActivity.class);
            if(fromWidget)
            {
                manager.setGroup(manager.getWidgetGroup());
                manager.resetCalendar();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startActivity(StartActivity.this.destination);
        finish();
    }

}
