/**
 * ListTasksActivity.java
 * com.burgess.rtd
 *
 * Created Jun 24, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.burgess.rtd.constants.Program;
import com.burgess.rtd.controller.ListTasksController;
import com.burgess.rtd.interfaces.view.IListTasksView;

/**
 *
 */
public class ListTasksActivity extends ListActivity implements IListTasksView {
	private ListTasksController controller;
	
	private class ListTasksCursorAdapter extends CursorAdapter {

		public ListTasksCursorAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public void bindView(View view, Context context, Cursor c) {
			String name = c.getString(1);
			String priority = c.getString(4);
			String due = c.getString(2);
			
			TextView tv = (TextView) view.findViewById(R.id.name);
			tv.setText(name);
			
			tv = (TextView) view.findViewById(R.id.priority);
			if (priority.equals("1"))
				tv.setBackgroundColor(Color.parseColor(getString(R.color.high_priority)));
			else if (priority.equals("2"))
				tv.setBackgroundColor(Color.parseColor(getString(R.color.medium_priority)));
			else if (priority.equals("3"))
				tv.setBackgroundColor(Color.parseColor(getString(R.color.low_priority)));
			else
				tv.setBackgroundColor(Color.TRANSPARENT);
			
			if (due != null) {
				Date date = new Date();
				try {
					date = Program.DATE_FORMAT.parse(due);
				} catch (ParseException e) {}
				
				long time = date.getTime();
				time = time + TimeZone.getDefault().getOffset(time);
				date.setTime(time);
				
				tv = (TextView) view.findViewById(R.id.due);
				
				if (DateUtils.isToday(date.getTime())) {
					tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
					if (c.getInt(3) > 0) {
						SimpleDateFormat df = new SimpleDateFormat("h:mm a");
						tv.setText(df.format(date));
					} else {
						tv.setText("Today");
					}
				} else {
					SimpleDateFormat df = new SimpleDateFormat("MMM dd");
					tv.setText(df.format(date));
					if (date.before(Calendar.getInstance().getTime())) {
						tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
						tv.setTextColor(Color.rgb(180, 10, 10));
					}
				}
			} else {
				tv = (TextView) view.findViewById(R.id.due);
				tv.setText("");
			}
		}

		@Override
		public View newView(Context context, Cursor c, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.list_tasks_row, parent, false);
			
			view.setId(c.getInt(0));
			
			return view;
		}
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		controller = new ListTasksController(this);
		controller.initializeView();
	}
	
	public long getListId() {
		return getIntent().getLongExtra("com.burgess.rtd.listId", 0);
	}
	
	public void setTaskListCursor(Cursor cursor) {
		setListAdapter(new ListTasksCursorAdapter(this, cursor));
	}

	@Override
	public Context getContext() {
		return this;
	}

	@Override
	public SharedPreferences getPreferences() {
		return getSharedPreferences(Program.APPLICATION, 0);
	}
}
