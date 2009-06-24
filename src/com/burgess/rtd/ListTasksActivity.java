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

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.burgess.rtd.constants.Program;
import com.burgess.rtd.controller.ListTasksController;
import com.burgess.rtd.interfaces.view.IListTasksView;
import com.burgess.rtd.model.List;

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
			
			TextView tv = (TextView) view.findViewById(R.id.name);
			tv.setText(name);
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
