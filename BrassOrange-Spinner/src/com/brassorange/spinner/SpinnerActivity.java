package com.brassorange.spinner;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

public class SpinnerActivity extends Activity implements OnItemSelectedListener {

	public SpinnerActivity(Activity ctx) {
		Spinner spinner = (Spinner)ctx.findViewById(R.id.spinnerTop);
		spinner.setOnItemSelectedListener(this);
	}
	
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
		Log.i("SpinnerActivity", "Selected: "+parent.getItemAtPosition(pos));
	}

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
