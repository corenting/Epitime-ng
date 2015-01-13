package fr.corenting.epitime_ng.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.adapters.GroupListAdapter;
import fr.corenting.epitime_ng.data.GroupItem;
import fr.corenting.epitime_ng.data.School;
import fr.corenting.epitime_ng.headers.GroupListHeader;
import fr.corenting.epitime_ng.managers.ScheduleManager;
import fr.corenting.epitime_ng.tasks.QueryLecturesNewTask;
import fr.corenting.epitime_ng.utils.MiscUtils;

public class GroupListActivity extends DrawerActivity implements AdapterView.OnItemLongClickListener, OnItemClickListener {

    public String school;
    private boolean hasNoGroup;

    private ListView groupList;
    private GroupListAdapter adapter;
    private GroupListHeader searchHeader;

    private InputListener searchInputListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        hasNoGroup = this.getIntent().getExtras().getBoolean("NoGroup");
        this.school = this.getIntent().getExtras().getString("School");
        this.layout = R.layout.activity_group_select;

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

        this.addHeaders();
        this.reloadListView();
    }

    public void reloadListView() {
        this.searchHeader.hideHeader();

        School school = this.groupManager.getSchool(this.school);
        this.searchHeader.showHeader();
        this.menuTitle.setTitleBarClosed(this.school);
        this.adapter = new GroupListAdapter(school);
        this.groupList.setAdapter(this.adapter);
        this.groupList.setOnItemClickListener(this);
        this.searchInputListener.setAdapter(this.adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EpiTime.getInstance().setCurrentActivity(this);
    }

    private void addHeaders() {
        this.searchHeader = new GroupListHeader(this.getLayoutInflater(),
                R.layout.group_select_list_item_search,
                R.id.group_select_list_section_input);
        EditText search = (EditText) this.searchHeader.longTitle;
        this.searchInputListener = new InputListener(this.adapter);
        search.addTextChangedListener(this.searchInputListener);
        this.searchHeader.addHeader(this.groupList);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final String group = ((GroupItem)GroupListActivity.this.adapter.getItem(position - 1)).getLongTitle();
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
        //Hide the keyboard
        EditText myEditText = (EditText) findViewById(R.id.group_select_list_section_input);
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);

        // Header items (Search, connecting, no Internet)
        GroupItem item = (GroupItem) GroupListActivity.this.adapter.getItem(position - 1);
        ScheduleManager manager = EpiTime.getInstance().getScheduleManager();

        manager.setGroup(item.getLongTitle());
        manager.resetCalendar();
        manager.requestLectures(true, -1, 0, 1);

        new GetDataTask().execute();
    }

    private class InputListener implements TextWatcher {

        private GroupListAdapter adapter;
        public String value;

        public InputListener(GroupListAdapter adapter) {
            super();
            this.adapter = adapter;
            this.value = "";
        }

        public void setAdapter(GroupListAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if (this.value.equals(s.toString())) {
                return;
            }
            this.value = s.toString();
            this.adapter.getFilter().filter(s);

        }

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
            if (EpiTime.getInstance().getCurrentActivity() instanceof DayList) {
                ((DayList) EpiTime.getInstance().getCurrentActivity()).updateAdapter();
            }
            progressDialog.dismiss();
            super.onPostExecute(result);
            Intent intent = new Intent(GroupListActivity.this, DayList.class);
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
