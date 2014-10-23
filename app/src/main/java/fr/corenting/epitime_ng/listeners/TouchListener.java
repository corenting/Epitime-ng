package fr.corenting.epitime_ng.listeners;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import fr.corenting.epitime_ng.EpiTime;

public class TouchListener implements OnTouchListener {
	
	private final View v;
	private final Context context;
	private final int backgroundId;
    private final int backgroundPressedId;
	
	@SuppressWarnings("SameParameterValue")
    public TouchListener(View view, int backgroundId, int backgroundPressedId) {
		super();
		
		this.v = view;
		this.backgroundId        = backgroundId;
		this.backgroundPressedId = backgroundPressedId;
		
		this.context = EpiTime.getInstance().getCurrentActivity();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
        		this.v.setBackgroundColor(Color.parseColor(
        				this.context.getResources().getString(this.backgroundId)));
        		this.v.performClick();
            break;
            default:
        		this.v.setBackgroundColor(Color.parseColor(
        				this.context.getResources().getString(this.backgroundPressedId)));
    	}
        return true;
	}
}