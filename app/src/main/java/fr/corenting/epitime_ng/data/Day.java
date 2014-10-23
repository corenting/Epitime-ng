package fr.corenting.epitime_ng.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Day implements Serializable, Parcelable {

	private static final long serialVersionUID = 1L;
	
	public Date date;
	public List<Lecture> lectures = new ArrayList<Lecture>();
	
	public Day(Date date, List<Lecture> lectures) {
		this.date     = date;
		this.lectures = lectures;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dst, int flags) {
		dst.writeValue(this.date);
        dst.writeInt(this.lectures.size());
        for (int i = 0; i < this.lectures.size(); ++i)
            dst.writeValue(this.lectures.get(i));
	}
	
	public static final Parcelable.Creator<Day> CREATOR = new Parcelable.Creator<Day>() {
        public Day createFromParcel(Parcel in) {
            return new Day(in);
        }

        public Day[] newArray(int size) {
            return new Day[size];
        }
    };
    
    private Day(Parcel in) {
        this.lectures = new ArrayList<Lecture>();
        this.date = (Date) in.readValue(Date.class.getClassLoader());
        int size = in.readInt();
        for (int i = 0; i < size; ++i)
            this.lectures.add((Lecture) in.readValue(Lecture.class.getClassLoader()));
    }

    public Day clone() {
        return new Day(this.date, this.lectures);
    }
}
