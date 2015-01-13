package fr.corenting.epitime_ng.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.activities.DayList;
import fr.corenting.epitime_ng.activities.DrawerActivity;
import fr.corenting.epitime_ng.adapters.LectureListAdapter;
import fr.corenting.epitime_ng.data.Day;
import fr.corenting.epitime_ng.data.Lecture;
import fr.corenting.epitime_ng.managers.ScheduleManager;
import fr.corenting.epitime_ng.tasks.QueryLecturesNewTask;
import fr.corenting.epitime_ng.utils.MiscUtils;

import static fr.corenting.epitime_ng.R.id;
import static fr.corenting.epitime_ng.R.layout;
import static fr.corenting.epitime_ng.R.string;

public class DayListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeLayout;
    private ListView lectureList;
    private Day day;
    private List<Lecture> displayed;

    private ScheduleManager manager;
    private ViewGroup view;
    private String lectureSelected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.initMemberVariables(inflater, container);
        this.setup();
        this.setListeners();
        return view;
    }

    private void initMemberVariables(LayoutInflater inflater, ViewGroup container) {
        this.view = (ViewGroup) inflater.inflate(layout.activity_week_list, container, false);
        this.displayed = new ArrayList<Lecture>();
        swipeLayout = (SwipeRefreshLayout) view.findViewById(id.swipe_container);
        swipeLayout.setOnRefreshListener(this);

        this.day = ((Day) getArguments().get("Day"));
        this.manager = EpiTime.getInstance().getScheduleManager();

        this.lectureList = (ListView) this.view.findViewById(id.lectures);
    }

    private void setup() {
        try {
            List<Lecture> errorList = new ArrayList<>
                    (Arrays.asList(new Lecture(getActivity().getString(R.string.list_error)), new Lecture(getActivity().getString(R.string.list_please_reload))));
            if (this.day == null) {
                this.lectureList.setAdapter(new LectureListAdapter(errorList));
            } else {
                this.displayed = this.manager.getNonBlacklistedLectures(EpiTime.getInstance().getScheduleManager().getGroup(), this.day.lectures);
                this.setLectureListAdapter(this.displayed);
            }
        }
        catch (Exception ignored)
        {
        }
    }

    public void updateFragment(Day d) {
        this.day = d;
        this.setup();
    }

    private void setListeners() {
        this.lectureList.setOnItemClickListener(new LectureListClickListener());
    }

    public void setLectureListAdapter(List<Lecture> lectures) {
        this.lectureList.setAdapter(new LectureListAdapter(lectures));
    }

    @Override
    public void onRefresh() {
        if (EpiTime.getInstance().getCurrentActivity() instanceof DrawerActivity) {
            ((DrawerActivity) EpiTime.getInstance().getCurrentActivity()).noInternetShown = false;
        }
        DayListFragment.this.manager.requestLectures(true, -1, 0, 1);
        new GetDataTask().execute();
    }

    private class LectureListClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Activity context = (Activity) EpiTime.getInstance().getCurrentActivity();

            Lecture item = DayListFragment.this.displayed.get(i);

            if (item.isMessage) {
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            DayListFragment.this.lectureSelected = item.title;

            String dialogText = "";
            if (item.instructor != null && item.instructor.length() > 0)
                dialogText = context.getString(string.details_teacher) + item.instructor + "<br />";
            dialogText += context.getString(string.details_schedule) + item.getBegin() + "-" + item.getEnd();
            if (item.trainee.size() > 0)
                dialogText += "<br />" + context.getString(string.details_group) + item.getTrainee(", ");
            if (item.room.size() > 0) {
                dialogText += item.room.size() > 1 ? "<br />" + context.getString(string.details_rooms) + item.getRoom(", ") : "<br />" + context.getString(string.details_room) + item.getRoom(", ");
            }

            builder.setPositiveButton(context.getString(string.ok), null);
            builder.setNeutralButton(context.getString(string.details_ignore_class), new onBlacklistClick());
            builder.setMessage(Html.fromHtml(dialogText));
            builder.setTitle(item.title);

            AlertDialog lecturesDetailDialog = builder.create();
            lecturesDetailDialog.show();
            lecturesDetailDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            lecturesDetailDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            lecturesDetailDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
            lecturesDetailDialog.getButton(DialogInterface.BUTTON_NEUTRAL);

        }
    }

    private class onBlacklistClick implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            EpiTime.getInstance().getScheduleManager().addToBlackList(DayListFragment.this.lectureSelected, EpiTime.getInstance().getScheduleManager().getGroup());

            Calendar cal = Calendar.getInstance(Locale.FRANCE);
            cal.setTime(DayListFragment.this.day.date);
            DayListFragment.this.manager.updateWidget(cal);

            DayListFragment.this.updateFragment(DayListFragment.this.day);
            MiscUtils.makeToast("Le cours " + DayListFragment.this.lectureSelected + " a été ignoré");

        }
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected void onPostExecute(String[] result) {
            if (EpiTime.getInstance().getCurrentActivity() instanceof DayList) {
                ((DayList) EpiTime.getInstance().getCurrentActivity()).updateAdapter();
            }
            swipeLayout.setRefreshing(false);
            super.onPostExecute(result);
        }

        @Override
        protected String[] doInBackground(Void... params) {
            while (QueryLecturesNewTask.isRefreshing()) {
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
