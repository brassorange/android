package com.brassorange.eventapp;

import java.util.ArrayList;

import com.brassorange.eventapp.adapters.AgendaAdapter;
import com.brassorange.eventapp.model.Program;
import com.brassorange.eventapp.model.ProgramItem;

import android.app.Activity;
import android.app.Fragment;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class AgendaFragment extends Fragment {
	private int viewWidth = 250;
	private int viewHeight = 150;
	private int btnWidth = 250;
	private int btnHeight = 150;

	private GridLayout gridAgenda;
	private RelativeLayout overlay;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_agenda, container, false);

		ViewTreeObserver vto = view.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
		    public boolean onPreDraw() {
		        // Remove after the first run so it doesn't fire forever
		    	view.getViewTreeObserver().removeOnPreDrawListener(this);
		        viewWidth = view.getMeasuredWidth();
		    	viewHeight = view.getMeasuredHeight();
		        return true;
		    }
		});
		return view;
	}

	public void updateProgram(Program program) {
		// Updates the agenda on the main page
		//final ListView listProgramItems = (ListView)getView().findViewById(R.id.listAgenda);
		//AgendaAdapter adapter = new AgendaAdapter(getActivity().getApplicationContext(), program);
		//listProgramItems.setAdapter(adapter);

		gridAgenda = (GridLayout)getView().findViewById(R.id.gridAgenda);
		gridAgenda.removeAllViews();
		if (viewWidth/btnWidth > 2)
			gridAgenda.setColumnCount(viewWidth/btnWidth - 1);
		else
			gridAgenda.setColumnCount(2);
		//gridAgenda.setRowCount(12);
		gridAgenda.setPadding((viewWidth-gridAgenda.getColumnCount()*btnWidth)/2 - 30, 0, 0, 0);
		final ArrayList<ProgramItem> listProgramItems = program.programItems;
		//final Activity activity = this.getActivity();
		for (int i=0; i<listProgramItems.size(); i++) {
			TextView item = new TextView(this.getActivity());
			item.setId(i);
			item.setWidth(btnWidth);
			item.setHeight(btnHeight);
            item.setBackground(getResources().getDrawable(R.drawable.background_agenda_item));
			//item.setBackgroundColor(287436);
			String title = listProgramItems.get(i).title;
			if (title != null && title.length() > 20)
				title = title.substring(0, 20) + "...";
			item.setText(title);
			item.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
                    TextView tv = (TextView)arg0;
					openSelectedItem(listProgramItems.get(tv.getId()));
					return false;
			}});
			gridAgenda.addView(item);

//MainActivity mainActivity = ((MainActivity)this.getActivity());
//View v = mainActivity.getWindow().getDecorView().getRootView().findViewById(R.id.content);
//System.out.println("***" + mainActivity.getWindow().getDecorView().getRootView().findViewById(R.layout.activity_main));
//System.out.println("***" + v.getClass());
//System.out.println("***" + v.findViewById(R.id.imageLogo));
//System.out.println("***" + v.findViewById(R.id.overlay));
//System.out.println("***" + mainActivity.getWindow().getDecorView().getRootView().findViewById(R.id.overlay));
/*
			overlay = (RelativeLayout)((MainActivity)this.getActivity()).findViewById(R.id.overlay);
			overlay.setVisibility(View.GONE);
			overlay.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					overlay.setVisibility(View.GONE);
					return false;
			}});
*/
		}
	}

	private void openSelectedItem(ProgramItem programItem) {
		//overlay.setVisibility(View.VISIBLE);
	}
}
