package fr.corenting.epitime_ng.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.adapters.BlacklistAdapter;
import fr.corenting.epitime_ng.headers.GroupListHeader;

/**
 * Created by KingGreed on 08/06/2014.
 */
public class BlackListActivity extends DrawerActivity {

    private ListView blacklist;
    private GroupListHeader noBlacklistHeader;
    private GroupListHeader disableToastHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.layout = R.layout.activity_blacklist;
        super.onCreate(savedInstanceState);

        EpiTime.getInstance().setCurrentActivity(this);
        this.menuTitle.setTitleBarClosed("Black List");

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
        this.noBlacklistHeader.setShortBackground("#27ae60", "#1d8046");
        this.noBlacklistHeader.setLongTitleText("Vous n'avez pas encore ignoré de cours");

        ((ImageView)this.noBlacklistHeader.shortTitle).setImageResource(R.drawable.ic_action_about);
        this.noBlacklistHeader.addHeader(this.blacklist);
        if(EpiTime.getInstance().getScheduleManager().getBlacklist().size() > 0) {
            this.noBlacklistHeader.hideHeader();
        }

        this.disableToastHeader = new GroupListHeader(this.getLayoutInflater(),
                R.layout.group_select_list_item_checkbox, R.id.group_select_list_section_checkbox_layout,
                R.id.group_select_list_connecting_text);
        this.disableToastHeader.setShortBackground("#27ae60", "#1d8046");
        this.disableToastHeader.setLongTitleText("Activer les Toasts");

        ((CheckBox)this.disableToastHeader.getLayout().findViewById(R.id.group_select_list_checkbox))
                .setChecked(EpiTime.getInstance().getScheduleManager().getHasToastActive());
        ((CheckBox)this.disableToastHeader.getLayout().findViewById(R.id.group_select_list_checkbox))
                .setOnCheckedChangeListener(new CheckToastChangedListener());

        this.disableToastHeader.addHeader(this.blacklist);

        if(EpiTime.getInstance().getScheduleManager().getBlacklist().size() == 0) {
            this.disableToastHeader.hideHeader();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.blacklist, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_back: this.finish(); return true;
            default: return super.onOptionsItemSelected(item);
        }
    }


    private class CheckToastChangedListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            EpiTime.getInstance().getScheduleManager().setHasToastActive(isChecked);
            if(isChecked) {
                Toast.makeText(EpiTime.getInstance().getCurrentActivity(), "Les Toasts ont été activé !", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EpiTime.getInstance().getCurrentActivity(), "Les Toasts ont été désactivé !", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
