package fr.corenting.epitime_ng.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.data.Day;
import fr.corenting.epitime_ng.data.Lecture;
import fr.corenting.epitime_ng.fragments.DayListFragment;

import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
	
	private List<Day> pages;
    private DayListFragment[] fragements = new DayListFragment[3];

    public ViewPagerAdapter(FragmentManager fm, List<Day> days) {
        super(fm);
        this.pages = days;
    }
	
	public void setPagesWithDays(List<Day> days) {
		this.pages = days;

        for (int i = 0; i < days.size(); ++i) {
            if(this.fragements[i] == null) {
                this.makeFragment(i, this.pages.get(i));
            } else {
                this.fragements[i].updateFragement(days.get(i));
            }
        }
    }

    private void makeFragment(int position, Day d) {
        this.fragements[position] = new DayListFragment();

        Bundle args = new Bundle();
        args.putParcelable("Day", d);
        args.putInt("DateOffset", position - 1);

        this.fragements[position].setArguments(args);
    }

    public int getBlacklisteSize(int index) {

        if(this.pages.size() <= index) {
            return 0;
        }

        int num = 0;
        if(this.pages.get(index) == null || this.pages.get(index).lectures == null)
            return -1;
        if(this.pages.get(index).lectures.size() == 1 && this.pages.get(index).lectures.get(0).isMessage) {
            return -1; // should not display toast for messages
        }

        for(Lecture l : this.pages.get(index).lectures) {
            if(EpiTime.getInstance().getScheduleManager().isLectureBlacklisted(l)) {
                ++num;
            }
        }

        return num;
    }



    @Override
    public Fragment getItem(int position) {
        if(this.fragements[position] == null) {
            this.makeFragment(position, this.pages.get(position));
        }

        return this.fragements[position];
    }

    @Override
    public int getCount() {
        return this.pages.size();
    }
}
