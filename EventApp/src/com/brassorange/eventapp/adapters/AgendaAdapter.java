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
import com.brassorange.eventapp.model.Program;
import com.brassorange.eventapp.model.ProgramItem;

/*
 * Takes care about the vertical list of program items on the home page
 */

public class AgendaAdapter extends ArrayAdapter<ProgramItem> {
	private final Context context;
	private final Program program;
	
	public AgendaAdapter(Context context, Program program) {
		super(context, R.layout.agenda_line, program.programItems);
		this.context = context;
		this.program = program;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Called by ProgramFragment.updateProgram

		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.agenda_line, parent, false);

		Person presenter = program.programItems.get(position).presenter;
		if (presenter != null && presenter.imageName != null) {
			ImageView imageAgendaItem = (ImageView)rowView.findViewById(R.id.agendaItemImage);
			int resourceID = context.getResources().getIdentifier(presenter.imageName, "drawable", context.getApplicationInfo().packageName);
			Drawable drawable = context.getResources().getDrawable(resourceID);
			imageAgendaItem.setImageDrawable(drawable);
		}

		TextView textAgendaItemTitle = (TextView)rowView.findViewById(R.id.agendaItemTitle);
		textAgendaItemTitle.setText(program.programItems.get(position).title);

		TextView textAgendaItemSummary = (TextView)rowView.findViewById(R.id.agendaItemSummary);
		if (program.programItems.get(position).summary != null)
			textAgendaItemSummary.setText(program.programItems.get(position).summary);

	    // change the icon for Windows and iPhone
	/*
		ImageView imageView = (ImageView)rowView.findViewById(R.id.icon);
	    String s = values[position];
	    if (s.startsWith("iPhone")) {
	      imageView.setImageResource(R.drawable.no);
	    } else {
	      imageView.setImageResource(R.drawable.ok);
	    }
	*/
		return rowView;
	}
}