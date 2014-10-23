package fr.corenting.epitime_ng.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.data.Lecture;

import java.util.List;

public class LectureListAdapter extends BaseAdapter {
    
    private final List<Lecture> lectures;
    private LayoutInflater inflater = null;
    
    public LectureListAdapter(List<Lecture> lectures) {
        this.lectures = lectures;
        this.inflater = (LayoutInflater)EpiTime.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        if(EpiTime.getInstance().getScheduleManager().getIsTeacher()) {
            return inflateTeachers(index, convertView);
        } else {
            return inflateStudents(index, convertView);
        }
    }

    private View inflateStudents(int index, View convertView) {
        View view = convertView == null ? inflater.inflate(R.layout.lecture_item, null) : convertView;
        Lecture item = this.lectures.get(index);

        if(index % 2 == 0) {
            view.findViewById(R.id.lecture_item_background).setBackgroundColor(Color.parseColor("#2c333d"));
        }

        if(item.isMessage) {
            ((TextView)view.findViewById(R.id.CourseTitle)    ).setText(item.title);
            ((TextView)view.findViewById(R.id.CourseTimeBegin)).setText("");
            ((TextView)view.findViewById(R.id.CourseTime)     ).setText("");
            ((TextView)view.findViewById(R.id.CourseTimeEnd)  ).setText("");
            ((TextView)view.findViewById(R.id.CourseRoom)     ).setText("");
        } else {
            ((TextView)view.findViewById(R.id.CourseTitle)    ).setText(item.title);
            ((TextView)view.findViewById(R.id.CourseTimeBegin)).setText(item.getBegin());
            ((TextView)view.findViewById(R.id.CourseTimeEnd)  ).setText(item.getEnd());
            ((TextView)view.findViewById(R.id.CourseRoom)     ).setText(item.getRoom(1) + (item.room.size() > 1 ? "..." : ""));
        }

        return view;
    }

   private View inflateTeachers(int index, View convertView) {
       View view = convertView == null ? inflater.inflate(R.layout.lecture_item_teacher, null) : convertView;
       Lecture item = this.lectures.get(index);

       if(index % 2 == 0) {
           view.findViewById(R.id.lecture_item_background).setBackgroundColor(Color.parseColor("#2c333d"));
       }

       if(item.isMessage) {
           ((TextView)view.findViewById(R.id.CourseTitle)    ).setText(item.title);
           ((TextView)view.findViewById(R.id.CourseTimeBegin)).setText("");
           ((TextView)view.findViewById(R.id.CourseTime)     ).setText("");
           ((TextView)view.findViewById(R.id.CourseTimeEnd)  ).setText("");
           ((TextView)view.findViewById(R.id.CourseRoom)     ).setText("");
           ((TextView)view.findViewById(R.id.Course)         ).setText("");
       } else {
           ((TextView)view.findViewById(R.id.CourseTitle)    ).setText(item.getTrainee("\n"));
           ((TextView)view.findViewById(R.id.CourseTimeBegin)).setText(item.getBegin());
           ((TextView)view.findViewById(R.id.CourseTimeEnd)  ).setText(item.getEnd());
           ((TextView)view.findViewById(R.id.CourseRoom)     ).setText(item.getRoom(1) + (item.room.size() > 1 ? "..." : ""));
           ((TextView)view.findViewById(R.id.Course)         ).setText(item.title);
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