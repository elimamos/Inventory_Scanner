package com.example.emamos.stockscanner;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.Scanner;


public class Browse extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Exception_Brower";
    private static final int ACTIVITY_CHOOSE_FILE = 2;
    Button btn1;
    Button btn2;
    Button btn3;
    String fileName;
    Uri uri;
    Uri uriRoom;
    Uri uriEvidence;
    Uri uriElement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        TextView roomsearchView = (TextView) findViewById(R.id.roomfilesearch);
         roomsearchView.setPaintFlags(roomsearchView.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        TextView evidencesearchView = (TextView) findViewById(R.id.evidencefilesearch);
          evidencesearchView.setPaintFlags(evidencesearchView.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        TextView elementsearchView = (TextView) findViewById(R.id.elementfilesearch);
          elementsearchView.setPaintFlags(elementsearchView.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        uriRoom = null;
        uriEvidence = null;
        uriElement = null;
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        btn1 = (Button) findViewById(R.id.search1);
        btn2 = (Button) findViewById(R.id.search2);
        btn3 = (Button) findViewById(R.id.search3);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);

    }

    int flag = -1;

    @Override
    public void onClick(View view) {
        if (view.getId() == btn1.getId()) {
            flag = 0;
            chooseFile();

        } else if (view.getId() == btn2.getId()) {
            flag = 1;
            chooseFile();

        } else if (view.getId() == btn3.getId()) {
            flag = 2;
            chooseFile();
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

    private void MoveFiles(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();

        }
    }

    public boolean copyFileFromUri(Context context, Uri fileUri, String filename) {

        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;

        try {
            ContentResolver content = context.getContentResolver();
            inputStream = new BufferedInputStream(content.openInputStream(fileUri));

            File root = Environment.getExternalStorageDirectory();
            if (root == null) {
                Log.d(TAG, "Failed to get root");
            }

            // create a directory
            File saveDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "StockScanner" + File.separator);
            // create direcotory if it doesn't exists
            saveDirectory.mkdirs();

            outputStream = new BufferedOutputStream(new FileOutputStream(saveDirectory + File.separator + filename)); // filename.png, .mp3, .mp4 ...
            if (outputStream != null) {
                Log.e(TAG, "Output Stream Opened successfully");
            }

            byte[] buffer = new byte[32 * 1024];
            int bytesRead = 0;
            while ((bytesRead = inputStream.read(buffer)) >= 0) {
                outputStream.write(buffer, 0, bytesRead);
                inputStream.close();
                outputStream.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception occurred " + e.getMessage());
        } finally {


        }
        return true;

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;
        String path = "";
        if (requestCode == ACTIVITY_CHOOSE_FILE) {//                      Log.d("ENTERED","HELLO!");

            uri = data.getData();

            switch (flag) {
                case 0:
                    uriRoom = uri;
                    copyFileFromUri(this, uriRoom, "listaPomieszczen.csv");

                    break;
                case 1:
                    uriEvidence = uri;
                    copyFileFromUri(this, uriEvidence, "listaEwidencji.csv");

                    break;
                case 2:
                    uriElement = uri;
                    copyFileFromUri(this, uriElement, "listaElementow.csv");

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
            fileName = "default_file_name";
            Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
            try {
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                returnCursor.moveToFirst();
                fileName = returnCursor.getString(nameIndex);

            } catch (Exception e) {
                Log.e(TAG, "error: ", e);
            } finally {
                returnCursor.close();

            }
            boolean b =
                    fileName.endsWith(".csv");
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
        if (uriElement == null | uriEvidence == null | uriRoom == null) {
            Toast.makeText(this, getResources().getString(R.string.missingFiles),
                    Toast.LENGTH_LONG).show();


        } else {
            String TAG = "SHIT";
            setResult(Activity.RESULT_OK,
                    new Intent().putExtra("uriRoom", uriRoom).putExtra("uriEvidence", uriEvidence).putExtra("uriElement", uriElement));
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }
}