package fr.corenting.epitime_ng.utils;

import android.content.Context;
import android.content.SharedPreferences;

import fr.corenting.epitime_ng.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by KingGreed on 08/06/2014.
 */
public class SaveUtils {

    private Context context;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public SaveUtils(Context context) {
        this.context = context;

        pref = context.getSharedPreferences(context.getString(
                R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    private void write() {
        if(this.editor == null) {
            this.editor = pref.edit();
        }
    }

    public void commit() {
        this.editor.commit();
    }

    public void putInt(String key, int val) {
        this.write();
        this.editor.putInt(key, val);
    }

    public void putBoolean(String key, boolean val) {
        this.write();
        this.editor.putBoolean(key, val);
    }

    public void putString(String key, String val) {
        this.write();
        this.editor.putString(key, val);
    }

    public void putList(String key, List<String> val) {
        this.write();
        for (int i = 0; i < val.size(); i++) {
            this.editor.putString(key + i, val.get(i));
        }
    }

    public <E> void put(String key, E val) throws UnsuportedTypeException {
        if(val instanceof Boolean) {
            this.putBoolean(key, (Boolean)val);
        } else if(val instanceof Integer) {
            this.putInt(key, (Integer) val);
        } else if(val instanceof String) {
            this.putString(key, (String) val);
        } else {
            throw new UnsuportedTypeException("Type not supported");
        }

    }

    public void putSet(String key, Set<String> val) {
        this.write();

        int i = 0;
        for(String s : val) {
            this.editor.putString(key + i, s);
            ++i;
        }
    }

    public int readInt(String key, int defaultValue) {
        return this.pref.getInt(key, defaultValue);
    }

    public boolean readBoolean(String key, boolean defaultValue) {
        return this.pref.getBoolean(key, defaultValue);
    }

    public String readString(String key, String defaultValue) {
        return this.pref.getString(key, defaultValue);
    }

    public List<String> readList(String key, String defaultValue, int size) {
        List<String> values = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            values.add(this.readString(key + i, defaultValue));
        }

        return values;
    }

    public Set<String> readSet(String key, String defaultValue, int size) {
        Set<String> values = new HashSet<String>();
        for (int i = 0; i < size; i++) {
            values.add(this.readString(key + i, defaultValue));
        }

        return values;
    }

}

