package com.brassorange.eventapp.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.brassorange.eventapp.R;
import com.brassorange.eventapp.model.Person;

/*
 * Takes care about the vertical list of presenters
 */

public class PresentersAdapter extends ArrayAdapter<Person> {
    private final Context context;
    private final Person[] persons;

    public PresentersAdapter(Context context, Person[] persons) {
        super(context, R.layout.presenter_line, persons);
        this.context = context;
        this.persons = persons;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Called by PresentersFragment
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.presenter_line, parent, false);
        TextView sectionBreak = (TextView)rowView.findViewById(R.id.sectionBreak);
        ImageView imagePresenter = (ImageView)rowView.findViewById(R.id.presenterImage);
        TextView textPresenterName = (TextView)rowView.findViewById(R.id.presenterName);
        TextView textPresenterDesc = (TextView)rowView.findViewById(R.id.presenterDesc);

        // Person view
        Person person = persons[position];
        if (person.uid.equals("-1")) {
            rowView.setClickable(false);
            //TODO: Check if needed:
            //rowView.setEnabled(false);
            //rowView.setFocusable(false);
            sectionBreak.setText(person.firstName);
        } else {
            sectionBreak.setText(null);
        }

        if (person.imageName != null) {
            int resourceID = context.getResources().getIdentifier(person.imageName, "drawable", context.getApplicationInfo().packageName);
            imagePresenter.setImageDrawable(context.getResources().getDrawable(resourceID));
        }
        textPresenterName.setText(person.getFullName());
        textPresenterDesc.setText(person.title);

        return rowView;
    }
}