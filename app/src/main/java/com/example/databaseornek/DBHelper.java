package com.example.databaseornek;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UsersDB";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "Users";
    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_SURNAME = "Surname";
    private static final String COLUMN_EMAIL = "Email";
    private static final String COLUMN_IMAGE = "Image";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    COLUMN_NAME + " TEXT," +
                    COLUMN_SURNAME + " TEXT," +
                    COLUMN_EMAIL + " TEXT PRIMARY KEY," +
                    COLUMN_IMAGE + " BLOB)";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertUser(String name, String surname, String email, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_SURNAME, surname);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_IMAGE, image);
        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public long updateUser(String name, String surname, String email, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_SURNAME, surname);
        values.put(COLUMN_IMAGE, image);
        long id = db.update(TABLE_NAME, values, COLUMN_EMAIL + " = ?", new String[]{email});
        db.close();
        return id;
    }

    public long deleteUser(String name, String surname, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.delete(TABLE_NAME, COLUMN_EMAIL + " = ?", new String[]{email});
        db.close();
        return id;
    }

    public ArrayList<String> getAllUsers() {
        ArrayList<String> usersList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                @SuppressLint("Range") String surname = cursor.getString(cursor.getColumnIndex(COLUMN_SURNAME));
                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL));
                usersList.add(name + " " + surname + " : " + email);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return usersList;
    }

    @SuppressLint("Range")
    public byte[] getUserImage(String name, String surname, String email) {
        byte[] image = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_IMAGE + " FROM " + TABLE_NAME + " WHERE " +
                COLUMN_NAME + " = ? AND " + COLUMN_SURNAME + " = ? AND " + COLUMN_EMAIL + " = ?", new String[]{name, surname, email});
        if (cursor.moveToFirst()) {
            image = cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE));
        }
        cursor.close();
        db.close();
        return image;
    }
}

