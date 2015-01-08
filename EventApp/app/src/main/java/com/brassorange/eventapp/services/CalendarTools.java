package com.brassorange.eventapp.services;

import java.util.Date;

import com.brassorange.eventapp.EventApp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;

public class CalendarTools {

	private Context ctx;
	private Long calendarId = -1L;

	public CalendarTools(Activity activity) {
		this.ctx = activity.getApplicationContext();

		String calendarName = ((EventApp)activity.getApplication()).getMailAccount();

		Cursor cur;
		ContentResolver cr = ctx.getContentResolver();
		Uri uri = Calendars.CONTENT_URI; 
		String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND (" 
								+ Calendars.ACCOUNT_TYPE + " = ?) AND ("
								+ Calendars.OWNER_ACCOUNT + " = ?))";
		String[] selectionArgs = new String[] {calendarName, "com.google", calendarName}; 

		// Projection array. Creating indices for this array instead of doing
		// dynamic lookups improves performance.
		String[] EVENT_PROJECTION = new String[] {
			Calendars._ID,                           // 0
			Calendars.ACCOUNT_NAME,                  // 1
			Calendars.CALENDAR_DISPLAY_NAME,         // 2
			Calendars.OWNER_ACCOUNT                  // 3
		};

		// Submit the query and get a Cursor object back. 
		cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
		//cur = cr.query(uri, EVENT_PROJECTION, null, null, null);

		// Use the cursor to step through the returned records
		while (cur.moveToNext()) {
			// Get the field values
			calendarId = cur.getLong(0);
//			String accountName = cur.getString(1);
//			String displayName = cur.getString(2);
//			String ownerName = cur.getString(3);
		}
	}

	public Long setEvent(Date dateStart, int durationMin, String eventTitle, String eventDesc) {
		// http://developer.android.com/guide/topics/providers/calendar-provider.html#intent-insert
		/*
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2014);
		cal.set(Calendar.MONTH, 5);
		cal.set(Calendar.DAY_OF_MONTH, 15);
		String eventTitle = programItem.title;
		String eventDesc = programItem.summary;
		Intent intent = new Intent(Intent.ACTION_INSERT)
			.setData(Events.CONTENT_URI)
			.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTimeInMillis())
			.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal.getTimeInMillis() + 2 * 3600 * 1000)
			.putExtra(Events.TITLE, eventTitle)
			.putExtra(Events.DESCRIPTION, eventDesc)
			.putExtra(Intent.EXTRA_EMAIL, "boris.georgiev@solarmapper.com");
		ctx.startActivity(intent);
		*/

		// http://developer.android.com/guide/topics/providers/calendar-provider.html#add-event
		ContentResolver cr = ctx.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(Events.CALENDAR_ID, calendarId);
		values.put(Events.DTSTART, dateStart.getTime());
		values.put(Events.DTEND, dateStart.getTime() + durationMin * 60 * 1000);
		values.put(Events.TITLE, eventTitle);
		values.put(Events.DESCRIPTION, eventDesc);
		//values.put(Events.EVENT_LOCATION, "London");
		//values.put(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
		//values.put(Events.ORGANIZER, "boris.m.georgiev@gmail.com");
		//values.put(Events.STATUS, Events.STATUS_CONFIRMED);
		values.put(Events.EVENT_TIMEZONE, "Europe/Sofia");
		Uri uri = cr.insert(Events.CONTENT_URI, values);

		// get the event ID that is the last element in the Uri
		return Long.parseLong(uri.getLastPathSegment());
	}
}
