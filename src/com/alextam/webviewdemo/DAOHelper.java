package com.alextam.webviewdemo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;



/**
 * Created on 5/21/2015
 * @author Alex Tam
 *
 */
public class DAOHelper {
	private DBOpenHeler dbOpenHeler;  
    
    
    public DAOHelper(Context context)  
    {  
        dbOpenHeler = new DBOpenHeler(context);  
    }  
      
    public synchronized void save(String imgurl , String imgpath)  
    {  
        SQLiteDatabase db = dbOpenHeler.getWritableDatabase();  
        db.beginTransaction();  
        try {  
            db.execSQL("insert into imagecachedb(imgurl , imgpath) values(?,?)", new String[]{imgurl, imgpath});  
            db.setTransactionSuccessful();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }finally{  
            db.endTransaction();  
        }  
        db.close();  
    }  
      
    public synchronized void delete(String imgurl)  
    {  
        SQLiteDatabase db = dbOpenHeler.getWritableDatabase();  
        db.execSQL("delete from imagecachedb where imgurl = ?",new String[]{imgurl});  
        db.close();  
    }  
      
    public synchronized void update(String imgurl , String imgpath)  
    {  
        SQLiteDatabase db = dbOpenHeler.getWritableDatabase();  
        db.beginTransaction();  
        try {  
            db.execSQL("update imagecachedb set imgpath = ? where imgurl = ?",new Object[]{imgpath, imgurl});  
            db.setTransactionSuccessful();  
              
        } catch (Exception e) {  
            e.printStackTrace();  
        }finally{  
            db.endTransaction();  
        }  
        db.close();  
    }  
      
    public synchronized String find(String imgurl)  
    {  
    	String imgPath = null;  
        SQLiteDatabase db = dbOpenHeler.getReadableDatabase();  
        db.beginTransaction();  
        try {  
            Cursor cursor = db.rawQuery("select * from imagecachedb where imgurl = ?",new String[]{imgurl});  
            if(cursor.moveToFirst())  
            {  
            	imgPath = cursor.getString(2);
            }  
            cursor.close();  
            db.setTransactionSuccessful();  
              
        } catch (Exception e) {  
            e.printStackTrace();  
        }finally{  
            db.endTransaction();  
        }  
        db.close();  
        return imgPath;  
    }  
	
	
}
