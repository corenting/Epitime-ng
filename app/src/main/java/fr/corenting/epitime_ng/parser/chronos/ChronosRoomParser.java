package fr.corenting.epitime_ng.parser.chronos;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.data.GroupItem;
import fr.corenting.epitime_ng.data.School;
import fr.corenting.epitime_ng.utils.ParserUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class ChronosRoomParser {
	
	private final School result;
	
	public ChronosRoomParser() {
		this.result = new School(EpiTime.getInstance().getString(R.string.Rooms), new ArrayList<GroupItem>());
	}
	
	public School parse(Document xml) {
		Node root = xml.getFirstChild();
        exploreNodes(root, 1, null);
        return this.result;
	}
	
	void exploreNodes(Node root, int depth, String parent) {
        NodeList childs = root.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
            exploreNode(childs.item(i), depth, parent);
        }
    }

    void exploreNode(Node n, int depth, String parent) {
        NodeList childs = n.getChildNodes();

        String name = null;
        boolean isLeaf = true;


        for (int i = 0; i < childs.getLength(); i++) {
            Node item = childs.item(i);
            if(item.getNodeName().equals("name")) {
                name = ParserUtils.getContent(item);
                if(depth == 1) { parent = name.substring(0, 2); }
            } else if(item.getNodeName().equals("nodes")) {
                isLeaf = false;
                exploreNodes(item, depth + 1, parent);
            }
        }

        if(isLeaf && name != null) {
        	int color = (int)parent.toUpperCase().charAt(0) + (int)parent.toUpperCase().charAt(1);
        	this.result.groups.add(new GroupItem(parent.toUpperCase(), name, color));
        }
    }
}
