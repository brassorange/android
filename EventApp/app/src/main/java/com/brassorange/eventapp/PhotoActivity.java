package com.brassorange.eventapp;

import com.brassorange.eventapp.services.FileUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);

		Bundle extras = getIntent().getExtras();
	    String fileName = extras.getString("fileName");

	    TextView viewPicDtls = (TextView)findViewById(R.id.viewPhotoDetails);
		ImageView viewPic = (ImageView)findViewById(R.id.viewPhoto);

	    FileUtils fileUtils = new FileUtils(getApplicationContext());
		Bitmap bm = fileUtils.readImageFromInternalStorage(fileName);
		viewPic.setImageBitmap(bm);
		viewPicDtls.setText(fileName);
	}
}
