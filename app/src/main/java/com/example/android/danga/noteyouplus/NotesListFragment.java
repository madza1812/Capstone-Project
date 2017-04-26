package com.example.android.danga.noteyouplus;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.danga.noteyouplus.data.NoteYouPlusContract;
import com.example.android.danga.noteyouplus.data.NoteYouPlusContract.NoteEntry;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link FragmentCallBack}
 * interface.
 */
public class NotesListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String TAG = NotesListFragment.class.getSimpleName();

    private static final String ARG_COLUMN_COUNT = "com.example.android.danga.noteyouplus.COLUMN_COUNT";
    private static final String ARG_NOTE_TYPE = "com.example.android.danga.noteyouplus.NOTE_TYPE";
    private static final String ARG_SEARCH_QUERY = "com.example.android.danga.noteyouplus.QUERY_SEARCH";

    private static final String SEARCH_QUERY_SAVED_KEY = "com.example.android.danga.noteyouplus.QUERY_SEARCHSAVED_KEY";

    public static final int ACTIVE_NOTES = 234;
    public static final int DELETED_NOTES = 235;
    public static final int ARCHIVED_NOTES = 236;


    public ViewStub mEmptyStub;
    private int mBgrColor;
    private String mSortOrder;
    private int mLoaderId;
    private int mColumnCount = 2;
    private int mNoteType = ACTIVE_NOTES;
    private FragmentCallBack mListener;
    public View rootView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;


    private String mSearchQuery = "";



    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NotesListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static NotesListFragment newInstance(int columnCount, int type) {
        Log.v(TAG, "on newInstance Fragment");
        NotesListFragment fragment = new NotesListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putInt(ARG_NOTE_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Log.v(TAG, "onCreate Fragment");

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mNoteType = getArguments().getInt(ARG_NOTE_TYPE, ACTIVE_NOTES);
        }
        mLoaderId = mNoteType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView Fragment");
        rootView = inflater.inflate(R.layout.fragment_notes_list, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.v(TAG, "onRefresh called from SwipeRefreshLayout");
                refreshListNotes();
            }
        });
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_notes_rv);
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_new_note);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBgrColor = Util.getPreferredDefaultColor(getActivity());
                Intent newNoteIntent = new Intent(getActivity(), DetailNoteActivity.class)
                        .setAction(DetailNoteActivity.ACTION_NEW_NOTE)
                        .putExtra(DetailNoteActivityFragment.BGR_INTENT_KEY, mBgrColor);
                startActivity(newNoteIntent);
            }
        });
        // get SharedPreferences
        mBgrColor = Util.getPreferredDefaultColor(getActivity());
        mSortOrder = Util.getPreferredSortOrder(getActivity());
        if (savedInstanceState != null)
            mSearchQuery = savedInstanceState.getString(SEARCH_QUERY_SAVED_KEY,"");
        getLoaderManager().initLoader(mLoaderId, null, this);

        mEmptyStub = (ViewStub) rootView.findViewById(R.id.empty_viewstub);
        mEmptyStub.inflate().setVisibility(View.GONE);
        ImageView emptyNoteIM = (ImageView) rootView.findViewById(R.id.empty_note_im);
        emptyNoteIM.setImageDrawable(getResources().getDrawable(Util.getDrawableIcon(mNoteType)));

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_fragment_menu, menu);
        final SearchView searchView = (SearchView) MenuItemCompat
                .getActionView(menu.findItem(R.id.action_search));
        final SearchManager searchManager = (SearchManager)
                getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.v(TAG, "onQuerySbumit: query = " + query);
                mSearchQuery = query;
                getLoaderManager().restartLoader(mLoaderId, null, NotesListFragment.this);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.v(TAG, "onClose: ");
                mSearchQuery= "";
                getLoaderManager().restartLoader(mLoaderId, null, NotesListFragment.this);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentCallBack) {
            mListener = (FragmentCallBack) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(mSearchQuery, SEARCH_QUERY_SAVED_KEY);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    public void refreshListNotes() {
        //TODO: loading or refreshing loader
        Log.v(TAG, "on refreshListNotes");
        getLoaderManager().restartLoader(mLoaderId, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(TAG, "onCreateLoader: mSearchQuery = " + mSearchQuery);
        mSortOrder = Util.getPreferredSortOrder(getActivity());
        if (!mSearchQuery.trim().isEmpty()) {
            String searchQuery = "%"+mSearchQuery+"%";
        switch (mNoteType) {
            case ACTIVE_NOTES: {
                final String selection = "(" + NoteEntry.COLUMN_TITLE + " LIKE ? OR "
                        + NoteEntry.COLUMN_CONTENT + " LIKE ?) AND "
                        + NoteEntry.COLUMN_IS_DELETED + " = ? AND "
                        + NoteEntry.COLUMN_IS_ARCHIVED + " = ?";
                return new CursorLoader(
                        getActivity(),
                        NoteEntry.NOTE_CONTENT_URI,
                        null,
                        selection,
                        new String[]{searchQuery, searchQuery, "0", "0"},
                        //NoteEntry.DEFAULT_SORT
                        mSortOrder
                );
            }
            case DELETED_NOTES: {
                final String selection = "(" + NoteEntry.COLUMN_TITLE + " LIKE ? OR "
                        + NoteEntry.COLUMN_CONTENT + " LIKE ?) AND "
                        + NoteEntry.COLUMN_IS_DELETED + " = ?";
                return new CursorLoader(
                        getActivity(),
                        NoteEntry.NOTE_CONTENT_URI,
                        null,
                        selection,
                        new String[]{searchQuery, searchQuery, "1"},
                        //NoteEntry.DEFAULT_SORT
                        mSortOrder
                );
            }
            case ARCHIVED_NOTES: {
                final String selection = "(" + NoteEntry.COLUMN_TITLE + " LIKE ? OR " +
                        NoteEntry.COLUMN_CONTENT + " LIKE ?) AND " + NoteEntry.COLUMN_IS_ARCHIVED + " = ?";
                return new CursorLoader(
                        getActivity(),
                        NoteEntry.NOTE_CONTENT_URI,
                        null,
                        selection,
                        new String[]{searchQuery, searchQuery, "1"},
                        //NoteEntry.DEFAULT_SORT
                        mSortOrder
                );
            }
            default:
                return null;
        }
        } else {
            switch (mNoteType) {
                case ACTIVE_NOTES: {
                    final String selection = NoteEntry.COLUMN_IS_DELETED + " = ? AND "
                            + NoteEntry.COLUMN_IS_ARCHIVED + " = ?";
                    return new CursorLoader(
                            getActivity(),
                            NoteEntry.NOTE_CONTENT_URI,
                            null,
                            selection,
                            new String[]{"0", "0"},
                            //NoteEntry.DEFAULT_SORT
                            mSortOrder
                    );
                }
                case DELETED_NOTES: {
                    final String selection = NoteEntry.COLUMN_IS_DELETED + " = ?";
                    return new CursorLoader(
                            getActivity(),
                            NoteEntry.NOTE_CONTENT_URI,
                            null,
                            selection,
                            new String[]{"1"},
                            //NoteEntry.DEFAULT_SORT
                            mSortOrder
                    );
                }
                case ARCHIVED_NOTES: {
                    final String selection = NoteEntry.COLUMN_IS_ARCHIVED + " = ?";
                    return new CursorLoader(
                            getActivity(),
                            NoteEntry.NOTE_CONTENT_URI,
                            null,
                            selection,
                            new String[]{"1"},
                            //NoteEntry.DEFAULT_SORT
                            mSortOrder
                    );
                }
                default:
                    return null;
            }
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(TAG, "onLoadFinished: update RecyclerView Adapter");
        if (data != null && data.getCount() > 0) {
            if (mEmptyStub.getParent() == null)
                mEmptyStub.setVisibility(View.GONE);
            mSearchQuery = "";
            NotesAdapter adapter = new NotesAdapter(data, mListener);
            adapter.setHasStableIds(true);
            mRecyclerView.setAdapter(adapter);
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            } else {
                mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(mColumnCount, StaggeredGridLayoutManager.VERTICAL));
            }
        } else {
            mEmptyStub.setVisibility(View.VISIBLE);

        }
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(TAG, "onLoaderReset");
        mRecyclerView.setAdapter(null);
    }

    public class NotesAdapter extends RecyclerView.Adapter<ViewHolder> {

        public final String TAG = NotesAdapter.class.getSimpleName();

        private final FragmentCallBack mListener;
        private Cursor mCursor;

        public NotesAdapter(Cursor cursor, FragmentCallBack listener) {
            mCursor = cursor;
            mListener = listener;
        }

        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getInt(NotesListFragment.Query.PROJECTION_ID);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_note, parent, false);
            final ViewHolder vh = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "onClick of ViewHolder");
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onNoteSelected(NoteYouPlusContract.NoteEntry.buildItemUri(
                                getItemId(vh.getAdapterPosition())));
                    }
                }
            });

            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mTitleView.setText(mCursor.getString(Query.PROJECTION_TITLE));
            holder.mTitleView.setContentDescription(mCursor.getString(Query.PROJECTION_TITLE));
            holder.mContentView.setText(mCursor.getString(Query.PROJECTION_CONTENT));
            holder.mContentView.setContentDescription(mCursor.getString(Query.PROJECTION_CONTENT));
            holder.mView.setBackgroundColor(Util.getBgrColor(mCursor.getInt(Query.PROJECTION_BG_COLOR)));
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
            cv = (CardView) view.findViewById(R.id.cardview_notes);
            mTitleView = (TextView) view.findViewById(R.id.note_title);
            mContentView = (TextView) view.findViewById(R.id.note_content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public interface Query {
        String[] PROJECTION = {
                NoteEntry.COLUMN_NOTE_ID,
                NoteEntry.COLUMN_TITLE,
                NoteEntry.COLUMN_CONTENT,
                NoteEntry.COLUMN_NOTE_TYPE,
                NoteEntry.COLUMN_PHOTO_URL,
                NoteEntry.COLUMN_ENCRYPTED_CONTENT,
                NoteEntry.COLUMN_ENCRYPTED_PASS,
                NoteEntry.COLUMN_CREATED_USER,
                NoteEntry.COLUMN_MODIFIED_USER,
                NoteEntry.COLUMN_MODIFIED_DATE,
                NoteEntry.COLUMN_OTHER_USERS,
                NoteEntry.COLUMN_IS_DELETED,
                NoteEntry.COLUMN_IS_ARCHIVED,
                NoteEntry.COLUMN_BG_COLOR
        };

        int PROJECTION_ID = 0;
        int PROJECTION_TITLE = 1;
        int PROJECTION_CONTENT = 2;
        int PROJECTION_NOTE_TYPE = 3;
        int PROJECTION_PHOTO_URL = 4;
        int PROJECTION_ENCRYPTED_CONTENT = 5;
        int PROJECTION_ENCRYPTED_PASS = 6;
        int PROJECTION_CREATED_USER = 7;
        int PROJECTION_MODIFIED_USER = 8;
        int PROJECTION_MODIFIED_DATE = 9;
        int PROJECTION_OTHER_USERS = 10;
        int PROJECTION_IS_DELETED = 11;
        int PROJECTION_IS_ARCHIVED = 12;
        int PROJECTION_BG_COLOR = 13;
    }
}
