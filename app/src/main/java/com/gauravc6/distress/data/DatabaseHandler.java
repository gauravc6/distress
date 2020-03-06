package com.gauravc6.distress.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.gauravc6.distress.model.Contact;
import com.gauravc6.distress.util.Constants;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {

    private final Context context;

    public DatabaseHandler(@Nullable Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DBHandler", "onCreate: Called!");

        String CREATE_CONTACT_TABLE = "CREATE TABLE " + Constants.TABLE_NAME + "("
                + Constants.KEY_ID + " INTEGER PRIMARY KEY,"
                + Constants.KEY_NAME + " TEXT,"
                + Constants.KEY_CONTACT_NUMBER + " LONG);";
        Log.d("DB Creation", "onCreate: " + CREATE_CONTACT_TABLE);

        db.execSQL(CREATE_CONTACT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
        onCreate(db);
    }

    // CRUD operations
    public void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("DBHandler", "addContact: got the database access");

        ContentValues values = new ContentValues();
        values.put(Constants.KEY_NAME, contact.getName());
        values.put(Constants.KEY_CONTACT_NUMBER, contact.getContactNumber());

        db.insert(Constants.TABLE_NAME, null, values);
    }

    public Contact getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Constants.TABLE_NAME,
                new String[]{Constants.KEY_ID,
                        Constants.KEY_NAME,
                        Constants.KEY_CONTACT_NUMBER},
                Constants.KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Contact contact = new Contact();
        if (cursor != null) {
            contact.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
            contact.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_NAME)));
            contact.setContactNumber(cursor.getLong(cursor.getColumnIndex(Constants.KEY_CONTACT_NUMBER)));

        }
        return contact;
    }

    //Get all Contacts
    public List<Contact> getAllContacts() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<Contact> contactList = new ArrayList<>();

        Cursor cursor = db.query(Constants.TABLE_NAME,
                new String[]{Constants.KEY_ID,
                        Constants.KEY_NAME,
                        Constants.KEY_CONTACT_NUMBER},
                null, null, null, null,
                Constants.KEY_NAME + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
                contact.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_NAME)));
                contact.setContactNumber(cursor.getLong(cursor.getColumnIndex(Constants.KEY_CONTACT_NUMBER)));

                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        return contactList;

    }

    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.KEY_NAME, contact.getName());
        values.put(Constants.KEY_CONTACT_NUMBER, contact.getContactNumber());

        return db.update(Constants.TABLE_NAME, values,
                Constants.KEY_ID + "=?",
                new String[]{String.valueOf(contact.getId())});
    }

    public void deleteContact(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constants.TABLE_NAME,
                Constants.KEY_ID + "=?",
                new String[]{String.valueOf(id)});

        db.close();
    }

    public int getContactsCount() {
        String countQuery = "SELECT * FROM " + Constants.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }
}
