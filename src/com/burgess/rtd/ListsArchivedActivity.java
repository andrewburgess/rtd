/**
 * ListsArchivedActivity.java
 * com.burgess.rtd
 *
 * Created Jul 2, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd;

import com.burgess.rtd.constants.Program;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 *
 */
public class ListsArchivedActivity extends ListsActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		viewingArchived = true;
		
		super.onCreate(savedInstanceState);
		
		setTitle("Remember the Droid :: Archived Lists");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, Program.Menu.VIEW_ARCHIVED, 0, "View Lists").setIcon(android.R.drawable.ic_menu_more);
    	return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		case Program.Menu.VIEW_ARCHIVED:
    			Intent intent = new Intent(context, ListsActivity.class);
    			startActivity(intent);
    			finish();
    			return true;
    		default:
    			return false;
    	}
    }
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("List Options");
		menu.add(MENU, VIEW_TASKS, VIEW_TASKS, "View Tasks");
		menu.add(MENU, RENAME_LIST, RENAME_LIST, "Rename List");
		menu.add(MENU, DELETE_LIST, DELETE_LIST, "Delete List");
		menu.add(MENU, ARCHIVE, ARCHIVE, "Unarchive");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		switch (item.getItemId()) {
			case VIEW_TASKS:
				viewTask(info.id);
				break;
			case RENAME_LIST:
				renameList(info.id, ((TextView)info.targetView.findViewById(R.id.name)).getText());
				break;
			case ARCHIVE:
				controller.setListArchived(info.id, false);
				controller.initializeView();
				Toast.makeText(this, "List unarchived", Toast.LENGTH_SHORT).show();
				break;
		}
		
		return true;
	}

}
