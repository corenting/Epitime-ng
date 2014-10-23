package fr.corenting.epitime_ng.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DrawerListAdapter extends BaseAdapter {
    
	private final List<String> schools;
	private LayoutInflater inflater = null;
	
	private final String[] flatUiColors = {"#f39c12", "#c0392b", "#8e44ad", "#27ae60", "#2980b9", "#7f8c8d"};
	private static final Random rn = new Random(42);
		private final HashMap<String, Integer> schoolsIcon =  new HashMap<String, Integer>();
    
	public DrawerListAdapter(List<String> schools) {
		this.schools = schools;
		this.inflater = (LayoutInflater)EpiTime.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       
		this.schoolsIcon.put("Enseignants", R.drawable.teacher);
		this.schoolsIcon.put("Salles", R.drawable.room);

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
		this.schools = new ArrayList<String>();
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
	   View view = convertView == null ? inflater.inflate(R.layout.drawer_list_item, null) : convertView;
	   
	   String item = schools.get(position);
	   ((TextView)view.findViewById(R.id.drawer_item_title)).setText(item);
	   
	   int color = Color.parseColor(this.flatUiColors[rn.nextInt(this.flatUiColors.length)]);
	   
	   ImageView icon = (ImageView) view.findViewById(R.id.drawer_icon);
	   
	   if(this.schoolsIcon.containsKey(item)) {
		   icon.setImageResource(this.schoolsIcon.get(item));
	   } else {
		   icon.setImageResource(R.drawable.transparent);
	   }
	   
	   ImageView img = (ImageView) view.findViewById(R.id.drawer_colored_box);
	   GradientDrawable gd = (GradientDrawable) img.getDrawable();

	   PorterDuff.Mode mode = PorterDuff.Mode.SRC_ATOP;
	   gd.setColorFilter(color, mode);
	   
	   
       return view;
   }

}