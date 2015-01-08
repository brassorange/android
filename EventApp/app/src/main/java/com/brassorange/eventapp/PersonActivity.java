package com.brassorange.eventapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.brassorange.eventapp.adapters.PresenterItemsAdapter;
import com.brassorange.eventapp.model.Person;
import com.brassorange.eventapp.model.ProgramItem;

import java.util.ArrayList;

public class PersonActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        Bundle extras = getIntent().getExtras();
        Person person = (Person)extras.getSerializable("person");

        // Set the respective view contents: name, title, image, biography
        ImageView imagePerson = (ImageView)findViewById(R.id.imagePerson);
        TextView textName = (TextView)findViewById(R.id.textName);
        TextView textTitle = (TextView)findViewById(R.id.textTitle);
        TextView textBio = (TextView)findViewById(R.id.textBio);

        if (person.imageName != null) {
            int resourceID = getApplicationContext().getResources().getIdentifier(person.imageName, "drawable", getApplicationContext().getApplicationInfo().packageName);
            imagePerson.setImageDrawable(getResources().getDrawable(resourceID));
        }
        textName.setText(person.getFullName());
        textTitle.setText(person.title);
        textBio.setText(person.biography);

        // Create the presenter items section
        ArrayList<ProgramItem> presenterItems = person.presenterItems;
        GridView gridPresenterItems = (GridView)findViewById(R.id.gridPresenterItems);
        PresenterItemsAdapter adapter = new PresenterItemsAdapter(getApplicationContext(), presenterItems);
        gridPresenterItems.setAdapter(adapter);

    }
}