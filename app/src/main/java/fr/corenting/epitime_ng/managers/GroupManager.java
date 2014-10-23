package fr.corenting.epitime_ng.managers;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.activities.DrawerActivity;
import fr.corenting.epitime_ng.activities.GroupListActivity;
import fr.corenting.epitime_ng.data.School;
import fr.corenting.epitime_ng.tasks.QueryGroups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class GroupManager {
	
	private final Map<String, School> groups = new HashMap<String, School>();
	private final List<String> schoolNames = new ArrayList<String>();
	
	public GroupManager() {
	}
		
	public List<String> getSchools() {
		return schoolNames;
	}
	
	public School getSchool(String name) {
		return this.groups.get(name);
	}
	
	public void setGroup(List<School> group) {

        for (School aGroup : group) {
            this.groups.put(aGroup.name, aGroup);

            if (!this.schoolNames.contains(aGroup.name)) {
                this.schoolNames.add(aGroup.name);
            }

        }
		this.reloadListViews();
        EpiTime.getInstance().updateWidget();
    }
	
	public void getGroups() {
		new QueryGroups().execute("trainnees");
		new QueryGroups().execute("instructors");
		new QueryGroups().execute("rooms");
		this.reloadListViews();
	}
	
	public void reloadListViews() {
		if (EpiTime.getInstance().getCurrentActivity() instanceof DrawerActivity) {
			DrawerActivity context = (DrawerActivity) EpiTime.getInstance().getCurrentActivity();
			context.reloadDrawerList();
		}
		
		if (EpiTime.getInstance().getCurrentActivity() instanceof GroupListActivity) {
			GroupListActivity context = (GroupListActivity) EpiTime.getInstance().getCurrentActivity();
			context.reloadListView();
		}
	}
}
