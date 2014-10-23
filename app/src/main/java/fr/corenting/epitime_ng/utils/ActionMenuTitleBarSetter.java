package fr.corenting.epitime_ng.utils;

import android.view.View;
import android.widget.TextView;

import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.activities.DrawerActivity.DrawerActionBarToggle;

public class ActionMenuTitleBarSetter {
	
	private final DrawerActionBarToggle drawerToggle;
	private final View title;
	
	private String titleDrawerClosed;
    private String titleDrawerOpened;
	
	
	public ActionMenuTitleBarSetter(DrawerActionBarToggle drawerToggle, View title) {
		this.drawerToggle = drawerToggle;
		this.title        = title;
		
		this.titleDrawerOpened = "Choisissez une école";
		this.titleDrawerClosed = "EpiTime";
		
	}
	
	void setTitleBar(String s) {
        ((TextView)title.findViewById(R.id.menu_title)).setText(s);
    }
	
	public void setTitleBar(String drawerOpened, String drawerClosed) {
		this.titleDrawerOpened = drawerOpened;
		this.titleDrawerClosed = drawerClosed;
		
		this.setTitle();
	}
	
	public void setTitleBarClosed(String drawerClosed) {
		this.titleDrawerClosed = drawerClosed;
		
		this.setTitle();
	}
	
	public void setTitleBarOpened(String drawerOpened) {
		this.titleDrawerOpened = drawerOpened;
		this.setTitle();
	}
	
	public void resetTitleBarOpened() {
		this.titleDrawerOpened = "Choisissez une école";
		this.setTitle();
	}
	
	public void resetTitleBarClosed() {
		this.titleDrawerClosed = "EpiTime";
		this.setTitle();
	}
	
	public void resetTitleBar() {
		this.titleDrawerOpened = "Choisissez une école";
		this.titleDrawerClosed = "EpiTime";
		this.setTitle();
	}
	
	public void setTitle() {
		if(this.drawerToggle.isDrawerOpened) { this.setTitleBar(this.titleDrawerOpened); }
		else  								 { this.setTitleBar(this.titleDrawerClosed); }
	}
	
}
