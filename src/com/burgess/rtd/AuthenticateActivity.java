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
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.burgess.rtd.controller.AuthenticateController;
import com.burgess.rtd.interfaces.view.IAuthenticateView;

public class AuthenticateActivity extends Activity implements IAuthenticateView {	
	private AuthenticateController controller;
	private Context context = this;
	private WebView wv;
	private ProgressDialog dialog;
	private AuthenticateActivity instance = this;
	
	TextView tv;
		
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
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		
		switch (id) {
			default:
				dialog = new Dialog(context);
		}
		
		return dialog;
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
		
		tv = (TextView) findViewById(R.id.text);
		
		controller = new AuthenticateController(this);
		controller.initializeView();
	}

	@Override
	public void createDialog(String message) {
		dialog = new ProgressDialog(this);
		dialog.setOwnerActivity(this);
		dialog.setTitle(message);
		dialog.show();
	}
	
	@Override
	public void dismissDialog() {
		dialog.dismiss();
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
	public void createErrorDialog(int id) {
		
	}
}
