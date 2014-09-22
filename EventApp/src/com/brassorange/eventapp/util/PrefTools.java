package com.brassorange.eventapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefTools {

	private Context ctx;

	public PrefTools(Context ctx) {
		this.ctx = ctx;
	}

	// Tools for dealing with Shared Preferences
	public String retrievePreference(String key) {
		SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return appPreferences.getString(key, "");
	}

	public void storePreference(String key, String value) {
		SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = appPreferences.edit();
        editor.putString(key, value);
        editor.commit();
	}

}
