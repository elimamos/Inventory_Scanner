package com.example.emamos.stockscanner;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.io.File;

/**
 * Created by e.mamos on 2017-08-08.
 */


public class Camera extends Activity {
    static final int CAM_REQUEST = 1;

    int MY_REQUEST_CODE = 1;
    int MY_REQUEST_CODE2 = 2;
    int MY_REQUEST_CODE3 = 3;
    String fileName;
    File image_file;

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, MY_REQUEST_CODE);

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_REQUEST_CODE2);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_REQUEST_CODE3);
        }
        capture();

    }

    private File getFile() {

        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File folder = new File(path, getResources().getString(R.string.app_name));
        boolean success = false;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (!success) {
            Toast.makeText(this, "Nie udalo sie stworzyc folderu!", Toast.LENGTH_SHORT);

        }

        Intent intent = getIntent();
        fileName = intent.getExtras().getString("przedmiotKod");

        image_file = new File(folder, fileName + ".jpg");

        return image_file;
    }

    public void capture() {
        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getFile();
        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(camera_intent, CAM_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (image_file.exists()) {

            setResult(Activity.RESULT_OK,
                    new Intent().putExtra("sciezkaObraz", image_file.getAbsolutePath()).putExtra("sciezkaObraz", image_file.getAbsolutePath()));
            finish();
        } else {

            setResult(Activity.RESULT_OK,
                    new Intent().putExtra("sciezkaObraz", image_file.getAbsolutePath()).putExtra("sciezkaObraz", ""));
            finish();

        }
    }


}
