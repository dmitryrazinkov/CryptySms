package com.quiet.cryptySms.activities;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.quiet.cryptySms.R;

import java.util.ArrayList;

public class AddContactActiity extends Activity {
    String TAG = "AddContactActivity";

    private EditText editTextName;
    private EditText editTextNumber;
    private Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact_activity);

        editTextName = (EditText) findViewById(R.id.nameTxt);
        editTextNumber = (EditText) findViewById(R.id.numberTxt);
        buttonAdd = (Button) findViewById(R.id.buttonAdd);


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                String number = editTextNumber.getText().toString();
                ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

                ops.add(ContentProviderOperation.newInsert(
                        ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .build());


                if (name != null) {
                    ops.add(ContentProviderOperation.newInsert(
                            ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                            .withValue(
                                    ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                    name).build());
                }
                if (number != null) {
                    ops.add(ContentProviderOperation.
                            newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                            .build());
                }
                try {
                    getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (Exception e) {
                    Log.w(TAG, e);
                }
                Toast.makeText(getApplicationContext(), "Contact added", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "contact added");
                finish();
            }
        });
    }
}
