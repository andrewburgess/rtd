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
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.burgess.rtd.constants.Program;
import com.burgess.rtd.controller.ListTasksController;
import com.burgess.rtd.interfaces.view.IListTasksView;

/**
 *
 */
public class ListTasksActivity extends ListActivity implements IListTasksView {
	protected static final int MENU = 0;
	protected static final int COMPLETE = 0;
	protected static final int POSTPONE = 1;
	
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
					tv.setTextColor(Color.parseColor(getString(R.color.medium_gray)));
					if (c.getInt(3) > 0) {
						SimpleDateFormat df = new SimpleDateFormat("h:mm a");
						tv.setText("Due: " + df.format(date));
					} else {
						tv.setText("Today");
					}
				} else {
					SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy");
					tv.setText("Due: " + df.format(date));
					if (date.before(Calendar.getInstance().getTime())) {
						tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
						tv.setTextColor(Color.parseColor(getString(R.color.warning)));
					} else {
						tv.setTextColor(Color.parseColor(getString(R.color.medium_gray)));
					}
				}
			} else {
				tv = (TextView) view.findViewById(R.id.due);
				tv.setVisibility(View.GONE);
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
	
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Task Options");
		menu.add(MENU, COMPLETE, COMPLETE, "Complete");
		menu.add(MENU, POSTPONE, POSTPONE, "Postpone");
	}
	
	public boolean onContextItemSelected(MenuItem item) {
		//AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		switch (item.getItemId()) {
			case COMPLETE:
				//viewTask(info.id);
				break;
			case POSTPONE:
				//renameList(info.id, ((TextView)info.targetView.findViewById(R.id.name)).getText());
				break;
		}
		
		return true;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		controller = new ListTasksController(this);
		controller.initializeView();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		controller.stop();
	}
	
	public long getListId() {
		return getIntent().getLongExtra("com.burgess.rtd.listId", 0);
	}
	
	public void setupTaskList(Cursor cursor) {
		setListAdapter(new ListTasksCursorAdapter(this, cursor));
		registerForContextMenu(getListView());
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
