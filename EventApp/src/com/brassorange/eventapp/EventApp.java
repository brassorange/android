package com.brassorange.eventapp;

import android.app.Application;
import android.hardware.Camera;

public class EventApp extends Application {

	//http://stackoverflow.com/questions/2785485/is-there-a-unique-android-device-id
	public static String uid = "";
	public static String firstName = "";
	public static String lastName = "";
	public static String biography = "";
	public static String mailAccount = "boris.m.georgiev@gmail.com";

	public static String urlAgenda     = "http://brassorange.com/samplepages/agenda.xml?";
	public static String urlGetProfile = "http://brassorange.com/ea/getProfile.php";
	public static String urlPutComment = "http://brassorange.com/ea/putComment.php";
	public static String urlPutRating  = "http://brassorange.com/ea/putRating.php";

	public static Camera mCamera;

	public EventApp() {
		try {
			mCamera = Camera.open();
		} catch (Exception e){
		}
	}

}
