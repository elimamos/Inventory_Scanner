package com.example.emamos.stockscanner;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emamos.stockscanner.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by e.mamos on 2017-08-08.
 */


public class Camera extends Activity {
    static final int CAM_REQUEST=1;
    Bitmap bitmap;

    int MY_REQUEST_CODE = 1;
    int MY_REQUEST_CODE2 = 2;
    int MY_REQUEST_CODE3 = 3;
    String fileName;
    File image_file;
    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_REQUEST_CODE);
        }
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_REQUEST_CODE2);
        }
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_REQUEST_CODE3);
        }
        capture();

    }

    private File getFile(){
       /* File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
      final String TAG = "Camera";

        File folder =  new File (path, "scanner");*/
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        final String TAG = "MainActivity";

        File folder =  new File (path, getResources().getString( R.string.app_name));
       boolean success = false;
       if(!folder.exists()){
            success=folder.mkdirs();
        }
        if (!success){
            Toast.makeText(this, "Nie udalo sie stworzyc folderu!",Toast.LENGTH_SHORT);
          //  Log.d(TAG,"Folder not created.");
        }
       /* else{
            Log.d(TAG,"Folder created!");
        }*/
        Intent intent = getIntent();
         fileName =intent.getExtras().getString("przedmiotKod");

         image_file = new File(folder, fileName+".jpg");

        return image_file;
    }

    public void capture() {
        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getFile();
        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
       startActivityForResult(camera_intent,CAM_REQUEST);

           }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


     /*   Context cw = this;
        File mypath = new File(image_file.getAbsolutePath());
        ImageView iw = (ImageView) findViewById(R.id.image_view);
        bitmap = BitmapFactory.decodeFile(mypath.getAbsolutePath());
        //iw.setImageBitmap(myBitmap);*//*myBitmap
        ExifInterface ei = null;
        try {
            ei = new ExifInterface( Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).toString()+"/scanner/"+fileName+".jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap = null;
        switch(orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;

        }
        iw.setImageBitmap(rotatedBitmap);*/
        if(image_file.exists()){
         //   String path = photoFile.getAbsolutePath();
            setResult(Activity.RESULT_OK,
                    new Intent().putExtra("sciezkaObraz", image_file.getAbsolutePath()).putExtra("sciezkaObraz", image_file.getAbsolutePath()));
            finish();
        }
        else {

            setResult(Activity.RESULT_OK,
                    new Intent().putExtra("sciezkaObraz",image_file.getAbsolutePath()).putExtra("sciezkaObraz",""));
            finish();

        }
    }


   /* public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }*/

  /*  public void back(View view) {
        setResult(Activity.RESULT_OK,
                new Intent().putExtra("sciezkaObraz",image_file.getAbsolutePath()).putExtra("sciezkaObraz",image_file.getAbsolutePath()));
        finish();

    }*/
}