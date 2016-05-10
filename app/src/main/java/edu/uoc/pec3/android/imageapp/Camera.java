package edu.uoc.pec3.android.imageapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.IOException;

/**
 * Created by Marc on 4/5/16.
 */
public class Camera {
    private static final String TAG = Camera.class.getSimpleName();
    static final int REQUEST_TAKE_PHOTO = 1;

    private Context mContext;
    private Logger mLogger;

    public Camera(Context context){
        this.mContext = context;
        this.mLogger = new Logger(TAG);
    }


    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.mContext.getPackageManager()) != null) {
            ((Activity)this.mContext).startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }




}
