/**
 * AuthenticateActivity.java
 * com.burgess.rtd
 *
 * Created Jun 8, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.burgess.rtd.R;
import com.burgess.rtd.constants.Program;
import com.burgess.rtd.controller.AuthenticateController;
import com.burgess.rtd.exceptions.RTDError;
import com.burgess.rtd.interfaces.view.IAuthenticateView;

public class AuthenticateActivity extends Activity implements IAuthenticateView {	
	private AuthenticateController controller;
	private Context context = this;
	private WebView wv;
	private AuthenticateActivity instance = this;
	private RTDError error;
		
	private class AuthenticateWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
	
	private class AuthenticateWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			instance.setProgress(newProgress * 100);
		}
	};
	
	private OnClickListener authButtonOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			Log.i(Program.LOG, "Clicked");
			controller.getAuthToken();
		}
	};
	
	private OnClickListener errorButtonOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			dismissDialog(Program.Dialog.ERROR);
			finish();
		}
	};
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
			case Program.Dialog.GET_FROB:
				dialog = new ProgressDialog(this);
				((ProgressDialog) dialog).setMessage(this.getString(R.string.dialog_get_frob_message));
				dialog.setTitle(this.getString(R.string.dialog_get_frob_title));
				return dialog;
			case Program.Dialog.GET_AUTH:
				dialog = new ProgressDialog(this);
				((ProgressDialog) dialog).setMessage(this.getString(R.string.dialog_get_auth_message));
				dialog.setTitle(this.getString(R.string.dialog_get_auth_title));
				return dialog;
			case Program.Dialog.ERROR:
				dialog = new Dialog(this);
				dialog.setContentView(R.layout.error_dialog);
				dialog.setTitle("Error #" + error.errorCode + " occurred");
				
				TextView tv = (TextView) dialog.findViewById(R.id.error_text);
				tv.setText(error.errorMessageId);
				
				if (!error.showIssueUrl) {
					TextView url = (TextView) dialog.findViewById(R.id.issue_url);
					url.setVisibility(View.INVISIBLE);
				}
				
				Button btn = (Button) dialog.findViewById(R.id.error_button);
				btn.setOnClickListener(errorButtonOnClickListener);
				
				return dialog;
			default:
				return null;
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.authenticate);
		
		wv = (WebView) findViewById(R.id.webview);
		wv.setWebViewClient(new AuthenticateWebViewClient());
		wv.getSettings().setJavaScriptEnabled(true);
		wv.setWebChromeClient(new AuthenticateWebChromeClient());
		
		Button btn = (Button) findViewById(R.id.authbutton);
		btn.setOnClickListener(authButtonOnClickListener);
		
		controller = new AuthenticateController(this);
		controller.initializeView();
	}

	@Override
	public Context getContext() {
		return context;
	}

	@Override
	public void loadUrl(String url) {
		wv.loadUrl(url);
	}

	@Override
	public void createErrorDialog(RTDError error) {
		this.error = error;
		showDialog(Program.Dialog.ERROR);
	}

	@Override
	public SharedPreferences getPreferences() {
		return this.getSharedPreferences(Program.APPLICATION, 0);
	}
}
