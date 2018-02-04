package com.example.emamos.stockscanner;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    private Map<String, String> roomDictionary;
    private Map<String, String> evidenceDictionary;
   private  Map<String, String> elementDictionary;
    private  String roomName;
    private String evidenceName;
    private String elementName;
    private String elementCode;
    private String commentName;
    private Button[] buttonArray;
    private String filePath;
    private Uri uriRoom;
    private Uri uriEvidence;
    private Uri uriElement;
    private File elementFile;
    private File roomFile;
    private File evidenceFile;
    private Boolean save;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton actionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        registerForContextMenu(actionButton);
     //   int PERMISSION_ALL = 1;
      /*  String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};


        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            permissionGranted = false;
        }
        else {
            permissionGranted = true;
        }*/
       /* while(!permissionGranted){
            Log.d("permission","waiting for permissions!");
        }
*/
        save=false;
        getFilesFromFolder();
        buttonArray = new Button[5];
        buttonArray[0] = (Button) findViewById(R.id.ex1);
        buttonArray[1] = (Button) findViewById(R.id.ex2);
        buttonArray[2] = (Button) findViewById(R.id.ex3);
        buttonArray[3] = (Button) findViewById(R.id.ex4);
        buttonArray[4] = (Button) findViewById(R.id.ex5);
        for (final Button btn : buttonArray) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearMyTextbox(btn);

                }
            });
        }

        final EditText tx = (EditText) findViewById(R.id.roomCode);
        final TextView tv = (TextView) findViewById(R.id.room);
        final EditText tx2 = (EditText) findViewById(R.id.evidenceCode);
        final TextView tv2 = (TextView) findViewById(R.id.evidence);
        tx.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    find(roomDictionary, tx, tv);
                    roomName = tv.getText().toString();

                }
                return false;
            }
        });
        tx.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tx.setAlpha(1f);
                tx2.setAlpha(1f);
                return false;
            }
        });

        tx2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tx.setAlpha(1f);
                tx2.setAlpha(1f);
                return false;
            }
        });
        // tv2.setText( Environment.getExternalStoragePublicDirectory(                Environment.DIRECTORY_PICTURES).toString());
        // tv2.setText(getFilesDir().getAbsolutePath()+"/scanner/filename.jpg");
        tx2.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    find(evidenceDictionary, tx2, tv2);
                    evidenceName = tv2.getText().toString();
                }
                return false;
            }
        });
        final EditText tx3 = (EditText) findViewById(R.id.elementCode);

        tx3.setOnKeyListener(new View.OnKeyListener() {
            final TextView tv3 = (TextView) findViewById(R.id.element);

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    tx.setAlpha(0.5f);
                    tx2.setAlpha(0.5f);
                    find(elementDictionary, tx3, tv3);
                    elementName = tv3.getText().toString();

                }

                return false;
            }
        });
        final EditText tx4 = (EditText) findViewById(R.id.elementNumber);
        tx4.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press

                    elementCode = tx4.getText().toString();
                }
                return false;
            }
        });


    }
    /*@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        permissionGranted=true;

    }*/
    //Cleaning one EditText and the matching TextView
    private void clearMyTextbox(Button btn) {

        Map<Button, EditText> clearingMap = new HashMap<>();
        clearingMap.put((Button) findViewById(R.id.ex1), (EditText) findViewById(R.id.roomCode));
        clearingMap.put((Button) findViewById(R.id.ex2), (EditText) findViewById(R.id.evidenceCode));
        clearingMap.put((Button) findViewById(R.id.ex3), (EditText) findViewById(R.id.elementCode));
        clearingMap.put((Button) findViewById(R.id.ex4), (EditText) findViewById(R.id.elementNumber));
        clearingMap.put((Button) findViewById(R.id.ex5), (EditText) findViewById(R.id.comment));
        EditText mytext = clearingMap.get(btn);
        TextView mt1 = (TextView) findViewById(R.id.room);
        TextView mt2 = (TextView) findViewById(R.id.evidence);
        TextView mt3 = (TextView) findViewById(R.id.element);
        if (mytext == findViewById(R.id.roomCode)) {
            mt1.setText(R.string.room);
            mytext.setText("");
            mytext.requestFocus();
            mytext.setSelection(0);
            roomName = null;
        } else if (mytext == findViewById(R.id.evidenceCode)) {
            mt2.setText(R.string.evidence);
            mytext.requestFocus();
            mytext.setText("");
            evidenceName = null;
        } else if (mytext == findViewById(R.id.elementCode)) {
            mt3.setText(R.string.element);
            mytext.requestFocus();
            mytext.setText("");
            elementName = null;
        } else if (mytext == findViewById(R.id.elementNumber)) {
            mytext.requestFocus();
            mytext.setText("");
            elementCode = null;
        } else {
            mytext.requestFocus();
            mytext.setText("");
            commentName = null;
        }

    }
    //Read files that were chosen in the File Browser
    public void readfileFromUri(Uri myUri, Map<String, String> myMap) {
        InputStream is = null;
        ContentResolver cr = getContentResolver();
        try {
            is = cr.openInputStream(myUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Scanner s = new Scanner(is).useDelimiter("\\A");
         while (s.hasNextLine()) {
            String line = s.nextLine();
            line = line.replaceAll(";", " ");
            line = line.trim();
            String array[] = line.split(" ");

            StringBuilder description = new StringBuilder();
            String descr = "";
            if (array.length > 2) {

                for (int i = 1; i < array.length; i++) {
                    description.append(array[i]);
                    description.append(" ");

                }

            } else {
                description.append(array[1]);
            }
            descr = description.toString().trim();
          //  Log.d("CurrentLine",descr);

            myMap.put(array[0], descr);

        }
        s.close();
    }
// Read file from file in StockScanner folder
    public void readfileFromFile(File filename, Map<String, String> myMap) {


        try {
            BufferedReader br = new BufferedReader( new InputStreamReader(new FileInputStream(filename),"ISO-8859-2")) ;
            String line;

            while ((line = br.readLine()) != null) {
                line = line.replaceAll(";", " ");
                line = line.trim();
                String array[] = line.split(" ");

                StringBuilder description = new StringBuilder();
                String descr = "";
                if (array.length > 2) {

                    for (int i = 1; i < array.length; i++) {
                        description.append(array[i]);
                        description.append(" ");

                    }

                } else {
                    description.append(array[1]);
                }
                descr = description.toString().trim();
                myMap.put(array[0], descr);
            }
            br.close();
        }
    catch (IOException e) {
        //You'll need to add proper error handling here

    }

     /*   InputStream is = null;
        ContentResolver cr = getContentResolver();
        try {
            is = cr.openInputStream(uriEvidence);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Scanner s = new Scanner(is).useDelimiter("\\A");
       // String result = s.hasNext() ? s.next() : "";
        // Log.d("HERE I AM", result);
       // s.close();
    *//*   int resource = getResources().getIdentifier(filename, "raw", getPackageName());
        InputStream input = getResources().openRawResource(resource);
        Scanner scan = new Scanner(input, "ISO-8859-2");

*//*
        while (s.hasNextLine()) {
            String line = s.nextLine();
            line = line.replaceAll(";", " ");
            line = line.trim();
            String array[] = line.split(" ");

            StringBuilder description = new StringBuilder();
            String descr = "";
            if (array.length > 2) {

                for (int i = 1; i < array.length; i++) {
                    description.append(array[i]);
                    description.append(" ");

                }

            } else {
                description.append(array[1]);
            }
            descr = description.toString().trim();
            Log.d("CurrentLine",descr);

            myMap.put(array[0], descr);

        }
        s.close();*/
    }
//Finding matching name to code
    public void find(Map<String, String> mydictionary, EditText code, TextView preview) {

        EditText tx = code;
        TextView df = preview;
        String keyValue = (String) tx.getText().toString();

        keyValue = keyValue.trim();

        df.setText(mydictionary.get(keyValue));


    }
//Clearing the whole form
    public void clear(View view) {
        TextView mt1 = (TextView) findViewById(R.id.room);
        TextView mt2 = (TextView) findViewById(R.id.evidence);
        TextView mt3 = (TextView) findViewById(R.id.element);
        EditText et1 = (EditText) findViewById(R.id.comment);
        EditText et2 = (EditText) findViewById(R.id.elementNumber);
        EditText et3 = (EditText) findViewById(R.id.elementCode);
        EditText et4 = (EditText) findViewById(R.id.evidenceCode);
        EditText et5 = (EditText) findViewById(R.id.roomCode);
        mt1.setText(R.string.room);
        mt2.setText(R.string.evidence);
        mt3.setText(R.string.element);
        et1.setText("");
        et2.setText("");
        et3.setText("");
        et4.setText("");
        et5.setText("");
        roomName = null;
        evidenceName = null;
        elementName = null;
        elementCode = null;
        commentName = null;
        filePath = null;
    et5.requestFocus();

    }
//Opening camera
    public void openPhoto(View view) {
        save=false;
        if (elementCode == null) {
            final AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle("Puste pola formularza")
                    .setMessage("Uzupelnij pola formuarza przed zrobieniem zdjecia!")
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else if (elementCode.equals("")) {
            final AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle("Puste pola formularza")
                    .setMessage("Uzupelnij pola formuarza przed zrobieniem zdjecia!")
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            String[] PERMISSIONS = {Manifest.permission.CAMERA};

            if(hasPermissions(this,PERMISSIONS)){
                Log.d("CAMERA ALLOWED", "Permission granted!");
                  Intent i = new Intent(MainActivity.this, Camera.class);
            i.putExtra("przedmiotKod", elementCode);

            startActivityForResult(i, 0);
            }
            else{
                Toast.makeText(this,"Proszê zezwoliæ na dostêp do aparatu!",Toast.LENGTH_SHORT);
                Log.d("CAMERA ALLOWED", "Permission not granted!");
            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            filePath = data.getStringExtra("sciezkaObraz");
            if(filePath==""){
                Toast.makeText(this,"Nie wykonano zdjecia!",Toast.LENGTH_LONG);

            }
            else{
                Toast.makeText(this,"Zdjecie zapisano!",Toast.LENGTH_LONG);
            }

        } else if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

            uriRoom = (Uri) data.getExtras().get("uriRoom");
            uriEvidence = (Uri) data.getExtras().get("uriEvidence");
            uriElement = (Uri) data.getExtras().get("uriElement");
            SharedPreferences sharedPreferences= getSharedPreferences("filePath",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("uriRoom",uriRoom.toString());
            editor.putString("uriEvidence",uriEvidence.toString());
            editor.putString("uriElement",uriElement.toString());
            editor.apply();
            evidenceDictionary = new HashMap<String, String>();
            readfileFromUri(uriEvidence, evidenceDictionary);
            roomDictionary = new HashMap<String, String>();
            readfileFromUri(uriRoom, roomDictionary);
            elementDictionary = new HashMap<String, String>();
             readfileFromUri(uriElement, elementDictionary);;

        }
    }
    public Long checkifNumberAlreadyExists(String[] number){
        InventoryDbAdapter.DatabaseHelper mDbHelper= new InventoryDbAdapter.DatabaseHelper(this);
        SQLiteDatabase mDb= mDbHelper.getWritableDatabase();;
        long l= DatabaseUtils.queryNumEntries(mDb,"Zeskanowane","Kod_kreskowy=?",number);
        return l;
    }

    public void save(View view) {
        EditText tx5 = (EditText) findViewById(R.id.comment);
        commentName = tx5.getText().toString();

       // InventoryDbAdapter iA= new InventoryDbAdapter(this);
        //Long countReturn=iA.checkifNumberAlreadyExists(elementCode);
        //Log.d("Count",countReturn.toString());
       // Log.d("CODE",elementCode);

//
        //sprawdzanie, czy pola s± puste
        //
        if(roomName==null|evidenceName==null|elementName==null|elementCode==null) {
            save =false;

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
       }
        else if(roomName.equals("")||evidenceName.equals("")|elementName.equals("")|elementCode.equals("")){
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
       }

        else  {
            save =false;
            String[] number= new String[1];
            elementCode=elementCode.trim();

            number[0]=elementCode;
            Long l=  checkifNumberAlreadyExists(number);
          //  Log.w("COUNTRESULT",l.toString());
            if(l!=0){
                final AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(this);
                }
                builder.setTitle("Uwaga!")
                        .setMessage(getResources().getString(R.string.alertPt1)+elementCode+" "+getResources().getString(R.string.alertPt2)+"\n"+getResources().getString( R.string.qestion))
                        .setPositiveButton("Anuluj",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Nadpisz", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                save=true;
                                Intent i = new Intent(MainActivity.this, Db_save_screen.class);
                                i.putExtra("update",true);
                                i.putExtra("sciezkaObraz", filePath);
                                i.putExtra("pomieszczenie", roomName);
                                i.putExtra("ewidencja", evidenceName);
                                i.putExtra("przedmiotNazwa", elementName);
                                i.putExtra("przedmiotKod", elementCode);
                                i.putExtra("komentarz", commentName);
                                i.putExtra("save",true);
                                filePath=null;
                                dialog.dismiss();
                                startActivity(i);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
            else {

                Intent i = new Intent(MainActivity.this, Db_save_screen.class);
               save =true;
                i.putExtra("update",false);
                i.putExtra("sciezkaObraz", filePath);
                i.putExtra("pomieszczenie", roomName);
                i.putExtra("ewidencja", evidenceName);
                i.putExtra("przedmiotNazwa", elementName);
                i.putExtra("przedmiotKod", elementCode);
                i.putExtra("komentarz", commentName);
                i.putExtra("save",true);
                filePath=null;
                Db_save_screen db = new Db_save_screen();
                Toast.makeText(this, "Dodano element do bazy!",
                        Toast.LENGTH_LONG).show();

               startActivity(i);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void getFilesFromFolder() {
        int MY_REQUEST_CODE2 = 2;
        int MY_REQUEST_CODE3 = 3;


        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        File path = Environment.getExternalStorageDirectory();
        final String TAG = "MainActivity";
        File folder = new File(path + "/StockScanner");
        if (!folder.exists()) {
            openBrowser();

        } else {

            elementFile = new File(folder + "/listaElementow.csv");
            roomFile = new File(folder + "/listaPomieszczen.csv");
            evidenceFile = new File(folder + "/listaEwidencji.csv");
            File[] myfiles = folder.listFiles();
            final AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }

            if (myfiles == null) {
                builder.setTitle(getResources().getString(R.string.missingFilesTitle))
                        .setMessage("Pusty folder " + getResources().getString(R.string.app_name) + " " + getResources().getString(R.string.storage))
                        .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                openBrowser();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            } else if (myfiles.length == 0) {

                Log.d(TAG, folder.getPath());
                builder.setTitle(getResources().getString(R.string.missingFilesTitle))
                        .setMessage("Pusty folder " + getResources().getString(R.string.app_name) + " " + getResources().getString(R.string.storage))
                        .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                openBrowser();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();



            } else {
                Hashtable<String, Boolean>fileMap= new Hashtable<String, Boolean>();
                boolean roomOK = false;
                boolean evidenceOK = false;
                boolean elementOK = false;


                if (Arrays.asList(myfiles).contains(roomFile)) {
                    roomOK = true;
                }
                if (Arrays.asList(myfiles).contains(evidenceFile)) {
                    evidenceOK = true;


                }
                if (Arrays.asList(myfiles).contains(elementFile)) {
                    elementOK = true;

                }
                fileMap.put(roomFile.getName(),roomOK);
                fileMap.put(evidenceFile.getName(),evidenceOK);
                fileMap.put(elementFile.getName(),elementOK);

              /*  boolean[] fileStatus = new boolean[3];
                fileStatus[0]=roomOK;
                fileStatus[1]=evidenceOK;
                fileStatus[2]=elementOK;*/
              //  List<boolean>[] missingFiles = new boolean[3];
                //ArrayList<String> missingFile = new ArrayList<>();
                String missingFiles="";
                if (roomOK && evidenceOK && elementOK) {
                    //Log.d(TAG,"ALL FILES THERE!");
                    evidenceDictionary = new HashMap<String, String>();
                    readfileFromFile(evidenceFile, evidenceDictionary);
                    roomDictionary = new HashMap<String, String>();
                    readfileFromFile(roomFile, roomDictionary);
                    elementDictionary = new HashMap<String, String>();
                     readfileFromFile(elementFile, elementDictionary);

                } else {
                    for(Map.Entry<String, Boolean> entry : fileMap.entrySet()){
                        if(!entry.getValue()){
                            //missingFile.add(entry.getKey());
                            missingFiles+=entry.getKey()+ ", ";
                          //  Log.d(TAG+" Iter", entry.getKey());
                        }

                    }
                    missingFiles=missingFiles.trim().substring(0,missingFiles.length()-2);
                    builder.setTitle(getResources().getString(R.string.missingFilesTitle))
                            .setMessage(new StringBuilder().append(getResources().getString(R.string.missingFilesInFolder1)).append(" ").append(missingFiles).append(" ").append(getResources().getString(R.string.missingFilesInFolder2)).toString())
                            .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    openBrowser();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                  /*  for(int i=0;i<3;i++){
                       if(!fileStatus[i]){

                       }
                    }*/


                }
            }
          /*  if(!elementFile.exists() ){

                builder.setTitle(getResources().getString(R.string.missingFilesTitle))
                        .setMessage(getResources().getString(R.string.missingFilesInFolder1)+" "+elementFile.getName()+" "+getResources().getString(R.string.missingFilesInFolder2))
                        .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
            else if(!roomFile.exists()){
                final AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(this);
                }
                builder.setTitle(getResources().getString(R.string.missingFilesTitle))
                        .setMessage(getResources().getString(R.string.missingFilesInFolder1)+" "+elementFile.getName()+" "+getResources().getString(R.string.missingFilesInFolder2))
                        .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
            else if(!evidenceFile.exists()){
                final AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(this);
                }
                builder.setTitle(getResources().getString(R.string.missingFilesTitle))
                        .setMessage(getResources().getString(R.string.missingFilesInFolder1)+" "+evidenceFile.getName()+" "+getResources().getString(R.string.missingFilesInFolder2))
                        .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
*/
            // }
           /* if (!success) {
                // Log.d(TAG, "Folder not created.");
               openBrowser();
            } else {

            }*/
            folder.setExecutable(true);
            folder.setReadable(true);
            folder.setWritable(true);
            Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri fileContentUri = Uri.fromFile(folder);
            mediaScannerIntent.setData(fileContentUri);
            this.sendBroadcast(mediaScannerIntent);

            MediaScannerConnection.scanFile(this, new String[]{folder.getAbsolutePath().toString()}, null, null);


        }

    }


    private void openBrowser(){
        Intent in = new Intent(this, Browse.class);
        startActivityForResult(in, 1);

    }
//Check if permission is granted
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.context_menu_main, menu);
    }

    public void openContextMenu(View view) {
        PopupMenu popup = new PopupMenu(MainActivity.this, findViewById(R.id.floatingActionButton));
        popup.getMenuInflater().inflate(R.menu.context_menu_main, popup.getMenu());
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.openDB:
                        openDBView();
                        return true;
                    case R.id.info:
                       openInfo();
                        return true;
                    case R.id.openBrowser:
                        openBrowser();
                        return true;
                    default:
                        return true;
                }

            }
        });

        popup.show();//showing popup menu

    //  view.showContextMenu();
    }
    void openInfo(){
        Intent info = new Intent(this, Info.class);
        startActivity(info);
    }
    void openDBView(){
        Intent i = new Intent(MainActivity.this, Db_save_screen.class);
        startActivity(i);
    }



    @Override
    protected void onResume() {
        super.onResume();
        if(save==true) {
            clearMyTextbox((Button) findViewById(R.id.ex3));
            clearMyTextbox((Button) findViewById(R.id.ex4));
            clearMyTextbox((Button) findViewById(R.id.ex5));
            EditText eleCode =(EditText) findViewById(R.id.elementCode);
            eleCode.requestFocus();
            eleCode.setActivated(true);
        }

    }


}

