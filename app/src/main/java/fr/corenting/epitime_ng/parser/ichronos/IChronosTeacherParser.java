package fr.corenting.epitime_ng.parser.ichronos;

import fr.corenting.epitime_ng.data.GroupItem;
import fr.corenting.epitime_ng.data.School;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class IChronosTeacherParser {
	public School parse(Document xml) {
		NodeList nodeList = xml.getElementsByTagName("name");
		
		List<GroupItem> groups = new ArrayList<GroupItem>();
		
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node n = nodeList.item(i);
			String name = n.getFirstChild().getNodeValue();
			groups.add(new GroupItem(name, (int)name.toUpperCase().charAt(0)));
		}
		
		return new School("Enseignants", groups);
	}
}
