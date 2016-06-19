package com.example.android.danga.noteyouplus;

import android.net.Uri;

/**
 * Created by An on 5/30/2016.
 */
public interface FragmentCallBack {
        void onNoteSelected(Uri uri);
        void onColorSelected(int color);
}
