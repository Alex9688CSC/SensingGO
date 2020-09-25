package edu.nctu.wirelab.sensinggo;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import static edu.nctu.wirelab.sensinggo.LoginActivity.animation_regi;
import static edu.nctu.wirelab.sensinggo.LoginActivity.container1;
import static edu.nctu.wirelab.sensinggo.LoginActivity.container2;
import static edu.nctu.wirelab.sensinggo.LoginActivity.press_register;

public class success_registDialog {
    private Activity activity;
    private AlertDialog dialog;

    success_registDialog(Activity mActivity){
        activity = mActivity;
    }

    public void start_success_registDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View v =  inflater.inflate(R.layout.success_regist_dialog, null);
        builder.setView(v);
        builder.setCancelable(false);
        Button OK = (Button)v.findViewById(R.id.button_ok);
        dialog = builder.create();
        OK.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
//                Log.i("ttt","no");
//                Toast.makeText(getActivity(), "no").show();
                press_register = false;
                container2.setVisibility(View.INVISIBLE);
                container1.startAnimation(animation_regi);
                container1.setVisibility(View.VISIBLE);
            }

        });
        dialog.show();

    }

    public void dismissDialog(){
        dialog.dismiss();
    }
}
