package com.example.android.danga.noteyouplus.data;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.example.android.danga.noteyouplus.NoteYouPlusAppWidget;
import com.example.android.danga.noteyouplus.data.NoteYouPlusContract.NoteEntry;

import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NoteService extends IntentService {
    private static final String TAG = NoteService.class.getSimpleName();
    // IntentService can perform
    private static final String ACTION_SYNC = "com.example.android.danga.noteyouplus.data.action.SYNC";
    private static final String ACTION_UPLOAD = "com.example.android.danga.noteyouplus.data.action.UPLOAD";
    private static final String ACTION_UPDATE_WIDGET = "com.example.android.danga.noteyouplus.data.action.WIDGET_UPDATE";

    //Params
    private static final String TITLE_NOTE_EXTRA = "com.example.android.danga.noteyouplus.data.extra.NOTE_TITLE";
    private static final String CONTENT_NOTE_EXTRA = "com.example.android.danga.noteyouplus.data.extra.NOTE_CONTENT";
    private static final String COLOR_NOTE_EXTRA = "com.example.android.danga.noteyouplus.data.extra.NOTE_BGR_COLOR";
    private static final String ID_NOTE_EXTRA = "com.example.android.danga.noteyouplus.data.extra.NOTE_ID";

    // Back-ground colors
    public static final int DEFAULT_BGR_COLOR = 900;
    public static final int RED_BGR_COLOR = 901;
    public static final int YELLOW_BGR_COLOR = 902;
    public static final int GREEN_BGR_COLOR = 903;
    public static final int BLUE_BGR_COLOR = 904;
    public static final int ORANGE_BGR_COLOR = 905;
    public static final int PURPLE_BGR_COLOR= 906;
    public static final int LIGHT_BLUE_BGR_COLOR = 907;
    public static final int LIGHT_YELLOW_BGR_COLOR = 908;

    public NoteService() {
        super("NoteService");
    }

    public static void startActionDownload(Context context, String param1, String param2) {
        Intent intent = new Intent(context, NoteService.class);
        intent.setAction(ACTION_SYNC);
        intent.putExtra(TITLE_NOTE_EXTRA, param1);
        intent.putExtra(CONTENT_NOTE_EXTRA, param2);
        context.startService(intent);
    }

    public static void startActionUpload(Context context, String param1, String param2) {
        Intent intent = new Intent(context, NoteService.class);
        intent.setAction(ACTION_UPLOAD);
        intent.putExtra(TITLE_NOTE_EXTRA, param1);
        intent.putExtra(CONTENT_NOTE_EXTRA, param2);
        context.startService(intent);
    }

    public static void startActionUpdateWidget(Context context, String title, String content, int bgrColor, int noteId) {
        Intent intent = new Intent(context, NoteService.class);
        intent.setAction(ACTION_UPDATE_WIDGET);
        intent.putExtra(TITLE_NOTE_EXTRA, title);
        intent.putExtra(CONTENT_NOTE_EXTRA, content);
        intent.putExtra(COLOR_NOTE_EXTRA, bgrColor);
        intent.putExtra(ID_NOTE_EXTRA, noteId);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SYNC.equals(action)) {
                final String param1 = intent.getStringExtra(TITLE_NOTE_EXTRA);
                final String param2 = intent.getStringExtra(CONTENT_NOTE_EXTRA);
                handleActionDownload(param1, param2);
            } else if (ACTION_UPLOAD.equals(action)) {
                final String param1 = intent.getStringExtra(TITLE_NOTE_EXTRA);
                final String param2 = intent.getStringExtra(CONTENT_NOTE_EXTRA);
                handleActionUpload(param1, param2);
            }  else if (ACTION_UPDATE_WIDGET.equals(action)) {
                String title = intent.getStringExtra(TITLE_NOTE_EXTRA);
                String content = intent.getStringExtra(CONTENT_NOTE_EXTRA);
                int bgrColor = intent.getIntExtra(COLOR_NOTE_EXTRA, DEFAULT_BGR_COLOR);
                int noteId = intent.getIntExtra(ID_NOTE_EXTRA, -1);
                handleActionUpdateWidget(title, content, bgrColor, noteId);
        }
        }
    }

    private void handleActionUpdateWidget(String title, String content, int bgrColor, int noteId) {
        Intent intent = new Intent(getApplicationContext(), NoteYouPlusAppWidget.class);
        intent.setAction(NoteYouPlusAppWidget.NOTE_UPDATED);
        intent.putExtra(NoteYouPlusAppWidget.TITLE_INTENT_EXTRA, title)
                .putExtra(NoteYouPlusAppWidget.CONTENT_INTENT_EXTRA, content)
                .putExtra(NoteYouPlusAppWidget.COLOR_INTENT_EXTRA, bgrColor)
                .putExtra(NoteYouPlusAppWidget.NOTEID_INTENT_EXTRA, noteId);
        getApplicationContext().sendBroadcast(intent);
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionDownload(String param1, String param2) {

        Log.v(TAG, "on handleDownload start insert into database");
        // Dummy Data for content provider
        ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();

        Uri dirUri = NoteEntry.buildDirUri();

        ContentValues values = new ContentValues();
        values.put(NoteEntry.COLUMN_TITLE, "Some test title");
        values.put(NoteEntry.COLUMN_CONTENT,"A web application is a piece of software that end users access over a network—just like HTML\n" +
                "pages. A web application, however, consists of more dynamic elements than plain vanilla HTML.\n" +
                "For instance, modern web applications have a lot of server-side languages. These languages\n" +
                "(e.g., PHP, JSP, and ASP) generate static HTML on the fly at runtime, based on an end user’s\n" +
                "input. The web application is installed on a web server and is hosted on hardware that can\n" +
                "be accessed by end users over a network such as the Internet. The server-side application\n" +
                "framework takes care of rendering the user interface, any application logic (e.g., search,\n" +
                "calculation, or any other process), and data storage or retrieval functions. All the end user has\n" +
                "to do is show up to the party with his favorite web browser. In other words, because all the\n" +
                "complex processing takes place at the back end or server side, the thinner, lighter web browser\n" +
                "is nothing more than a mechanism of interacting with the user interface.\n" +
                "Web applications offer developers a number of advantages and are a ubiquitous part of online\n" +
                "life today. One of their biggest advantages is the ability to roll out updates or patches to the\n" +
                "server and not have to worry about updating hundreds or thousands of clients. Another big\n" +
                "advantage of web applications is that end users only require a thin client—a web browser—and\n" +
                "that’s it. Thus, you can reach not only a large number of users from the personal computing\n" +
                "crowd, but also the mobile computing crowd.");
        values.put(NoteEntry.COLUMN_NOTE_TYPE,"text");
        values.put(NoteEntry.COLUMN_PHOTO_URL,"");
        values.put(NoteEntry.COLUMN_ENCRYPTED_CONTENT,"");
        values.put(NoteEntry.COLUMN_ENCRYPTED_PASS,"");
        values.put(NoteEntry.COLUMN_CREATED_USER,"adang@email.com");
        values.put(NoteEntry.COLUMN_MODIFIED_USER,"adang@email.com");
        values.put(NoteEntry.COLUMN_MODIFIED_DATE, System.currentTimeMillis());
        values.put(NoteEntry.COLUMN_OTHER_USERS,"superman@email.com;batman@email.com ");
        values.put(NoteEntry.COLUMN_IS_DELETED,false);
        values.put(NoteEntry.COLUMN_IS_ARCHIVED,false);
        values.put(NoteEntry.COLUMN_BG_COLOR, PURPLE_BGR_COLOR);

        cpo.add(ContentProviderOperation.newInsert(dirUri).withValues(values).build());

        try {
            Log.v(TAG,"prepared insert");
            getContentResolver().applyBatch(NoteYouPlusContract.CONTENT_AUTHORITY, cpo);
        } catch (RemoteException e) {
            Log.v(TAG, "Error adding content.", e);
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Insert completed!");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpload(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
