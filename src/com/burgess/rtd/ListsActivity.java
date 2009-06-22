/**
 * ListsActivity.java
 * com.burgess.rtd
 *
 * Created Jun 22, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.burgess.rtd.constants.Program;
import com.burgess.rtd.controller.ListsController;
import com.burgess.rtd.exceptions.RTDError;
import com.burgess.rtd.interfaces.view.IListsView;
import com.burgess.rtd.model.List;

/**
 *
 */
public class ListsActivity extends ListActivity implements IListsView {
	private ListsController controller;
	private RTDError error;
	
	private OnClickListener errorButtonOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			dismissDialog(Program.Dialog.ERROR);
			finish();
		}
	};
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		controller = new ListsController(this);
		controller.initializeView();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
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
	public Context getContext() {
		return this;
	}

	@Override
	public void setListsCursor(Cursor cursor) {
		setListAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, new String[] {List.NAME}, new int[] {android.R.id.text1}));		
	}
	
	@Override
	public void createErrorDialog(RTDError error) {
		this.error = error;
		showDialog(Program.Dialog.ERROR);
	}
	
}
