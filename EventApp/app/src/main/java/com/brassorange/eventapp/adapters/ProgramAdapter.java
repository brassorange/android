package com.brassorange.eventapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.brassorange.eventapp.R;
import com.brassorange.eventapp.model.Program;
import com.brassorange.eventapp.model.ProgramItem;

/*
 * Takes care about the vertical list of program items
 */

public class ProgramAdapter extends ArrayAdapter<ProgramItem> {
	private final Context context;
	private final Program program;
	
	public ProgramAdapter(Context context, Program program) {
		super(context, R.layout.program_line, program.programItems);
		this.context = context;
		this.program = program;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Called by ProgramFragment.updateProgram

		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.program_line, parent, false);
		TextView textView = (TextView)rowView.findViewById(R.id.eventName);
		textView.setText(program.programItems.get(position).title);

		return rowView;
	}
}