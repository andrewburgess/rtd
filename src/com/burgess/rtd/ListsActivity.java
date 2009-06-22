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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.burgess.rtd.constants.Program;
import com.burgess.rtd.controller.ListsController;
import com.burgess.rtd.exceptions.RTDError;
import com.burgess.rtd.interfaces.view.IListsView;
import com.burgess.rtd.model.List;
import com.burgess.rtd.model.Task;
import com.burgess.rtd.model.TaskSeries;

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
	
	private class TaskCursorAdapter extends CursorAdapter {

		public TaskCursorAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public void bindView(View view, Context context, Cursor c) {
			String name = c.getString(c.getColumnIndex(TaskSeries.NAME));
			String due = c.getString(c.getColumnIndex(Task.DUE_DATE));
			String priority = c.getString(c.getColumnIndex(Task.PRIORITY));
			
			TextView tv = (TextView) view.findViewById(R.id.name);
			tv.setText(name);
			
			if (c.getInt(c.getColumnIndex(Task.HAS_DUE_TIME)) > 0) {
				SimpleDateFormat df = new SimpleDateFormat("h:mma");
				Date date = new Date();
				try {
					date = Program.DATE_FORMAT.parse(due);
				} catch (ParseException e) {
					
				}
				
				long time = date.getTime();
				time = time + TimeZone.getDefault().getOffset(time);
				date.setTime(time);
				
				tv = (TextView) view.findViewById(R.id.due);
				tv.setText(df.format(date));
			}
			
			tv = (TextView) view.findViewById(R.id.priority);
			if (priority.equals("1"))
				tv.setBackgroundColor(Color.parseColor(getString(R.color.high_priority)));
			else if (priority.equals("2"))
				tv.setBackgroundColor(Color.parseColor(getString(R.color.medium_priority)));
			else if (priority.equals("3"))
				tv.setBackgroundColor(Color.parseColor(getString(R.color.low_priority)));
		}

		@Override
		public View newView(Context context, Cursor c, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.initial_row, parent, false);
			
			view.setId(c.getInt(c.getColumnIndex(List.ID)));
			
			return view;
		}
		
	}
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle("Remember the Droid :: Lists");
		
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
