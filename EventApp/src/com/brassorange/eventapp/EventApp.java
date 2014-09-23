package com.brassorange.eventapp;

import android.app.Application;
import android.hardware.Camera;

public class EventApp extends Application {

	//http://stackoverflow.com/questions/2785485/is-there-a-unique-android-device-id
	public static String uid = "";

	public static String urlGetProfile = "http://brassorange.com";
	public static String urlPutComment = "http://brassorange.com/ea/putComment.php";
	public static String urlPutRating = "http://brassorange.com/ea/putRating.php";

	public static Camera mCamera;

	public EventApp() {
		try {
			mCamera = Camera.open();
		} catch (Exception e){
		}
	}

}
