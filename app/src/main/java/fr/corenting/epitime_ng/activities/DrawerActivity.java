package fr.corenting.epitime_ng.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.adapters.DrawerListAdapter;
import fr.corenting.epitime_ng.managers.GroupManager;
import fr.corenting.epitime_ng.tasks.QueryGroups;
import fr.corenting.epitime_ng.utils.ActionBarTitleSetter;
import fr.corenting.epitime_ng.utils.DialogUtils;
import fr.corenting.epitime_ng.utils.MiscUtils;


/*
 * 
 * You may inherit this class if you ever need to use the drawer
 * Note that the following things are still required :

 * * You need to set this.layout with a layout which has a DrawerLayout and not call setContentView
 * 		and that DrawerLayout needs to have the id drawer_layout. You also need to have a listView 
 * 		with the id left_drawer
 * * You still need to call onCreateOptionsMenu and inflate a menu if you want items
 * 
 * 
 */
public abstract class DrawerActivity extends ActionBarActivity {

	private ListView drawerList;
    private List<String> schools;

    public boolean noInternetShown = false;
    
    GroupManager groupManager;
	int layout;
    DrawerLayout drawerLayout;
    ActionBarTitleSetter menuTitle;
    DrawerActionBarToggle drawerToggle;
    ProgressDialog progressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));

		super.onCreate(savedInstanceState);
        super.setTheme(MiscUtils.getTheme(this));
		setContentView(this.layout);
		
		EpiTime.getInstance().setCurrentActivity(this);
		this.groupManager = EpiTime.getInstance().getGroupManager();
				
		this.setUp();
		
		        
        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.drawerList = (ListView) findViewById(R.id.left_drawer);
        
        this.drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
        this.drawerToggle = new DrawerActionBarToggle(this, this.drawerLayout);
        this.drawerLayout.setDrawerListener(this.drawerToggle);

        this.menuTitle = new ActionBarTitleSetter(getSupportActionBar(), this.drawerToggle,
				getApplicationContext());
		this.menuTitle.setTitle();

        this.reloadDrawerList();
        
        this.drawerList.setOnItemClickListener(new DrawerItemClickListener());
		
	}

    @Override
    protected void onResume() {
        super.onResume();
        EpiTime.getInstance().setCurrentActivity(this);
        this.noInternetShown = false;
    }

	
	public void reloadDrawerList() {
		this.schools = this.groupManager.getSchools();

		this.progressDialog.hide();
		
		if(this.schools != null && this.schools.size() != 0) {
			this.resetDividerColor();
			
			if(QueryGroups.isLoading() && drawerLayout.isDrawerOpen(GravityCompat.START)) {
                progressDialog.show();
			}
			
			this.drawerList.setAdapter(new DrawerListAdapter(this.schools));

			this.menuTitle.resetTitleBarOpened();
		} else {
			this.setDividerTransparent();
			
			if(QueryGroups.isLoading()) {
                if(drawerLayout.isDrawerOpen(GravityCompat.START))
                {
                    progressDialog.show();

                }this.menuTitle.setTitleBarOpened(this.getResources().getString(R.string.loading));
			} else {
                DialogUtils.displaySimpleAlert(getString(R.string.no_internet), getString(R.string.no_internet_msg));
				this.menuTitle.setTitleBarOpened(this.getResources().getString(R.string.no_internet));
			}
			
			this.drawerList.setAdapter(new DrawerListAdapter());
		}
	}
	
	private void setDividerTransparent() {
		this.drawerList.setDivider(new ColorDrawable(this.getResources().getColor(R.color.drawerlist_transparent)));
	}
	
	private void resetDividerColor() {
		this.drawerList.setDivider(new ColorDrawable(this.getResources().getColor(R.color.drawerlist_color)));
	}
	
	private void setUp() {
        ActionBar ac = getSupportActionBar();
    	ac.setDisplayShowTitleEnabled(true);
        ac.setDisplayHomeAsUpEnabled(true);
        ac.setHomeButtonEnabled(true);
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        this.drawerToggle.syncState(); // Sync the toggle state after onRestoreInstanceState has occurred.
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.drawerToggle.onConfigurationChanged(newConfig); // Pass any configuration change to the drawer toggls
    }
	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return this.drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
	
	public void noInternetConnexion() {
        if(this.noInternetShown) { return; }
        this.noInternetShown = true;
    	DialogUtils.displaySimpleAlert(getString(R.string.no_internet), getString(R.string.no_internet_msg));
    }
    
    public void chronosError() {
    	DialogUtils.displaySimpleAlert(getString(R.string.chronos_error), getString(R.string.chronos_incorrect_data));
    }

    public void noInternetConnexion(final String groupFailed) {
        DialogUtils.displaySimpleAlert(getString(R.string.no_internet), getString(R.string.no_internet_msg));

        String message = getString(R.string.no_internet) + "\n";
        if(groupFailed.equals("trainnees")) {
            message += getString(R.string.error_getting_list) + " "  + getString(R.string.students);
        } else if(groupFailed.equals("instructors")) {
            message += getString(R.string.error_getting_list)+ " "  + getString(R.string.teachers);
        } else if(groupFailed.equals("rooms")) {
            message += getString(R.string.error_getting_list) + " "  + getString(R.string.rooms);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message + "\n")
                .setTitle(getString(R.string.no_internet))
                .setInverseBackgroundForced(true)
                .setPositiveButton(getString(R.string.reload), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new QueryGroups().execute(groupFailed);
                        DrawerActivity.this.reloadDrawerList();
                        if (EpiTime.getInstance().getCurrentActivity() instanceof GroupListActivity) {
                            GroupListActivity context = (GroupListActivity) EpiTime.getInstance().getCurrentActivity();
                            context.reloadListView();
                        }
                    }
                }).setNegativeButton(getString(R.string.cancel), null).create().show();

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	
        	String schoolName = DrawerActivity.this.schools.get(position);
        	if(DrawerActivity.this instanceof GroupListActivity) {
        		GroupListActivity context = (GroupListActivity) DrawerActivity.this;
        		if(context.school.equals(schoolName)) {
        			DrawerActivity.this.drawerLayout.closeDrawer(Gravity.LEFT);
        			return ;
        		}
			}
        	
        	Intent destination = new Intent(DrawerActivity.this, GroupListActivity.class);
        	
        	Bundle b = new Bundle();
    		b.putBoolean("NoGroup", false);
    		b.putString("School", schoolName);
    		
        	destination.putExtras(b);
        	startActivity(destination);
        }
    }
	
	public class DrawerActionBarToggle extends ActionBarDrawerToggle {
		public boolean isDrawerOpened = false;

		public DrawerActionBarToggle(Activity activity, DrawerLayout drawerLayout) {
			super(activity, drawerLayout, R.string.drawer_open, R.string.drawer_close);
		}
		
		public void onDrawerClosed(View view) {
			this.isDrawerOpened = false;
			DrawerActivity.this.menuTitle.setTitle();
        }
		
		public void onDrawerOpened(View drawerView) {
			this.isDrawerOpened = true;
			DrawerActivity.this.menuTitle.setTitle();
        }
	}
}
