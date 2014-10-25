package fr.corenting.epitime_ng.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.data.GroupItem;
import fr.corenting.epitime_ng.data.School;

import java.util.ArrayList;
import java.util.List;

public class GroupListAdapter extends BaseAdapter {

	private final List<GroupItem> groups;
	private List<GroupItem> groupsSelected;
	
	private LayoutInflater inflater = null;
	private int previousConstraintLenght = -1;
	
	public GroupListAdapter(List<GroupItem> groups) {
		if(groups != null) {
			this.groups = groups;
			this.groupsSelected = groups;
		} else {
			this.groups = new ArrayList<GroupItem>();
			this.groupsSelected = new ArrayList<GroupItem>();
		}
		this.inflater = (LayoutInflater)EpiTime.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public GroupListAdapter(School school) {
		if(school != null) {
			this.groups = school.groups;
			this.groupsSelected = school.groups;
		} else {
			this.groups = new ArrayList<GroupItem>();
			this.groupsSelected = new ArrayList<GroupItem>();
		}
		this.inflater = (LayoutInflater)EpiTime.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return this.groupsSelected.size();
	}

	@Override
	public Object getItem(int position) {
		return this.groupsSelected.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	public Filter getFilter() {
		return new Filter() {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				
				FilterResults result = new FilterResults();
				List<GroupItem> items = this.getFilteredResults(constraint);
				result.values = items;
				result.count  = items.size(); 
				return result;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				groupsSelected = (List<GroupItem>)results.values;
				notifyDataSetChanged();
			}
			
			private List<GroupItem> getFilteredResults(CharSequence constraint) {
				
				List<GroupItem> results = new ArrayList<GroupItem>();
				
				boolean isConstraintLengthIncreasing = (previousConstraintLenght != -1
                        || constraint.length() - previousConstraintLenght > 0);
				
				if(isConstraintLengthIncreasing) {
                    for (GroupItem item : groups) {
                        if (item.getLongTitle().toLowerCase()
                                .matches("(.*)" + constraint.toString().toLowerCase() + "(.*)")) {
                            results.add(item);
                        }
                    }
				} else {
                    for (GroupItem item : groupsSelected) {
                        if (item.getLongTitle().toLowerCase()
                                .matches("(.*)" + constraint.toString().toLowerCase() + "(.*)")) {
                            results.add(item);
                        }
                    }
				}
				
				previousConstraintLenght = constraint.length();
				return results;
			}
			
		};
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView == null ? inflater.inflate(R.layout.group_select_list_item, null) : convertView;
		GroupItem item = this.groupsSelected.get(position);

				
		TextView longTitle = (TextView)view.findViewById(R.id.group_select_list_section_long);
		
		longTitle.setText(item.getLongTitle());

		return view;
	}
	
	public static void setBackground(LayerDrawable ld, String shortColor, String shortColorShadow) {
		setBackground(ld, Color.parseColor(shortColor), Color.parseColor(shortColorShadow));
	}

    //TODO : remove
    public static void setBackground(LayerDrawable ld, int shortColor, int shortColorShadow) {
	}
	
}
