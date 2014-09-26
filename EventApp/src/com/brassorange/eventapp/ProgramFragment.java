package com.brassorange.eventapp;

/* ProgramFragment
 * 
 * Displays the program item content.
 * 
 */

import java.io.File;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.brassorange.eventapp.adapters.ProgramAdapter;
import com.brassorange.eventapp.model.Program;
import com.brassorange.eventapp.model.ProgramItem;
import com.brassorange.eventapp.services.CalendarTools;
import com.brassorange.eventapp.services.EmailTools;
import com.brassorange.eventapp.services.CompletionListener;
import com.brassorange.eventapp.services.UserService;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.ScrollView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

public class ProgramFragment extends Fragment implements CompletionListener {

	private String programItemId = "-1";
	private Context ctx;
	private CalendarTools calendarTools;
	private EmailTools emailTools;
	private String tmplCmtName = "cmt-{0}.txt";
	private String tmplRtgName = "rtg-{0}.txt";
	private String tmplPicName = "pic-{0}-{1}.png";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_program, container, false);

		ctx = getActivity().getApplicationContext();
		calendarTools = new CalendarTools(getActivity());
		emailTools = new EmailTools(getActivity());

		return view;
	}


	public void clickedProgramItem(final ProgramItem programItem) {
		// Called on ProgramItem click
		// Displays the ProgramItem summary, description, ...

		programItemId = programItem.id;

		TextView viewPISummary = (TextView)getView().findViewById(R.id.programItemSummary);
		ScrollView scrollPI = (ScrollView)getView().findViewById(R.id.programItemScroll);
		LinearLayout viewPI = (LinearLayout)getView().findViewById(R.id.programItem);
		TextView viewPIDesc = (TextView)getView().findViewById(R.id.programItemDesc);
		final EditText editPIComments = (EditText)getView().findViewById(R.id.programItemComments);
		ImageButton btnPogramItemCal = (ImageButton)getView().findViewById(R.id.programItemCal);
		ImageButton btnPogramItemMail = (ImageButton)getView().findViewById(R.id.programItemMail);
		ImageButton btnPogramItemSave = (ImageButton)getView().findViewById(R.id.programItemSave);
		ImageButton btnPogramItemCam = (ImageButton)getView().findViewById(R.id.programItemCam);
		RatingBar ratingPI = (RatingBar)getView().findViewById(R.id.programItemRatingBar);
		final TextView viewPICommentsInfo = (TextView)getView().findViewById(R.id.programItemCommentsInfo);

		// Adjust the ProgramItem width
		int availableWidth = getView().getWidth() - 440;
		viewPISummary.setWidth(availableWidth);
		scrollPI.setMinimumWidth(availableWidth);
		viewPI.setMinimumWidth(availableWidth);

		// Originally created invisible, make sure the ProgramItem fields become visible
		viewPISummary.setVisibility(View.VISIBLE);
		scrollPI.setVisibility(View.VISIBLE);
		if (ratingPI != null)
			ratingPI.setVisibility(View.VISIBLE);

		// Display the ProgramItem text contents
		viewPISummary.setText(programItem.summary);
		viewPIDesc.setText(programItem.content);

		// Display the user-controlled contents:
		// Comments
		final MainActivity mainActivity = (MainActivity)getActivity();
		final String fileNameComments = MessageFormat.format(tmplCmtName, new Object[]{programItemId});
		final String prefNameRating = MessageFormat.format(tmplRtgName, new Object[]{programItemId});
		editPIComments.setText(mainActivity.fileUtils.readFileFromInternalStorage(fileNameComments));
		viewPICommentsInfo.setText("");
		long lastModified = 0;
		File fileComments = mainActivity.fileUtils.getFileFromInternalStorage(fileNameComments);
		if (fileComments != null)
			lastModified = fileComments.lastModified();
		if (lastModified > 0) {
			String lastModifiedAsText = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date(lastModified));
			viewPICommentsInfo.setText("Last modified: " + lastModifiedAsText);
		}
		// Rating
		if (ratingPI != null) {
			try {
				ratingPI.setRating(Float.valueOf(mainActivity.prefTools.retrievePreference(prefNameRating)));
			} catch(Exception e) {
				ratingPI.setRating(0);
			}
		}
		// Pictures
		LinearLayout viewPIPics = (LinearLayout)getView().findViewById(R.id.programItemPics);
		viewPIPics.removeAllViews();
		List<String> fileNames = ((MainActivity)getActivity()).fileUtils.getFileNamesFromInternalStorage(MessageFormat.format(tmplPicName, new Object[]{programItemId, "\\d{3}"}));
		if (fileNames != null) {
			for (final String fileName : fileNames) {
				Bitmap bm = ((MainActivity)getActivity()).fileUtils.readImageFromInternalStorage(fileName);
				addImageToList(fileName, bm);
		    }
		}

	    // Creating a Google Calendar event
		String eventID = mainActivity.prefTools.retrievePreference("GCal_" + programItem.id);
		if (eventID != null && eventID != "") {
			btnPogramItemCal.setVisibility(View.INVISIBLE);
		} else {
			btnPogramItemCal.setVisibility(View.VISIBLE);
			btnPogramItemCal.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Long eventID = calendarTools.setEvent(programItem.date, 
														programItem.durationMin,
														programItem.title,
														programItem.title);
					mainActivity.prefTools.storePreference("GCal_" + programItem.id, String.valueOf(eventID));
				}
			});
		}

		// Sending e-mail
		String emailPresenter = programItem.presenter.email;
		if (emailPresenter == null || emailPresenter == "") {
			btnPogramItemMail.setVisibility(View.INVISIBLE);
		} else {
			btnPogramItemMail.setVisibility(View.VISIBLE);
			btnPogramItemMail.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					emailTools.sendMail(programItem.title, programItem.summary, programItem.presenter.email);
				}
			});
		}

		final ProgramFragment programFragment = this;

		// Saving the ProgramItem Comments
		btnPogramItemSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String commentsText = String.valueOf(editPIComments.getText());
				mainActivity.fileUtils.writeFileToInternalStorage(fileNameComments, commentsText);
				//mainActivity.provideResponse();
				String lastModifiedAsText = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date());
				viewPICommentsInfo.setText("Last modified: " + lastModifiedAsText);

				// Upload update to server
				(new UserService(programFragment)).execute("comment", String.valueOf(programItemId), commentsText);
			}
		});

		// Taking a picture
		btnPogramItemCam.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// http://chrisrisner.com/31-Days-of-Android--Day-29%E2%80%93Using-the-Camera
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, 100);
			}
		});

		// Saving the ProgramItem Rating
		if (ratingPI != null) {
			ratingPI.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
				@Override
				public void onRatingChanged(RatingBar arg0, float value, boolean fromUser) {
					if (fromUser) {
						mainActivity.prefTools.storePreference(prefNameRating, String.valueOf(value));
						//mainActivity.provideResponse();

						// Upload update to server
						(new UserService(programFragment)).execute("rate", String.valueOf(programItemId), String.valueOf(value));
					}
				}
			});
		}
/*
		ImageView imagePerson = (ImageView)getView().findViewById(R.id.personImage);
		final LinearLayout persOverlay = (LinearLayout)getView().findViewById(R.id.persOverlay);
		imagePerson.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Hello toast!", Toast.LENGTH_SHORT);
				//toast.show();
				persOverlay.setVisibility(View.VISIBLE);
			}
		});
		persOverlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				persOverlay.setVisibility(View.INVISIBLE);
			}
		});
*/
		// Update the slider with presenter data
		SlidingDrawer sliderPresenter = (SlidingDrawer)getView().findViewById(R.id.sliderPresenter);
		if (sliderPresenter != null) {
			if (programItem.presenter == null || programItem.presenter.uid == "") {
				// Hide if there's no presenter
				sliderPresenter.setVisibility(View.GONE);
			} else {
				String presenterName = programItem.presenter.lastName;
				if (programItem.presenter.middleNames != null && programItem.presenter.middleNames != "")
					presenterName = programItem.presenter.middleNames + " " + presenterName;
				if (programItem.presenter.firstName != null && programItem.presenter.firstName != "")
					presenterName = programItem.presenter.firstName + " " + presenterName;

				// Make slider visible
				sliderPresenter.setVisibility(View.VISIBLE);

				// Only for large screen sizes:
				if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
						== Configuration.SCREENLAYOUT_SIZE_LARGE) {
					// Load the presenter's name and image
					Button btnPersonName = (Button)getView().findViewById(R.id.handlePerson);
					int resourceID = ctx.getResources().getIdentifier(programItem.presenter.imageName, "drawable", ctx.getApplicationInfo().packageName);
					Drawable drawableTop = getResources().getDrawable(resourceID);
					btnPersonName.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
					// Display the presenter's name in the slider
					btnPersonName.setText(presenterName);
				}

				// Display the presenter's data in the slider's contents
				TextView viewPersonName = (TextView)getView().findViewById(R.id.personName);
				viewPersonName.setText(presenterName);	
				TextView viewPersonBio = (TextView)getView().findViewById(R.id.personBio);
				viewPersonBio.setText(programItem.presenter.biography);
			}
		}
	}

	public void updateProgram(Context ctx, final Program program) {
		// Called by Updater.onPostExecute
		// Lists the ProgramItems in "listview_events"

		final ListView listProgramItems = (ListView)getView().findViewById(R.id.listProgramItems);
		ProgramAdapter adapter = new ProgramAdapter(ctx, program);
		listProgramItems.setAdapter(adapter);
		listProgramItems.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int idInList, long arg3) {
				ProgramFragment programFragment = (ProgramFragment)getFragmentManager().findFragmentById(R.id.fragPrg);
				ProgramItem programItem = program.programItems.get(idInList);
				programFragment.clickedProgramItem(programItem);
			}
		});
		listProgramItems.performItemClick(listProgramItems.findViewById(R.id.eventName), 1, 0);
	}

	// After taking a picture:
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Bundle extras = intent.getExtras();
		Bitmap bm = (Bitmap)extras.get("data");
		String newFileName = MessageFormat.format(tmplPicName, new Object[]{programItemId, "001"});
		List<String> fileNames = ((MainActivity)getActivity()).fileUtils.getFileNamesFromInternalStorage(MessageFormat.format(tmplPicName, new Object[]{programItemId, "\\d{3}"}));
		if (fileNames.size() > 0) {
			try {
				String lastFileName = fileNames.get(fileNames.size() - 1);
				String picIdx = lastFileName.replace("pic-" + programItemId + "-", "").replace(".png", "");
				picIdx = String.format("%03d", Integer.valueOf(picIdx) + 1);
				newFileName = MessageFormat.format(tmplPicName, new Object[]{programItemId, picIdx});
			} catch(Exception e) {
			}
		}
		((MainActivity)getActivity()).fileUtils.writeImageToInternalStorage(newFileName, bm);
		addImageToList(newFileName, bm);
	}

	public void addImageToList(final String fileName, Bitmap bm) {
		LinearLayout viewPIPics = (LinearLayout)getView().findViewById(R.id.programItemPics);
		ImageView viewPic = new ImageView(getActivity().getApplicationContext());
		LayoutParams params = new LayoutParams(new ViewGroup.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT));
		viewPic.setLayoutParams(params);
		viewPic.setPadding(10, 0, 0, 0);
		viewPic.setAdjustViewBounds(true);
		viewPic.setMaxHeight(75);
		viewPic.setImageBitmap(bm);
		final MainActivity mainActivity = (MainActivity)getActivity();
		viewPic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mainActivity, PhotoActivity.class);
				intent.putExtra("fileName", fileName);
				startActivity(intent); 
			}
		});
		viewPIPics.addView(viewPic);
	}


	@Override
	public void onTaskCompleted() {
		// TODO Auto-generated method stub
		
	}
}
