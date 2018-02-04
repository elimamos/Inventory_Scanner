package com.example.emamos.stockscanner;

/**
 * Created by e.mamos on 2017-08-22.
 */

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.os.Build;

import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ViewDebug;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class InventoryDbAdapter {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_LOC = "Lokalizacja";
    public static final String KEY_EWI = "Ewidencja";
    public static final String KEY_NAME = "Nazwa_skladnika";
    public static final String KEY_KOD = "Kod_kreskowy";
    public static final String KEY_PIC= "Zdjecie";
    public static final String KEY_COM= "Uwagi";
    public static final String KEY_DATE="Data";

    private static final String TAG = "InventoryDbAdapter";
    private DatabaseHelper mDbHelper;
    public SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "Inwentaryzacja";
    public static final String SQLITE_TABLE = "Zeskanowane";
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;

    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + SQLITE_TABLE + " (" +
                    KEY_ROWID + " integer PRIMARY KEY autoincrement," +
                    KEY_LOC + "," +
                    KEY_EWI + "," +
                    KEY_NAME + "," +
                    KEY_KOD + ","+
                    KEY_PIC + ","+
                    KEY_COM +","+KEY_DATE+");";

    static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
          //  Log.w(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
          //  Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
            //        + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
            onCreate(db);
        }

    }


    public InventoryDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }


    public InventoryDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    public void update (String loc, String ewi,
                        String name, String code, String com, String filepath, String date ) {
      /*  mDb.execSQL("UPDATE " + SQLITE_TABLE+ " SET "+ KEY_LOC +" = "+loc+", "+KEY_EWI +" = "+ewi+", "+KEY_NAME +" = "+name+", "+KEY_KOD +" = "+code+", "+
                KEY_PIC +" = "+filepath+", "+KEY_COM +" = "+com +"WHERE "+KEY_KOD+" = "+ code);*/
      String[]codeStr=new String[1];
        codeStr[0]=code;
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_LOC, loc);
        initialValues.put(KEY_EWI, ewi);
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_KOD, code);
        initialValues.put(KEY_PIC, filepath);
        initialValues.put(KEY_COM, com);
        initialValues.put(KEY_DATE,date);

        mDb.update(SQLITE_TABLE,initialValues,KEY_KOD+"=?",codeStr);
    //    Log.w("UPDATE!","Updated Row "+ code);
    }
    public void deleteRow(String code){
        String[]codeStr=new String[1];
        codeStr[0]=code;
        mDb.delete(SQLITE_TABLE,KEY_KOD+"=?",codeStr);
    }
    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }


    public long createRow(String loc, String ewi,
                          String name, String code, String com, String filepath, String date) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_LOC, loc);
        initialValues.put(KEY_EWI, ewi);
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_KOD, code);
        initialValues.put(KEY_PIC, filepath);
        initialValues.put(KEY_COM, com);
        initialValues.put(KEY_DATE,date);
     //   Log.w("CREATION",loc);
        return mDb.insert(SQLITE_TABLE, null, initialValues);
    }
    int doneDelete =-1;
    public int deleteAllRows(Context some) {
        /*final AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(some, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(some);
        }*/
        doneDelete = mDb.delete(SQLITE_TABLE, null , null);

      /*  builder.setTitle("Uwaga!")
                .setMessage(some.getResources().getString(R.string.deleteCheck))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                         doneDelete = 0;
                      // Db_save_screen db_save_screen= new Db_save_screen();
                        dialog.dismiss();

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                      @Override
                     public void onClick(DialogInterface dialog, int which) {
                           doneDelete=1;
                          dialog.dismiss();
                      }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)*/
            //    .show();
        /*Boolean result=false;
        if(doneDelete==0){
            result=true;
        }
        else result=false;*/
        return doneDelete;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public Cursor fetchRowsByName(String inputText) throws SQLException {
      //  Log.w(TAG, inputText);
        Cursor mCursor = null;
        if (inputText == null  ||  inputText.length () == 0)  {
            mCursor = mDb.query(SQLITE_TABLE, new String[] {KEY_ROWID,
                            KEY_LOC, KEY_EWI, KEY_NAME, KEY_KOD, KEY_PIC, KEY_COM, KEY_DATE},
                    null, null, null, null, null);

        }
     /*   else {
            mCursor = mDb.query(true, SQLITE_TABLE, new String[] {KEY_ROWID,
                            KEY_LOC, KEY_EWI, KEY_NAME, KEY_KOD, KEY_PIC, KEY_COM},
                    KEY_EWI + " like '%" + inputText + "%'", null,
                    null, null, null, null);
        }*/
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public Cursor fetchAllRows() {
        if(mDb.isOpen()){ Log.w(TAG, "DataBase Open!");}

        Cursor mCursor = mDb.query(SQLITE_TABLE, new String[] {KEY_ROWID,
                        KEY_LOC, KEY_EWI, KEY_NAME, KEY_KOD, KEY_PIC, KEY_COM,KEY_DATE},
                null, null, null, null, KEY_ROWID+" DESC");

        if (mCursor != null) {
            mCursor.moveToFirst();

        }



        return mCursor;
    }
    public Long countMyElements(){
        long l = DatabaseUtils.queryNumEntries(mDb,SQLITE_TABLE);

        return l;


    }




    public void insertMyElement(String loc, String ewi,
                                String name, String code, String com, String filepath) {

        String currentTime =  new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime());

        createRow(loc, ewi, name, code, com,filepath, currentTime);


    }
     void exportDBtoCSV(){
      //  File dbFile=getDatabasePath("MyDBName.db");
        File exportDir = new File(Environment.getExternalStorageDirectory(), "StockScanner");
        if (!exportDir.exists())
        {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "WyeksportowanaBaza.csv");
        try
        {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

            Cursor curCSV = mDb.rawQuery("SELECT * FROM "+SQLITE_TABLE,null);
            csvWrite.writeNext(curCSV.getColumnNames());
            int i=1;
            Long l= countMyElements();
            while(curCSV.moveToNext())
            {
                //Which column you want to export
                String arrStr[] ={Integer.toString(i), curCSV.getString(1), curCSV.getString(2),curCSV.getString(3),curCSV.getString(4),curCSV.getString(5), curCSV.getString(6), curCSV.getString(7)};
                i++;
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
          /*  Db_save_screen db = new Db_save_screen();
            db.showAlert();*/
            curCSV.close();
        }
        catch(Exception sqlEx)
        {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }






}

