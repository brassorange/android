package com.brassorange.eventapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.brassorange.eventapp.R;
import com.brassorange.eventapp.model.ProgramItem;

import java.util.ArrayList;

/*
 * Takes care about the vertical list of presenter's items
 */

public class PresenterItemsAdapter extends ArrayAdapter<ProgramItem> {
    private final Context context;
    private final ArrayList<ProgramItem> presenterItems;

    public PresenterItemsAdapter(Context context, ArrayList<ProgramItem> presenterItems) {
        super(context, R.layout.program_line, presenterItems);
        this.context = context;
        this.presenterItems = presenterItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        //http://stackoverflow.com/questions/25381435/unconditional-layout-inflation-from-view-adapter-should-use-view-holder-patter
//        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.presenter_items_line, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.programItemTitle);
            textView.setText(presenterItems.get(position).title);
//        } else {
//            rowView = (View)convertView.getTag();
//        }

        return rowView;
    }
}