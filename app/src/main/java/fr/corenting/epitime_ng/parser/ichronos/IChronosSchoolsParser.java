package fr.corenting.epitime_ng.parser.ichronos;

import fr.corenting.epitime_ng.data.GroupItem;
import fr.corenting.epitime_ng.data.School;
import fr.corenting.epitime_ng.utils.ParserUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class IChronosSchoolsParser {

    private final List<School> result;

    public IChronosSchoolsParser() {
        this.result = new ArrayList<School>();
    }

    public List<School> parse(Document xml) {
        this.exploreRoot(xml.getFirstChild());
        return this.result;
    }

    void exploreRoot(Node root) {
        NodeList schools = root.getChildNodes();

        for (int i = 0; i < schools.getLength(); ++i) {
            Node nd = schools.item(i);
            if(nd.getNodeName().equals("node")) {
                String name = ParserUtils.getContent(ParserUtils.getElement((Element) nd, "name"));
                School school = new School(name, new ArrayList<GroupItem>());
                this.result.add(school);
                exploreNode(nd, name, school);
            }
        }
    }

    void exploreNodes(Node nd, String parent, School school) {
        NodeList nodes = nd.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            if(nodes.item(i).getNodeName().equals("node")) {
                exploreNode(nodes.item(i), parent, school);
            }
        }
    }

    void exploreNode(Node nd, String parent, School school) {
        NodeList childs = nd.getChildNodes();
        String nodeName = null;
        boolean hasChild = false;

        for (int i = 0; i < childs.getLength(); ++i) {
            Node item = childs.item(i);
            if(item.getNodeName().equals("name")) {
                nodeName = ParserUtils.getContent(item);
            } else if(item.getNodeName().equals("nodes")) {
                hasChild = true;
                if(parent != null && (parent.equals("IPSA") || parent.equals("EARTSUP") || countChildNodes(item) > 2)) {
                    parent = nodeName;
                }
                exploreNodes(item, parent, school);
            }
        }

        if(!hasChild) {
        	String shortText = getShortText(nodeName, parent);
        	int color = this.valueOf(parent);
            school.groups.add(new GroupItem(shortText, nodeName, color));
        }
    }
    
    private int valueOf(String s) {
    	int val = 0;
    	for(int i = 0; i < s.length(); ++i) {
    		val += (int)s.charAt(i);
    	}
    	return val;
    }

    int countChildNodes(Node nd) {
        NodeList nodes = nd.getChildNodes();
        int count = 0;
        for (int i = 0; i < nodes.getLength(); ++i) {
            if(nodes.item(i).getNodeName().equals("node")) {
                ++count;
            }
        }
        return count;
    }
    
    String getShortText(String name, String parent) {
        parent = parent.trim();
        if(parent.equals("ASSO"))                        { return "AS";   }
        if(parent.equals("Erasmus & Exchange Students")) { return "ER"; }
        if(parent.equals("INTERNATIONAL MASTERS"))       { return "IM"; }
        if(parent.equals("EPITA/MAJEURES"))              { return "MJ"; }
        if(parent.equals("ADM"))                         { return "AD";   }
        if(parent.equals("IONIS-STM"))                   { return "IS";   }

        if(name.equals("API"))                           { return "AP";   }
        if(name.equals("DIVERS/INFO"))                   { return "DI";   }
        if(name.equals("API"))                           { return "AP";   }
        if(name.equals("API"))                           { return "AP";   }


        String[] regex = {"INFOS\\w\\wS(\\d|\\w)#(.*)", "INFOS\\w\\w(.*)-(.*)", "INFOS\\w\\w(.+)", ".*GR(.{2}).*", ".*DIVERS.*", "APPING (\\d)",
                          "TECH(\\d\\w).*", "TECH(\\d).*", ".*Aéro(\\d).*", ".*(#\\d).*", ".*PS\\d(\\d).*", ".*BIOTECH(\\d).*", "(\\d\\w)/EARTSUP",
                          "(\\d)/EARTSUP"};
        String[] group = {"#$1", "$1$2", "$1", "$1", "DV", "A$1", "$1", "T$1", "A$1", "41", "P$1", "B$1", "$1", "$1 "};

        for (int i = 0; i < regex.length; ++i) {
            if(name.matches(regex[i])) {
                String out = (name.replaceAll(regex[i], group[i]));
                if(out.length() == 1) { return out + " "; }
                return out.length() <= 2 ? out : out.substring(0, 2);
            }
        }

        return "";
    }

}
