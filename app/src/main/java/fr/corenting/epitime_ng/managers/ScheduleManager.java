package fr.corenting.epitime_ng.managers;

import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.widget.Toast;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.activities.DrawerActivity;
import fr.corenting.epitime_ng.data.Day;
import fr.corenting.epitime_ng.data.Lecture;
import fr.corenting.epitime_ng.parser.chronos.ChronosLectureParser;
import fr.corenting.epitime_ng.tasks.QueryLecturesNewTask;
import fr.corenting.epitime_ng.utils.FileUtils;
import fr.corenting.epitime_ng.utils.SaveUtils;

import org.w3c.dom.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ScheduleManager {

    private Set<String> lectureBlacklisted = new HashSet<String>();
    private final Calendar selectedDate;
    private final Map<String, SparseArray<Day>> lectures = new HashMap<String, SparseArray<Day>>();
	private static final Date FIRST_WEEK;

    public int offset = 0;
    public ConfigManager configs;
    public final SparseBooleanArray fetchingWeek;
    public static final String defaultGroup = "NotAGroup";

	static {
		Calendar cal = Calendar.getInstance(Locale.FRANCE);
		if (cal.get(Calendar.MONTH) < Calendar.SEPTEMBER) {
			cal.add(Calendar.YEAR, -1);
		}

		cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
		cal.set(Calendar.DAY_OF_MONTH, 1);

        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
		
		FIRST_WEEK = cal.getTime();
    	
	}
	
	public ScheduleManager() {
        this.configs      = new ConfigManager();
		this.selectedDate = Calendar.getInstance(Locale.FRANCE);
		this.fetchingWeek = new SparseBooleanArray();

	}

    public void load() {
        this.readBlacklist();
    }

    public void setIsTeacher     (boolean value) { this.configs.writeBoolean("isTeacher", value);      }
	public void setGroup         (String group)  { this.configs.writeString("group", group);           }
    public void setHasToastActive(boolean value) { this.configs.writeBoolean("hasToastActive", value); }

    public String getGroup()           { return this.configs.readString("group", defaultGroup);   }
    public boolean getIsTeacher()      { return this.configs.readBoolean("isTeacher", false);     }
    public boolean getHasToastActive() { return this.configs.readBoolean("hasToastActive", true); }


    public void addToCalendar(int days) {
		this.offset += days;
		this.selectedDate.add(Calendar.DATE, days);

        int week = getCurrentWeek(this.selectedDate.getTime());
        if(week >= 52 || week < 0) {
            this.offset -= days;
            this.selectedDate.add(Calendar.DATE, -days);
        }

    }

	public void resetCalendar() {
		this.addToCalendar(-this.offset);
	}

	public static int getCurrentWeek(Date selectedDate) {
    	return selectedDate.getTime() - FIRST_WEEK.getTime() > 0 ?
                (int) ((selectedDate.getTime() - FIRST_WEEK.getTime()) / (7 * 24 * 60 * 60 * 1000)) + 1 :
                -1;
    }

    public static  Date getWeek(int num) {
        Calendar cal = Calendar.getInstance(Locale.FRANCE);
        cal.setTime(FIRST_WEEK);

        cal.add(Calendar.WEEK_OF_YEAR, num - 1);
        return cal.getTime();
    }

    public int getCurrentWeek(Date selectedDate, int offset) {
        Calendar cal = Calendar.getInstance(Locale.FRANCE);
        cal.setTime(selectedDate); cal.add(Calendar.DATE, offset);
        return getCurrentWeek(cal.getTime());
    }

	public Day getLectures(String group, Calendar cal) {
		SparseArray<Day> days = this.lectures.get(group);
		return days == null ? null : days.get(cal.get(Calendar.DAY_OF_YEAR));
	}


    @SuppressWarnings("unused")
    public Day getLectures(String group, Date date) {
		Calendar cal = Calendar.getInstance(Locale.FRANCE);
		cal.setTime(date);

		return this.getLectures(group, cal);
	}

    public Day getLectures(String group, Date date, int offset) {
		Calendar cal = Calendar.getInstance(Locale.FRANCE);
		cal.setTime(date); cal.add(Calendar.DATE, offset);

		return this.getLectures(group, cal);
	}

    public void setLectures(String group, Calendar cal, Day value) {
        this.updateWidget(cal);
		SparseArray<Day> days = this.lectures.get(group);
		if (days == null) {
			this.lectures.put(group, days = new SparseArray<Day>());
		}

		days.put(cal.get(Calendar.DAY_OF_YEAR), value);
	}

	public void setLectures(String group, Date date, Day value) {

		Calendar cal = Calendar.getInstance(Locale.FRANCE);
		cal.setTime(date);

		this.setLectures(group, cal, value);
	}

    @SuppressWarnings("unused")
	public void setLectures(String group, Date date, Integer offset, Day value) {

		Calendar cal = Calendar.getInstance(Locale.FRANCE);
		cal.setTime(date); cal.add(Calendar.DATE, offset);

		this.setLectures(group, cal, value);
	}

	public Date getDay() {
		return this.selectedDate.getTime();
	}

    public void addToBlackList(String lecture) {
        this.lectureBlacklisted.add(lecture);
        this.saveBlackList();
    }

    public boolean isLectureBlacklisted(String lecture) {
        return this.lectureBlacklisted.contains(lecture);
    }

    public void removeFromBlacklist(String lecture) {
        this.lectureBlacklisted.remove(lecture);
        this.saveBlackList();
    }

    public int getBlacklistSize() {
        return this.lectureBlacklisted.size();
    }

    public List<Lecture> getNonBlacklistedLectures(List<Lecture> lectures) {
        List<Lecture> display = new ArrayList<Lecture>();

        for (Lecture l :  lectures) {
            if(!this.isLectureBlacklisted(l)) { display.add(l); }
        }

        if(display.size() == 0) { display.add(new Lecture("Pas de cours")); }

        return display;
    }

    public List<Lecture> getNonBlacklistedLectures(String group, Date date, int offset) {
        Day d = this.getLectures(group, date, offset);
        return d == null ? null : this.getNonBlacklistedLectures(d.lectures);
    }

    public List<String> getBlacklist() {
        List<String> blacklist = new ArrayList<String>();
        for (String s : this.lectureBlacklisted) {
            blacklist.add(s);
        }
        return blacklist;
    }

    public boolean isLectureBlacklisted(Lecture lecture) {
        return this.isLectureBlacklisted(lecture.title);
    }

    private void saveBlackList() {
        SaveUtils save = new SaveUtils(EpiTime.getInstance().getCurrentActivity());

        save.putInt("blacklist length", this.lectureBlacklisted.size());
        save.putSet("blacklist", this.lectureBlacklisted);
        save.commit();
    }

    private void readBlacklist() {

        SaveUtils save = new SaveUtils(EpiTime.getInstance().getCurrentActivity());

        int size = save.readInt("blacklist length", -1); if(size == -1) { return; }
        this.lectureBlacklisted = save.readSet("blacklist", "NotAGroup", size);
    }

	public boolean hasCache(int week, String group) {
		String filename = FileUtils.makeLecturesFilename(week, group);
		File file = EpiTime.getInstance().getCurrentActivity().getFileStreamPath(filename);
		return file.exists();
	}
	
	
	// @return : false if there is no cache
    boolean loadLecturesFromFile(int week, String group) {
		
		try {
			Document xml = QueryLecturesNewTask.retrieveXMLFromFile(week, group);
			if(xml == null) { return false; }
			
			List<Day> days = new ChronosLectureParser().parse(xml);

            Calendar cal = Calendar.getInstance(Locale.FRANCE);
            cal.setTime(getWeek(week));

            for (Day day : days) {
                day.date = cal.getTime();
                this.setLectures(group, cal.getTime(), day);
                cal.add(Calendar.DAY_OF_YEAR, 1);
            }
			
			return true;
			
		} catch(Exception e) {
			Toast.makeText(EpiTime.getInstance(), "Could not load lecture list from file.", Toast.LENGTH_SHORT).show();
		}
		
		return false;
	}

    // Request before get !
	public List<Day> getPreviousCurrentAndNextDay(Date day, String group) {
		
		List<Day> l = new ArrayList<Day>();
        Calendar cal = Calendar.getInstance(Locale.FRANCE);


        for (int i = -1; i <= 1; ++i) {
            cal.setTime(day); cal.add(Calendar.DATE, i);
            Date currentDay = cal.getTime();
            int w = getCurrentWeek(currentDay);
            if(w >= 52) { continue; }

            l.add(this.getLectures(group, day, i));

        }
		return l;
	}
	
	
	public void requestLectures(boolean forceUpdate, Integer... offsets) {
		this.requestLectures(this.getDay(), forceUpdate, offsets);
	}


    // Loads lectures from file if they exists
    // Else gets them from chronos.
    // Note : While lectures are beeing downloaded day's lectures are set to "Loading"
	void requestLectures(Date day, boolean forceUpdate, Integer... offsets) {
		Calendar cal = Calendar.getInstance(Locale.FRANCE);
		HashSet<Integer> weeksToUpdate = new HashSet<Integer>();

        for (Integer offset1 : offsets) {
            //Date operations
            cal.setTime(day); cal.add(Calendar.DATE, offset1);
            Date currentDay = cal.getTime();
            int w = getCurrentWeek(currentDay);

            if(w >= 52 || w < 0) {
                continue;
            }

            if (forceUpdate) {
                weeksToUpdate.add(w);
            }

            boolean hasLecture = this.getLectures(this.getGroup(), cal) != null;
            if (!hasLecture) {

                boolean cache = this.hasCache(w, this.getGroup());
                if (cache) {
                    this.loadLecturesFromFile(w, this.getGroup());
                } else {
                    weeksToUpdate.add(w);
                }
            }
        }
		
		if(!EpiTime.getInstance().hasInternet()) {
			if(EpiTime.getInstance().getCurrentActivity() instanceof DrawerActivity) {
				DrawerActivity context = (DrawerActivity)EpiTime.getInstance().getCurrentActivity();
				context.noInternetConnexion();
			}
            for (Integer week : weeksToUpdate) {
                this.setWeekTo(week, this.getGroup(), makeNoInternetDay(getWeek(week)));
            }
			return;
		}
        for (Integer week : weeksToUpdate) {
            if (this.fetchingWeek.get(week)) {
                continue;
            }

            this.setWeekTo(week, this.getGroup(), makeLoadingDay(getWeek(week)));
            this.fetchingWeek.put(week, true);
            new QueryLecturesNewTask(this, week, this.getGroup()).execute();
        }
	}


	public static Day makeLoadingDay(Date date) {
		ArrayList<Lecture> loading = new ArrayList<Lecture>
    	(Arrays.asList(new Lecture("En cours de chargement !", true)));

		return new Day(date, loading);
	}

    public static Day makeNoInternetDay(Date date) {
        ArrayList<Lecture> loading = new ArrayList<Lecture>
                (Arrays.asList(new Lecture("Connection internet requise !", true)));

        return new Day(date, loading);
    }

    public void updateWidget(Calendar cal) {
        Calendar today = Calendar.getInstance();

        if(cal.get(Calendar.DAY_OF_YEAR) != today.get(Calendar.DAY_OF_YEAR)
        || cal.get(Calendar.YEAR) != today.get(Calendar.YEAR)) {
            return;
        }

        EpiTime.getInstance().updateWidget();

    }

    // Returns 3 days with message loading
    // Date of these days are as follows : [date - 1 day; date; date + 1 day]
    public static List<Day> makeLoadingDays(Date date) {
        Calendar cal = Calendar.getInstance(); cal.setTime(date);
        cal.add(Calendar.DATE, -1);

        List<Day> days = new ArrayList<Day>();
        days.add(makeLoadingDay(cal.getTime())); cal.add(Calendar.DATE, 1);
        days.add(makeLoadingDay(cal.getTime())); cal.add(Calendar.DATE, 1);
        days.add(makeLoadingDay(cal.getTime()));

        return days;
    }

    // Returns 3 days with message no internet
    // Date of these days are as follows : [date - 1 day; date; date + 1 day]
    @SuppressWarnings("unused")
    public static List<Day> makeNoInternetDays(Date date) {
        Calendar cal = Calendar.getInstance(); cal.setTime(date);
        cal.add(Calendar.DATE, -1);

        List<Day> days = new ArrayList<Day>();
        days.add(makeNoInternetDay(cal.getTime())); cal.add(Calendar.DATE, 1);
        days.add(makeNoInternetDay(cal.getTime())); cal.add(Calendar.DATE, 1);
        days.add(makeNoInternetDay(cal.getTime()));

        return days;
    }

    public void setWeekTo(int week, String group, Day message) {
        Calendar cal = Calendar.getInstance(Locale.FRANCE);

        for(int i = 0; i < 7; ++i) {
            cal.setTime(getWeek(week));
            cal.add(Calendar.DAY_OF_YEAR, i);
            message.date = cal.getTime();

            if(this.getLectures(group, cal) == null) {
                this.setLectures(group, cal, message.clone());
            }
        }

    }
}

