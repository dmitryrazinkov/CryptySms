package com.quiet.test.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.quiet.test.R;


public class ContactFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private CursorAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // create adapter once
        Context context = getActivity();
        int layout = R.layout.contact_list_item;
        Cursor c = null; // there is no cursor yet
        int flags = 0; // no auto-requery! Loader requeries.
        mAdapter = new SimpleCursorAdapter(context, layout, c, FROM, TO, flags);

    }


    // columns requested from the database
    private static final String[] PROJECTION = {
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    // and name should be displayed in the text1 textview in item layout
    private static final String[] FROM = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER};
    private static final int[] TO = {R.id.text1, R.id.number1};

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // load from the "Contacts table"
        Uri contentUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        ;

        // no sub-selection, no sort order, simply every row
        // projection says we want just the _id and the name column
        return new CursorLoader(getActivity(),
                contentUri,
                PROJECTION,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Once cursor is loaded, give it to adapter
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // on reset take any old cursor away
        mAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ListView listView = getListView();

        int firstPosition = listView.getFirstVisiblePosition() - listView.getHeaderViewsCount();
        int wantedChild = position - firstPosition;
        if (wantedChild < 0 || wantedChild >= listView.getChildCount()) {
            Log.w("", "Unable to get view for desired position," +
                    " because it's not being displayed on screen.");
            return;
        }
        View wantedView = listView.getChildAt(wantedChild);
        LinearLayout linearLayout = (LinearLayout) wantedView;
        TextView textView = (TextView) linearLayout.getChildAt(1);
        String number = textView.getText().toString();

        Intent intent = new Intent("test.chat");
        intent.putExtra("number", number);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // each time we are started use our listadapter
        setListAdapter(mAdapter);
        // and tell loader manager to start loading
        getLoaderManager().initLoader(0, null, this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

}