package fr.corenting.epitime_ng.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
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
import fr.corenting.epitime_ng.tasks.QueryGroups;
import fr.corenting.epitime_ng.tasks.QueryLecturesNewTask;

public class GroupListActivity extends DrawerActivity {

    public String school;


    private ListView         groupList;
    private GroupListAdapter adapter;
    private GroupListHeader  searchHeader;

	GroupListHeader connectingHeader;
	GroupListHeader noInternetHeader;
	
	private InputListener searchInputListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        boolean hasNoGroup = this.getIntent().getExtras().getBoolean("NoGroup");
		this.school     = this.getIntent().getExtras().getString("School");
		this.layout     = R.layout.activity_group_select;
		
		super.onCreate(savedInstanceState);
		EpiTime.getInstance().setCurrentActivity(this);
		
		
		if(hasNoGroup) {
			this.drawerToggle.isDrawerOpened = true;
			this.drawerLayout.openDrawer(Gravity.LEFT);
		}
		
		this.groupList = (ListView)this.findViewById(R.id.group_list);
		this.groupList.setOnItemClickListener(new OnGroupListItemClick());
		
		this.addHeaders(); this.reloadListView();
	}
	
	public void reloadListView() {
		this.searchHeader    .hideHeader();
		this.connectingHeader.hideHeader();
		this.noInternetHeader.hideHeader();
		
		School school = this.groupManager.getSchool(this.school);
		if(school == null || school.groups == null || school.groups.size() == 0) {
			if(QueryGroups.isLoading()) {
				this.connectingHeader.showHeader();

				this.menuTitle.setTitleBarClosed(this.getResources().getString(R.string.loading));
			} else {
				this.noInternetHeader.showHeader();

				this.menuTitle.setTitleBarClosed(this.getResources().getString(R.string.no_internet));
			}
		} else {
			this.searchHeader.showHeader();

			this.menuTitle.setTitleBarClosed(this.school);
		}
		
		this.adapter = new GroupListAdapter(school);
		this.groupList.setAdapter(this.adapter);
		this.searchInputListener.setAdapter(this.adapter);
	}

    @Override
    protected void onResume() {
    	super.onResume();

        EpiTime.getInstance().setCurrentActivity(this);
    }

    private void addHeaders() {

        this.searchHeader = new GroupListHeader(this.getLayoutInflater(),
                R.layout.group_select_list_item_search, R.id.group_select_list_section_short_image,
                R.id.group_select_list_section_input);
        EditText search = (EditText) this.searchHeader.longTitle;

        this.searchInputListener = new InputListener(this.adapter);
        search.addTextChangedListener(this.searchInputListener);

        this.searchHeader.addHeader(this.groupList);



        this.connectingHeader = new GroupListHeader(this.getLayoutInflater(),
                R.layout.group_select_list_item_connecting, R.id.group_select_list_section_spinner,
                R.id.group_select_list_connecting_text);
        this.connectingHeader.setLongTitleText(this.getResources().getString(R.string.loading));
        this.connectingHeader.addHeader(this.groupList);

        this.noInternetHeader = new GroupListHeader(this.getLayoutInflater(),
                R.layout.group_select_list_item_image, R.id.group_select_list_section_short_image,
                R.id.group_select_list_section_text);
        this.noInternetHeader.setLongTitleText(this.getResources().getString(R.string.no_internet));
        this.noInternetHeader.addHeader(this.groupList);
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
		public void afterTextChanged(Editable s) {}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if(this.value.equals(s.toString())) { return; }
			this.value = s.toString();
			this.adapter.getFilter().filter(s);
			
		}
		
	}
	
	private class OnGroupListItemClick implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

            if (position < 2) {
                return;
            }
            if (position == 2) {
                GroupListActivity.this.drawerListNoConnectionHeader.specialItem.performClick();
                return;
            }

            // Header items (Search, connecting, no Internet)
            GroupItem item = (GroupItem) GroupListActivity.this.adapter.getItem(position - 3);
            ScheduleManager manager = EpiTime.getInstance().getScheduleManager();

            manager.setGroup(item.getLongTitle());
            manager.resetCalendar();
            manager.requestLectures(true, -1, 0, 1);

            new GetDataTask().execute();

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
            if(EpiTime.getInstance().getCurrentActivity() instanceof DayList) {
                ((DayList)EpiTime.getInstance().getCurrentActivity()).updateAdapter();
            }
            progressDialog.dismiss();
            super.onPostExecute(result);
            Intent intent = new Intent(GroupListActivity.this, DayList.class);
            startActivity(intent);
            finish();
        }

        @Override
        protected String[] doInBackground(Void... params) {

            while(QueryLecturesNewTask.isRefreshing()) {
            }
            return null;
        }
    }
}
