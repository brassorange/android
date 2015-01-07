package com.brassorange.eventapp;

import java.util.ArrayList;

import com.brassorange.eventapp.model.Program;
import com.brassorange.eventapp.model.ProgramItem;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/*
One of the 2 main fragments, selectable through the Navigation Drawer.
*/

public class AgendaFragment extends Fragment {
	private int viewWidth = 250;
	private int viewHeight = 150;
	private int btnWidth = 250;
	private int btnHeight = 150;

	private GridLayout gridAgenda;
	private RelativeLayout overlay;
    private Program program;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_agenda, container, false);

        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                viewWidth = view.getMeasuredWidth();
                viewHeight = view.getMeasuredHeight();
                updateView();

                // Remove after the first run so it doesn't fire forever
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
        return view;
    }

    public void updateView() {
        if (getView() != null && program != null) {
            // Updates the agenda on the main page
            gridAgenda = (GridLayout)getView().findViewById(R.id.gridAgenda);
            gridAgenda.setUseDefaultMargins(true);
            gridAgenda.removeAllViews();
            if (viewWidth / btnWidth > 2)
                gridAgenda.setColumnCount(viewWidth / btnWidth - 1);
            else
                gridAgenda.setColumnCount(2);
            gridAgenda.setPadding((viewWidth - gridAgenda.getColumnCount() * btnWidth) / 2 - 30, 0, 0, 0);

            final ArrayList<ProgramItem> listProgramItems = program.programItems;
            for (int i = 0; i < listProgramItems.size(); i++) {
                final ProgramItem programItem = listProgramItems.get(i);

                String title = programItem.title;
                if (title != null && title.length() > 20)
                    title = title.substring(0, 20) + "...";

                /*
                ProgramItemView viewItem = new ProgramItemView(getActivity().getApplicationContext(), null);
                viewItem.setId(i);
                viewItem.setBackground(getResources().getDrawable(R.drawable.background_agenda_item));
                viewItem.setMinimumWidth(btnWidth);
                viewItem.setMinimumHeight(btnHeight);
                ((TextView)viewItem.findViewById(R.id.itemName)).setText("*** " + title);
                gridAgenda.addView(viewItem);
                */


                TextView viewItem = new TextView(getActivity());
                viewItem.setId(i);
                viewItem.setWidth(btnWidth);
                viewItem.setHeight(btnHeight);
                viewItem.setBackground(getResources().getDrawable(R.drawable.background_agenda_item));
                viewItem.setText(title);
                gridAgenda.addView(viewItem);

                viewItem.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View arg0, MotionEvent arg1) {
                        Intent intent = new Intent(getActivity(), AgendaItemActivity.class);
                        intent.putExtra("programItem", programItem);
                        startActivity(intent);

                        return false;
                    }
                });
            }
        }
	}

	public void updateProgram(Program program) {
        this.program = program;
        updateView();
    }
}
