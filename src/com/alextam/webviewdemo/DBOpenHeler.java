package com.alextam.webviewdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;



/**
 * Created on 5/21/2015
 * @author Alex Tam
 *
 */
public class DBOpenHeler extends SQLiteOpenHelper{
	private static final String DB_Name = "mydatabase.db";  
    private static final int VERSION = 1;  
      
      
    public DBOpenHeler(Context context) {  
        super(context, DB_Name, null, VERSION);  
    }  
      
    public DBOpenHeler(Context context, String name, CursorFactory factory,  
            int version) {  
        super(context, DB_Name, null, VERSION);  
    }  
  
    @Override  
    public void onCreate(SQLiteDatabase db) {  
        db.execSQL("CREATE TABLE IF NOT EXISTS imagecachedb (_id integer primary key autoincrement,imgurl varchar(300),imgpath varchar(300)) ");  
    }  
  
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
    }  
	
	
}
