package fr.corenting.epitime_ng.parser.ichronos;

import fr.corenting.epitime_ng.data.Day;
import fr.corenting.epitime_ng.data.Lecture;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class IChronosLectureParser {

    private final List<Day> result;

    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.FRANCE);
    private HashMap<String, Integer> map = new HashMap<String, Integer>();
    private Date currentDay;

    public IChronosLectureParser() {

        map.put("date"   , 0); map.put("begin"  , 1);
        map.put("length" , 2); map.put("id"     , 3);
        map.put("name"   , 4); map.put("group"  , 5);
        map.put("teacher", 6); map.put("room"   , 7);

        this.result = new ArrayList<Day>();
    }

    public List<Day> parse(JSONArray json) throws Exception {
        this.buildLectures(json);
        return result;
    }

    private Day getDay(Date day) {
        for(int i = 0; i < this.result.size(); ++i) {
            if(this.result.get(i).date.equals(day)) {
                return this.result.get(i);
            }
        }

        this.result.add(new Day(day, new ArrayList<Lecture>()));
        return this.result.get(this.result.size() - 1);
    }

    private void buildLectures(JSONArray json) throws Exception {
        if(json == null) { return ; }

        for(int i = 0; i < json.length(); ++i) {
            JSONArray jsonLecture = json.getJSONArray(i);
            Lecture lecture = getLecture(jsonLecture);

            Day d = getDay(this.currentDay);
            d.lectures.add(lecture);
        }
    }


    private Lecture getLecture(JSONArray course) throws Exception {
        if(course == null) { return null; }

        SimpleDateFormat currentDayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE);

        Date   begin, end;
        String title, instructor;

        List<String> rooms, trainees;

        Calendar calendar = Calendar.getInstance();
        Date courseLength;

        title      = course.getString(map.get("name"));
        instructor = course.getString(map.get("teacher"));

        rooms       = Arrays.asList(course.getString(map.get("name")).split("\\s* \\s*"));
        trainees    = Arrays.asList(course.getString(map.get("group")).split("\\s* \\s*"));

        String hour     = course.getString(map.get("begin"));
        String duration = course.getString(map.get("length"));
        String day      = course.getString(map.get("date"));

        day = day.replace("\\", "");
        this.currentDay = currentDayFormat.parse(day);

        day += " " + hour;

        calendar.setTime(format.parse(day));
        begin = calendar.getTime();

        try {
            SimpleDateFormat formatDuration = new SimpleDateFormat("HH'h'mm'min'", Locale.FRANCE);
            courseLength = formatDuration.parse(duration);

        } catch (Exception e) {
            try {
                SimpleDateFormat formatDuration = new SimpleDateFormat("HH'h''", Locale.FRANCE);
                courseLength = formatDuration.parse(duration);
            } catch (Exception e2) {
                try {
                    SimpleDateFormat formatDuration = new SimpleDateFormat("mm'min'", Locale.FRANCE);
                    courseLength = formatDuration.parse(duration);
                } catch (Exception e3) {
                    throw new Exception("Unparsable duration");
                }
            }
        }
        end   = new Date(calendar.getTime().getTime() + courseLength.getTime());

        return new Lecture(begin, end, title, instructor, rooms, trainees);
    }

}
