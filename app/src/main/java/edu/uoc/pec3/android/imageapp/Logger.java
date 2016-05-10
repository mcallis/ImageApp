package edu.uoc.pec3.android.imageapp;

import android.util.Log;

/**
 * Created by Marc on 7/5/16.
 */
public class Logger {
    static final String CONSTANT = "UOC";

    private String mTAG;

    public Logger(String TAG){
        this.mTAG = TAG;
    }

    public void d (String msg){
        Log.d(CONSTANT + " - " + mTAG, msg);
    }

    public void i (String msg){
        Log.i(CONSTANT + " - " + mTAG, msg);
    }

    public void w (String msg){
        Log.w(CONSTANT + " - " + mTAG, msg);
    }

    public void e (String msg){
        Log.e(CONSTANT + " - " + mTAG, msg);
    }

    public void v (String msg){
        Log.v(CONSTANT + " - " + mTAG, msg);
    }

}

