package com.example.android.danga.noteyouplus.data;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.example.android.danga.noteyouplus.NotesListFragment;

/**
 * Created by An on 9/8/2016.
 */
public class NoteContentObserver extends ContentObserver {

    private ContentObserverCallBack contentObserverCallback;
    private Context mContext;
    public NoteContentObserver(Handler handler, Context context, ContentObserverCallBack contentObserverCallBack) {
        super(handler);
        this.contentObserverCallback = contentObserverCallBack;
        mContext = context;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                NotesListFragment.Query.PROJECTION,
                null,
                null,
                null
        );
        if(cursor != null && cursor.getCount() > 0) {
            contentObserverCallback.updateNoteContent(cursor);
        }
    }
}
