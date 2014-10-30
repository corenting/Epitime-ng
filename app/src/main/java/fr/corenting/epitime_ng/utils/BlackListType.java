package fr.corenting.epitime_ng.utils;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.LinkedList;
import java.util.List;

import fr.corenting.epitime_ng.EpiTime;

public class BlackListType {

    Dictionary<String, List<String>> values;
    List<String> keysList;
    TinyDB tinyDB;

    public BlackListType()
    {
        tinyDB = new TinyDB(EpiTime.getInstance());
        keysList = tinyDB.getList("blacklist_keys_list");
        values = tinyDB.getBlacklistDictionnary(keysList);
    }

    public List<String> getBlacklistedLectures(String group)
    {
        return values.get(group);
    }



    public int getSize(String group) {
        try
        {
            int ret = values.get(group).size();
            return ret;
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    public void addBlacklistedLecture(String group, String lecture)
    {
        List<String> blacklistedLectures = getBlacklistedLectures(group);
        if(blacklistedLectures == null)
        {
            blacklistedLectures = new LinkedList<String>();

        }
        blacklistedLectures.add(lecture);
        values.remove(group);
        values.put(group, blacklistedLectures);
        updateKeysList(group);
    }

    public void saveBlacklist()
    {
        tinyDB.putBlacklistDictionnary(values);
    }

    public void removeBlacklistedLecture(String group, String lecture)
    {
        List<String> blacklistedLectures = getBlacklistedLectures(group);
        blacklistedLectures.remove(lecture);
        values.remove(group);
        values.put(group, blacklistedLectures);
        updateKeysList(group);
    }

    public boolean isBlacklisted(String group, String lecture)
    {
        try
        {
            return values.get(group).contains(lecture);
        }
        catch (Exception e)
        {
         return  false;
        }
    }

    private void updateKeysList(String group)
    {
        if(!keysList.contains(group)) keysList.add(group);
        tinyDB.putList("blacklist_keys_list", new ArrayList<String>(keysList));
    }
}
