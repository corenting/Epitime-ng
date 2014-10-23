package fr.corenting.epitime_ng.parser.chronos;

import fr.corenting.epitime_ng.data.Day;
import fr.corenting.epitime_ng.data.Lecture;
import fr.corenting.epitime_ng.utils.ParserUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChronosLectureParser {

    private final List<Day> result;

    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.FRANCE);

    public ChronosLectureParser() {
        this.result = new ArrayList<Day>();
    }

    public List<Day> parse(Document xml) throws ParseException {
        this.buildLectures(xml);
        return result;
    }

    private void buildLectures(Document xml) throws ParseException {
        if(xml == null) { return ; }

        Node root = xml.getFirstChild();
        NodeList nodeList = root.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node n = nodeList.item(i);
            this.result.addAll(getWeek(n));
        }
    }

    private List<Day> getWeek(Node week) throws ParseException {
        if(week == null) { return null; }

        NodeList nodeList = week.getChildNodes();
        List<Day> w       = new ArrayList<Day>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node n = nodeList.item(i);

            if(n.getNodeName().equals("day")) {
                Element day = (Element) n;
                Date date = getDate(day);
                Day d = new Day(date, getDay(day, date));

                w.add(d);
            }
        }

        return w;
    }

    private Date getDate(Element day) throws ParseException {
        if(day == null) { return null; }

        String date = ParserUtils.getContent(ParserUtils.getElement(day, "date"));

        return this.dateFormat.parse(date);

    }

    private List<Lecture> getDay(Element day, Date date) {
        if(day == null) { return null; }

        NodeList nodeList      = day.getElementsByTagName("course");
        List<Lecture> lectures = new ArrayList<Lecture>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node n = nodeList.item(i);
            lectures.add(getLecture((Element)n, date));
        }

        if(lectures.size() == 0) { lectures.add(new Lecture("Pas de cours")); }

        return lectures;
    }

    private Lecture getLecture(Element course, Date day) {
        if(course == null) { return null; }

        Date   begin, end;
        String title, instructor;

        List<String> rooms, trainees;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);

        title      = ParserUtils.getContent(ParserUtils.getElement(course, "title"));
        instructor = ParserUtils.getContent(ParserUtils.getElement(course, "instructor"));

        rooms       = ParserUtils.getContent(ParserUtils.getElements(course, "room"));
        trainees    = ParserUtils.getContent(ParserUtils.getElements(course, "trainee"));

        int hour     = Integer.parseInt(ParserUtils.getContent(ParserUtils.getElement(course, "hour")));
        int duration = Integer.parseInt(ParserUtils.getContent(ParserUtils.getElement(course, "duration")));

        calendar.add(Calendar.MINUTE, 15 * hour);     begin = calendar.getTime();
        calendar.add(Calendar.MINUTE, 15 * duration); end   = calendar.getTime();

        return new Lecture(begin, end, title, instructor, rooms, trainees);
    }

}
