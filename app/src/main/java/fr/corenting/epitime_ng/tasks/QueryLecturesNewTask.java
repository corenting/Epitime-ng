package fr.corenting.epitime_ng.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.activities.DayList;
import fr.corenting.epitime_ng.activities.DrawerActivity;
import fr.corenting.epitime_ng.data.Day;
import fr.corenting.epitime_ng.managers.ScheduleManager;
import fr.corenting.epitime_ng.parser.chronos.ChronosLectureParser;
import fr.corenting.epitime_ng.utils.FileUtils;
import fr.corenting.epitime_ng.utils.InternetUtils;
import fr.corenting.epitime_ng.utils.UrlUtils;

public class QueryLecturesNewTask extends AsyncTask<Object, Void, String> {

    private static final String TAG = EpiTime.TAG + "::QueryLecturesTask";
    private static DocumentBuilder documentBuilder;
    private static int isRefreshing = 0;

    private final ScheduleManager scheduleManager;
    private final String group;
    private final int week;
    private List<Day> result;

    static {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        try {
            documentBuilder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }


    public QueryLecturesNewTask(Context c, ScheduleManager manager, int week, String group) {
        super();
        this.scheduleManager = manager;
        this.week = week;
        this.group = group;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        QueryLecturesNewTask.isRefreshing++;
    }

    @Override
    protected String doInBackground(Object... params) {
        try {
            this.result = run(this.week, this.group);
        } catch (MalformedURLException e) {
            return "BadUrl";
        } catch (ParseException e) {
            return "ParseException";
        } catch (SAXException e) {
            return "SAXException";
        } catch (IOException e) {
            return "IOException";
        }
        return "Ok";
    }

    @Override
    protected void onPostExecute(String resultMessage) {
        QueryLecturesNewTask.isRefreshing--;

        this.scheduleManager.fetchingWeek.delete(this.week);

        if (resultMessage.equals("Ok")) {

            Calendar cal = Calendar.getInstance(Locale.FRANCE);
            cal.setTime(ScheduleManager.getWeek(week));

            for (Day d : result) {
                d.date = cal.getTime();
                this.scheduleManager.setLectures(group, cal.getTime(), d);
                cal.add(Calendar.DAY_OF_YEAR, 1);
            }

            if (EpiTime.getInstance().getCurrentActivity() instanceof DayList) {
                ((DayList) EpiTime.getInstance().getCurrentActivity()).updateAdapter();
            }
        } else {
            if (EpiTime.getInstance().getCurrentActivity() instanceof DrawerActivity) {
                Log.e(TAG, "An error occured while retrieving the lecture list : " + resultMessage);
                DrawerActivity context = (DrawerActivity) EpiTime.getInstance().getCurrentActivity();
                if (resultMessage.equals("BadUrl")) {
                    //This shouldn't happen in production (because it means the coded url
                    //or url in the resource isn't good (as in syntax error not api error)
                } else if (resultMessage.equals("ParseException") || resultMessage.equals("SAXException")) {
                    context.chronosError();
                } else if (resultMessage.equals("IOException")) {
                    context.noInternetConnexion();
                }

            }
        }
    }

    private List<Day> run(int week, String group) throws ParseException, IOException, SAXException {
        return new ChronosLectureParser().parse(retrieveXML(week, group));
    }


    private static Document retrieveXML(int week, String group) throws IOException, SAXException {

        InputStream input = InternetUtils.getFromInternet(UrlUtils.makeLectureStringUrl(week, group));
        input = FileUtils.save(input, FileUtils.makeLecturesFilename(week, group));

        return documentBuilder.parse(input);
    }

    public static Document retrieveXMLFromFile(int week, String group) throws SAXException, IOException {

        InputStream is = FileUtils.getFromFile(FileUtils.makeLecturesFilename(week, group));
        return documentBuilder.parse(is);
    }

    public static boolean isRefreshing() {
        return isRefreshing > 0;
    }
}
