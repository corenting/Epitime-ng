package fr.corenting.epitime_ng.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.data.GroupItem;
import fr.corenting.epitime_ng.managers.ScheduleManager;
import fr.corenting.epitime_ng.tasks.QueryLecturesNewTask;

public class GroupSelectedActivity extends Activity {

    private Handler handler;
	private Runnable runnable;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_selected);
		EpiTime.getInstance().setCurrentActivity(this);

        ScheduleManager manager = EpiTime.getInstance().getScheduleManager();

        GroupItem item   = ((GroupItem) getIntent().getExtras().get("itemSelected"));
        String    school = getIntent().getStringExtra("school");

        if(school.equals(getString(R.string.Teachers))) { manager.setIsTeacher(true);  }
        else                             { manager.setIsTeacher(false); }

		LinearLayout l = (LinearLayout)this.findViewById(R.id.group_selected_background);
		l.setBackgroundColor(item.getShortColor());
		
		
		((TextView)this.findViewById(R.id.group_selected_selected_item)).setText(item.getLongTitle());
		

		manager.setGroup(item.getLongTitle());
		manager.resetCalendar();
		manager.requestLectures(true, -1, 0, 1);
		new GetDataTask().execute();
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		this.handler = new Handler();
		this.runnable = new Runnable() { 
	         public void run() { 
	        	 Intent intent = new Intent(GroupSelectedActivity.this, DayList.class);
	        	 startActivity(intent);
	        	 finish();
	         } 
	    }; 

		this.handler.postDelayed(this.runnable, 1500);

	}

    @Override
    public void onBackPressed() {
    }


    public void onClick(View view) {
		this.handler.removeCallbacks(this.runnable);
		Intent intent = new Intent(GroupSelectedActivity.this, DayList.class);
		startActivity(intent);
		finish();
	}
	
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {
	    
	    @Override
	    protected void onPostExecute(String[] result) {
	    	if(EpiTime.getInstance().getCurrentActivity() instanceof DayList) {
	    		((DayList)EpiTime.getInstance().getCurrentActivity()).updateAdapter();
	    	}
	        super.onPostExecute(result);
	    }

		@Override
		protected String[] doInBackground(Void... params) {
			while(QueryLecturesNewTask.isRefreshing()) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
	}
}
