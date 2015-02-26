package fr.corenting.epitime_ng.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;

public class DrawerListAdapter extends BaseAdapter {

    private final List<String> schools;
    private LayoutInflater inflater = null;
    private final HashMap<String, Integer> schoolsIcon = new HashMap<>();

    public DrawerListAdapter(List<String> schools) {
        this.schools = schools;
        this.inflater = (LayoutInflater) EpiTime.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Context context = EpiTime.getInstance();

        this.schoolsIcon.put(context.getString(R.string.Teachers), R.drawable.teacher);
        this.schoolsIcon.put(context.getString(R.string.Rooms), R.drawable.room);
        this.schoolsIcon.put(context.getString(R.string.Favorites), R.drawable.ic_action_important);
        this.schoolsIcon.put("EPITA", R.drawable.epita);
        this.schoolsIcon.put("EPITECH", R.drawable.epitech);
        this.schoolsIcon.put("IPSA", R.drawable.ipsa);
        this.schoolsIcon.put("ISBP", R.drawable.isbp);
        this.schoolsIcon.put("ETNA", R.drawable.etna);
        this.schoolsIcon.put("ASSO", R.drawable.asso);
        this.schoolsIcon.put("IONIS-STM", R.drawable.stm);
        this.schoolsIcon.put("EARTSUP", R.drawable.eartsup);
        this.schoolsIcon.put("ADM", R.drawable.adm);
    }

    public DrawerListAdapter() {
        this.schools = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return this.schools.size();
    }

    @Override
    public Object getItem(int position) {
        return this.schools.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.drawer_list_item, parent, false);
        }

        String item = schools.get(position);
        TextView schoolText = ((TextView) convertView.findViewById(R.id.drawer_item_title));
        schoolText.setText(item);

        if (this.schoolsIcon.containsKey(item)) {
            schoolText.setCompoundDrawablesWithIntrinsicBounds(convertView.getResources().getDrawable(this.schoolsIcon.get(item)), null, null, null);
        }
        return convertView;
    }
}