package com.solarmapper.smapp.controllers;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class HttpRetriever {
    private static final String strDeleteSuffix = "<!-- Hosting24 Analytics Code -->";

    private DefaultHttpClient client = new DefaultHttpClient();
    public Object retrieve(String url, Class c) {
        Log.d(getClass().getSimpleName(), "client: " + client);
        Log.d(getClass().getSimpleName(), "retrieve " + url);
        HttpGet getRequest = new HttpGet(url);

        try {
            HttpResponse getResponse = client.execute(getRequest);
            final int statusCode = getResponse.getStatusLine().getStatusCode();
            Log.d(getClass().getSimpleName(), "statusCode=" + statusCode);

            if (statusCode != HttpStatus.SC_OK) {
                Log.w(getClass().getSimpleName(), "Error " + statusCode + " for URL " + url);
                return null;
            }

            HttpEntity getResponseEntity = getResponse.getEntity();
            if (getResponseEntity != null) {
                if (c.equals(String.class)) {
                    String response = EntityUtils.toString(getResponseEntity);
                    String strResponse = String.valueOf(response);
                    if (strResponse.contains(strDeleteSuffix))
                        strResponse = strResponse.substring(0, strResponse.indexOf(strDeleteSuffix));
                    Log.d(getClass().getSimpleName(), "response: " + strResponse.length() + " bytes.");
                    return strResponse;
                } else if (c.equals(Bitmap.class)) {
                    byte[] response = EntityUtils.toByteArray(getResponseEntity);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(response, 0, response.length);
                    Log.d(getClass().getSimpleName(), "size: " + bitmap.getWidth() + " x " + bitmap.getHeight());
                    return bitmap;
                }
            }
        }
        catch (IOException e) {
            getRequest.abort();
            Log.w(getClass().getSimpleName(), "Error for URL " + url, e);
        }

        return null;
    }

}
