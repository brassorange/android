package com.brassorange.http;

import android.content.Intent;
import android.provider.Settings;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        final HttpActivity httpActivity = new HttpActivity((TextView)findViewById(R.id.textHtml));
        final Button button = (Button)findViewById(R.id.btnGo);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView txtProto = (TextView)findViewById(R.id.txtProto);
                EditText txtUrl = (EditText)findViewById(R.id.editUrl);
                httpActivity.getHttpResponse(txtProto.getText() + "" + txtUrl.getText());
            }
        });

        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
        	Log.e("NW", "No Network Info");
        } else if (!networkInfo.isConnectedOrConnecting()) {
        	Log.e("NW", "Network not connected");
        	turnNetworkOn();
        } else {
        	Log.i("NW", "Network available: " + networkInfo.getType());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void turnNetworkOn() {
		// custom dialog
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog_network);
		dialog.setTitle("Need netwotk connection");

		TextView text = (TextView) dialog.findViewById(R.id.text);
		text.setText("Android custom dialog example!");
		ImageView image = (ImageView) dialog.findViewById(R.id.image);
		image.setImageResource(R.drawable.ic_launcher);

		Button dialogButtonOk = (Button) dialog.findViewById(R.id.dialogButtonOK);
		dialogButtonOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                //startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
				dialog.dismiss();
			}
		});
		Button dialogButtonCancel = (Button) dialog.findViewById(R.id.dialogButtonCancel);
		dialogButtonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
    }
}
