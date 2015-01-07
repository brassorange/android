package com.brassorange.eventapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

// http://www.vogella.com/tutorials/AndroidCustomViews/article.html

public class ProgramItemView extends ViewGroup {
    public ProgramItemView(Context context, AttributeSet attrs) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_program_item, this, true);

        ((TextView)findViewById(R.id.itemName)).setText("2983749283");
        System.out.println("*************************");
    }

    @Override
    public void onLayout (boolean changed, int left, int top, int right, int bottom) {

    }
}
