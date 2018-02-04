package com.example.emamos.stockscanner;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;


public class Browse extends AppCompatActivity implements View.OnClickListener {
    private static final int PICKFILE_REQUEST_CODE = 21;
    private static final String TAG = "TEST";
    private static final int ACTIVITY_CHOOSE_FILE = 2;
    Button btn1;
    Button btn2;
    Button btn3;
    String fileName;
    Uri uri;
    Uri uriRoom;
    Uri uriEvidence;
    Uri uriElement;

    //int PICK_REQUEST_CODE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        TextView roomsearchView = (TextView) findViewById(R.id.roomfilesearch);
        //  roomsearchView.setPaintFlags(roomsearchView.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        TextView evidencesearchView = (TextView) findViewById(R.id.evidencefilesearch);
        //  evidencesearchView.setPaintFlags(evidencesearchView.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        TextView elementsearchView = (TextView) findViewById(R.id.elementfilesearch);
        //  elementsearchView.setPaintFlags(elementsearchView.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        uriRoom = null;
        uriEvidence = null;
        uriElement = null;
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        btn1 = (Button) findViewById(R.id.search1);
   /*     btn1.setTag(1);*/
        btn2 = (Button) findViewById(R.id.search2);
    /*    btn2.setTag(1);*/
        btn3 = (Button) findViewById(R.id.search3);
  /*      btn3.setTag(1);
        final int tag = 1;*/
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);

    }

    int flag = -1;

    @Override
    public void onClick(View view) {
        if (view.getId() == btn1.getId()) {
            flag = 0;
            //  String deviceMan = android.os.Build.MANUFACTURER;
           /* if (deviceMan == "Samsung") {
                Intent intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
               intent.putExtra("CONTENT_TYPE", "**///*//*");
            // intent.addCategory(Intent.CATEGORY_DEFAULT);
            //  } else {*/}
            chooseFile();


            //   Log.d("File",uriRoom.getPath());

            // EditText edRoom = (EditText) findViewById(R.id.roomFileName);
            //  edRoom.setText(fileName);
        } else if (view.getId() == btn2.getId()) {
            flag = 1;
            chooseFile();
            //uriEvidence=uri;
            // Log.d("File",uriEvidence.getPath());


        } else if (view.getId() == btn3.getId()) {
            flag = 2;
            chooseFile();
            //  uriElement=uri;

        }
    }


    private void chooseFile() {
        Intent chooseFile;
        Intent intent;
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("*/*");
        intent = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Log.d("HERE", "I AM HERE!");
        if (resultCode != RESULT_OK) return;
        String path = "";
        if (requestCode == ACTIVITY_CHOOSE_FILE) {//                      Log.d("ENTERED","HELLO!");

            uri = data.getData();
            //  Log.d("File",uri.getPath());
            switch (flag) {
                case 0:
                    uriRoom = uri;

                    break;
                case 1:
                    uriEvidence = uri;
                    break;
                case 2:
                    uriElement = uri;
                    break;
            }
            ContentResolver cr = getContentResolver();

            InputStream is = null;
            try {
                is = cr.openInputStream(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Scanner s = new Scanner(is).useDelimiter("\\A");
            // String result = s.hasNext() ? s.next() : "";
            //  Log.d("HERE I AM", result);

            fileName = "default_file_name";
            Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
            try {
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                returnCursor.moveToFirst();
                fileName = returnCursor.getString(nameIndex);

            } catch (Exception e) {
                Log.e(TAG, "error: ", e);
                //handle the failure cases here
            } finally {
                returnCursor.close();

            }
            boolean b =
                    fileName.endsWith(".csv");//Pattern.matches("[a-zA-Z]+\\.csv", fileName);

            switch (flag) {
                case 0:

                    if (!b) {
                        Toast.makeText(this, "Niepoprany format pliku! Wymagane rozszerzenie CSV!", Toast.LENGTH_LONG).show();
                    } else {
                        TextView tv = (TextView) findViewById(R.id.roomFileName);
                        tv.setVisibility(View.VISIBLE);
                        tv.setText(fileName);
                    }
                    return;
                case 1:
                    if (!b) {
                        Toast.makeText(this, "Niepoprany format pliku! Wymagane rozszerzenie CSV!", Toast.LENGTH_LONG).show();
                    } else {
                        TextView tv1 = (TextView) findViewById(R.id.evidenceFileName);
                        tv1.setVisibility(View.VISIBLE);
                        tv1.setText(fileName);
                    }

                    return;
                case 2:
                    if (!b) {
                        Toast.makeText(this, "Niepoprany format pliku! Wymagane rozszerzenie CSV!", Toast.LENGTH_LONG).show();
                    } else {
                        TextView tv2 = (TextView) findViewById(R.id.elementFileName);
                        tv2.setVisibility(View.VISIBLE);
                        tv2.setText(fileName);
                    }

                    return;
            }
        }
    }

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


    public void backToMain(View view) {
      /*  Log.d("File",uriRoom.getPath());

        Log.d("File",uriEvidence.getPath());
        Log.d("File",uriElement.getPath());
*/


        if (uriElement == null | uriEvidence == null | uriRoom == null) {
            Toast.makeText(this, getResources().getString(R.string.missingFiles),
                    Toast.LENGTH_LONG).show();


        }
        //  else if(uriElement.){}
        else {
            setResult(Activity.RESULT_OK,
                    new Intent().putExtra("uriRoom", uriRoom).putExtra("uriEvidence", uriEvidence).putExtra("uriElement", uriElement));
            SharedPreferences sharedPreferences= getSharedPreferences("filePath",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("uriRoom",uriRoom.toString());
            editor.putString("uriEvidence",uriEvidence.toString());
            editor.putString("uriElement",uriElement.toString());
            editor.apply();
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }
}