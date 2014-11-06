package fr.corenting.epitime_ng.managers;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.utils.TinyDB;

public class BlacklistManager {

    Dictionary<String, List<String>> values;
    List<String> keysList;
    TinyDB tinyDB;

    public BlacklistManager() {
        tinyDB = new TinyDB(EpiTime.getInstance());
        keysList = tinyDB.getList("blacklist_keys_list");
        values = tinyDB.getBlacklistDictionnary(keysList);
    }

    public List<String> getBlacklistedLectures(String group) {
        List<String> ret = values.get(group);
        return ret != null ? ret : new LinkedList<String>();
    }


    public int getSize(String group) {
        try {
            return values.get(group).size();
        } catch (Exception e) {
            return 0;
        }
    }

    public void addBlacklistedLecture(String group, String lecture) {
        List<String> blacklistedLectures = getBlacklistedLectures(group);
        if (blacklistedLectures == null) {
            blacklistedLectures = new LinkedList<String>();

        }
        blacklistedLectures.add(lecture);
        values.remove(group);
        values.put(group, blacklistedLectures);
        updateKeysList(group);
    }

    public void saveBlacklist() {
        tinyDB.putBlacklistDictionnary(values);
    }

    public void removeBlacklistedLecture(String group, String lecture) {
        List<String> blacklistedLectures = getBlacklistedLectures(group);
        blacklistedLectures.remove(lecture);
        values.remove(group);
        values.put(group, blacklistedLectures);
        updateKeysList(group);
    }

    public boolean isBlacklisted(String group, String lecture) {
        try {
            return values.get(group).contains(lecture);
        } catch (Exception e) {
            return false;
        }
    }

    private void updateKeysList(String group) {
        if (!keysList.contains(group)) keysList.add(group);
        tinyDB.putList("blacklist_keys_list", new ArrayList<String>(keysList));
    }
}
