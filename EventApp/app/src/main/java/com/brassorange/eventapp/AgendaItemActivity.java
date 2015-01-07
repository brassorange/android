package com.brassorange.eventapp;

import android.app.Activity;
import android.os.Bundle;

import com.brassorange.eventapp.model.Program;
import com.brassorange.eventapp.model.ProgramItem;

import java.util.ArrayList;

public class AgendaItemActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda_item);

        Bundle extras = getIntent().getExtras();
        ProgramItem programItem = (ProgramItem)extras.getSerializable("programItem");

        ProgramFragment programFragment = (ProgramFragment)getFragmentManager().findFragmentById(R.id.fragPrg);
        programFragment.clickedProgramItem(programItem);
    }
}