package edu.nctu.wirelab.sensinggo.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.net.URL;

import edu.nctu.wirelab.sensinggo.Connect.HttpsConnection;
import edu.nctu.wirelab.sensinggo.Connect.Setting;
import edu.nctu.wirelab.sensinggo.R;
import edu.nctu.wirelab.sensinggo.ShowDialogMsg;
import edu.nctu.wirelab.sensinggo.UserConfig;
import pl.droidsonroids.gif.GifImageView;

public class SpecialEventFragment extends Fragment {

    TextView topic_TV, date_TV, coins_TV, text3_TV, text4_TV, qualified_TV;
    TextView camp_TV;
    GifImageView gifview;
    Button chooseCampBtn;
    AlertDialog alertDialog;
    int campchoice = -1;




    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_specialevent, container, false);
        topic_TV = view.findViewById(R.id.topic_content);
        date_TV = view.findViewById(R.id.date_content);
        coins_TV = view.findViewById(R.id.totalmoney_content);
        text3_TV = view.findViewById(R.id.text3);
        text4_TV = view.findViewById(R.id.text4);
        qualified_TV = view.findViewById(R.id.qualified_content);
        chooseCampBtn = view.findViewById(R.id.chooseCampBtn);
        gifview = view.findViewById(R.id.gif);
        camp_TV = view.findViewById(R.id.camp_content);

        topic_TV.setText(UserConfig.content);
        date_TV.setText(UserConfig.date);
        coins_TV.setText(UserConfig.totalcoins);

        if(!UserConfig.showmoney){
            text3_TV.setVisibility(View.INVISIBLE);
            coins_TV.setVisibility(View.INVISIBLE);
        }

        Log.d("asdfwfw", "value"+UserConfig.qualified);

        if(!UserConfig.qualified){
            qualified_TV.setText(R.string.not_qualified);
        }
        else{
            qualified_TV.setText(R.string.qualified_content);
        }

        if(UserConfig.choosecamp!=-1 || UserConfig.myUserName.compareTo("DefaultUser")==0){
            chooseCampBtn.setVisibility(View.INVISIBLE);
        }

        if(UserConfig.choosecamp==1){
            String internetUrl = Setting.HTTPSSERVER + "fox" + ".gif";
            Glide.with(getActivity())
                    .load(internetUrl)
                    .error(R.drawable.icon_manb) //load失敗的Drawable
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(gifview);
            camp_TV.setText(R.string.camp_fox);
        }
        if(UserConfig.choosecamp==0){
            String internetUrl = Setting.HTTPSSERVER + "panda" + ".gif";
            Glide.with(getActivity())
                    .load(internetUrl)
                    .error(R.drawable.icon_manb) //load失敗的Drawable
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(gifview);
            camp_TV.setText(R.string.camp_panda);
        }


        chooseCampBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){

                final View registerView =  LayoutInflater.from(getActivity()).inflate(R.layout.specialevent_dialog,null);
                TextView chooseResult = registerView.findViewById(R.id.resultTV);
                ImageView foxIV = registerView.findViewById(R.id.foxIV);
                ImageView pandaIV = registerView.findViewById(R.id.pandaIV);

                foxIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chooseResult.setText("你是狐狸戰士");
                        camp_TV.setText(R.string.camp_fox);
                        campchoice = 1;
                    }

                });

                pandaIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chooseResult.setText("你是熊貓戰士");
                        camp_TV.setText(R.string.camp_panda);
                        campchoice = 0;
                    }

                });

                alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle("請選擇陣營")
                        .setView(registerView)
                        .setPositiveButton("出征", null)
                        .show();

                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        // send message to server
                        String Info = "username=" + UserConfig.myUserName +
                                "&campChoice=" + campchoice;

                        connectServer("POST", "/chooseCamp", Info);

                        // assign choosecamp into user personal information
                        UserConfig.choosecamp = campchoice;

                        // let choose button invisible
                        chooseCampBtn.setVisibility(View.INVISIBLE);

                        // show gif on the place where the choose button first be
                        String internetUrl = "";
                        if(campchoice == 1){
                            internetUrl = Setting.HTTPSSERVER + "fox" + ".gif";
                        }
                        else if(campchoice == 0){
                            internetUrl = Setting.HTTPSSERVER + "panda" + ".gif";
                        }


                        Glide.with(getActivity())
                                .load(internetUrl)
                                .error(R.drawable.icon_manb) //load失敗的Drawable
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(gifview);

//                        AlertDialog testDialog = new AlertDialog.Builder(getActivity())
//                                .setTitle("請選擇陣營")
//                                .setView(testview)
//                                .setPositiveButton("出征", null)
//                                .show();

                        alertDialog.dismiss();

                    }
                });

            }
        });



        return  view;
    }

    private void connectServer(String method, String path, String info){
        Log.d("0612conn", info);
        HttpsConnection httpsConnection = new HttpsConnection(getActivity());
        httpsConnection.setActivity(getActivity());
        httpsConnection.setMethod(method, info);
        httpsConnection.execute(path);

    }



}
