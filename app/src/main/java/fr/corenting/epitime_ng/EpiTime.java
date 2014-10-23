package fr.corenting.epitime_ng;

import android.annotation.TargetApi;
import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import fr.corenting.epitime_ng.managers.GroupManager;
import fr.corenting.epitime_ng.managers.ScheduleManager;
import fr.corenting.epitime_ng.widget.EpitimeWidgetProvider;

@SuppressWarnings("SameParameterValue")
public class EpiTime extends Application {
	
	public static final String TAG = "EpiTime";

	private static EpiTime instance;
	private Context context;
	
	private ScheduleManager scheduleManager;
	private GroupManager groupManager;
	
	@Override
	public void onCreate() {
		EpiTime.instance = this;

		this.scheduleManager = new ScheduleManager();
		this.groupManager    = new GroupManager();

		super.onCreate();
	}
	
	public Context getCurrentActivity() {
		return context;
	}

	public void setCurrentActivity(Context currentActivity) {
		this.context = currentActivity;
	}

	public ScheduleManager getScheduleManager() {
		return this.scheduleManager;
	}
	
	public GroupManager getGroupManager() {
		return this.groupManager;
	}
	
	public static EpiTime getInstance() {
		return instance;
	}
	
	public boolean hasInternet() {
		if(this.context == null) { return false; }
		ConnectivityManager connMgr = (ConnectivityManager) this.context.getSystemService(
				Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void updateWidget() {

        if(!context.getResources().getBoolean(R.bool.is_honeycomb)) { return; }

        Intent intent = new Intent(this.context, EpitimeWidgetProvider.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");

        int ids[] = AppWidgetManager.getInstance(this.context).getAppWidgetIds(new ComponentName(this.context, EpitimeWidgetProvider.class));
        AppWidgetManager.getInstance(this.context).notifyAppWidgetViewDataChanged(ids, R.id.widget_lecture_list);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        this.context.sendBroadcast(intent);
    }
}
