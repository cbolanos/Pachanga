package com.pachanga.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.pachanga.android.db.DatabaseContract.PlacesColumns;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String database = "pachanga";
	private static final int VERSION = 1;

	/**
	 * Represents all the tables name on the database
	 * 
	 * */
	interface Tables {
		String PLACES = "Places";
	}

	public DatabaseHelper(Context context) {
		super(context, database, null, VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Places table
		db.execSQL("CREATE TABLE " + Tables.PLACES + " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTO_INCREMENT, "
				+ PlacesColumns.PLACE_ID + " TEXT, "
				+ PlacesColumns.NAME + " TEXT, "
				+ PlacesColumns.DESCRIPTION + " TEXT, "
				+ PlacesColumns.PICTURE + " BLOB, "
				+ PlacesColumns.RATE + " REAL, "
				+ PlacesColumns.TYPE + " TEXT, "
				+ PlacesColumns.COORD_X + " REAL, "
				+ PlacesColumns.COORD_Y + " REAL, "
				+ PlacesColumns.FLAG + " INTEGER);");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}