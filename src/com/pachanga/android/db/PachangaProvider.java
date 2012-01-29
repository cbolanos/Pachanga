package com.pachanga.android.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;

import com.pachanga.android.db.DatabaseContract.Places;

public class PachangaProvider extends ContentProvider {

	private static final String LOG = PachangaProvider.class.getName();

	public static final int PLACES = 0x100A;
	public static final int PLACES_ID = 0x101A;

	private DatabaseHelper databaseHelper;

	private static final UriMatcher uriMatcher = buildMatcher();

	public static UriMatcher buildMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String CONTENT_AUTHORITY = DatabaseContract.CONTENT_AUTHORITY;

		matcher.addURI(CONTENT_AUTHORITY, "places", PLACES);
		matcher.addURI(CONTENT_AUTHORITY, "places/*", PLACES_ID);

		return matcher;
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {

		return 0;
	}

	@Override
	public String getType(Uri uri) {
		final int match = uriMatcher.match(uri);

		switch (match) {
		case PLACES:
			return Places.CONTENT_TYPE;
		case PLACES_ID:
			return Places.CONTENT_ITEM_TYPE;
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	/**
	 * Insert into the database and notify the change to any Observer that it's
	 * interested on knowing changes for specific Uri
	 * */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final SQLiteDatabase db = databaseHelper.getWritableDatabase();
		final int match = uriMatcher.match(uri);
		switch (match) {

		case PLACES:
			db.insertOrThrow(DatabaseHelper.Tables.PLACES, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Places.buildUri(values.getAsString(BaseColumns._ID));

		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public boolean onCreate() {
		databaseHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		final int match = uriMatcher.match(uri);

		final SQLiteDatabase db = databaseHelper.getReadableDatabase();

		SQLiteQueryBuilder builder = buildExpandedSelection(uri, match);

		Cursor c = builder.query(db, projection, selection, selectionArgs,null, null, sortOrder);

		return c;

	}

	private SQLiteQueryBuilder buildExpandedSelection(Uri uri, int match) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

		String id = null;
		switch (match) {
		case PLACES:
			builder.setTables(DatabaseHelper.Tables.PLACES);
			break;

		case PLACES_ID:
			id = Places.getId(uri);
			builder.setTables(DatabaseHelper.Tables.PLACES);
			builder.appendWhere(BaseColumns._ID + "=" + id);
			break;
		default:
			// If the URI doesn't match any of the known patterns, throw an
			// exception.
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		return builder;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		// Opens the database object in "write" mode.
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		int count;
		String id;
		String finalWhere = null;
		int match = uriMatcher.match(uri);
		switch (match) {

		case PLACES:
			count = db.update(DatabaseHelper.Tables.PLACES, values, selection,selectionArgs);
			break;
		case PLACES_ID:
			id = Places.getId(uri);
			finalWhere = BaseColumns._ID + " = " + id;
			if (selection != null)
				finalWhere = finalWhere + " AND " + selection;

			count = db.update(DatabaseHelper.Tables.PLACES, values, finalWhere,
					selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		
		
		}

		return count;

	}

}
