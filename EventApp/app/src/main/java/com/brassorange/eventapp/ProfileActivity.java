package com.brassorange.eventapp;

import com.brassorange.eventapp.services.CameraPreview;
import com.brassorange.eventapp.services.CompletionListener;
import com.brassorange.eventapp.services.UserService;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import net.sourceforge.zbar.Config;

//http://thorbek.net/online/2013/10/11/an-integrated-qr-scanner-in-android-activity/
//   http://code.tutsplus.com/tutorials/android-sdk-create-a-barcode-reader--mobile-17162

public class ProfileActivity extends Activity implements CompletionListener {
	private Camera mCamera;
	private CameraPreview mPreview;
	private Handler autoFocusHandler;

	TextView txtScan;
	Button btnScan;
	LinearLayout cameraWindow;
	GridLayout profileData;

	ImageScanner scanner;

	private boolean barcodeScanned = false;
	private boolean previewing = true;

	static {
		System.loadLibrary("iconv");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		mCamera = getCameraInstance();
		mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCb);
		autoFocusHandler = new Handler();

		// Instance barcode scanner
		scanner = new ImageScanner();
		scanner.setConfig(0, Config.X_DENSITY, 3);
		scanner.setConfig(0, Config.Y_DENSITY, 3);
		cameraWindow = (LinearLayout)findViewById(R.id.cameraWindow);
		cameraWindow.addView(mPreview);
		txtScan = (TextView)findViewById(R.id.txtScan);
		btnScan = (Button)findViewById(R.id.btnScan);
		profileData = (GridLayout)findViewById(R.id.profileData);

		txtScan.setText("onCreate");

		onTaskCompleted();

		final EventApp eventApp = ((EventApp)this.getApplication());

		btnScan.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// When ran in emulator
				if (eventApp.isRanInEmulator()) {
					getProfile(eventApp.uidInEmulator);
					return;
				}
				if (barcodeScanned) {
					barcodeScanned = false;
					txtScan.setText("Scanning...");
					mCamera.setPreviewCallback(previewCb);
					mCamera.startPreview();
					previewing = true;
					mCamera.autoFocus(autoFocusCb);
				}
			}
		});
	}

    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e){
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

	private Runnable doAutoFocus = new Runnable() {
		public void run() {
			if (previewing)
				mCamera.autoFocus(autoFocusCb);
		}
	};

	PreviewCallback previewCb = new PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			Camera.Parameters parameters = camera.getParameters();
			Size size = parameters.getPreviewSize();
			Image barcode = new Image(size.width, size.height, "Y800");
			barcode.setData(data);

			int result = scanner.scanImage(barcode);
			if (result != 0) {
				previewing = false;
				mCamera.setPreviewCallback(null);
				mCamera.stopPreview();
				 
				SymbolSet syms = scanner.getResults();
				for (Symbol sym : syms) {
					txtScan.setText("barcode result " + sym.getData());
					barcodeScanned = true;
					getProfile(sym.getData());
				}
			}
		}
	};

	// Mimic continuous auto-focusing
	AutoFocusCallback autoFocusCb = new AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			autoFocusHandler.postDelayed(doAutoFocus, 1000);
		}
	};

	private void getProfile(String profileId) {
//		txtScan.setText("getProfile " + profileId);
		UserService us = new UserService(this.getApplication(), this);
		us.execute("profile", profileId);
	}

	@Override
	public void onTaskCompleted() {
		String uid = ((EventApp)this.getApplication()).getUid();
		String firstName = ((EventApp)this.getApplication()).getFirstName();
		String lastName = ((EventApp)this.getApplication()).getLastName();
		String biography = ((EventApp)this.getApplication()).getBiography();
		String email = ((EventApp)this.getApplication()).getMailAccount();
		if (lastName != null && lastName != "") {
			txtScan.setText(uid + ". " + firstName + " " + lastName + " --- " + biography);
			cameraWindow.setVisibility(View.GONE);
			profileData.setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.txtName)).setText(firstName);
			((TextView)findViewById(R.id.txtEmail)).setText(email);
		}
	}

}
