package com.brassorange.eventapp.services;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import com.brassorange.eventapp.EventApp;
import com.sun.mail.smtp.SMTPTransport;
import com.sun.mail.util.BASE64EncoderStream;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class EmailTools {
	private Activity activity;
	private Session session;
	private String token;
	private String mailAccount;

	//http://www.jondev.net/articles/Sending_Emails_without_User_Intervention_(no_Intents)_in_Android
	//http://www.tiemenschut.com/how-to-send-e-mail-directly-from-android-application/
    //http://stackoverflow.com/questions/12503303/javamail-api-in-android-using-xoauth

	public EmailTools(final Activity activity) {
		this.activity = activity;
		mailAccount = ((EventApp)activity.getApplication()).getMailAccount();

		final String SCOPE = "https://mail.google.com/";

		// Browse through the accounts, get authentication token
		AccountManager mAccountManager = AccountManager.get(activity.getApplicationContext());
	    Account[] accounts = mAccountManager.getAccounts();
	    for (int i = 0; i < accounts.length; i++) {
	    	if (accounts[i].name.equals(mailAccount) 
	    			&& accounts[i].type.equals("com.google"))
	    		mAccountManager.getAuthToken(accounts[i], "oauth2:" + SCOPE, null, 
	    				activity, new OnTokenAcquired(), null);
	    }
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
					token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
//					authPreferences.setToken(token);
					//doCoolAuthenticatedStuff();
				}
			} catch (Exception e) {
				//throw new RuntimeException(e);
			}
		}
	}

	public SMTPTransport connectToSmtp(String host, int port, String userEmail,
	        String oauthToken, boolean debug) throws Exception {

		Properties props = new Properties();
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.starttls.required", "true");
		props.put("mail.smtp.sasl.enable", "false");
		session = Session.getInstance(props);
		session.setDebug(debug);

		final URLName unusedUrlName = null;
		SMTPTransport transport = new SMTPTransport(session, unusedUrlName);
		// If the password is non-null, SMTP tries to do AUTH LOGIN.
		final String emptyPassword = null;
		transport.connect(host, port, userEmail, emptyPassword);

		byte[] response = String.format("user=%s\1auth=Bearer %s\1\1", userEmail,
										oauthToken).getBytes();
		response = BASE64EncoderStream.encode(response);

		transport.issueCommand("AUTH XOAUTH2 " + new String(response), 235);

		return transport;
	}

	public synchronized void sendMail(final String subject, final String body, final String recipients) {
		new Thread() {
			public void run(){
				try {
					SMTPTransport smtpTransport 
						= connectToSmtp("smtp.gmail.com", 587, mailAccount, token, true);

					MimeMessage message = new MimeMessage(session);
								
					DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));   
					message.setSender(new InternetAddress(mailAccount));   
					message.setSubject(subject);   
					message.setDataHandler(handler);

					if (recipients.indexOf(',') > 0)   
						message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));   
					else  
						message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));

					smtpTransport.sendMessage(message, message.getAllRecipients());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void sendEmailAsIntent(String[] recipients) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , recipients);
		i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
		i.putExtra(Intent.EXTRA_TEXT   , "body of email");
		try {
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
			activity.startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(activity, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}
}
