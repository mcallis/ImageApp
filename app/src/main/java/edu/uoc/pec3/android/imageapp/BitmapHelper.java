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
    private String mCurrentPhotoPath;
    private Logger mLogger;
    private File mFile;
    private Bitmap thumbnail = null;

    public BitmapHelper(Context context){
        this.mContext = context;
        this.mLogger = new Logger(TAG);
    }

    public void createImageFile() {
        mFile = newImageFile();
        if (mFile.exists()){
            mFile.delete();
        }



        /*
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + mFile.getAbsolutePath();
        mLogger.i("File has been created to " + mCurrentPhotoPath);
        return mFile;
        */
    }

    private File newImageFile() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(getDirPath() + File.separator + getImageFileName());
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //return new File(getDirPath() + File.separator + getImageFileName());
        return destination;
    }


    public File getDirPath(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    }

    public String getImageFileName(){
        return  "/imageapp.jpg";
    }

    public void setThumbnail(Bitmap bitmap) {
        this.thumbnail = bitmap;
    }



    public void galleryAddPic() {
        /*
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, galleryGetPic().getAbsolutePath());
        mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
*/
        createImageFile();

        Toast.makeText(mContext, "Image saved!", Toast.LENGTH_LONG).show();

        refreshGallery();
    }
    

    public void galleryDeletePic(CallbackDelete callback){
        File fdelete = galleryGetPic();
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                mLogger.i("file Deleted :" + fdelete.getAbsolutePath());
                refreshGallery();
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

    public File galleryGetPic(){
        return imageFile();
    }

}
