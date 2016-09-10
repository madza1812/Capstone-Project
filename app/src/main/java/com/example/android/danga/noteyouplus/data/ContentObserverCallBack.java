package com.example.android.danga.noteyouplus.data;

import android.database.Cursor;

/**
 * Created by An on 9/8/2016.
 */
public interface ContentObserverCallBack {
    void updateNoteContent(Cursor cursor);
}
