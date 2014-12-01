package fr.corenting.epitime_ng.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.data.Lecture;
import fr.corenting.epitime_ng.managers.ScheduleManager;
import fr.corenting.epitime_ng.utils.Colors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by KingGreed on 04/06/2014.
 */
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
        if(EpiTime.getInstance().getCurrentActivity() == null) {
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
        if(this.manager.getGroup().equals(ScheduleManager.defaultGroup)) {
            this.lectures = new ArrayList<Lecture>
                    (Arrays.asList(new Lecture(context.getString(R.string.widget_no_group)), new Lecture(context.getString(R.string.widget_touch_to_select))));
        } else {
            if(!EpiTime.getInstance().hasInternet()) {

                if(this.manager.hasCache(this.manager.getCurrentWeek(this.manager.getDay(), -this.manager.offset), this.manager.getGroup())) {
                    this.loadLectures();
                } else {
                    this.lectures = new ArrayList<Lecture>
                            (Arrays.asList(new Lecture("Pas connexion internet (et pas de cache) !"), new Lecture("Cliquez pour recharger")));
                }
            } else {
                this.loadLectures();
            }
        }
    }

    private void loadLectures() {
        this.manager.requestLectures(false, -this.manager.offset);
        this.lectures = this.manager.getNonBlacklistedLectures(this.manager.getGroup(), this.manager.getDay(), -this.manager.offset);
    }

    @Override
    public void onDataSetChanged() {
        this.updateData();
    }

    @Override
    public void onDestroy() {}

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

        if(index % 2 == 0) {
            rv.setInt(R.id.lecture_item_background, "setBackgroundColor", Colors.getBackgroundVariantColor(EpiTime.getInstance()));
        }


        if(item.isMessage) {
            rv.setTextViewText(R.id.CourseTitle, item.title);
            rv.setTextViewText(R.id.CourseTime, "");
            rv.setTextViewText(R.id.CourseRoom, "");
        } else {
            rv.setTextViewText(R.id.CourseTitle, item.title);
            rv.setTextViewText(R.id.CourseTime, item.getBegin() + " - " + item.getEnd());
            rv.setTextViewText(R.id.CourseRoom, item.getRoom(", "));
        }

        Intent intent = new Intent();
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
