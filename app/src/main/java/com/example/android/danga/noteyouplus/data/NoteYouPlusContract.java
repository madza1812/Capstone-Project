package com.example.android.danga.noteyouplus.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by An on 5/30/2016.
 */
public class NoteYouPlusContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.android.danga.noteyouplus";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_NOTE = "note";

    public static final class NoteEntry implements BaseColumns {

        public static final Uri NOTE_CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_NOTE).build();

        public static final String NOTE_CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTE;
        public static final String NOTE_CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTE;

        // Table name
        public static final String NOTE_TABLE_NAME = "note_t";

        // Column list
        public static final String COLUMN_NOTE_ID = "noteId";
        public static final String COLUMN_TITLE = "noteTitle";
        public static final String COLUMN_CONTENT = "noteContent";
        public static final String COLUMN_NOTE_TYPE = "noteType";
        public static final String COLUMN_PHOTO_URL = "photoUrl";
        public static final String COLUMN_ENCRYPTED_CONTENT = "encryptedNoteContent";
        public static final String COLUMN_ENCRYPTED_PASS = "encryptedPassword";
        public static final String COLUMN_CREATED_USER = "createUser";
        public static final String COLUMN_MODIFIED_DATE = "modifiedDate";
        public static final String COLUMN_MODIFIED_USER = "modifiedUser";
        public static final String COLUMN_OTHER_USERS = "otherUsers";
        public static final String COLUMN_IS_DELETED = "isDeleted";
        public static final String COLUMN_IS_ARCHIVED = "isArchived";
        public static final String COLUMN_BG_COLOR = "bgrColor";

        // default sort
        public static final String DEFAULT_SORT = COLUMN_MODIFIED_DATE + " DESC";

        // uri building functions
        /** Matches: /all_notes/ */
        public static Uri buildDirUri() {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_NOTE).build();
        }
        /** Matches: /items/[id]/ */
        public static Uri buildItemUri(long id){
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_NOTE).appendPath(Long.toString(id)).build();
        }

        /** Item detail Uri */
        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(1));
        }
    }

    private NoteYouPlusContract(){}
}
