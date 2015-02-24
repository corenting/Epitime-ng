package fr.corenting.epitime_ng.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.adapters.BlacklistAdapter;
import fr.corenting.epitime_ng.headers.GroupListHeader;
import fr.corenting.epitime_ng.utils.ThemeUtils;

public class BlackListActivity extends ActionBarActivity {

    private ListView blacklist;
    private GroupListHeader noBlacklistHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setTheme(ThemeUtils.getTheme(this));
        EpiTime.getInstance().setCurrentActivity(this);
        setContentView(R.layout.activity_blacklist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.blacklist = (ListView)this.findViewById(R.id.blacklist);
        this.setAdapter();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setAdapter() {
        this.addHeaders();
        this.blacklist.setAdapter(new BlacklistAdapter(this.noBlacklistHeader));
    }

    private void addHeaders() {

        this.noBlacklistHeader = new GroupListHeader(this.getLayoutInflater(),
                R.layout.group_select_list_item,
                R.id.group_select_list_section_long);
        this.noBlacklistHeader.setLongTitleText(getString(R.string.no_blacklisted_class));
        this.noBlacklistHeader.addHeader(this.blacklist);

        if(EpiTime.getInstance().getScheduleManager().getBlacklist(EpiTime.getInstance().getScheduleManager().getGroup()).size() > 0) {
            this.noBlacklistHeader.hideHeader();
        }

    }


}
