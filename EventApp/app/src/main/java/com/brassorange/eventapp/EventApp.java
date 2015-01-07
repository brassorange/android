package com.brassorange.eventapp;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import com.brassorange.eventapp.model.Program;
import com.brassorange.eventapp.services.FileUtils;
import com.brassorange.eventapp.util.PrefTools;

public class EventApp extends Application {

	//http://rdcworld-android.blogspot.in/2012/07/read-store-log-cat-programmatically-in.html
	//http://stackoverflow.com/questions/2785485/is-there-a-unique-android-device-id
	//http://stackoverflow.com/questions/1944656/android-global-variable
	private String uid = "";
	private String firstName = "";
	private String lastName = "";
	private String biography = "";
	private String mailAccount = "";

	public final String uidInEmulator = "abcde12345";

	private final String urlHostName   = "http://brassorange.com/ea/";
	private final String urlAgenda     = urlHostName + "getProgramItems.php";
	private final String urlGetProfile = urlHostName + "getProfile.php";
	private final String urlPutComment = urlHostName + "putComment.php";
	private final String urlPutRating  = urlHostName + "putRating.php";

	public final boolean allowAnonymousComments = false;
	public final boolean allowAnonymousRatings = false;

    public FileUtils fileUtils;
    public PrefTools prefTools;

	public boolean isRanInEmulator() {
		return ("sdk".equals(Build.PRODUCT));
	}

	private boolean isLoggedIn() {
		return (uid != null && uid != "");
	}

	public boolean checkLoginStatus() {
		if (!isLoggedIn()) {
			Context context = getApplicationContext();
			CharSequence text = "Not logged in.";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			return false;
		}
		return true;
	}

	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getBiography() {
		return biography;
	}
	public void setBiography(String biography) {
		this.biography = biography;
	}
	public String getMailAccount() {
		return mailAccount;
	}
	public void setMailAccount(String mailAccount) {
		this.mailAccount = mailAccount;
	}

	public String getUrlAgenda() {
		return urlAgenda;
	}
	public String getUrlGetProfile() {
		return urlGetProfile;
	}
	public String getUrlPutComment() {
		return urlPutComment;
	}
	public String getUrlPutRating() {
		return urlPutRating;
	}

}
