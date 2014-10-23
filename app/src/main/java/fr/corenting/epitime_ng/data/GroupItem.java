package fr.corenting.epitime_ng.data;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class GroupItem implements Serializable, Parcelable, Comparable {

	private static final long serialVersionUID = 1L;
	private String shortTitle;
	private String longTitle;
	
	private int shortColor;
	private int shortColorShadow;

    private static final FlatUIColor generator = new FlatUIColor(42);

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.shortTitle);
		dest.writeString(this.longTitle);
		
		dest.writeInt(this.shortColor);
		dest.writeInt(this.shortColorShadow);
	}
	
	public static final Parcelable.Creator<GroupItem> CREATOR = new Parcelable.Creator<GroupItem>() {
        public GroupItem createFromParcel(Parcel in) {
            return new GroupItem(in);
        }

        public GroupItem[] newArray(int size) {
            return new GroupItem[size];
        }
    };
	
    private GroupItem(Parcel in) {
    	this.shortTitle = in.readString();
		this.longTitle = in.readString();
		
		this.setShortColor(in.readInt());
		this.setShortColorShadow(in.readInt());
    }

    @SuppressWarnings("unused")
    public GroupItem(String longTitle, String shortColor, String shortColorShadow) {
    	this.shortTitle = "";
		this.longTitle = longTitle;

        generator.nextRandomColor();

		this.setShortColor(shortColor);
		this.setShortColorShadow(shortColorShadow);
    }

    public GroupItem(String longTitle) {
        this.setShortTitle("");
        this.setLongTitle(longTitle);

        generator.nextRandomColor();

		this.setShortColor(generator.getColor());
		this.setShortColorShadow(generator.getShadowColor());
    }
    
    public GroupItem(String longTitle, int color) {
        this.setShortTitle("");
        this.setLongTitle(longTitle);

        generator.setRandomColor(color);

        this.setShortColor(generator.getColor());
        this.setShortColorShadow(generator.getShadowColor());
    }
    
    public GroupItem(String shortTitle, String longTitle, int color) {
        this.setShortTitle(shortTitle);
        this.setLongTitle(longTitle);

        generator.setRandomColor(color);

        this.setShortColor(generator.getColor());
        this.setShortColorShadow(generator.getShadowColor());
    }


	public GroupItem(String shortTitle, String longTitle) {
        this.setShortTitle(shortTitle);
        this.setLongTitle(longTitle);

        generator.nextRandomColor();

        this.setShortColor(generator.getColor());
        this.setShortColorShadow(generator.getShadowColor());
	}

    @SuppressWarnings("unused")
	public GroupItem(String shortTitle, String longTitle, String shortColor, String shortColorShadow) {
        this.setShortTitle(shortTitle);
        this.setLongTitle(longTitle);
		
		this.setShortColor(shortColor);
		this.setShortColorShadow(shortColorShadow);
	}

    @SuppressWarnings("unused")
    public static void newRandomSeedColor() {
        generator.newRandomSeed();
    }

    public static void newSeedColor(int seed) {
        generator.newSeed(seed);
    }

	public String getShortTitle()    { return this.shortTitle;       }
	public String getLongTitle()     { return this.longTitle;        }
    public int getShortColor()       { return this.shortColor;       }
    public int getShortColorShadow() { return this.shortColorShadow; }

    public void setShortTitle(String shortTitle)              { this.shortTitle       = shortTitle;                   }
    public void setLongTitle(String longTitle)                { this.longTitle        = longTitle;                    }
    public void setShortColor(int shortColor)                 { this.shortColor       = shortColor;                   }
    public void setShortColor(String shortColor)              { this.shortColor       = Color.parseColor(shortColor); }
    public void setShortColorShadow(Integer shortColorShadow) { this.shortColorShadow = shortColorShadow;             }
    public void setShortColorShadow(String shortColor)        { this.shortColorShadow = Color.parseColor(shortColor); }

    @Override
    public int compareTo(Object another) {
        if(another instanceof GroupItem) {
            return this.longTitle.compareTo(((GroupItem) another).getLongTitle());
        }
        return -1;
    }
}
