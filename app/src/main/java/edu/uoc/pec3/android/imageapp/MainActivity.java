package edu.uoc.pec3.android.imageapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.DialogPreference;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // TAG logs
    private final String TAG = this.getClass().getSimpleName();

    // Views
    private Button mButtonOpenImage;
    private ImageView mImageView;
    private TextView mTextView;
    private BitmapHelper mBitmapHelper;
    private Camera mCamera;
    private Logger mLogger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLogger = new Logger(TAG);

        // Set views
        mButtonOpenImage = (Button) findViewById(R.id.button);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mTextView = (TextView) findViewById(R.id.textView);

        // Create Bitmap Manager instance
        mBitmapHelper = new BitmapHelper(this);
        // Create Camera instance
        mCamera = new Camera(this);

        // Show image if exists
        showImage();

        // Set listeners
        mButtonOpenImage.setOnClickListener(this);

        // Check permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            mButtonOpenImage.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    private void showImage() {
        mBitmapHelper.refreshGallery();
        // Get File
        File storedImage = mBitmapHelper.getFile();
        // Check if file exists
        if (storedImage.exists()){
            // Set image
            mImageView.setImageURI(Uri.fromFile(storedImage));
            // Set Visibilities
            mImageView.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.GONE);
        } else {
            // Delete image
            mImageView.setImageURI(null);
            // Set Visibilities
            mImageView.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mButtonOpenImage) {
            // launching an intent to get an image from camera
            mCamera.dispatchTakePictureIntent();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Camera.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            // Get bitmap
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            // Set Visibilities
            mImageView.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.GONE);
            // Set images
            mImageView.setImageBitmap(bitmap);
            mBitmapHelper.setThumbnail(bitmap);
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                mButtonOpenImage.setEnabled(true);
            }
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.save:
                // Add pic
                mBitmapHelper.galleryAddPic();
                break;
            case R.id.delete:
                // Check if file exists
                if (mBitmapHelper.getFile().exists()){
                    showAlert();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Delete Image");
        dialog.setMessage("Do you want to delete this image?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBitmapHelper.galleryDeletePic(new BitmapHelper.CallbackDelete() {
                            @Override
                            public void success() {
                                Toast.makeText(MainActivity.this, "Image deleted!", Toast.LENGTH_LONG).show();
                                showImage();
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }


}
