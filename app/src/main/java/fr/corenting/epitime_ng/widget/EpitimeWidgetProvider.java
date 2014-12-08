package fr.corenting.epitime_ng.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.utils.ThemeUtils;
import fr.corenting.epitime_ng.activities.StartActivity;
import fr.corenting.epitime_ng.managers.ScheduleManager;

/**
 * Created by KingGreed on 03/06/2014.
 */
public class EpitimeWidgetProvider extends AppWidgetProvider {

    private ScheduleManager manager;
    private Calendar today;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        if(this.manager == null) {
            this.manager = EpiTime.getInstance().getScheduleManager();
        }
        if(EpiTime.getInstance().getCurrentActivity() == null) {
            EpiTime.getInstance().setCurrentActivity(context);
        }

        if(today == null || Calendar.getInstance().get(Calendar.DAY_OF_YEAR) != today.get(Calendar.DAY_OF_YEAR)
                || Calendar.getInstance().get(Calendar.YEAR) != today.get(Calendar.YEAR)) {

            today = Calendar.getInstance();
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_lecture_list);
        }


        // update each of the widgets with the remote adapter
        for (int appWidgetId : appWidgetIds) {



            Intent intent = new Intent(context, LectureWidgetService.class);
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_lecture_list);


            String day = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.FRANCE).format(this.manager.getDay());
            day = day.substring(0, 1).toUpperCase(Locale.FRANCE) + day.substring(1);

            rv.setTextViewText(R.id.widget_lecture_group_title, this.manager.getGroup().equals(ScheduleManager.defaultGroup) ?
                    "Pas de groupe" : this.manager.getGroup());
            rv.setTextViewText(R.id.widget_lecture_day, day);

            //Set theme
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            String pref = sp.getString("widgetTheme", "Blue");
            rv.setInt(R.id.widget_lecture_day,"setBackgroundResource", ThemeUtils.getWidgetDateBackground(pref));
            rv.setInt(R.id.menu_title_layout,"setBackgroundResource", ThemeUtils.getWidgetGroupBackground(pref));
            rv.setInt(R.id.widget_lecture_group_title,"setBackgroundResource", ThemeUtils.getWidgetGroupBackground(pref));


            rv.setRemoteAdapter(appWidgetId, R.id.widget_lecture_list, intent);
            Intent configIntent = new Intent(context, StartActivity.class);
            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);

            rv.setPendingIntentTemplate(R.id.widget_lecture_list, configPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
