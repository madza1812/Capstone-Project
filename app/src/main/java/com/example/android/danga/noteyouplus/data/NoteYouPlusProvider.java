package com.example.android.danga.noteyouplus.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.danga.noteyouplus.data.NoteYouPlusContract.NoteEntry;

/**
 * Created by An on 5/30/2016.
 */
public class NoteYouPlusProvider extends ContentProvider {

    public static final String TAG = NoteYouPlusProvider.class.getSimpleName();

    private SQLiteOpenHelper mOpenHelper;

    private static final int NOTES = 507;
    private static final int NOTE_ID = 508;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = NoteYouPlusContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, NoteYouPlusContract.PATH_NOTE, NOTES);
        matcher.addURI(authority, NoteYouPlusContract.PATH_NOTE + "/#", NOTE_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new NoteYouPlusDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTES:
                return NoteEntry.NOTE_CONTENT_TYPE;
            case NOTE_ID:
                return NoteEntry.NOTE_CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: "+ uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor = null;
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        switch (sUriMatcher.match(uri)) {
            case NOTES: {
                retCursor = db.query(
                        NoteEntry.NOTE_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case NOTE_ID: {
                retCursor = db.query(
                        NoteEntry.NOTE_TABLE_NAME,
                        projection,
                        NoteEntry.COLUMN_NOTE_ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NOTES: {
                final long id = db.insertOrThrow(NoteEntry.NOTE_TABLE_NAME, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                if (id > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    return NoteEntry.buildItemUri(id);
                } else
                    throw new SQLException("Failed to insert row into " + uri);
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int affectedRows = -1;
        switch (match) {
            case NOTES: {
                affectedRows = db.delete(NoteEntry.NOTE_TABLE_NAME,selection, selectionArgs);
                break;
            }
            case NOTE_ID: {
                affectedRows = db.delete(
                        NoteEntry.NOTE_TABLE_NAME,
                        NoteEntry.COLUMN_NOTE_ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs
                        );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // notify change
        if (selection == null || affectedRows > 0 ) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return affectedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Log.v(TAG, "update item uri: " + uri);
        final int match = sUriMatcher.match(uri);
        int affectedRows = -1;
        switch (match) {
            case NOTES: {
                affectedRows = db.update(
                        NoteEntry.NOTE_TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            }
            case NOTE_ID: {
                Log.v(TAG, "update: parsed id = " + ContentUris.parseId(uri));
                affectedRows = db.update(
                        NoteEntry.NOTE_TABLE_NAME,
                        values,
                        NoteEntry.COLUMN_NOTE_ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // notify change
        if (selection == null || affectedRows > 0 ) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return affectedRows;
    }
}
