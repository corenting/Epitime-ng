package fr.corenting.epitime_ng.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import fr.corenting.epitime_ng.EpiTime;
import fr.corenting.epitime_ng.R;
import fr.corenting.epitime_ng.data.GroupItem;
import fr.corenting.epitime_ng.headers.GroupListHeader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KingGreed on 08/06/2014.
 */
public class BlacklistAdapter extends BaseAdapter implements View.OnClickListener {

    private LayoutInflater inflater = null;
    private List<String> lecturesBlacklisted;
    private List<GroupItem> items;
    private GroupListHeader noBlacklistHeader;

    public BlacklistAdapter(GroupListHeader noBlacklistHeader) {
        this.lecturesBlacklisted = EpiTime.getInstance().getScheduleManager().getBlacklist(EpiTime.getInstance().getScheduleManager().getGroup());
        this.inflater = (LayoutInflater) EpiTime.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.noBlacklistHeader = noBlacklistHeader;
        this.items = new ArrayList<GroupItem>();

        this.makeItems();

    }

    private void makeItems() {
        this.items.clear();

        GroupItem.newSeedColor(42 * 42 + 1);

        for (String aLecturesBlacklisted : this.lecturesBlacklisted) {
            this.items.add(new GroupItem(aLecturesBlacklisted));
        }
    }

    @Override
    public int getCount() {
        return this.lecturesBlacklisted.size();
    }

    @Override
    public Object getItem(int i) {
        return this.lecturesBlacklisted.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        View view = convertView == null ? inflater.inflate(R.layout.group_select_list_item, null) : convertView;

        GroupItem item = this.items.get(index);

        TextView tv = (TextView) view.findViewById(R.id.group_select_list_section_long);

        tv.setText(item.getLongTitle());

        tv.setTag(index);
        tv.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        this.onCancelClick((Integer) v.getTag());
    }


    private void onCancelClick(int index) {
        Activity context = (Activity) EpiTime.getInstance().getCurrentActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(context.getString(R.string.validate), new RemoveLectureListener(index));
        builder.setNegativeButton(context.getString(R.string.cancel), null);
        AlertDialog dialog = builder.create();
        dialog.setTitle(context.getString(R.string.dialog_alert_blacklist_title));
        dialog.setMessage(context.getString(R.string.dialog_alert_blacklist_text));
        dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        dialog.show();
    }

    private void onRemoveLecture(int index) {
        EpiTime.getInstance().getScheduleManager().removeFromBlacklist(EpiTime.getInstance().getScheduleManager().getGroup(), this.lecturesBlacklisted.get(index));
        this.makeItems();
        this.notifyDataSetChanged();

        if (this.lecturesBlacklisted.size() == 0) {
            this.noBlacklistHeader.showHeader();
        }
    }

    private class RemoveLectureListener implements DialogInterface.OnClickListener {

        private int index;

        public RemoveLectureListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            BlacklistAdapter.this.onRemoveLecture(this.index);
        }
    }
}
