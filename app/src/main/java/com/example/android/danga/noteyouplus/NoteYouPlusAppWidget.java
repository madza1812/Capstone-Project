package com.example.android.danga.noteyouplus;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.example.android.danga.noteyouplus.data.NoteYouPlusContract;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link NoteYouPlusAppWidgetConfigureActivity NoteYouPlusAppWidgetConfigureActivity}
 */
public class NoteYouPlusAppWidget extends AppWidgetProvider{

    public static final String TAG = NoteYouPlusAppWidget.class.getSimpleName();
    private static final String SMALL_SIZE_CLICKED = "com.nyp.danga_small_clicked";
    private static final String MEDIUM_SIZE_CLICKED = "com.nyp.danga_medium_clicked";
    private static final String LARGE_SIZE_CLICKED = "com.nyp.danga_large_clicked";

    static Uri mUri;
    static Context mContext;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String title, String content, int bgrColor) {

        Long noteId = NoteYouPlusAppWidgetConfigureActivity.loadNoteIdPref(context, appWidgetId);
        mUri = NoteYouPlusContract.NoteEntry.buildItemUri(noteId);
        mContext = context;
        int curTextSize = NoteYouPlusAppWidgetConfigureActivity.loadTextSizePref(context, appWidgetId);
        int[] textSizes = Util.getWidgetTextSize(curTextSize);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.note_you_plus_app_widget);
        // Set view with selected Note title and content.
        views.setTextViewText(R.id.appwidget_note_title, title);
        views.setContentDescription(R.id.appwidget_note_title, title);
        views.setTextViewTextSize(R.id.appwidget_note_title, TypedValue.COMPLEX_UNIT_SP, textSizes[0]);
        // Content textview
        views.setTextViewText(R.id.appwidget_note_content, content);
        views.setContentDescription(R.id.appwidget_note_content, content);
        views.setTextViewTextSize(R.id.appwidget_note_content, TypedValue.COMPLEX_UNIT_SP, textSizes[1]);
        // Background color
        views.setInt(R.id.appwidget_note_container, "setBackgroundColor", bgrColor);

        // Setup size buttons clicked
        Intent clickedIntent = new Intent(context, NoteYouPlusAppWidget.class);
        clickedIntent.setAction(SMALL_SIZE_CLICKED);
        clickedIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
        views.setOnClickPendingIntent(R.id.widget_small_text_size,
                PendingIntent.getBroadcast(context, appWidgetId, clickedIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        clickedIntent = new Intent(context, NoteYouPlusAppWidget.class);
        clickedIntent.setAction(MEDIUM_SIZE_CLICKED);
        clickedIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
        views.setOnClickPendingIntent(R.id.widget_medium_text_size,
                PendingIntent.getBroadcast(context, appWidgetId, clickedIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        clickedIntent = new Intent(context, NoteYouPlusAppWidget.class);
        clickedIntent.setAction(LARGE_SIZE_CLICKED);
        clickedIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
        views.setOnClickPendingIntent(R.id.widget_large_text_size,
                PendingIntent.getBroadcast(context, appWidgetId, clickedIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        //Text size buttons
        switch(curTextSize) {
            case NoteYouPlusAppWidgetConfigureActivity.WIDGET_SMALL_TEXT_SIZE:
                views.setTextColor(R.id.widget_small_text_size,context.getResources().getColor(R.color.colorAccent));
                views.setTextColor(R.id.widget_medium_text_size,context.getResources().getColor(R.color.text_primary_inverse));
                views.setTextColor(R.id.widget_large_text_size,context.getResources().getColor(R.color.text_primary_inverse));
                break;
            case NoteYouPlusAppWidgetConfigureActivity.WIDGET_MEDIUM_TEXT_SIZE:
                views.setTextColor(R.id.widget_small_text_size,context.getResources().getColor(R.color.text_primary_inverse));
                views.setTextColor(R.id.widget_medium_text_size,context.getResources().getColor(R.color.colorAccent));
                views.setTextColor(R.id.widget_large_text_size,context.getResources().getColor(R.color.text_primary_inverse));
                break;
            case NoteYouPlusAppWidgetConfigureActivity.WIDGET_LARGE_TEXT_SIZE:
                views.setTextColor(R.id.widget_small_text_size,context.getResources().getColor(R.color.text_primary_inverse));
                views.setTextColor(R.id.widget_medium_text_size,context.getResources().getColor(R.color.text_primary_inverse));
                views.setTextColor(R.id.widget_large_text_size,context.getResources().getColor(R.color.colorAccent));
                break;
            default:
                break;
        }

        // Setup item click pending intent
        Intent itemClickIntent = new Intent(context, DetailNoteActivity.class);
        itemClickIntent.setAction(DetailNoteActivity.ACTION_VIEW_NOTE)
                .setData(mUri);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, itemClickIntent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_note_container, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    /*@Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }*/

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            NoteYouPlusAppWidgetConfigureActivity.deleteNoteIdPref(context, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.note_you_plus_app_widget);
        int[] textSizes;
        int appWidgetId;
        String action = intent.getAction();
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
        switch (action) {
            case SMALL_SIZE_CLICKED:
                textSizes =  Util.getWidgetTextSize(NoteYouPlusAppWidgetConfigureActivity.WIDGET_SMALL_TEXT_SIZE);
                views.setTextViewTextSize(R.id.appwidget_note_title, TypedValue.COMPLEX_UNIT_SP, textSizes[0]);
                views.setTextViewTextSize(R.id.appwidget_note_content, TypedValue.COMPLEX_UNIT_SP, textSizes[1]);
                views.setTextColor(R.id.widget_small_text_size,context.getResources().getColor(R.color.colorAccent));
                views.setTextColor(R.id.widget_medium_text_size,context.getResources().getColor(R.color.text_primary_inverse));
                views.setTextColor(R.id.widget_large_text_size,context.getResources().getColor(R.color.text_primary_inverse));
                break;
            case MEDIUM_SIZE_CLICKED:
                textSizes =  Util.getWidgetTextSize(NoteYouPlusAppWidgetConfigureActivity.WIDGET_MEDIUM_TEXT_SIZE);
                views.setTextViewTextSize(R.id.appwidget_note_title, TypedValue.COMPLEX_UNIT_SP, textSizes[0]);
                views.setTextViewTextSize(R.id.appwidget_note_content, TypedValue.COMPLEX_UNIT_SP, textSizes[1]);
                views.setTextColor(R.id.widget_small_text_size,context.getResources().getColor(R.color.text_primary_inverse));
                views.setTextColor(R.id.widget_medium_text_size,context.getResources().getColor(R.color.colorAccent));
                views.setTextColor(R.id.widget_large_text_size,context.getResources().getColor(R.color.text_primary_inverse));
                break;
            case LARGE_SIZE_CLICKED:
                textSizes =  Util.getWidgetTextSize(NoteYouPlusAppWidgetConfigureActivity.WIDGET_LARGE_TEXT_SIZE);
                views.setTextViewTextSize(R.id.appwidget_note_title, TypedValue.COMPLEX_UNIT_SP, textSizes[0]);
                views.setTextViewTextSize(R.id.appwidget_note_content, TypedValue.COMPLEX_UNIT_SP, textSizes[1]);
                views.setTextColor(R.id.widget_small_text_size,context.getResources().getColor(R.color.text_primary_inverse));
                views.setTextColor(R.id.widget_medium_text_size,context.getResources().getColor(R.color.text_primary_inverse));
                views.setTextColor(R.id.widget_large_text_size,context.getResources().getColor(R.color.colorAccent));
                break;
            default:
                break;
        }
        appWidgetManager.updateAppWidget(appWidgetId,views);
    }

}

