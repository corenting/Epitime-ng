package fr.corenting.epitime_ng.headers;

import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import fr.corenting.epitime_ng.adapters.GroupListAdapter;

public class GroupListHeader {
	public final View shortTitle;
	public final View longTitle;
	
	private final View layout;
	
	public GroupListHeader(LayoutInflater inflator, int layoutId, int shortTitleId, int longTitleId) {
		this.layout = inflator.inflate(layoutId, null);
		
		this.shortTitle = this.layout.findViewById(shortTitleId);
		this.longTitle  = this.layout.findViewById(longTitleId);
	}
	
	public void setShortBackground(String shortColor, String shortColorShadow) {
		GroupListAdapter.setBackground((LayerDrawable)this.shortTitle.getBackground(),
					shortColor, shortColorShadow);	
	}
	
	//Sets long title to @text iaoi the longTitle element is a TextView
	public void setLongTitleText(String text) {
		if(this.longTitle instanceof TextView) {
			((TextView)this.longTitle).setText(text);
		}
	}
	
	public void addHeader(ListView list) {
		list.addHeaderView(this.layout);
	}
	
	public void hideHeader() {
		this.shortTitle.setVisibility(View.GONE);
		this.longTitle.setVisibility(View.GONE);
	}
	
	public void showHeader() {
		this.shortTitle.setVisibility(View.VISIBLE);
		this.longTitle.setVisibility(View.VISIBLE);
	}

    public View getLayout() {
        return this.layout;
    }
}
