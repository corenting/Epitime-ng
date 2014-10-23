package fr.corenting.epitime_ng.headers;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

public class DrawerListHeader {
	
	private final View layout;
	
	private final View text;
	private final View image;
	public View specialItem; //Can be a button or a progress bar
	
	public DrawerListHeader(LayoutInflater inflator, int layoutId,
				int textId, int imageId, int speclialItemId) {
		
		this.layout = inflator.inflate(layoutId, null);
		
		this.text        = this.layout.findViewById(textId);
		this.image       = this.layout.findViewById(imageId);
		this.specialItem = this.layout.findViewById(speclialItemId);
	}
	
	public void addHeader(ListView list) {
		list.addHeaderView(this.layout);
	}
	
	public void hideHeader() {
		if(this.text        != null) { this.text.       setVisibility(View.GONE); }
		if(this.image       != null) { this.image.      setVisibility(View.GONE); }
		if(this.specialItem != null) { this.specialItem.setVisibility(View.GONE); }
	}
	
	public void showHeader() {
		if(this.text        != null) { this.text.       setVisibility(View.VISIBLE); }
		if(this.image       != null) { this.image.      setVisibility(View.VISIBLE); }
		if(this.specialItem != null) { this.specialItem.setVisibility(View.VISIBLE); }
	}
}
