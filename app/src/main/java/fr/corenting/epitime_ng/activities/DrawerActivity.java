package fr.corenting.epitime_ng.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.adapters.DrawerListAdapter;
import fr.corenting.epitime_ng.headers.DrawerListHeader;
import fr.corenting.epitime_ng.managers.GroupManager;
import fr.corenting.epitime_ng.tasks.QueryGroups;
import fr.corenting.epitime_ng.utils.ActionMenuTitleBarSetter;
import fr.corenting.epitime_ng.utils.DialogUtils;

import java.util.List;


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
    private DrawerListHeader drawerListConnectingHeader;
    private List<String> schools;

    public boolean noInternetShown = false;
    
    GroupManager groupManager;
	int layout;
    DrawerLayout drawerLayout;
    ActionMenuTitleBarSetter menuTitle;
    DrawerActionBarToggle drawerToggle;
    DrawerListHeader drawerListNoConnectionHeader;
    
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(this.layout);
		
		EpiTime.getInstance().setCurrentActivity(this);
		this.groupManager = EpiTime.getInstance().getGroupManager();
				
		this.setUp();
		
		        
        this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.drawerList = (ListView) findViewById(R.id.left_drawer);
        
        this.drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
        this.drawerToggle = new DrawerActionBarToggle(this, this.drawerLayout);
        this.drawerLayout.setDrawerListener(this.drawerToggle);
        
        this.menuTitle = new ActionMenuTitleBarSetter(this.drawerToggle,
				getSupportActionBar().getCustomView());
		this.menuTitle.setTitle();
        
        this.addHeaders();
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
		
		this.drawerListConnectingHeader.hideHeader(); this.drawerListNoConnectionHeader.hideHeader();
		
		if(this.schools != null && this.schools.size() != 0) {
			this.resetDividerColor();
			
			if(QueryGroups.isLoading()) {
				this.drawerListConnectingHeader.showHeader();
			}
			
			this.drawerList.setAdapter(new DrawerListAdapter(this.schools));
			
			this.menuTitle.resetTitleBarOpened();
		} else {
			this.setDividerTransparent();
			
			if(QueryGroups.isLoading()) {
				this.drawerListConnectingHeader.showHeader();
				this.menuTitle.setTitleBarOpened(this.getResources().getString(R.string.connecting));
			} else {
				this.drawerListNoConnectionHeader.showHeader();
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
	
	private void addHeaders() {
		this.drawerListConnectingHeader = new DrawerListHeader(this.getLayoutInflater(),
				R.layout.header_item_connecting, R.id.drawer_header_text, 0, R.id.drawer_header_spinner);
		this.drawerListConnectingHeader.addHeader(this.drawerList);
		
		this.drawerListNoConnectionHeader = new DrawerListHeader(this.getLayoutInflater(),
				R.layout.header_item_no_connection, R.id.drawer_header_no_connection_text,
				R.id.drawer_header_no_connection_about, R.id.drawer_header_no_connection_button);
		this.drawerListNoConnectionHeader.addHeader(this.drawerList);
		this.drawerListNoConnectionHeader.specialItem.setOnClickListener(new OnReloadListener());
				
	}
	
	private void setUp() {
    	getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setCustomView(R.layout.menu_title);
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
    	DialogUtils.displaySimpleAlert(this, "Pas de connexion internet", "Nous n'avons pas pu établir une connexion a internet...");
    }
    
    public void chronosError() {
    	DialogUtils.displaySimpleAlert(this, "Erreur Chronos", "Les données transmises par Chronos sont incorrecte (ou l'API a été modifiée)");
    }

    public void badUrl() {
        DialogUtils.displaySimpleAlert(this, "Erreur EpiTime", "L'URL fournit est incorrecte");
    }

    public void ChronosNotUpToDateYet() {
        DialogUtils.displaySimpleAlert(this, "Erreur Chronos", "Chronos n'a toujours pas mis à jour l'emploi du temps !");
    }

    // Set lectures to error iaoi
    public void setError() {

    }

    public void noInternetConnexion(final String groupFailed) {
        this.drawerListNoConnectionHeader.showHeader();

        String message = "Nous n'avons pas pu établir une connexion à internet...\n";
        if(groupFailed.equals("trainnees")) {
            message += "Nous nous donc pas pu récupérer la liste des groupes étudiants";
        } else if(groupFailed.equals("instructors")) {
            message += "Nous nous donc pas pu récupérer la liste du groupe enseignants";
        } else if(groupFailed.equals("rooms")) {
            message += "Nous nous donc pas pu récupérer la liste des salles";
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message + "\n")
                .setTitle("Pas de connexion internet")
                .setInverseBackgroundForced(true)
                .setPositiveButton("Recharger", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new QueryGroups().execute(groupFailed);
                        DrawerActivity.this.reloadDrawerList();
                        if (EpiTime.getInstance().getCurrentActivity() instanceof GroupListActivity) {
                            GroupListActivity context = (GroupListActivity) EpiTime.getInstance().getCurrentActivity();
                            context.reloadListView();
                        }
                    }
                }).setNegativeButton("Annuler", null).create().show();

    }
	
	private class OnReloadListener implements OnClickListener {
		
		@Override
		public void onClick(View v) {
			DrawerActivity.this.drawerListConnectingHeader  .showHeader();
			DrawerActivity.this.drawerListNoConnectionHeader.hideHeader();
			
			if(EpiTime.getInstance().hasInternet()) {
				DrawerActivity.this.groupManager.getGroups();
			} else {
				
				if(DrawerActivity.this instanceof GroupListActivity) {
					GroupListActivity context = (GroupListActivity) DrawerActivity.this;
					context.connectingHeader.showHeader();
					context.noInternetHeader.hideHeader();
				}
				
				Handler handler   = new Handler ();
		    	Runnable runnable = new Runnable() { 
		    		public void run() { 
			        	 DrawerActivity.this.drawerListConnectingHeader  .hideHeader();
			        	 DrawerActivity.this.drawerListNoConnectionHeader.showHeader();
			        	 
			        	 if(DrawerActivity.this instanceof GroupListActivity) {
			        		 GroupListActivity context = (GroupListActivity) DrawerActivity.this;
			        		 context.connectingHeader.hideHeader();
			        		 context.noInternetHeader.showHeader();
						}
			         } 
			    }; 

				handler.postDelayed(runnable, 450);
			}
			
		}
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	
        	if(position == 0 || position == 1) { return ; }
        	
        	String schoolName = DrawerActivity.this.schools.get(position - 2);
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
