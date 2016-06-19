package com.example.android.danga.noteyouplus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.android.danga.noteyouplus.data.NoteService;
import com.example.android.danga.noteyouplus.data.NoteYouPlusContract;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailNoteActivityFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor>, NotesListFragment.Query{

    public static final String TAG = DetailNoteActivityFragment.class.getSimpleName();

    public static final String NOTE_ID_SAVED_KEY = "com.example.android.danga.noteyouplus.SAVED_NOTE_ID_KEY";
    public static final String TITLE_SAVED_KEY = "com.example.android.danga.noteyouplus.SAVED_TITLE_KEY";
    public static final String CONTENT_SAVED_KEY = "com.example.android.danga.noteyouplus.SAVED_CONTENT_KEY";
    public static final String BGR_SAVED_KEY = "com.example.android.danga.noteyouplus.SAVED_BGR_KEY";
    public static final String BGR_INTENT_KEY = "com.example.android.danga.noteyouplus.INTENT_BGR_KEY";

    public View rootView;
    public Uri mUri;
    public LinearLayout mDetailNoteView;
    public EditText mNoteTitle;
    public EditText mNoteContent;
    public static int mBgrColor = NoteService.DEFAULT_BGR_COLOR;
    public static boolean isDeletedNote = false;
    public static boolean isArchivedNote = false;
    AlertDialog dialog;

    public int mNoteId = -1;

    public static final int DETAIL_LOADER = 0;


    public DetailNoteActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "Detail Fragment onCreate");
        setHasOptionsMenu(true);
        // Initial
        if (savedInstanceState == null) {
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.getAction().equals(DetailNoteActivity.ACTION_VIEW_NOTE)) {
                mUri = intent.getData();
                getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            } else if (intent != null && intent.getAction().equals(DetailNoteActivity.ACTION_NEW_NOTE)) {
                mBgrColor = intent.getIntExtra(BGR_INTENT_KEY, NoteService.DEFAULT_BGR_COLOR);
            }
        }
        // Screen rotation or other hardware reconfiguration
        else {
            Log.v(TAG, "Screen rotation detected.");

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_detail_note, container, false);
        mDetailNoteView = (LinearLayout) rootView.findViewById(R.id.detail_note_view);
        mNoteTitle = (EditText) rootView.findViewById(R.id.text_note_title);
        mNoteContent = (EditText) rootView.findViewById(R.id.text_note_body);
        mBgrColor = NoteService.DEFAULT_BGR_COLOR;
        return rootView;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mNoteId = savedInstanceState.getInt(NOTE_ID_SAVED_KEY);
            mNoteTitle.setText(savedInstanceState.getString(TITLE_SAVED_KEY, ""));
            mNoteContent.setText(savedInstanceState.getString(CONTENT_SAVED_KEY, ""));
            mBgrColor = savedInstanceState.getInt(BGR_SAVED_KEY, NoteService.DEFAULT_BGR_COLOR);
            mDetailNoteView.setBackgroundColor(Util.getBgrColor(mBgrColor));
        }
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail_note_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        final MenuItem miColorView = menu.findItem(R.id.action_colorize);
        final MenuItem miDeleteView = menu.findItem(R.id.action_delete);
        final MenuItem miRecoverView = menu.findItem(R.id.action_recover);
        if (isDeletedNote) {
            miColorView.setVisible(false);
            miDeleteView.setVisible(true);
            miRecoverView.setVisible(true);
        } else if (isArchivedNote){
            miColorView.setVisible(false);
            miDeleteView.setVisible(false);
            miRecoverView.setVisible(true);
        } else {
            miColorView.setVisible(true);
            miDeleteView.setVisible(true);
            miRecoverView.setVisible(true);
            miColorView.setIcon(Util.getDrawable(mBgrColor));
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_colorize: {
                Log.v(TAG, "onColorize Button clicked!");
                FragmentManager fm = getActivity().getSupportFragmentManager();
                ColorPickDialog colorFragment = new ColorPickDialog();
                colorFragment.show(fm, "color_dialog");
                break;
            }
            case R.id.action_delete: {
                Log.v(TAG, "Delete note is clicked.");
                handleDeleteMenuIconClick();

                break;
            }
            case R.id.action_recover: {
                Log.v(TAG, "Move note back to inbox");
                handleRecoveryMenuIconCLick();
                getActivity().navigateUpTo(NavUtils.getParentActivityIntent(getActivity()));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void setBgrColor(int color) {
        mBgrColor = color;
        mDetailNoteView.setBackgroundColor(Util.getBgrColor(color));
        getActivity().invalidateOptionsMenu();
    }

    public String getShareText(){
        return ("Note title: " + mNoteTitle.getText().toString()
                + "\n Note content: " + mNoteContent.getText().toString());
    }

    public void handleDeleteMenuIconClick() {
        Uri itemUri = NoteYouPlusContract.NoteEntry.buildItemUri(mNoteId);
        ContentValues values = new ContentValues();
        int affectedRows = 0;
        // Delete the deleted note.
        if (isDeletedNote) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.forever_delete_note_message)
                    .setTitle(R.string.forever_delete_note_title)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        //TODO: implement delete function here
                            Uri itemUri = NoteYouPlusContract.NoteEntry.buildItemUri(mNoteId);
                            int affectedRows = getActivity().getContentResolver().delete(
                                    itemUri,
                                    null,
                                    null
                            );
                            if (affectedRows <= 0) {
                                Log.v(TAG, "Failed to delete this note id; " + mNoteId);
                            }
                            dialog.cancel();
                            getActivity().navigateUpTo(NavUtils.getParentActivityIntent(getActivity()));
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            dialog = builder.create();
            dialog.show();
        }
        // Delete the archived note
        else if (isArchivedNote) {
            values.put(NoteYouPlusContract.NoteEntry.COLUMN_TITLE, mNoteTitle.getText().toString());
            values.put(NoteYouPlusContract.NoteEntry.COLUMN_CONTENT,mNoteContent.getText().toString());
            values.put(NoteYouPlusContract.NoteEntry.COLUMN_BG_COLOR, mBgrColor);
            values.put(NoteYouPlusContract.NoteEntry.COLUMN_IS_DELETED, true);
            values.put(NoteYouPlusContract.NoteEntry.COLUMN_IS_ARCHIVED, false);
            affectedRows = getActivity().getContentResolver().update(
                    itemUri,
                    values,
                    null,
                    null
            );
            if (affectedRows <= 0) {
                Log.v(TAG, "Failed to update this note id; " + mNoteId);
            }
            getActivity().navigateUpTo(NavUtils.getParentActivityIntent(getActivity()));
        }
        //  Delete the currently active note.
        else {
            values.put(NoteYouPlusContract.NoteEntry.COLUMN_TITLE, mNoteTitle.getText().toString());
            values.put(NoteYouPlusContract.NoteEntry.COLUMN_CONTENT,mNoteContent.getText().toString());
            values.put(NoteYouPlusContract.NoteEntry.COLUMN_BG_COLOR, mBgrColor);
            values.put(NoteYouPlusContract.NoteEntry.COLUMN_IS_DELETED, true);
            affectedRows = getActivity().getContentResolver().update(
                    itemUri,
                    values,
                    null,
                    null
            );
            if (affectedRows <= 0) {
                Log.v(TAG, "Failed to update this note id; " + mNoteId);
            }
            getActivity().navigateUpTo(NavUtils.getParentActivityIntent(getActivity()));
        }
    }

    public void handleRecoveryMenuIconCLick() {
        Uri itemUri = NoteYouPlusContract.NoteEntry.buildItemUri(mNoteId);
        ContentValues values = new ContentValues();
        int affectedRows = 0;
        if(isDeletedNote) {
            values.put(NoteYouPlusContract.NoteEntry.COLUMN_TITLE, mNoteTitle.getText().toString());
            values.put(NoteYouPlusContract.NoteEntry.COLUMN_CONTENT,mNoteContent.getText().toString());
            values.put(NoteYouPlusContract.NoteEntry.COLUMN_BG_COLOR, mBgrColor);
            values.put(NoteYouPlusContract.NoteEntry.COLUMN_IS_DELETED, false);
            affectedRows = getActivity().getContentResolver().update(
                    itemUri,
                    values,
                    null,
                    null
            );
            if (affectedRows <= 0) {
                Log.v(TAG, "Failed to update this note id; " + mNoteId);
            }
            getActivity().navigateUpTo(NavUtils.getParentActivityIntent(getActivity()));
        } else if (isArchivedNote) {
            values.put(NoteYouPlusContract.NoteEntry.COLUMN_TITLE, mNoteTitle.getText().toString());
            values.put(NoteYouPlusContract.NoteEntry.COLUMN_CONTENT,mNoteContent.getText().toString());
            values.put(NoteYouPlusContract.NoteEntry.COLUMN_BG_COLOR, mBgrColor);
            values.put(NoteYouPlusContract.NoteEntry.COLUMN_IS_ARCHIVED, false);
            affectedRows = getActivity().getContentResolver().update(
                    itemUri,
                    values,
                    null,
                    null
            );
            if (affectedRows <= 0) {
                Log.v(TAG, "Failed to update this note id; " + mNoteId);
            }
            getActivity().navigateUpTo(NavUtils.getParentActivityIntent(getActivity()));
        } else {
            values.put(NoteYouPlusContract.NoteEntry.COLUMN_TITLE, mNoteTitle.getText().toString());
            values.put(NoteYouPlusContract.NoteEntry.COLUMN_CONTENT,mNoteContent.getText().toString());
            values.put(NoteYouPlusContract.NoteEntry.COLUMN_BG_COLOR, mBgrColor);
            values.put(NoteYouPlusContract.NoteEntry.COLUMN_IS_ARCHIVED, true);
            affectedRows = getActivity().getContentResolver().update(
                    itemUri,
                    values,
                    null,
                    null
            );
            if (affectedRows <= 0) {
                Log.v(TAG, "Failed to update this note id; " + mNoteId);
            }
            getActivity().navigateUpTo(NavUtils.getParentActivityIntent(getActivity()));
        }
    }

    @Override
    public void onPause() {
        Log.v(TAG, "onPause: updating current note");
        // Updating the current note
        if (!isArchivedNote && !isDeletedNote && mNoteId > -1) {
            Uri itemUri = NoteYouPlusContract.NoteEntry.buildItemUri(mNoteId);
            ContentValues values = new ContentValues();
            int rowsUpdated = 0;
            values.put(NoteYouPlusContract.NoteEntry.COLUMN_TITLE, mNoteTitle.getText().toString());
            values.put(NoteYouPlusContract.NoteEntry.COLUMN_CONTENT,mNoteContent.getText().toString());
            values.put(NoteYouPlusContract.NoteEntry.COLUMN_BG_COLOR, mBgrColor);
            rowsUpdated = getActivity().getContentResolver().update(
                    itemUri,
                    values,
                    null,
                    null
            );
            if (rowsUpdated <= 0) {
                Log.v(TAG, "Failed to update this note id; " + mNoteId);
            }
        }
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(NOTE_ID_SAVED_KEY, mNoteId);
        outState.putString(TITLE_SAVED_KEY, mNoteTitle.getText().toString());
        outState.putString(CONTENT_SAVED_KEY, mNoteContent.getText().toString());
        outState.putInt(BGR_SAVED_KEY, mBgrColor);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    NotesListFragment.Query.PROJECTION,
                    null,
                    null,
                    null
            );
        } else {
            Log.v(TAG, "incorrect Uri");
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(TAG, "onLoadFinished");
        if(data != null && data.moveToFirst()) {
            mNoteId = data.getInt(NotesListFragment.Query.PROJECTION_ID);
            mNoteTitle.setText(data.getString(NotesListFragment.Query.PROJECTION_TITLE));
            mNoteContent.setText(data.getString(NotesListFragment.Query.PROJECTION_CONTENT));
            mBgrColor = data.getInt(NotesListFragment.Query.PROJECTION_BG_COLOR);
            mDetailNoteView.setBackgroundColor(Util.getBgrColor(mBgrColor));
            isDeletedNote = (data.getInt(NotesListFragment.Query.PROJECTION_IS_DELETED) == 1);
            isArchivedNote = (data.getInt(NotesListFragment.Query.PROJECTION_IS_ARCHIVED) == 1);
            if (isDeletedNote || isArchivedNote){
                disableEditText(mNoteTitle);
                disableEditText(mNoteContent);
            }
            getActivity().invalidateOptionsMenu();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setBackgroundColor(Color.TRANSPARENT);
    }
}
