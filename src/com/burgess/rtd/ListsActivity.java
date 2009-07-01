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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.burgess.rtd.constants.Program;
import com.burgess.rtd.controller.ListsController;
import com.burgess.rtd.exceptions.RTDError;
import com.burgess.rtd.interfaces.view.IListsView;
import com.burgess.rtd.model.List;

/**
 *
 */
public class ListsActivity extends ListActivity implements IListsView {
	private static final int MENU = 0;
	private static final int VIEW_TASKS = 0;
	private static final int RENAME_LIST = 2;
	private static final int DELETE_LIST = 3;
	private static final int SET_AS_DEFAULT = 1;
	private static final int ARCHIVE = 4;
	
	private ListsController controller;
	private RTDError error;
	private Context context = this;
	private AlertDialog renameDialog;
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			viewTask(id);
		}
	
	};
	
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("List Options");
		menu.add(MENU, VIEW_TASKS, VIEW_TASKS, "View Tasks");
		menu.add(MENU, RENAME_LIST, RENAME_LIST, "Rename List");
		menu.add(MENU, DELETE_LIST, DELETE_LIST, "Delete List");
		menu.add(MENU, SET_AS_DEFAULT, SET_AS_DEFAULT, "Set As Default");
		menu.add(MENU, ARCHIVE, ARCHIVE, "Archive");
	}
	
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		switch (item.getItemId()) {
			case VIEW_TASKS:
				viewTask(info.id);
				break;
			case RENAME_LIST:
				renameList(info.id, ((TextView)info.targetView.findViewById(R.id.name)).getText());
		}
		
		return true;
	}
	
	private OnClickListener errorButtonOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			dismissDialog(Program.Dialog.ERROR);
			if (error.isFatal)
				finish();
		}
	};
	
	private OnClickListener onRenameEnterClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			
			
			renameDialog.dismiss();
		}
	};
	
	private OnClickListener onRenameCancelClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			renameDialog.dismiss();
		}
	};
	
	private class ListsCursorAdapter extends CursorAdapter {

		public ListsCursorAdapter(Context context, Cursor c) {
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
			View view = inflater.inflate(R.layout.lists_row, parent, false);
			
			view.setId(c.getInt(c.getColumnIndex(List.ID)));
			
			return view;
		}
		
	}
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle("Remember the Droid :: Lists");
		
		getListView().setOnItemClickListener(itemClickListener);
		
		controller = new ListsController(this);
		controller.initializeView();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		controller.stop();
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
	public void setupListView(Cursor cursor) {
		setListAdapter(new ListsCursorAdapter(this, cursor));
		registerForContextMenu(getListView());
	}
	
	@Override
	public void createErrorDialog(RTDError error) {
		this.error = error;
		showDialog(Program.Dialog.ERROR);
	}
	
	private void viewTask(long taskId) {
		Intent intent = new Intent(context, ListTasksActivity.class);
		intent.putExtra("com.burgess.rtd.listId", taskId);
		
		startActivityForResult(intent, 0);
	}
	
	private void renameList(long listId, CharSequence name) {
		AlertDialog.Builder builder;
		
		LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.rename_list, (ViewGroup) getListView(), false);
		EditText edit = (EditText)layout.findViewById(R.id.edit);
		edit.setText(name);
		
		Button button = (Button)layout.findViewById(R.id.enter);
		button.setOnClickListener(onRenameEnterClickListener);
		
		button = (Button)layout.findViewById(R.id.cancel);
		button.setOnClickListener(onRenameCancelClickListener);
		
		builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		
		renameDialog = builder.create();
		renameDialog.setTitle("Rename List");
		renameDialog.show();
	}
	
}
