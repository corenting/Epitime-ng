package fr.corenting.epitime_ng.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.data.Lecture;

public class LectureListAdapter extends BaseAdapter {

    private final List<Lecture> lectures;
    private LayoutInflater inflater = null;

    public LectureListAdapter(List<Lecture> lectures) {
        this.lectures = lectures;
        this.inflater = (LayoutInflater) EpiTime.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        View view = convertView == null ? inflater.inflate(R.layout.lecture_item, parent,  false) : convertView;
        Lecture item = this.lectures.get(index);


        if (index % 2 == 0) {
            view.findViewById(R.id.lecture_item_background).setBackgroundColor(view.getResources().getColor(R.color.background_variant));
        }

        if (item.isMessage) {
            ((TextView) view.findViewById(R.id.CourseTitle)).setText(item.title);
            ((TextView) view.findViewById(R.id.CourseTime)).setText("");
            ((TextView) view.findViewById(R.id.CourseRoom)).setText("");
        } else {
            String displayedRooms;
            if (item.room.size() > 4) {
                displayedRooms = item.getRoom(", ", 4) + "...";
            } else {
                displayedRooms = item.getRoom(", ");
            }
            ((TextView) view.findViewById(R.id.CourseTitle)).setText(item.title);
            ((TextView) view.findViewById(R.id.CourseTime)).setText(item.getBegin() + " - " + item.getEnd());
            ((TextView) view.findViewById(R.id.CourseRoom)).setText(displayedRooms);
        }

        return view;
    }

    @Override
    public int getCount() {
        return lectures.size();
    }

    @Override
    public Object getItem(int index) {
        return this.lectures.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}