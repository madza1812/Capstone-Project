package com.example.android.danga.noteyouplus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class DetailNoteActivity extends AppCompatActivity
    implements FragmentCallBack{

    public static final String TAG = DetailNoteActivity.class.getSimpleName();
    public static final String ACTION_NEW_NOTE= "com.example.android.danga.noteyouplus.ACTION_NEW_NOTE";
    public static final String ACTION_VIEW_NOTE = "com.example.android.danga.noteyouplus.ACTIONVIEW_NOTE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_detail_text_note);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.detail_share_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailNoteActivityFragment detailFragment = (DetailNoteActivityFragment)
                        getSupportFragmentManager().findFragmentById(R.id.detail_note_fragment);
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(DetailNoteActivity.this)
                        .setType("text/plain")
                        .setText(detailFragment.getShareText())
                        .getIntent(), getString(R.string.action_share)));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_note, menu);
        return true;
    }

    @Override
    public void onNoteSelected(Uri uri) {
        // Not handling here
    }

    @Override
    public void onColorSelected(int color) {
        //TODO: updating the Note Detail Background color.
        Log.v(TAG, "onColorSelected");
        DetailNoteActivityFragment detailFragment = (DetailNoteActivityFragment)
                getSupportFragmentManager().findFragmentById(R.id.detail_note_fragment);
        if (detailFragment != null) {
            detailFragment.setBgrColor(color);
        }
    }
}
