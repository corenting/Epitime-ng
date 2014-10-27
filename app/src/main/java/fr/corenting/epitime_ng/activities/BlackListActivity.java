package fr.corenting.epitime_ng.activities;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

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
                R.layout.group_select_list_item_image, R.id.group_select_list_section_short_image,
                R.id.group_select_list_section_text);
        this.noBlacklistHeader.setLongTitleText(getString(R.string.no_blacklisted_class));
        this.noBlacklistHeader.addHeader(this.blacklist);
        if(EpiTime.getInstance().getScheduleManager().getBlacklist().size() > 0) {
            this.noBlacklistHeader.hideHeader();
        }

        GroupListHeader disableToastHeader = new GroupListHeader(this.getLayoutInflater(),
                R.layout.group_select_list_item_checkbox, R.id.group_select_list_section_checkbox_layout,
                R.id.group_select_list_connecting_text);
        disableToastHeader.setLongTitleText(getString(R.string.activate_toast));

        ((CheckBox) disableToastHeader.getLayout().findViewById(R.id.group_select_list_checkbox))
                .setChecked(EpiTime.getInstance().getScheduleManager().getHasToastActive());
        ((CheckBox) disableToastHeader.getLayout().findViewById(R.id.group_select_list_checkbox))
                .setOnCheckedChangeListener(new CheckToastChangedListener());

        disableToastHeader.addHeader(this.blacklist);

        if(EpiTime.getInstance().getScheduleManager().getBlacklist().size() == 0) {
            disableToastHeader.hideHeader();
        }

    }

    private class CheckToastChangedListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            EpiTime.getInstance().getScheduleManager().setHasToastActive(isChecked);
            if(isChecked) {
                Toast.makeText(EpiTime.getInstance().getCurrentActivity(), getString(R.string.toasts_activated), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EpiTime.getInstance().getCurrentActivity(), getString(R.string.toast_desactivated), Toast.LENGTH_SHORT).show();
            }
        }
    }


}
