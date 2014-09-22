package com.brassorange.eventapp;

import com.brassorange.eventapp.adapters.AgendaAdapter;
import com.brassorange.eventapp.model.Program;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class AgendaFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_agenda, container, false);
		return view;
	}

	public void updateProgram(Program program) {
		final ListView listProgramItems = (ListView)getView().findViewById(R.id.listAgenda);
		AgendaAdapter adapter = new AgendaAdapter(getActivity().getApplicationContext(), program);
		listProgramItems.setAdapter(adapter);
	}
}
