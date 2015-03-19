package fr.corenting.epitime_ng.managers;

import android.content.Context;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.widget.Toast;

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

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.activities.DrawerActivity;
import fr.corenting.epitime_ng.data.Day;
import fr.corenting.epitime_ng.data.Lecture;
import fr.corenting.epitime_ng.parser.chronos.ChronosLectureParser;
import fr.corenting.epitime_ng.tasks.QueryLecturesNewTask;
import fr.corenting.epitime_ng.utils.FileUtils;
import fr.corenting.epitime_ng.utils.TinyDB;

public class ScheduleManager {

    private final Context context;
    private BlacklistManager lectureBlacklisted = new BlacklistManager();
    private final Calendar selectedDate;
    private final Map<String, SparseArray<Day>> lectures = new HashMap<>();
    private static final Date FIRST_WEEK;

    public int offset = 0;
    private TinyDB tinyDB;
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

    public ScheduleManager(Context c) {
        this.context = c;
        this.tinyDB = new TinyDB(c);
        this.selectedDate = Calendar.getInstance(Locale.FRANCE);
        this.fetchingWeek = new SparseBooleanArray();
    }

    public void setGroup(String group) {
        this.tinyDB.putString("group", group);
    }

    public void setWidgetGroup(String group) {
        this.tinyDB.putString("widgetGroup", group);
    }

    public String getGroup() {
        String group;
        group = this.tinyDB.getString("group");
        return !group.equals("") ? group : defaultGroup;
    }

    public String getWidgetGroup() {
        String group;
        group = this.tinyDB.getString("widgetGroup");
        return !group.equals("") ? group : defaultGroup;
    }

    public boolean getHasToastActive() {
        return this.tinyDB.getBooleanDefaultTrue("hasToastActive");
    }


    public void addToCalendar(int days) {
        this.offset += days;
        this.selectedDate.add(Calendar.DATE, days);

        int week = getCurrentWeek(this.selectedDate.getTime());
        if (week >= 52 || week < 0) {
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

    public static Date getWeek(int num) {
        Calendar cal = Calendar.getInstance(Locale.FRANCE);
        cal.setTime(FIRST_WEEK);

        cal.add(Calendar.WEEK_OF_YEAR, num - 1);
        return cal.getTime();
    }

    public int getCurrentWeek(Date selectedDate, int offset) {
        Calendar cal = Calendar.getInstance(Locale.FRANCE);
        cal.setTime(selectedDate);
        cal.add(Calendar.DATE, offset);
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
        cal.setTime(date);
        cal.add(Calendar.DATE, offset);

        return this.getLectures(group, cal);
    }

    public void setLectures(String group, Calendar cal, Day value) {
        this.updateWidget(cal);
        SparseArray<Day> days = this.lectures.get(group);
        if (days == null) {
            this.lectures.put(group, days = new SparseArray<>());
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
        cal.setTime(date);
        cal.add(Calendar.DATE, offset);

        this.setLectures(group, cal, value);
    }

    public Date getDay() {
        return this.selectedDate.getTime();
    }

    public void addToBlackList(String lecture, String group) {
        this.lectureBlacklisted.addBlacklistedLecture(group, lecture);
        lectureBlacklisted.saveBlacklist();
    }

    public boolean isFavoriteGroup(String group) {
        TinyDB tinyDB = new TinyDB(context);
        List<String> favorites = tinyDB.getList(context.getString(R.string.tinydb_favorites));
        for (String favoritesGroup : favorites) {
            if (favoritesGroup.equals(group)) {
                return true;
            }
        }
        return false;
    }

    public void addFavoriteGroup(String group) {
        TinyDB tinyDB = new TinyDB(context);
        List<String> favorites = tinyDB.getList(context.getString(R.string.tinydb_favorites));
        favorites.add(group);
        tinyDB.putList(context.getString(R.string.tinydb_favorites), new ArrayList<>(favorites));
    }

    public void removeFavoriteGroup(String group) {
        TinyDB tinyDB = new TinyDB(context);
        List<String> favorites = tinyDB.getList(context.getString(R.string.tinydb_favorites));
        favorites.remove(group);
        tinyDB.putList(context.getString(R.string.tinydb_favorites), new ArrayList<>(favorites));
    }

    public boolean isLectureBlacklisted(String lecture, String group) {
        return this.lectureBlacklisted.isBlacklisted(group, lecture);
    }

    public void removeFromBlacklist(String group, String lecture) {
        this.lectureBlacklisted.removeBlacklistedLecture(group, lecture);
        lectureBlacklisted.saveBlacklist();
    }

    public int getBlacklistSize(String group) {
        return this.lectureBlacklisted.getSize(group);
    }

    public List<Lecture> getNonBlacklistedLectures(String group, List<Lecture> lectures) {
        List<Lecture> display = new ArrayList<>();

        for (Lecture l : lectures) {
            if (!this.isLectureBlacklisted(l.title, group)) {
                display.add(l);
            }
        }

        return display;
    }

    public List<Lecture> getNonBlacklistedLectures(String group, Date date, int offset) {
        Day d = this.getLectures(group, date, offset);
        return d == null ? null : this.getNonBlacklistedLectures(group, d.lectures);
    }

    public List<String> getBlacklist(String group) {
        return lectureBlacklisted.getBlacklistedLectures(group);
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
            if (xml == null) {
                return false;
            }

            List<Day> days = new ChronosLectureParser().parse(xml);

            Calendar cal = Calendar.getInstance(Locale.FRANCE);
            cal.setTime(getWeek(week));

            for (Day day : days) {
                day.date = cal.getTime();
                this.setLectures(group, cal.getTime(), day);
                cal.add(Calendar.DAY_OF_YEAR, 1);
            }

            return true;

        } catch (Exception e) {
            Toast.makeText(EpiTime.getInstance(), "Could not load lecture list from file.", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    // Request before get !
    public List<Day> getPreviousCurrentAndNextDay(Date day, String group) {

        List<Day> l = new ArrayList<>();
        Calendar cal = Calendar.getInstance(Locale.FRANCE);


        for (int i = -1; i <= 1; ++i) {
            cal.setTime(day);
            cal.add(Calendar.DATE, i);
            Date currentDay = cal.getTime();
            int w = getCurrentWeek(currentDay);
            if (w >= 52) {
                continue;
            }

            l.add(this.getLectures(group, day, i));

        }
        return l;
    }


    public void requestLectures(boolean forceUpdate, Integer... offsets) {
        this.requestLectures(this.getDay(), forceUpdate, offsets);
    }


    // Loads lectures from file if they exists
    // Else gets them from Chronos.
    // Note : While lectures are being downloaded day's lectures are set to "Loading"
    void requestLectures(Date day, boolean forceUpdate, Integer... offsets) {
        Calendar cal = Calendar.getInstance(Locale.FRANCE);
        HashSet<Integer> weeksToUpdate = new HashSet<>();

        for (Integer offset1 : offsets) {
            //Date operations
            cal.setTime(day);
            cal.add(Calendar.DATE, offset1);
            Date currentDay = cal.getTime();
            int w = getCurrentWeek(currentDay);

            if (w >= 52 || w < 0) {
                continue;
            }

            if (forceUpdate) {
                weeksToUpdate.add(w);
            }

            boolean hasLecture = this.getLectures(this.getGroup(), cal) != null;
            boolean hasLectureWidget = this.getLectures(this.getWidgetGroup(), cal) != null;
            if (!hasLecture) {

                boolean cache = this.hasCache(w, this.getGroup());
                if (cache) {
                    this.loadLecturesFromFile(w, this.getGroup());
                } else {
                    weeksToUpdate.add(w);
                }
            }
            if (!hasLectureWidget) {

                boolean cache = this.hasCache(w, this.getWidgetGroup());
                if (cache) {
                    this.loadLecturesFromFile(w, this.getWidgetGroup());
                } else {
                    weeksToUpdate.add(w);
                }
            }
        }

        if (!EpiTime.getInstance().hasInternet()) {
            if (EpiTime.getInstance().getCurrentActivity() instanceof DrawerActivity) {
                DrawerActivity context = (DrawerActivity) EpiTime.getInstance().getCurrentActivity();
                context.noInternetConnexion();
            }
            for (Integer week : weeksToUpdate) {
                this.setWeekTo(week, this.getGroup(), makeNoInternetDay(context, getWeek(week)));
            }
            return;
        }
        for (Integer week : weeksToUpdate) {
            if (this.fetchingWeek.get(week)) {
                continue;
            }

            this.setWeekTo(week, this.getGroup(), makeLoadingDay(context, getWeek(week)));
            this.fetchingWeek.put(week, true);
            new QueryLecturesNewTask(this, week, this.getGroup()).execute();
            new QueryLecturesNewTask(this, week, this.getWidgetGroup()).execute();
        }
    }



    public static Day makeLoadingDay(Context c, Date date) {
        ArrayList<Lecture> loading = new ArrayList<>
                (Arrays.asList(new Lecture(c.getString(R.string.loading), true)));

        return new Day(date, loading);
    }

    public static Day makeNoInternetDay(Context c, Date date) {
        ArrayList<Lecture> loading = new ArrayList<>
                (Arrays.asList(new Lecture(c.getString(R.string.no_internet), true)));

        return new Day(date, loading);
    }

    public void updateWidget(Calendar cal) {
        Calendar today = Calendar.getInstance();

        if (cal.get(Calendar.DAY_OF_YEAR) != today.get(Calendar.DAY_OF_YEAR)
                || cal.get(Calendar.YEAR) != today.get(Calendar.YEAR)) {
            return;
        }

        EpiTime.getInstance().updateWidget();

    }

    // Returns 3 days with message loading
    // Date of these days are as follows : [date - 1 day; date; date + 1 day]
    public static List<Day> makeLoadingDays(Context c, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -1);

        List<Day> days = new ArrayList<>();
        days.add(makeLoadingDay(c, cal.getTime()));
        cal.add(Calendar.DATE, 1);
        days.add(makeLoadingDay(c, cal.getTime()));
        cal.add(Calendar.DATE, 1);
        days.add(makeLoadingDay(c, cal.getTime()));

        return days;
    }

    // Returns 3 days with message no internet
    // Date of these days are as follows : [date - 1 day; date; date + 1 day]
    @SuppressWarnings("unused")
    public static List<Day> makeNoInternetDays(Context c, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -1);

        List<Day> days = new ArrayList<>();
        days.add(makeNoInternetDay(c, cal.getTime()));
        cal.add(Calendar.DATE, 1);
        days.add(makeNoInternetDay(c, cal.getTime()));
        cal.add(Calendar.DATE, 1);
        days.add(makeNoInternetDay(c, cal.getTime()));

        return days;
    }

    public void setWeekTo(int week, String group, Day message) {
        Calendar cal = Calendar.getInstance(Locale.FRANCE);

        for (int i = 0; i < 7; ++i) {
            cal.setTime(getWeek(week));
            cal.add(Calendar.DAY_OF_YEAR, i);
            message.date = cal.getTime();

            if (this.getLectures(group, cal) == null) {
                this.setLectures(group, cal, message.clone());
            }
        }

    }
}