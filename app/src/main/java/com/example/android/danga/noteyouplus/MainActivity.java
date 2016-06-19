package com.example.android.danga.noteyouplus;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.danga.noteyouplus.data.NoteService;
import com.example.android.danga.noteyouplus.data.NoteYouPlusContract;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FragmentCallBack {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String FRAGMENT_ARG_KEY = "com.example.android.danga.noteyouplus.FRAGMENT_ARG_KEY";
    public static final String NOTES_TAG = "com.example.android.danga.noteyouplus.NOTES_TAG";
    public static final String DELETED_NOTES_TAG = "com.example.android.danga.noteyouplus.DELETED_NOTES_TAG";
    public static final String ARCHIVED_NOTES_TAG = "com.example.android.danga.noteyouplus.ARCHIVED_NOTES_TAG";

    public static final String NAV_IND_SAVED_KEY = "com.example.android.danga.noteyouplus.NAV_IND_SAVED_KEY";

    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    public static int mNavDrawerInd = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        // Insert a dummy record.
        //NoteService.startActionDownload(MainActivity.this, "param1", "param2" );

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        // Launching View
        if (savedInstanceState == null) {
            mNavigationView.getMenu().getItem(0).setChecked(true);
            loadFragment(R.id.nav_notes);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNavigationView.getMenu().getItem(mNavDrawerInd).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_help) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mNavDrawerInd = savedInstanceState.getInt(NAV_IND_SAVED_KEY, 0);
            mNavigationView.getMenu().getItem(mNavDrawerInd).setChecked(true);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(NAV_IND_SAVED_KEY, mNavDrawerInd);
        super.onSaveInstanceState(outState);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        loadFragment(id);
        return true;
    }

    public void loadFragment(int id) {
        // Initialize fragment manager to add new fragment into its container.
        FragmentManager fm = getSupportFragmentManager();
        Fragment nextFragment = null;
        String tag = "";
        switch (id) {
            case R.id.nav_notes:
                mNavDrawerInd = 0;
                tag = NOTES_TAG;
                nextFragment = fm.findFragmentByTag(NOTES_TAG);
                if (nextFragment == null)
                    nextFragment = NotesListFragment.newInstance(2,NotesListFragment.ACTIVE_NOTES);
                fm.beginTransaction()
                        .replace(R.id.main_fragment_container, nextFragment, tag)
                        .addToBackStack(tag)
                        .commit();

                //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (mDrawerLayout != null &&  mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
                break;
            case R.id.nav_deleted:
                mNavDrawerInd = 1;
                tag = DELETED_NOTES_TAG;
                nextFragment = fm.findFragmentByTag(DELETED_NOTES_TAG);
                if (nextFragment == null) {
                    nextFragment = NotesListFragment.newInstance(1,NotesListFragment.DELETED_NOTES);
                }
                fm.beginTransaction()
                        .replace(R.id.main_fragment_container, nextFragment, tag)
                        .addToBackStack(tag)
                        .commit();

                //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (mDrawerLayout != null &&  mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
                break;
            case R.id.nav_archived:
                mNavDrawerInd = 2;
                tag = ARCHIVED_NOTES_TAG;
                nextFragment = fm.findFragmentByTag(ARCHIVED_NOTES_TAG);
                if (nextFragment == null)
                    nextFragment = NotesListFragment.newInstance(1, NotesListFragment.ARCHIVED_NOTES);
                fm.beginTransaction()
                        .replace(R.id.main_fragment_container, nextFragment, tag)
                        .addToBackStack(tag)
                        .commit();

                //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (mDrawerLayout != null &&  mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
                break;
            case R.id.nav_setting:
                startActivity(new Intent(this, SettingsActivity.class));
                if (mDrawerLayout != null &&  mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
            default:
                break;
        }
    }

    @Override
    public void onNoteSelected(Uri selectedItemUri) {
        // Fragment Item Selected
        // TODO: Launch Details Activity for selected note.
        Log.v(TAG, "Selected Item uri " + selectedItemUri);
        Intent detailIntent = new Intent(this, DetailNoteActivity.class)
                .setAction(DetailNoteActivity.ACTION_VIEW_NOTE)
                .setData(selectedItemUri);
        startActivity(detailIntent);
    }

    @Override
    public void onColorSelected(int color) {
        //Not handling here
    }
}
