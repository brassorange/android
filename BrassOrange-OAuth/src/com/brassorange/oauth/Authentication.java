package com.brassorange.oauth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

public class Authentication extends AsyncTask<Void,Void,Void> {
	private final String SCOPE = "https://www.googleapis.com/auth/googletalk";
	private AuthPreferences authPreferences;
	private Activity ctx;
	private TextView textView;
	
	public Authentication(Activity ctx) {
		this.ctx = ctx;
		this.textView = (TextView)ctx.findViewById(R.id.textView_helloWorld);
	}
	
	@Override
    protected Void doInBackground(Void... params) {
		textView.setText("...");
		AccountManager mAccountManager = AccountManager.get(ctx);
	    Account[] accounts = mAccountManager.getAccounts();
	    for (int i = 0; i < accounts.length; i++) {
	    	mAccountManager.getAuthToken(accounts[i], "oauth2:" + SCOPE, null, ctx, new OnTokenAcquired(), null);
	        textView.setText(textView.getText() + "\n\n" + accounts[i].name + " (" + accounts[i].type + ")");
	    }
	    return null;
	}

	private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
		 
		@Override
		public void run(AccountManagerFuture<Bundle> result) {
			try {
				Bundle bundle = result.getResult();
				Intent launch = (Intent) bundle.get(AccountManager.KEY_INTENT);
				if (launch != null) {
//					startActivityForResult(launch, AUTHORIZATION_CODE);
				} else {
					String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
					authPreferences.setToken(token);
					//doCoolAuthenticatedStuff();
					textView.setText(textView.getText() + "\n  --- " + authPreferences.getUser() + " " + authPreferences.getToken());
				}
			} catch (Exception e) {
				//throw new RuntimeException(e);
			}
		}
	}
}
