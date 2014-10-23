package fr.corenting.epitime_ng.managers;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.utils.SaveUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by KingGreed on 12/06/2014.
 */
public class ConfigManager {

    private Map<String, String>  stringConfigs  = new HashMap<String, String>();
    private Map<String, Integer> integerConfigs = new HashMap<String, Integer>();
    private Map<String, Boolean> booleanConfigs = new HashMap<String, Boolean>();

    private static final String TAG = "config_";

/*    public ConfigManager() {

    }*/

    public Integer readInt(String key, Integer defaultValue) {
        if(!this.integerConfigs.containsKey(key)) {
            SaveUtils save = new SaveUtils(EpiTime.getInstance().getCurrentActivity());
            this.integerConfigs.put(key, save.readInt(TAG + key, defaultValue));
        }

        return this.integerConfigs.get(key);
    }

    public Boolean readBoolean(String key, Boolean defaultValue) {
        if(!this.integerConfigs.containsKey(key)) {
            SaveUtils save = new SaveUtils(EpiTime.getInstance().getCurrentActivity());
            this.booleanConfigs.put(key, save.readBoolean(TAG + key, defaultValue));
        }

        return this.booleanConfigs.get(key);
    }

    public String readString(String key, String defaultValue) {
        if(!this.integerConfigs.containsKey(key)) {
            SaveUtils save = new SaveUtils(EpiTime.getInstance().getCurrentActivity());
            this.stringConfigs.put(key, save.readString(TAG + key, defaultValue));
        }

        return this.stringConfigs.get(key);
    }

    public void writeInt(String key, Integer val) {
        SaveUtils save = new SaveUtils(EpiTime.getInstance().getCurrentActivity());
        save.putInt(TAG + key, val); save.commit();
        this.integerConfigs.put(key, val);
    }

    public void writeBoolean(String key, Boolean val) {
        SaveUtils save = new SaveUtils(EpiTime.getInstance().getCurrentActivity());
        save.putBoolean(TAG + key, val); save.commit();
        this.booleanConfigs.put(key, val);
    }

    public void writeString(String key, String val) {
        SaveUtils save = new SaveUtils(EpiTime.getInstance().getCurrentActivity());
        save.putString(TAG + key, val); save.commit();
        this.stringConfigs.put(key, val);
    }

}
