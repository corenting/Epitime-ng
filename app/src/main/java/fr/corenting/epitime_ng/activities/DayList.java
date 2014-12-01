package fr.corenting.epitime_ng.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;
import java.util.List;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.adapters.ViewPagerAdapter;
import fr.corenting.epitime_ng.data.Day;
import fr.corenting.epitime_ng.managers.ScheduleManager;
import fr.corenting.epitime_ng.utils.MiscUtils;


public class DayList extends DrawerActivity {

    private int pageIndex = 0;

    private ScheduleManager manager;
    private ViewPagerAdapter adapter;

    private int pagerScrollState = ViewPager.SCROLL_STATE_IDLE;
    private PageViewerListener pageChangeListener;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.layout = R.layout.page_viewer;
        super.onCreate(savedInstanceState);

        EpiTime.getInstance().setCurrentActivity(this);

        this.initMemberVariables();
        this.setListeners();

        this.setUp();

        this.menuTitle.setTitleBarClosed(this.manager.getGroup());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.day_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initMemberVariables() {
        this.manager = EpiTime.getInstance().getScheduleManager();
        this.pager = (ViewPager) this.findViewById(R.id.lectures_viewpager);

        this.initAdapter();
    }

    private void initAdapter() {
        this.adapter = new ViewPagerAdapter(this.getSupportFragmentManager(), ScheduleManager.makeLoadingDays(getApplicationContext(), new Date()));
    }

    private void setListeners() {
        this.pageChangeListener = new PageViewerListener();
        this.pager.setOnPageChangeListener(this.pageChangeListener);
    }

    private void setUp() {
        this.pager.setAdapter(this.adapter);
        this.pager.setOffscreenPageLimit(2);
        this.manager.requestLectures(true, -1, 0, 1);

    }

    @Override
    protected void onResume() {
        super.onResume();
        EpiTime.getInstance().setCurrentActivity(this);

        this.refreshPageViewer(true);
        if (!EpiTime.getInstance().hasInternet()) {
            this.noInternetConnexion();
        }
        this.pageChangeListener.toastShown = false;
        this.makeBlacklistInfoToast(1);
    }

    @SuppressWarnings("SameParameterValue")
    void refreshPageViewer(boolean force) {
        this.manager.requestLectures(force, -1, 0, 1);
        this.updateAdapter();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem favoriteButton = menu.getItem(0);

        if (manager.isFavoriteGroup(manager.getGroup())) {

            favoriteButton.setIcon(getResources().getDrawable(R.drawable.ic_action_important));

        } else {
            favoriteButton.setIcon(getResources().getDrawable(R.drawable.ic_action_not_important));
        }

        groupManager.getGroups();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_blacklist:
                this.startActivity(new Intent(this, BlackListActivity.class));
                return true;
            case R.id.menu_item_settings:
                this.startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.menu_item_favorite:
                this.onMenuItemFavoriteCLick();
                return true;
            case R.id.menu_item_previous_week:
                this.dayChanged(-7);
                return true;
            case R.id.menu_item_next_week:
                this.dayChanged(+7);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onMenuItemFavoriteCLick() {
        String group = this.manager.getGroup();

        if (manager.isFavoriteGroup(group)) {
            manager.removeFavoriteGroup(group);
            MiscUtils.makeToast(group + getString(R.string.favorite_deleted));
        } else {
            manager.addFavoriteGroup(group);
            MiscUtils.makeToast(group + getString(R.string.favorite_added));
        }
        invalidateOptionsMenu();
    }

    void dayChanged(int offset) {
        if (offset == 0) {
            return;
        }
        this.manager.addToCalendar(offset);

        this.manager.requestLectures(false, 1, 0, -1);
        this.refreshPageViewer(true);

    }

    void updateAdapter(List<Day> days) {
        if (this.pagerScrollState != ViewPager.SCROLL_STATE_IDLE) {
            return;
        }
        this.adapter.setPagesWithDays(days);
        this.adapter.notifyDataSetChanged();
        this.pager.setCurrentItem(1, false);
        this.pageChangeListener.offset = 0;
    }

    public void updateAdapter() {
        this.updateAdapter(
                this.manager.getPreviousCurrentAndNextDay(this.manager.getDay(), this.manager.getGroup())
        );
    }

    public void makeBlacklistInfoToast(int index) {

        if (EpiTime.getInstance().getScheduleManager().getBlacklistSize(EpiTime.getInstance().getScheduleManager().getGroup()) == 0
                || !EpiTime.getInstance().getScheduleManager().getHasToastActive()
                || this.pageChangeListener.toastShown) {
            return;
        }

        int blacklisted = this.adapter.getBlacklistSize(index);

        if (blacklisted == -1) {
            return;
        }
        this.pageChangeListener.toastShown = true;

        if (blacklisted == 1) {
            MiscUtils.makeToast("1 cours a été ignoré");
        } else if (blacklisted > 1) {
            MiscUtils.makeToast(blacklisted + " cours ont été ignorés");
        }

    }


    private class PageViewerListener implements OnPageChangeListener {
        public int offset;
        public boolean toastShown = false;

        public void onPageSelected(int index) {
            this.offset = index - pageIndex;
            pageIndex = index;
            this.toastShown = false;

            DayList.this.makeBlacklistInfoToast(index);
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void onPageScrollStateChanged(int state) {
            DayList.this.pagerScrollState = state;
            if (state != ViewPager.SCROLL_STATE_IDLE) {
                return;
            }
            DayList.this.dayChanged(this.offset);
            this.offset = 0;

        }
    }
}

