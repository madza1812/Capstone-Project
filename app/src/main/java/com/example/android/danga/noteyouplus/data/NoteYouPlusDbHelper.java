package com.example.android.danga.noteyouplus.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.danga.noteyouplus.data.NoteYouPlusContract.NoteEntry;

/**
 * Created by An on 5/30/2016.
 */
public class NoteYouPlusDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "noteyouplus.db";
    private static final int DATABASE_VERSION = 1;

    public NoteYouPlusDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ NoteEntry.NOTE_TABLE_NAME + " ("
            + NoteEntry.COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + NoteEntry.COLUMN_TITLE + " TEXT,"
            + NoteEntry.COLUMN_CONTENT + " TEXT,"
            + NoteEntry.COLUMN_NOTE_TYPE + " TEXT NOT NULL,"
            + NoteEntry.COLUMN_PHOTO_URL + " TEXT,"
            + NoteEntry.COLUMN_ENCRYPTED_CONTENT + " TEXT,"
            + NoteEntry.COLUMN_ENCRYPTED_PASS + " TEXT,"
            + NoteEntry.COLUMN_CREATED_USER + " TEXT NOT NULL,"
            + NoteEntry.COLUMN_MODIFIED_USER + " TEXT NOT NULL,"
            + NoteEntry.COLUMN_MODIFIED_DATE + " INTEGER NOT NULL,"
            + NoteEntry.COLUMN_OTHER_USERS + " TEXT,"
            + NoteEntry.COLUMN_IS_DELETED + " BOOLEAN NOT NULL DEFAULT FALSE,"
            + NoteEntry.COLUMN_IS_ARCHIVED + " BOOLEAN NOT NULL DEFAULT FALSE,"
            + NoteEntry.COLUMN_BG_COLOR + " INTEGER NOT NULL DEFAULT 0);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NoteEntry.NOTE_TABLE_NAME);
        onCreate(db);
    }
}
