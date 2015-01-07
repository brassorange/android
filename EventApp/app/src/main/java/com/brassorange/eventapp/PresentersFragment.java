package com.brassorange.eventapp;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.brassorange.eventapp.adapters.PresentersAdapter;
import com.brassorange.eventapp.adapters.ProgramAdapter;
import com.brassorange.eventapp.model.Person;
import com.brassorange.eventapp.model.Program;
import com.brassorange.eventapp.model.ProgramItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/*
One of the 2 main fragments, selectable through the Navigation Drawer.
*/

public class PresentersFragment extends Fragment {
    private int viewWidth = 250;
    private int viewHeight = 150;
    private int btnWidth = 350;
    private int btnHeight = 150;
    private Person[] persons;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_presenters, container, false);

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
        if (getView() != null && persons != null) {
            final GridView gridPresenters = (GridView)getView().findViewById(R.id.gridPresenters);
            //gridPresenters.setUseDefaultMargins(true);
            //gridPresenters.removeAllViews();
            //if (viewWidth / btnWidth > 2)
            //    gridPresenters.setNumColumns(viewWidth / btnWidth - 1);
            //else
            //    gridPresenters.setNumColumns(2);
            gridPresenters.setNumColumns(1);
            gridPresenters.setPadding(30, 0, 0, 0);
            PresentersAdapter adapter = new PresentersAdapter(getActivity().getApplicationContext(), persons);
            gridPresenters.setAdapter(adapter);
            gridPresenters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int idInList, long arg3) {
                    Person person = persons[idInList];
                    if (person.uid.equals("-1"))
                        return;
                    if (viewWidth > 600) {
                        //TODO: Open PersonActivity in a fragment...
                    } else {
                        Intent intent = new Intent(getActivity(), PersonActivity.class);
                        intent.putExtra("person", persons[idInList]);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    public void updatePresenters(Program program) {
        ArrayList<String> personNames = new ArrayList<>();
        HashMap<String, Person> personHashMap = new HashMap();

        // Put all presenter names in an arraylist, presenters in a hashmap
        for(ProgramItem programItem : program.programItems) {
            if (programItem.presenter != null) {
                String fullName = programItem.presenter.getFullName();
                if (!personHashMap.containsKey(fullName) && fullName!= null && fullName != "") {
                    personNames.add(fullName);
                    personHashMap.put(fullName, programItem.presenter);
                }
            }
        }
        Collections.sort(personNames);
        ArrayList<String> personNamesWithSections = new ArrayList<>();
        String sectionName = "";
        for (int i=0; i<personNames.size() - 1; i++) {
            String thisPersonName = personNames.get(i);
            if (i == 0 || !sectionName.equals(thisPersonName.substring(0, 1))) {
                sectionName = thisPersonName.substring(0, 1);
                personNamesWithSections.add(sectionName);
            }
            personNamesWithSections.add(thisPersonName);
        }

        Person[] persons = new Person[personNamesWithSections.size()];
        int i = 0;
        for (String personName : personNamesWithSections) {
            int idx = i++;
            Person person = personHashMap.get(personName);
            if (person == null) {
                person = new Person();
                person.uid = "-1";
                person.firstName = personName.toUpperCase();
            }
            persons[idx] = person;
        }
        this.persons = persons;

        updateView();
    }
}