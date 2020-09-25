package edu.nctu.wirelab.sensinggo;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import android.os.Handler;

public class ShowDialogMsg {
    public static Toast mToast;
    public static Context mcontext;
    public static Handler mHandler = new Handler();
    private static Runnable r = new Runnable() {
        @Override
        public void run() {
            mToast.cancel();
        }
    };

    public static void showDialog(String msg){
        Toast.makeText(mcontext, msg, Toast.LENGTH_SHORT).show(); //show the msg in 200ms
    }

    public static void showDialogLong(String msg){
        Toast.makeText(mcontext, msg, Toast.LENGTH_LONG).show();
    }


    // Ref: http://123android.blogspot.com/2013/01/android-toast-toast.html
    public static void showDialogShort(String msg, int duration) {
        mHandler.removeCallbacks(r);
        if (mToast==null) {
            mToast = Toast.makeText(mcontext, msg, Toast.LENGTH_SHORT);
        }
        else {
            mToast.setText(msg);
        }
        mHandler.postDelayed(r, duration);
        mToast.show();
    }

}
