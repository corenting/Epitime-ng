package fr.corenting.epitime_ng.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.adapters.GroupListAdapter;
import fr.corenting.epitime_ng.data.GroupItem;
import fr.corenting.epitime_ng.data.School;
import fr.corenting.epitime_ng.managers.ScheduleManager;
import fr.corenting.epitime_ng.tasks.QueryLecturesNewTask;
import fr.corenting.epitime_ng.utils.MiscUtils;

public class GroupListActivity extends DrawerActivity implements AdapterView.OnItemLongClickListener, OnItemClickListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    public String school;

    private ListView groupList;
    private GroupListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        boolean hasNoGroup = this.getIntent().getExtras().getBoolean("NoGroup");
        this.school = this.getIntent().getExtras().getString("School");
        this.layout = R.layout.activity_group_list;

        super.onCreate(savedInstanceState);
        EpiTime.getInstance().setCurrentActivity(this);

        if (hasNoGroup) {
            this.drawerToggle.isDrawerOpened = true;
            this.drawerLayout.openDrawer(Gravity.LEFT);
        }

        this.groupList = (ListView) this.findViewById(R.id.group_list);
        if (school.equals(getString(R.string.Favorites))) {
            groupList.setOnItemLongClickListener(this);
        }

        this.reloadListView();
    }


    public void reloadListView() {

        School school = this.groupManager.getSchool(this.school);
        this.menuTitle.setTitleBarClosed(this.school);
        this.adapter = new GroupListAdapter(school);
        this.groupList.setAdapter(this.adapter);
        this.groupList.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EpiTime.getInstance().setCurrentActivity(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        getMenuInflater().inflate(R.menu.group_list, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(false);
        return true;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final String group = ((GroupItem) GroupListActivity.this.adapter.getItem(position)).getLongTitle();
        final ScheduleManager manager = EpiTime.getInstance().getScheduleManager();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format(getString(R.string.dialog_remove_favorite), group))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        manager.removeFavoriteGroup(group);
                        MiscUtils.makeToast(group + getString(R.string.favorite_deleted));
                        dialog.cancel();
                        reloadListView();
                    }
                })
                .setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Header items (Search, connecting, no Internet)
        GroupItem item = (GroupItem) GroupListActivity.this.adapter.getItem(position);
        ScheduleManager manager = EpiTime.getInstance().getScheduleManager();

        manager.setGroup(item.getLongTitle());
        manager.resetCalendar();
        manager.requestLectures(true, -1, 0, 1);

        new GetDataTask().execute();
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return onQueryTextChange(s);
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if(s.toLowerCase().equals("upupdowndownleftrightleftrightba"))
        {
            Intent myIntent = new Intent(GroupListActivity.this, EasterEggActivity.class);
            startActivity(myIntent);
        }
        this.adapter.getFilter().filter(s);
        return true;
    }

    @Override
    public boolean onClose() {
        return false;
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(GroupListActivity.this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (EpiTime.getInstance().getCurrentActivity() instanceof DayListActivity) {
                ((DayListActivity) EpiTime.getInstance().getCurrentActivity()).updateAdapter();
            }
            progressDialog.dismiss();
            super.onPostExecute(result);
            Intent intent = new Intent(GroupListActivity.this, DayListActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        protected String[] doInBackground(Void... params) {

            while (QueryLecturesNewTask.isRefreshing()) {
            }
            return null;
        }
    }
}
