package com.example.emamos.stockscanner;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.emamos.stockscanner.R.id.roomName;

/**
 * Created by e.mamos on 2017-08-10.
 */

public class Db_save_screen extends Activity {

        private InventoryDbAdapter dbHelper;
        private SimpleCursorAdapter dataAdapter;
        private Cursor mycursor ;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.main);
                dbHelper = new InventoryDbAdapter(this);
                dbHelper.open();
        countMyelements();
               ///Clean all data
            //    dbHelper.deleteAllRows();
                //Add some data
                Intent intent = getIntent();
            String roomName;
            String filePath;
            String evidenceName;
            String elementName;
            String elementCode;
            String commentName;
            boolean save;
            boolean update;
            if(intent.hasExtra("update")){
                update= intent.getExtras().getBoolean("update");
            }
            else {
                update =false;
            }
            if(intent.hasExtra("save")){
                 save= intent.getExtras().getBoolean("save");
            }
            else {
                save =false;
            }
            if(save==true) {
                if (intent.hasExtra("pomieszczenie")) {

                    roomName = intent.getExtras().getString("pomieszczenie");
                } else {
                    roomName = null;
                }
                if (intent.hasExtra("ewidencja")) {
                    evidenceName = intent.getExtras().getString("ewidencja");
                } else {
                    evidenceName = null;
                }
                if (intent.hasExtra("przedmiotNazwa")) {
                    elementName = intent.getExtras().getString("przedmiotNazwa");
                } else {
                    elementName = null;
                }
                if (intent.hasExtra("przedmiotKod")) {
                    elementCode = intent.getExtras().getString("przedmiotKod");
                } else {
                    elementCode = null;
                }
                if (intent.hasExtra("komentarz")) {
                    commentName = intent.getExtras().getString("komentarz");
                } else {
                    commentName = null;
                }
                if (intent.hasExtra("sciezkaObraz")) {

                    filePath = intent.getExtras().getString("sciezkaObraz");
                } else {
                    filePath = null;
                }

                if (roomName == null | evidenceName == null | elementName == null | elementCode == null) {

                    final AlertDialog.Builder builder;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(this);
                    }
                    builder.setTitle("Puste pola formularza")
                            .setMessage("Uzupelnij pola formuarza przed zapisaniem elementu do bazy!")
                            .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    if(update ==true){
                        String currentTime =  new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime());


                        dbHelper.update(roomName, evidenceName, elementName, elementCode, commentName, filePath,currentTime.toString());

                    }
                    else {
                        dbHelper.insertMyElement(roomName, evidenceName, elementName, elementCode, commentName, filePath);
                    }
                }
                displayListView();
                finish();
            }
            else {
                displayListView();
            }
                //Generate ListView from SQLite Database



        }


    @Override
    protected void onStart() {
        super.onStart();
        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbarDB);

        Long count=  dbHelper.countMyElements();
        mActionBarToolbar.setTitle("Liczba wierszy: "+count.toString());
    }

    private void displayListView() {


                Cursor cursor = dbHelper.fetchAllRows();

                // The desired columns to be bound
                String[] columns = new String[] {
                        InventoryDbAdapter.KEY_LOC,
                        InventoryDbAdapter.KEY_EWI,
                        InventoryDbAdapter.KEY_NAME,
                        InventoryDbAdapter.KEY_KOD,
                        InventoryDbAdapter.KEY_COM,
                        InventoryDbAdapter.KEY_PIC,
                        InventoryDbAdapter.KEY_DATE,
                };

                // the XML defined views which the data will be bound to
              int[] to = new int[] {
                        roomName,
                        R.id.evidence,
                        R.id.thing,
                        R.id.code,
                        R.id.comment,
                        R.id.filepath,
                        R.id.dateView,

                };

                // create the adapter using the cursor pointing to the desired data
              //as well as the layout information
               dataAdapter = new SimpleCursorAdapter(
                        this, R.layout.base_row,
                        cursor,
                        columns,
                        to,
                        0);


                final ListView listView = (ListView) findViewById(R.id.listView1);
                // Assign adapter to ListView

                listView.setAdapter(dataAdapter);



                 registerForContextMenu(listView);
                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        mycursor=(Cursor)listView.getItemAtPosition(position);
                        openContextMenu(listView);
                        return true;
                    }
                });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> listView, View view,
                                        int position, long id) {
                    // Get the cursor, positioned to the corresponding row in the result set
                    Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                    // Get the state's capital from this row in the database.
                    String photoPath =
                            cursor.getString(cursor.getColumnIndexOrThrow("Zdjecie"));
                    if (photoPath == null) {
                    } else if (photoPath.equals("")) {
                    } else {
                        File imgFile = new File(photoPath);

                        if (imgFile.exists()) {

                            final Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                            final ImageView myImage = (ImageView) findViewById(R.id.imView);

                            myImage.setVisibility(view.VISIBLE);
                            Animation fadeIn = new AlphaAnimation(0, 1);
                            fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
                            fadeIn.setDuration(500);


                            AnimationSet animation = new AnimationSet(false); //change to false
                            animation.addAnimation(fadeIn);

                            myImage.setAnimation(animation);


                            myImage.setImageBitmap(myBitmap);

                        }

                    }
                }
            });

        }

    public void backToMain(View view) {
        finish();
    }
    public void dissapear(View view) {
        ImageView myImage = (ImageView) findViewById(R.id.imView);
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(500);
        fadeOut.setDuration(500);
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(fadeOut);
        myImage.setAnimation(animation);


        myImage.setVisibility(view.GONE);
        ListView listView = (ListView) findViewById(R.id.listView1);
        RelativeLayout.LayoutParams mParam = new RelativeLayout.LayoutParams((RelativeLayout.LayoutParams.MATCH_PARENT),(int)(listView.getHeight()/0.8));
        listView.setLayoutParams(mParam);
    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_delete_row, menu);


    }

    public void openDBMenu(View view) {
        PopupMenu popup = new PopupMenu(this, findViewById(R.id.floatingActionButtonDB));
        popup.getMenuInflater().inflate(R.menu.context_menu_db, popup.getMenu());
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.cleanDB:
                        //Clean all data
                        final AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(Db_save_screen.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(Db_save_screen.this);
                        }
                        builder.setTitle("Uwaga!")
                                .setMessage(Db_save_screen.this.getResources().getString(R.string.deleteCheck))
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int answer=  dbHelper.deleteAllRows(Db_save_screen.this);

                                        dialog.dismiss();
                                        Intent intent = getIntent();
                                        finish();
                                        countMyelements();
                                        startActivity(intent);

                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                        return true;
                    case R.id.export:
                        dbHelper.exportDBtoCSV();

                        showAlert();

                        return true;
                    default:
                        return true;
                }

            }
        });
        popup.show();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        if(item.getItemId()==R.id.deleteRow){
            String codeToDelete=mycursor.getString(4);
            dbHelper.deleteRow(codeToDelete);

            countMyelements();
            displayListView();
            return true;
        }
        else{
        return false;}

    }

    public void showAlert(){
        Toast.makeText(this,"Wyeksportowano plik!",Toast.LENGTH_SHORT).show();

    }
    void countMyelements(){
        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbarDB);

        Long count=  dbHelper.countMyElements();

        mActionBarToolbar.setTitle("Liczba wierszy: "+count.toString());
        displayListView();

    }
}
