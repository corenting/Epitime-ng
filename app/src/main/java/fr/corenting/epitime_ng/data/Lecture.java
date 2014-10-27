package fr.corenting.epitime_ng.data;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Lecture implements Serializable {
	

	private static final long serialVersionUID = 1L;

	private final Date begin;
    private final Date end;
	
	public final String title;
	public final String instructor;
	public final List<String> room;
	public final List<String> trainee;
	
	public final boolean isMessage;
    public final boolean isLoading;
	
	public Lecture(Date begin, Date end, String title, String instructor, List<String> room, List<String> trainee) {
		this.begin      = begin;
		this.end        = end;
		this.title      = title;
		this.instructor = instructor;
		this.room       = room;
		this.trainee    = trainee;
		this.isMessage  = false;
        this.isLoading  = false;
	}
	
	public Lecture(String title) {
		this.isMessage = true;
		this.title     = title;
		
		this.begin      = null;
		this.end        = null;
		this.instructor = null;
		this.room       = null;
		this.trainee    = null;
        this.isLoading  = false;
	}

    @SuppressWarnings("unused")
    public Lecture(String title, boolean isLoading) {
        this.isMessage = true;
        this.title     = title;

        this.begin      = null;
        this.end        = null;
        this.instructor = null;
        this.room       = null;
        this.trainee    = null;
        this.isLoading  = true;
    }
	
	public String getBegin() {
		DateFormat dateFormat = new SimpleDateFormat("HH'h'mm", Locale.FRANCE);
		return dateFormat.format(this.begin);
	}
	
	public String getEnd() {
		DateFormat dateFormat = new SimpleDateFormat("HH'h'mm", Locale.FRANCE);
		return dateFormat.format(this.end);
	}

    public String getTrainee(String delimiter) { return this.getTrainee(delimiter, this.trainee.size());  }

    public String getRoom(String delimiter) { return this.getRoom(delimiter, this.room.size());  }

    public String getTrainee(String delimiter, int size) {
        String value = "";

        for (int i = 0; i < size && i < this.trainee.size(); i++) {
            value += delimiter + this.trainee.get(i);
        }

        return value.length() == 0 ? value : value.substring(1);
    }

    public String getRoom(String delimiter, int size) {
        String value = "";

        for (int i = 0; i < size && i < this.room.size(); i++) {
            value += delimiter + this.room.get(i);
        }
        return value.length() == 0 ? value.trim() : value.substring(1).trim();
    }
}