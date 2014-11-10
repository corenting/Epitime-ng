package fr.corenting.epitime_ng.activities;

import android.os.Bundle;
import android.widget.ListView;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.adapters.BlacklistAdapter;
import fr.corenting.epitime_ng.headers.GroupListHeader;

public class BlackListActivity extends DrawerActivity {

    private ListView blacklist;
    private GroupListHeader noBlacklistHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.layout = R.layout.activity_blacklist;
        super.onCreate(savedInstanceState);

        EpiTime.getInstance().setCurrentActivity(this);
        this.menuTitle.setTitleBarClosed(getResources().getString(R.string.blacklist));

        this.blacklist = (ListView)this.findViewById(R.id.blacklist);
        this.setAdapter();
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
