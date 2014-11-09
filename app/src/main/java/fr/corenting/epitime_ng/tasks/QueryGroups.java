package fr.corenting.epitime_ng.tasks;

import android.content.Context;
import android.os.AsyncTask;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.activities.DrawerActivity;
import fr.corenting.epitime_ng.data.GroupItem;
import fr.corenting.epitime_ng.data.School;
import fr.corenting.epitime_ng.parser.chronos.ChronosRoomParser;
import fr.corenting.epitime_ng.parser.chronos.ChronosSchoolsParser;
import fr.corenting.epitime_ng.parser.chronos.ChronosTeacherParser;
import fr.corenting.epitime_ng.utils.FileUtils;
import fr.corenting.epitime_ng.utils.InternetUtils;
import fr.corenting.epitime_ng.utils.TinyDB;
import fr.corenting.epitime_ng.utils.UrlUtils;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class QueryGroups extends AsyncTask<String, String, String> {

	private List<School> groups;
    private String groupName;

	private static DocumentBuilder documentBuilder;
	private static int instances = 0;
	
	static {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		try {
		    documentBuilder = builderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
		    e.printStackTrace();  
		}
	}
	
	public static boolean isLoading() { return instances > 0; }
	public QueryGroups()              { ++instances; }
	
	@Override
	protected String doInBackground(String... params) {
        this.groupName = params[0];
		this.groups = new ArrayList<School>();

		
		try                    { this.setGroup(this.groupName) ; }
		catch (IOException e)  { return "IOException" ; }
		catch (SAXException e) { return "SAXException"; }

        Context c = EpiTime.getInstance();

        TinyDB tinydb = new TinyDB(c);
        List<String> FavoriteStringList = tinydb.getList(c.getString(R.string.tinydb_favorites));
        List<GroupItem> favoritesGroups = new ArrayList<GroupItem>();
        for(String favorite : FavoriteStringList)
        {
            favoritesGroups.add(new GroupItem(favorite));
        }
        School favoritesSchool = new School(EpiTime.getInstance().getString(R.string.Favorites), favoritesGroups);
        groups.add(0,favoritesSchool);
		
		return "Ok";
	}
	
	@Override
	protected void onPostExecute(String result) {
		--instances;
		if(result.equals("Ok")) {
            EpiTime.getInstance().getGroupManager().setGroup(this.groups);
			return;
		}
		
		EpiTime.getInstance().getGroupManager().reloadListViews();
		if(EpiTime.getInstance().getCurrentActivity() instanceof DrawerActivity) {
			DrawerActivity context = (DrawerActivity)EpiTime.getInstance().getCurrentActivity();
			if(result.equals("IOException")) {
                context.noInternetConnexion(this.groupName);
			} else if(result.equals("SAXException")) {
				context.chronosError();
			}
		}
		
	}
	
	void setGroup(String group) throws IOException, SAXException {
		
		if(!hasGroup(group)) { this.setGroupsFromInternet(group);   }
		else                 { this.setGroupsFromLocalCache(group); }
	}
	
	private static boolean hasGroup(String group) {
		String filename = group + ".xml";
		File file = EpiTime.getInstance().getCurrentActivity().getFileStreamPath(filename);
		return file.exists();
	}
	
	void parseGroup(String group, InputStream is) throws SAXException, IOException {
		Document xml = documentBuilder.parse(is);
		
		if(group.equals("instructors"))    { this.groups.add(new ChronosTeacherParser().parse(xml)); }
		else if(group.equals("rooms"))     { this.groups.add(new ChronosRoomParser().parse(xml)); }
		else if(group.equals("trainnees")) { this.groups.addAll(new ChronosSchoolsParser().parse(xml)); }
	}
	
	private void setGroupsFromInternet(String group) throws IOException, SAXException {
		InputStream is = InternetUtils.getFromInternet(UrlUtils.makeGroupStringUrl(group));
		is = FileUtils.save(is, group + ".xml");
		this.parseGroup(group, is);
	}
	
	private void setGroupsFromLocalCache(String group) throws IOException, SAXException {		
		this.parseGroup(group, FileUtils.getFromFile(group + ".xml"));
	}
	
}
