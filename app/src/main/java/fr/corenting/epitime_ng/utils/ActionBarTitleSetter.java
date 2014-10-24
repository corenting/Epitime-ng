package fr.corenting.epitime_ng.utils;

import android.content.Context;
import android.support.v7.app.ActionBar;

import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.activities.DrawerActivity.DrawerActionBarToggle;

public class ActionBarTitleSetter {

    private final DrawerActionBarToggle drawerToggle;
    private final Context context;
    private ActionBar actionBar;

    private String titleDrawerClosed;
    private String titleDrawerOpened;


    public ActionBarTitleSetter(ActionBar actionBar, DrawerActionBarToggle drawerToggle, Context c) {
        this.drawerToggle = drawerToggle;
        this.context        = c;
        this.actionBar = actionBar;

        this.titleDrawerOpened = "Choisissez une école";
        this.titleDrawerClosed = "EpiTime";

    }

    void setTitleBar(String s) {
        actionBar.setTitle(s);
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
        this.titleDrawerOpened = context.getString(R.string.choose_school);
        this.setTitle();
    }

    public void setTitle() {
        if(this.drawerToggle.isDrawerOpened) { this.setTitleBar(this.titleDrawerOpened); }
        else  								 { this.setTitleBar(this.titleDrawerClosed); }
    }

}
