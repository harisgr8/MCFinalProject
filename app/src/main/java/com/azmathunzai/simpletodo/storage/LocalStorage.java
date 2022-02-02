package com.azmathunzai.simpletodo.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.azmathunzai.simpletodo.classes.ToDo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by virgo on 9/24/16.
 */

public class LocalStorage extends SQLiteOpenHelper {

    public static final String DB_NAME = "simpletodo.db";
    public static final int DB_VERSION =2;
    public static final String TABLE_TODO_LIST = "todolist";



    SQLiteDatabase sqLiteDatabase;

    public LocalStorage(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        String todoTable = "CREATE TABLE IF NOT EXISTS " + TABLE_TODO_LIST + " (" +
                ToDo.KEY_ID + " integer NOT NULL," +
                ToDo.KEY_TITLE + " text NOT NULL," +
                ToDo. KEY_NOTE + " text NOT NULL," +
                ToDo.KEY_ADD_IMAGE + " text ," +
                ToDo.KEY_DATE + " text ," +
                ToDo.KEY_BG_COLOR + " text ," +
                ToDo.KEY_IS_CHECK_BOX + " integer ," +
                ToDo. KEY_STATUS + " integer NOT NULL DEFAULT 0," +
                "PRIMARY KEY (" + ToDo.KEY_ID + "))";
        db.execSQL(todoTable);
    }

    public void openDatabase() {
        sqLiteDatabase = this.getWritableDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_TODO_LIST);
        onCreate(db);
    }

    public long addNewToDo(ToDo newToDo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ToDo.KEY_TITLE, newToDo.getTitle());
        contentValues.put(ToDo.KEY_NOTE, newToDo.getTextNote());
        contentValues.put(ToDo.KEY_ADD_IMAGE, newToDo.getImage());
        contentValues.put(ToDo.KEY_DATE, newToDo.getDate());
        contentValues.put(ToDo.KEY_BG_COLOR, newToDo.getBgColor());
        contentValues.put(ToDo.KEY_STATUS, newToDo.getStatus());
        contentValues.put(ToDo.KEY_IS_CHECK_BOX, newToDo.getIsCheckList());

        long itemID = db.insert(TABLE_TODO_LIST,null, contentValues);
        return itemID ;
    }

    public long updateToDo(ToDo updateToDo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(ToDo.KEY_TITLE, updateToDo.getTitle());
        contentValues.put(ToDo.KEY_NOTE, updateToDo.getTextNote());
        contentValues.put(ToDo.KEY_ADD_IMAGE, updateToDo.getImage());
        contentValues.put(ToDo.KEY_DATE, updateToDo.getDate());
        contentValues.put(ToDo.KEY_BG_COLOR, updateToDo.getBgColor());
        contentValues.put(ToDo.KEY_STATUS, updateToDo.getStatus());
        contentValues.put(ToDo.KEY_IS_CHECK_BOX, updateToDo.getIsCheckList());

        return db.update(TABLE_TODO_LIST,contentValues,ToDo.KEY_ID+"=?",new String[]{updateToDo.getID()+""});

    }

    public long deleteToDO(int ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_TODO_LIST,ToDo.KEY_ID+"=?",new String[]{ID+""});
    }

    public List<ToDo> getToDoList() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {ToDo.KEY_ID,
                ToDo.KEY_TITLE,
                ToDo.KEY_NOTE,
                ToDo.KEY_ADD_IMAGE,
                ToDo.KEY_DATE,
                ToDo.KEY_BG_COLOR,
                ToDo.KEY_IS_CHECK_BOX,
                ToDo.KEY_STATUS};
        Cursor cursor = null;
        List<ToDo> toDoList = new ArrayList<ToDo>();


        cursor = db.query(TABLE_TODO_LIST, columns, null, null, null, null, ToDo.KEY_ID+" DESC");

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                ToDo temp = new ToDo();
                temp.setID((int) cursor.getLong(cursor.getColumnIndexOrThrow(ToDo.KEY_ID)));
                temp.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(ToDo.KEY_TITLE)));
                temp.setTextNote(cursor.getString(cursor.getColumnIndexOrThrow(ToDo.KEY_NOTE)));
                temp.setImage(cursor.getString(cursor.getColumnIndexOrThrow(ToDo.KEY_ADD_IMAGE)));
                temp.setDate(cursor.getString(cursor.getColumnIndexOrThrow(ToDo.KEY_DATE)));
                temp.setBgColor(cursor.getString(cursor.getColumnIndexOrThrow(ToDo.KEY_BG_COLOR)));
                temp.setIsCheckList(cursor.getInt(cursor.getColumnIndexOrThrow(ToDo.KEY_IS_CHECK_BOX)));
                temp.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(ToDo.KEY_STATUS)));
                toDoList.add(temp);
            } while (cursor.moveToNext());

        }
        return toDoList;
    }

    public void updateStatus(int id, int status) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ToDo.KEY_STATUS, status);
        sqLiteDatabase.update(TABLE_TODO_LIST, contentValues, ToDo.KEY_ID + "= ?", new String[] {String.valueOf(id)});
    }

    public ToDo getToDo(long todoId) {
        ToDo temp = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {ToDo.KEY_ID,
                ToDo.KEY_TITLE,
                ToDo.KEY_NOTE,
                ToDo.KEY_ADD_IMAGE,
                ToDo.KEY_DATE,
                ToDo.KEY_BG_COLOR,
                ToDo.KEY_IS_CHECK_BOX,
                ToDo.KEY_STATUS};
        Cursor cursor = null;

        cursor = db.query(TABLE_TODO_LIST, columns, ToDo.KEY_ID+"=?", new String[]{todoId+""}, null, null, ToDo.KEY_ID+" DESC","1");

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                temp = new ToDo();
                temp.setID((int) cursor.getLong(cursor.getColumnIndexOrThrow(ToDo.KEY_ID)));
                temp.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(ToDo.KEY_TITLE)));
                temp.setTextNote(cursor.getString(cursor.getColumnIndexOrThrow(ToDo.KEY_NOTE)));
                temp.setImage(cursor.getString(cursor.getColumnIndexOrThrow(ToDo.KEY_ADD_IMAGE)));
                temp.setDate(cursor.getString(cursor.getColumnIndexOrThrow(ToDo.KEY_DATE)));
                temp.setBgColor(cursor.getString(cursor.getColumnIndexOrThrow(ToDo.KEY_BG_COLOR)));
                temp.setIsCheckList(cursor.getInt(cursor.getColumnIndexOrThrow(ToDo.KEY_IS_CHECK_BOX)));
                temp.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(ToDo.KEY_STATUS)));
        }
        return temp;
    }
}
