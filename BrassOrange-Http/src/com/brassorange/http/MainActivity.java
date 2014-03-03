package com.brassorange.http;

import com.brassorange.http.HttpActivity;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        if (networkInfo == null)
        	Log.e("NW", "No Network Info");
        else if (!networkInfo.isConnected())
        	Log.e("NW", "Network not connected");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
