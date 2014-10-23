package fr.corenting.epitime_ng.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.activities.DayList;
import fr.corenting.epitime_ng.activities.DrawerActivity;
import fr.corenting.epitime_ng.adapters.LectureListAdapter;
import fr.corenting.epitime_ng.data.Day;
import fr.corenting.epitime_ng.data.Lecture;
import fr.corenting.epitime_ng.listeners.TouchListener;
import fr.corenting.epitime_ng.managers.ScheduleManager;
import fr.corenting.epitime_ng.tasks.QueryLecturesNewTask;
import fr.corenting.epitime_ng.utils.PullToRefreshListView;
import fr.corenting.epitime_ng.utils.PullToRefreshListView.OnRefreshListener;
import fr.corenting.epitime_ng.utils.ToastMaker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DayListFragment extends Fragment {
	
	private PullToRefreshListView lectureList;
    private Day day;
    private List<Lecture> displayed;

    private ScheduleManager manager;
    private ViewGroup       view;
    private ImageView       previousWeek;
    private ImageView       nextWeek;
    private String          lectureSelected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.initMemberVariables(inflater, container);

        this.setup();
        this.setListeners();
        
        this.setTime();
        

        return view;
    }
	
	private void initMemberVariables(LayoutInflater inflater, ViewGroup container) {
		this.view      = (ViewGroup) inflater.inflate(R.layout.activity_week_list, container, false);
        this.displayed = new ArrayList<Lecture>();
		
		this.day     = ((Day) getArguments().get("Day"));
        this.manager = EpiTime.getInstance().getScheduleManager();
        
        this.lectureList  = (PullToRefreshListView) this.view.findViewById(R.id.lectures);
        this.previousWeek = (ImageView) 			this.view.findViewById(R.id.PreviousWeek);
        this.nextWeek     = (ImageView) 			this.view.findViewById(R.id.NextWeek);
	}
	
	private void setup() {
        if(this.day == null) {
            ArrayList<Lecture> error = new ArrayList<Lecture>
                    (Arrays.asList(new Lecture("Une erreur est survenue"), new Lecture("Veuillez recharger la liste")));
            this.lectureList.setAdapter(new LectureListAdapter(error));

        } else {
            this.displayed = this.manager.getNonBlacklistedLectures(this.day.lectures);
            this.setLectureListAdapter(this.displayed);
        }
	}

    public void updateFragement(Day d) {
        this.day = d;
        this.setup(); this.setTime();
    }
	
	private void setListeners() {
		this.lectureList.setOnRefreshListener(new RefreshListener());
		
		this.previousWeek.setOnTouchListener(new TouchListener(this.previousWeek,
				R.color.date_background, R.color.date_background_pressed));
		
		this.nextWeek.setOnTouchListener(new TouchListener(this.nextWeek,
				R.color.date_background, R.color.date_background_pressed));

        this.lectureList.setOnItemClickListener(new LectureListClickListener());
	}
	
	void setTime() {
        if(day == null) {
            ((TextView) view.findViewById(R.id.Day)).setText("erreur");
            return;
        }
    	String day  = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.FRANCE).format(this.day.date);
    	
    	day = day.substring(0, 1).toUpperCase(Locale.FRANCE) + day.substring(1);
    	
    	((TextView) view.findViewById(R.id.Day)).setText(day);
    }

    public void setLectureListAdapter(List<Lecture> lectures) {
        this.lectureList.setAdapter(new LectureListAdapter(lectures));
    }
	
	private class RefreshListener implements OnRefreshListener {
		@Override
        public void onRefresh() {
            if(EpiTime.getInstance().getCurrentActivity() instanceof DrawerActivity) {
                ((DrawerActivity)EpiTime.getInstance().getCurrentActivity()).noInternetShown = false;
            }
    		DayListFragment.this.manager.requestLectures(true, -1, 0, 1);
        	new GetDataTask().execute();
    	
        }
	}

    private class LectureListClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Activity context = (Activity)EpiTime.getInstance().getCurrentActivity();

            Lecture item = DayListFragment.this.displayed.get(i);

            if(item.isMessage) { return; }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater     = context.getLayoutInflater();

            View v = inflater.inflate(R.layout.dialog_lecture_details, null);

            DayListFragment.this.lectureSelected = item.title;

            if(item.trainee.size() > 1) {
                ((TextView)v.findViewById(R.id.dialog_lecture_group)).setText(context.getResources().getString(R.string.dialog_lecture_groups));
            }

            if(item.room.size() > 1) {
                ((TextView)v.findViewById(R.id.dialog_lecture_room)).setText(context.getResources().getString(R.string.dialog_lecture_rooms));
            }

            ((TextView)v.findViewById(R.id.dialog_lecture_title))  .setText(item.title);
            ((TextView)v.findViewById(R.id.dialog_lecture_teacher)).setText(item.instructor);
            ((TextView)v.findViewById(R.id.dialog_lecture_time))   .setText(item.getBegin() + " - " + item.getEnd());
            ((TextView)v.findViewById(R.id.dialog_lecture_group))  .setText(item.getTrainee(", "));
            ((TextView)v.findViewById(R.id.dialog_lecture_room))   .setText(item.getRoom(", "));

            builder.setView(v);
            builder.setPositiveButton("Ok", null);
            builder.setNeutralButton("Ignorer ce cours", new onBlacklistClick());

            AlertDialog lecturesDetailDialog = builder.create();
            lecturesDetailDialog.show();



            lecturesDetailDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(Color.parseColor("#282828"));
            lecturesDetailDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#ffffff"));

            lecturesDetailDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setBackgroundColor(Color.parseColor("#282828"));
            lecturesDetailDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#ffffff"));

        }
    }

    private class onBlacklistClick implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            EpiTime.getInstance().getScheduleManager().addToBlackList(DayListFragment.this.lectureSelected);

            Calendar cal = Calendar.getInstance(Locale.FRANCE); cal.setTime(DayListFragment.this.day.date);
            DayListFragment.this.manager.updateWidget(cal);

            DayListFragment.this.updateFragement(DayListFragment.this.day);
            ToastMaker.makeToast("Le cours " + DayListFragment.this.lectureSelected + " a été ignoré", 750);

        }
    }

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {
	    
	    @Override
	    protected void onPostExecute(String[] result) {
	    	lectureList.onRefreshComplete();
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
