package edu.uoc.pec3.android.imageapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

import javax.security.auth.callback.Callback;

/**
 * Created by Marc on 6/5/16.
 */
public class BitmapHelper {
    private static final String TAG = BitmapHelper.class.getSimpleName();
    private static final String DIRECTORY_PICTURES = "UOCImageApp";
    private Context mContext;
    private Logger mLogger;
    private File mFile;
    private Bitmap thumbnail = null;
    public boolean thumbnailExist = false;

    public BitmapHelper(Context context){
        this.mContext = context;
        this.mLogger = new Logger(TAG);
    }


    /**
     * Method to create a file by image
     */
    private void newImageFile() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        mFile = getFile();
        if (mFile.exists()){
            mFile.delete();
        }
        FileOutputStream fo;
        try {
            fo = new FileOutputStream(mFile);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to define dir
     * @return File
     */
    public File getDirPath(){
        return Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES);
    }

    /**
     * Method to define file name
     * @return String
     */
    public String getImageFileName(){
        return  "/imageapp.jpg";
    }

    /**
     * Method to set thumbnail and define boolean
     * @param bitmap
     */
    public void setThumbnail(Bitmap bitmap) {
        this.thumbnail = bitmap;
        this.thumbnailExist = true;
    }


    /**
     * Method to add pic to device gallery
     */
    public void galleryAddPic() {
        if (thumbnailExist){
            newImageFile();
            Toast.makeText(mContext, "Image saved!", Toast.LENGTH_LONG).show();
            refreshGallery();
        }
    }

    /**
     * Method to delete image of gallery
     * @param callback
     */
    public void galleryDeletePic(CallbackDelete callback){
        File fdelete = getFile();
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                mLogger.i("file Deleted :" + fdelete.getAbsolutePath());
                refreshGallery();
                this.thumbnailExist = false;
                if (callback != null){
                    callback.success();
                }
            } else {
                mLogger.i("file not Deleted :" + fdelete.getAbsolutePath());
            }
        }
    }

    interface CallbackDelete{
        void success();
    }

    /**
     * Method to refresh gallery
     */
    public void refreshGallery() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageState())));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            MediaScannerConnection.scanFile(mContext, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                /*
                 *   (non-Javadoc)
                 * @see android.media.MediaScannerConnection.OnScanCompletedListener#onScanCompleted(java.lang.String, android.net.Uri)
                 */
                public void onScanCompleted(String path, Uri uri) {
                    mLogger.i("ExternalStorage - Scanned " + path + ":");
                    mLogger.i("ExternalStorage - -> uri=" + uri);
                }
            });
        }
    }

    public File getFile(){
        return new File(getDirPath(), getImageFileName());
    }

}
