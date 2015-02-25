package fr.corenting.epitime_ng.activities;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.adapters.GroupListAdapter;
import fr.corenting.epitime_ng.data.GroupItem;
import fr.corenting.epitime_ng.data.School;
import fr.corenting.epitime_ng.managers.ScheduleManager;
import fr.corenting.epitime_ng.utils.ThemeUtils;
import fr.corenting.epitime_ng.utils.TinyDB;

public class WidgetConfigActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private int mAppWidgetId;
    private ListView groupList;
    private GroupListAdapter adapter;

    ScheduleManager scheduleManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EpiTime.getInstance().setCurrentActivity(this);
        scheduleManager = EpiTime.getInstance().getScheduleManager();
        super.onCreate(savedInstanceState);
        super.setTheme(ThemeUtils.getTheme(this));
        setContentView(R.layout.activity_widget_config);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle((getString(R.string.Favorites)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ThemeUtils.setStatusBarColor(this);
        }

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        this.groupList = (ListView) this.findViewById(R.id.group_list);
        this.reloadListView();
    }


    public void reloadListView() {
        //Construct group from favorites
        TinyDB tinydb = new TinyDB(this);
        List<String> FavoriteStringList = tinydb.getList(getString(R.string.tinydb_favorites));
        List<GroupItem> favoritesGroups = new ArrayList<>();
        for(String favorite : FavoriteStringList)
        {
            favoritesGroups.add(new GroupItem(favorite));
        }
        School favoritesSchool = new School(getString(R.string.Favorites), favoritesGroups);

        this.adapter = new GroupListAdapter(favoritesSchool);
        this.groupList.setAdapter(this.adapter);
        this.groupList.setOnItemClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final String group = ((GroupItem) adapter.getItem(position)).getLongTitle();
        scheduleManager.setWidgetGroup(group);
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        int resultCode = RESULT_OK;
        setResult(resultCode, resultValue);
        EpiTime.getInstance().updateWidget();
        finish();
    }
}
