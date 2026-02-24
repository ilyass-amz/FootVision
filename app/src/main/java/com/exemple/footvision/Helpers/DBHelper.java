package com.exemple.footvision.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "footvision.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)");
        db.execSQL("CREATE TABLE comments(id INTEGER PRIMARY KEY AUTOINCREMENT, match_id TEXT, user TEXT, comment TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS comments");
        onCreate(db);
    }

    public boolean insertUser(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", password);
        return db.insert("users", null, cv) != -1;
    }
    public void updateComment(int id, String newComment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("comment", newComment);
        db.update("comments", values, "id=?", new String[]{String.valueOf(id)});
    }
    public boolean checkUser(String username, String password){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username=? AND password=?", new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean insertComment(String matchId, String user, String comment){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("match_id", matchId);
        cv.put("user", user);
        cv.put("comment", comment);
        return db.insert("comments", null, cv) != -1;
    }

    public Cursor getComments(String matchId){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM comments WHERE match_id=?", new String[]{matchId});
    }
}
