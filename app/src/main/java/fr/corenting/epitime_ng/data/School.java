package fr.corenting.epitime_ng.data;

import java.util.List;

public class School {
	public final String name;
	public final List<GroupItem> groups;
	
	public School(String name, List<GroupItem> groups) {
		this.name   = name;
		this.groups = groups;
	}
		
}
