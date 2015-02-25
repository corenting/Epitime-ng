package fr.corenting.epitime_ng.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.data.Lecture;
import fr.corenting.epitime_ng.managers.ScheduleManager;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LectureWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new LectureWidgetFactory(this.getApplicationContext());
    }
}

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class LectureWidgetFactory implements RemoteViewsService.RemoteViewsFactory {


    private Context context;
    private ScheduleManager manager;
    private List<Lecture> lectures;

    public LectureWidgetFactory(Context context) {
        if (EpiTime.getInstance().getCurrentActivity() == null) {
            EpiTime.getInstance().setCurrentActivity(context);
        }
        this.context = context;
        this.manager = EpiTime.getInstance().getScheduleManager();

    }

    @Override
    public void onCreate() {
        this.updateData();
    }

    private void updateData() {
        if (!EpiTime.getInstance().hasInternet()) {
            if (this.manager.hasCache(this.manager.getCurrentWeek(Calendar.getInstance().getTime(), 0), this.manager.getWidgetGroup())) {
                this.loadLectures();
            } else {
                this.lectures = new ArrayList<>
                        (Arrays.asList(new Lecture("Pas connexion internet (et pas de cache) !"), new Lecture("Cliquez pour recharger")));
            }
        } else {
            this.loadLectures();
        }

    }

    private void loadLectures() {
        this.manager.requestLectures(false, 0);
        this.lectures = this.manager.getNonBlacklistedLectures(this.manager.getWidgetGroup(), Calendar.getInstance().getTime(), 0);
    }

    @Override
    public void onDataSetChanged() {
        this.updateData();
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        return this.lectures.size();
    }

    @Override
    public RemoteViews getViewAt(int index) {
        Lecture item = this.lectures.get(index);
        return this.getLectureView(item, index);

    }

    private RemoteViews getLectureView(Lecture item, int index) {

        RemoteViews rv = new RemoteViews(this.context.getPackageName(), R.layout.lecture_item);
        if (index % 2 == 0) {
            rv.setInt(R.id.lecture_item_background, "setBackgroundColor", R.color.background_variant);
        }


        if (item.isMessage) {
            rv.setTextViewText(R.id.CourseTitle, item.title);
            rv.setTextViewText(R.id.CourseTime, "");
            rv.setTextViewText(R.id.CourseRoom, "");
        } else {
            rv.setTextViewText(R.id.CourseTitle, item.title);
            rv.setTextViewText(R.id.CourseTime, item.getBegin() + " - " + item.getEnd());
            rv.setTextViewText(R.id.CourseRoom, item.getRoom(", "));
        }

        Intent intent = new Intent();
        intent.putExtra("fromWidget", true);
        rv.setOnClickFillInIntent(R.id.lecture_item_background, intent);

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
