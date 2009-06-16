/**
 * Database.java
 * com.burgess.rtd.model
 *
 * Created Jun 15, 2009
 *
 * Author: Andrew Burgess
 * Email: abombm1@gmail.com
 * Copyright: 2009
 */
package com.burgess.rtd.model;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.burgess.rtd.R;
import com.burgess.rtd.constants.Program;
import com.burgess.rtd.exceptions.RTDException;

/**
 *
 */
public class Database {
	private static final String DATABASE_NAME = "rtd.db";
	private static final int DATABASE_VERSION = 2;
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(Program.LOG, "Creating database");
			db.execSQL(List.CREATE);
			db.execSQL(Location.CREATE);
			db.execSQL(Note.CREATE);
			db.execSQL(Tag.CREATE);
			db.execSQL(Task.CREATE);
			db.execSQL(TaskSeries.CREATE);
			db.execSQL(TaskTag.CREATE);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL(List.DESTROY);
			db.execSQL(Location.DESTROY);
			db.execSQL(Note.DESTROY);
			db.execSQL(Tag.DESTROY);
			db.execSQL(Task.DESTROY);
			db.execSQL(TaskSeries.DESTROY);
			db.execSQL(TaskTag.DESTROY);
			onCreate(db);
		}
	}
	
	private DatabaseHelper dbHelper;
	private Context context;
	private SQLiteDatabase db;
	
	public Database(Context context) {
		this.context = context;
	}
	
	public Database open() throws RTDException {
		try {
			dbHelper = new DatabaseHelper(context);
			db = dbHelper.getWritableDatabase();
			return this;
		} catch (SQLException e) {
			throw new RTDException(Program.Error.SQL_EXCEPTION, R.string.error_sql, true, e);
		}
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public SQLiteDatabase getDb() throws RTDException {
		if (db.isOpen())
			return db;
		else {
			this.open();
			return db;
		}
	}
}
