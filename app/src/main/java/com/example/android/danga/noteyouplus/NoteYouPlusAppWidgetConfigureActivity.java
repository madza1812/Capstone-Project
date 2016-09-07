package com.example.android.danga.noteyouplus;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.danga.noteyouplus.data.NoteYouPlusContract;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The configuration screen for the {@link NoteYouPlusAppWidget NoteYouPlusAppWidget} AppWidget.
 */
public class NoteYouPlusAppWidgetConfigureActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String PREFS_NAME = "com.example.android.danga.noteyouplus.NoteYouPlusAppWidget.WIDGETNOTEID_NAME";
    private static final String PREF_WIDGET_PREFIX_KEY = "adang.noteyouplus_appwidget_id";
    private static final String PREF_WIDGET_PREFIX_NOTE_KEY = "adang.noteyouplus_note_id";
    private static final String PREF_WIDGET_PREFIX_TEXT_SIZE_KEY = "adang.noteyouplus_widget_text_size";
    static final int WIDGET_SMALL_TEXT_SIZE = 10001;
    static final int WIDGET_MEDIUM_TEXT_SIZE = 10002;
    static final int WIDGET_LARGE_TEXT_SIZE = 10003;
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    long mNoteId = -1;
    RecyclerView mAppWidgetConfigureRv;

    public NoteYouPlusAppWidgetConfigureActivity() {
        super();
    }

    // Save app widget Id for note id and note Id for set of widget Ids
    static void saveNoteIdPref(Context context, int appWidgetId, long noteId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putLong(PREF_WIDGET_PREFIX_KEY + appWidgetId, noteId);
        Set<String> listAppWidgetId = loadListAppWidgetIdPref(context, noteId);
        listAppWidgetId.add(String.valueOf(appWidgetId));
        prefs.putStringSet(PREF_WIDGET_PREFIX_NOTE_KEY + noteId,listAppWidgetId);
        prefs.putInt(PREF_WIDGET_PREFIX_TEXT_SIZE_KEY + appWidgetId, WIDGET_MEDIUM_TEXT_SIZE);
        prefs.apply();
    }

    static long loadNoteIdPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Long noteId = prefs.getLong(PREF_WIDGET_PREFIX_KEY + appWidgetId, -1);
        if (noteId != null) {
            return noteId;
        } else {
            return -1;
        }
    }

    static void deleteNoteIdPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        Long noteId = loadNoteIdPref(context, appWidgetId);
        Set<String> listAppWidgetId = loadListAppWidgetIdPref(context, noteId);
        listAppWidgetId.remove(appWidgetId);
        prefs.remove(PREF_WIDGET_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    static Set<String> loadListAppWidgetIdPref(Context context, long noteId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Set<String> listAppWidgetId = prefs.getStringSet(PREF_WIDGET_PREFIX_NOTE_KEY + noteId, new HashSet<String>());
        return listAppWidgetId;
    }

    static int loadTextSizePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        int textSize = prefs.getInt(PREF_WIDGET_PREFIX_TEXT_SIZE_KEY + appWidgetId, WIDGET_MEDIUM_TEXT_SIZE);
        return textSize;
    }

    static void saveTextSizePref(Context context, int appWidgetId, int textsize){
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_WIDGET_PREFIX_TEXT_SIZE_KEY + appWidgetId, textsize);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setResult(RESULT_CANCELED);

        setContentView(R.layout.note_you_plus_app_widget_configure);

        mAppWidgetConfigureRv = (RecyclerView) findViewById(R.id.appwidget_configure_rv);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
        getLoaderManager().initLoader(0,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String selection = NoteYouPlusContract.NoteEntry.COLUMN_IS_DELETED + " = ? AND "
                + NoteYouPlusContract.NoteEntry.COLUMN_IS_ARCHIVED + " = ?";
        return new CursorLoader(
                this,
                NoteYouPlusContract.NoteEntry.NOTE_CONTENT_URI,
                null,
                selection,
                new String[]{"0", "0"},
                NoteYouPlusContract.NoteEntry.DEFAULT_SORT
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.getCount() > 0) {
            AppWidgetNotesAdapter adapter = new AppWidgetNotesAdapter(data);
            adapter.setHasStableIds(true);
            mAppWidgetConfigureRv.setAdapter(adapter);
            mAppWidgetConfigureRv.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAppWidgetConfigureRv.setAdapter(null);
    }

    public class AppWidgetNotesAdapter extends RecyclerView.Adapter<ViewHolder> {

        public final String TAG = AppWidgetNotesAdapter.class.getSimpleName();

        private Cursor mCursor;

        public AppWidgetNotesAdapter(Cursor cursor) {
            mCursor = cursor;
        }

        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getInt(NotesListFragment.Query.PROJECTION_ID);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.app_widget_configure_list_item, parent, false);
            final ViewHolder vh = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "onClick of ViewHolder");
                    mNoteId = getItemId(vh.getAdapterPosition());
                    final Context context = NoteYouPlusAppWidgetConfigureActivity.this;
                    saveNoteIdPref(context, mAppWidgetId, mNoteId);

                    // It is the responsibility of the configuration activity to update the app widget
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    NoteYouPlusAppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId,
                            vh.mTitleView.getText().toString(),
                            vh.mContentView.getText().toString(),
                            ((ColorDrawable) vh.mView.getBackground()).getColor()
                    );

                    // Make sure we pass back the original appWidgetId
                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                    setResult(RESULT_OK, resultValue);
                    finish();
                }
            });

            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mTitleView.setText(mCursor.getString(NotesListFragment.Query.PROJECTION_TITLE));
            holder.mContentView.setText(mCursor.getString(NotesListFragment.Query.PROJECTION_CONTENT));
            holder.mView.setBackgroundColor(Util.getBgrColor(mCursor.getInt(NotesListFragment.Query.PROJECTION_BG_COLOR)));
        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mContentView;
        public final CardView cv;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            cv = (CardView) view.findViewById(R.id.appwidget_cardview_notes);
            mTitleView = (TextView) view.findViewById(R.id.appwidget_configure_note_title);
            mContentView = (TextView) view.findViewById(R.id.appwidget_configure_note_content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}

