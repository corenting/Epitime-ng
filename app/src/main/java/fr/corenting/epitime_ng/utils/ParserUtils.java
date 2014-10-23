package fr.corenting.epitime_ng.utils;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class ParserUtils {
	
	public static String getContent(Node n) {
		if(n == null) { return ""; }
		return n.getFirstChild().getNodeValue();
	}

    public static List<String> getContent(NodeList n) {
        if(n == null) { return new ArrayList<String>(); }

        List<String> val = new ArrayList<String>();

        for (int i = 0; i < n.getLength(); i++) {
            val.add(n.item(i).getFirstChild().getNodeValue());
        }

        return val;
    }

	public static Node getElement(Element e, String name) {
		NodeList n = e.getElementsByTagName(name);
		if(n.getLength() == 0) { return null; }
		return n.item(0);
	}

    public static NodeList getElements(Element e, String name) {
        NodeList n = e.getElementsByTagName(name);
        if(n.getLength() == 0) { return null; }
        return n;
    }
	
}
