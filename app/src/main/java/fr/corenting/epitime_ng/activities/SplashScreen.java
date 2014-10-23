package fr.corenting.epitime_ng.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.managers.ScheduleManager;

public class SplashScreen extends Activity {
	
	private Intent destination = null;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_splash_screen);
		EpiTime.getInstance().setCurrentActivity(this);
        ScheduleManager manager = EpiTime.getInstance().getScheduleManager();
        manager.load();

        EpiTime.getInstance().getGroupManager().getGroups();

		this.destination = new Intent(this, GroupListActivity.class);
		
		Bundle b = new Bundle();
		b.putBoolean("NoGroup", true);
		b.putString("School", "EPITA");
		
		this.destination.putExtras(b);
		
		if(!manager.getGroup().equals(ScheduleManager.defaultGroup)) {
			this.destination = new Intent(this, DayList.class);
		}
	}

    @Override
    public void onResume() {
    	super.onResume();
    	
    	Handler handler = new Handler();
    	Runnable runnable = new Runnable() { 
	         public void run() { 
	        	 startActivity(SplashScreen.this.destination);
	        	 finish();
	         } 
	    }; 

		handler.postDelayed(runnable, 300);
    }
	
}
