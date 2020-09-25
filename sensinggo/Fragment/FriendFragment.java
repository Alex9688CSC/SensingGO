package edu.nctu.wirelab.sensinggo.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.nctu.wirelab.sensinggo.Connect.HttpsConnection;
import edu.nctu.wirelab.sensinggo.Connect.Setting;
import edu.nctu.wirelab.sensinggo.PhotoActivity;
import edu.nctu.wirelab.sensinggo.R;
import edu.nctu.wirelab.sensinggo.ShowDialogMsg;
import edu.nctu.wirelab.sensinggo.SocialUserConfig;
import edu.nctu.wirelab.sensinggo.UserConfig;
public class FriendFragment extends Fragment{

    //    private Button sendmailButton, addblackButton; //, sendmoneyButton;
    private ImageButton editNicknameImageButton, sendmailImageButton, addblackImageButton;
    private TextView textViewNickname, textViewHelloMsg, age_gender_TV, distance_TV, email_TV;
    private CircleImageView photoCircleImageView;
    private String gmail = "";

    String successfulNickname = ""; // use for updating the nickname when get "successfully"

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        editNicknameImageButton = (ImageButton) view.findViewById(R.id.imageButtonEditNickname);
        textViewNickname = (TextView) view.findViewById(R.id.textViewNickname);
        textViewHelloMsg = (TextView) view.findViewById(R.id.textViewHelloMsg);
        age_gender_TV = (TextView) view.findViewById(R.id.age_gender_TV);
        distance_TV = (TextView) view.findViewById(R.id.distance_TV);
        email_TV = (TextView) view.findViewById(R.id.email_TV);
        photoCircleImageView = (CircleImageView) view.findViewById(R.id.imageViewUser);
//        sendmailImageButton = view.findViewById(R.id.gmailimageButton);
        addblackImageButton = view.findViewById(R.id.addblackimageButton);

        email_TV.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        String temp_age = "", temp_gender = "", temp_friend_username = "";

        for (int i = 0; i < SocialUserConfig.infoValue.length; i++){
            if(SocialUserConfig.infoItems[i].equals("username")) {
                temp_friend_username = SocialUserConfig.infoValue[i];
            }
            else if (SocialUserConfig.infoItems[i].equals("nickname")) {
                textViewNickname.setText(SocialUserConfig.infoValue[i]);
            }
            else if (SocialUserConfig.infoItems[i].equals("gender")) {
                if (SocialUserConfig.infoValue[i].equals("female")) {
                    temp_gender += (getString(R.string.gender_female));
                }
                else {
                    temp_gender += (getString(R.string.gender_male));
                }
            }
            else if (SocialUserConfig.infoItems[i].equals("birthday")) {
                temp_age += Integer.toString(CalculateAge(SocialUserConfig.infoValue[i]));
            }
            else if (SocialUserConfig.infoItems[i].equals("email")) {
                email_TV.setText(SocialUserConfig.infoValue[i]);
            }
            else if (SocialUserConfig.infoItems[i].equals("helloMsg")) {
                textViewHelloMsg.setText(SocialUserConfig.infoValue[i]);
            }
            else if (SocialUserConfig.infoItems[i].equals("distance")) {
                distance_TV.setText(CalculateDistance(SocialUserConfig.infoValue[i]));
            }
        }
        age_gender_TV.setText(temp_age + " " + getString(R.string.yearsold) + temp_gender);

        String internetUrl = Setting.HTTPSSERVER + temp_friend_username + ".jpg";
        ColorDrawable cd = new ColorDrawable(0x000000);

        Glide.with(getActivity())
                .load(internetUrl)
                .placeholder(cd)
                .error(R.drawable.icon_manb) //load失敗的Drawable
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(photoCircleImageView);

        photoCircleImageView.setOnClickListener(new CircleImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PhotoActivity.class);
                intent.addFlags( Intent.FLAG_ACTIVITY_NO_ANIMATION );
                intent.putExtra("url", internetUrl);
                startActivity(intent);
            }
        });




//        sendmailImageButton.setOnClickListener(new Button.OnClickListener(){
//            @Override
//            public void onClick(View v){
//
//                String userInfo = "username=" + UserConfig.myUserName;
//                final HttpsConnection httpsConnection = new HttpsConnection(getActivity());
//                //httpsConnection.setJsonParser(jsonParser);
//                httpsConnection.setMethod("POST", userInfo);
//                //httpsConnection.setFragment(LoginFragment.this);
//                httpsConnection.setActivity(getActivity());
//                httpsConnection.execute("/mailCount");
//
//
//                Log.d("SDFAWFAWAWE", gmail);
//                Intent i = new Intent(Intent.ACTION_SEND);
//                i.setType("message/rfc822");
//                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{gmail});
//                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.emailtitle));
//                i.putExtra(Intent.EXTRA_TEXT   , "");
//                try {
//                    startActivity(Intent.createChooser(i, getString(R.string.sendemail)));
//                } catch (android.content.ActivityNotFoundException ex) {
//                    Toast.makeText(getActivity(), getString(R.string.failemail), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });



        addblackImageButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                final AlertDialog.Builder addblackDialog = new AlertDialog.Builder(getActivity());
                addblackDialog.setMessage(getString(R.string.addintoblack))
                        .setIcon(R.mipmap.ic_launcher)
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Post_Addblack(SocialUserConfig.infoValue[0]);

                                    }
                                }).start();


                            }
                        })
                        .setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .show();


            }

        });

        email_TV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String userInfo = "username=" + UserConfig.myUserName;
                final HttpsConnection httpsConnection = new HttpsConnection(getActivity());
                //httpsConnection.setJsonParser(jsonParser);
                httpsConnection.setMethod("POST", userInfo);
                //httpsConnection.setFragment(LoginFragment.this);
                httpsConnection.setActivity(getActivity());
                httpsConnection.execute("/mailCount");

                Log.d("SDFAWFAWAWE", gmail);
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{gmail});
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.emailtitle));
                i.putExtra(Intent.EXTRA_TEXT   , "");
                try {
                    startActivity(Intent.createChooser(i, getString(R.string.sendemail)));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), getString(R.string.failemail), Toast.LENGTH_SHORT).show();
                }
            }
        });

//        sendmoneyButton.setOnClickListener(new Button.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                final View registerView =  LayoutInflater.from(getActivity()).inflate(R.layout.fragment_sendmoney,null);
//                final EditText numbercoinsEditText = (EditText) registerView.findViewById(R.id.number_of_coins);
//                final AlertDialog.Builder GetCoinDialog = new AlertDialog.Builder(getActivity());
//
//
//                alertDialog = new AlertDialog.Builder(getActivity())
//                        .setTitle(getString(R.string.sendmoneytitle))
//                        .setView(registerView)
//                        .setNegativeButton(getString(R.string.cancel),new DialogInterface.OnClickListener(){
//                            @Override
//                            public void onClick(DialogInterface dialog, int which){
//                            }
//                        })
//                        .setPositiveButton(getString(R.string.okresend), new DialogInterface.OnClickListener(){
//                            @Override
//                            public void onClick(DialogInterface dialog, int which){
//                                String numbercoins = numbercoinsEditText.getText().toString();
//                                if(numbercoins.equals("")){
//                                    ShowDialogMsg.showDialog(getString(R.string.noblank1));
//                                }
//                                else {
//                                    // one more check whether send coins
//                                    GetCoinDialog.setMessage(getString(R.string.sendmoneyrecheck1) + numbercoins + getString(R.string.sendmoneyrecheck2) + SocialUserConfig.infoValue[0] + getString(R.string.sendmoneyrecheck3) + getString(R.string.sendmoneyrecheck4))
//                                            .setIcon(R.mipmap.ic_launcher)
//                                            .setPositiveButton("yes", (dialogInterface, i) -> {
//                                                String sendmoneyInfo = "numbercoins=" + numbercoins +
//                                                        "&username=" + UserConfig.myUserName +
//                                                        "&targetname=" + SocialUserConfig.infoValue[0];
//                                                //dialogNoDismiss(dialog);
//
//                                                final HttpsConnection httpsConnection = new HttpsConnection(getActivity());
//                                                //httpsConnection.setJsonParser(jsonParser);
//                                                httpsConnection.setMethod("POST", sendmoneyInfo);
//                                                httpsConnection.setFragment(SimUserFragment.this);
//                                                httpsConnection.setActivity(getActivity());
//                                                httpsConnection.setDialogInterface(dialog);
//                                                httpsConnection.execute("/moneyTransaction");
//                                            })
//                                            .setNegativeButton("no", (dialogInterface, i) -> {
//                                            })
//                                            .show();
//                                }
//                            }
//                        }).show();
//
//
//            }
//
//        });

        editNicknameImageButton.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v) {

                final View editNicknameView =  inflater.inflate(R.layout.dialog_edit_nickname, null);
                final EditText nicknameEditText = editNicknameView.findViewById(R.id.edittext_edit_nickname);

                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.edit_nickname))
                        .setView(editNicknameView)
                        .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){

                                String nickname = nicknameEditText.getText().toString();
                                if(nickname.equals("")){
                                    ShowDialogMsg.showDialog(getString(R.string.noblank_nostar));
                                }
                                else{
                                    successfulNickname = nickname;
                                    String encodedNickName = "";

                                    try {
                                        encodedNickName = URLEncoder.encode(nickname, "UTF-8");
                                        Log.d("testEncode", "encodedNickName = " + encodedNickName);
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }

                                    String info = "username=" + UserConfig.myUserName +
                                            "&friendname=" + SocialUserConfig.infoValue[0] +
                                            "&nickname=" + encodedNickName;

                                    connectServer("POST", "/modifyNickname", info);
                                    Log.d("test9797", "info = " + info);
                                }

                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                            }
                        }).show();

            }

        });




        return  view;
    }

    public void modifyNickname() {
        textViewNickname.setText(successfulNickname);
    }

    private void connectServer(String method, String path, String info){
        Log.d("0612conn", info);
        HttpsConnection httpsConnection = new HttpsConnection(getActivity());
        httpsConnection.setActivity(getActivity());
        httpsConnection.setFragment(this);
        httpsConnection.setMethod(method, info);
        httpsConnection.execute(path);
    }

    private void Post_Addblack(String blackman){
        //String urlstring = "http://140.113.216.39/addBlacklist";
        String Info = "username=" + UserConfig.myUserName +
                "&blackname=" + blackman;
        connectServer("POST", "/addBlacklist", Info);

    }

    private int CalculateAge(String birthday){
        Log.d("071sdf7",birthday);
        String[] split_birth = birthday.split("/");
        int age=0;
        if(split_birth.length>0 && split_birth[0].compareTo("")!=0) {
            int user_yser = Integer.valueOf(split_birth[0]);

            int current_year = Calendar.getInstance().get(Calendar.YEAR);
            age = current_year-user_yser;
        }
        return age;
    }

    private String CalculateDistance(String distance){
        int distance_int =  Integer.valueOf(distance);
        String return_value = "";
        if (distance_int < 1000){
            return_value += distance + getString(R.string.m);
        }
        else{
            return_value += distance_int/1000 + getString(R.string.km);
        }
        return return_value;
    }


}

//public class FriendFragment extends Fragment{
//
////    private Button sendmailButton, addblackButton; //, sendmoneyButton;
//    private ImageButton editNicknameImageButton, sendmailImageButton, addblackImageButton;
//    private TextView textViewNickname, textViewHelloMsg, textViewUsername, textViewBirthday, textViewGender, textViewEmail;
//    private CircleImageView photoCircleImageView;
//    private String gmail = "";
//
//    String successfulNickname = ""; // use for updating the nickname when get "successfully"
//
//    @Override
//    public void onCreate(Bundle savedInstanceState){
//        super.onCreate(savedInstanceState);
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        View view = inflater.inflate(R.layout.fragment_friends, container, false);
//        editNicknameImageButton = (ImageButton) view.findViewById(R.id.imageButtonEditNickname);
//        textViewNickname = (TextView) view.findViewById(R.id.textViewNickname);
//        textViewHelloMsg = (TextView) view.findViewById(R.id.textViewHelloMsg);
//        textViewUsername = (TextView) view.findViewById(R.id.textViewUsername);
//        textViewBirthday = (TextView) view.findViewById(R.id.textViewBirthday);
//        textViewGender = (TextView) view.findViewById(R.id.textViewGender);
//        textViewEmail = (TextView) view.findViewById(R.id.textViewEmail);
//        photoCircleImageView = (CircleImageView) view.findViewById(R.id.imageViewUser);
////        sendmailImageButton = view.findViewById(R.id.gmailimageButton);
//        addblackImageButton = view.findViewById(R.id.addblackimageButton);
//
//        textViewEmail.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
//
//        for (int i = 0; i < SocialUserConfig.infoValue.length; i++){
//            if (SocialUserConfig.infoItems[i].equals("username")) {
//                textViewUsername.setText(SocialUserConfig.infoValue[i]);
//            }
//            else if (SocialUserConfig.infoItems[i].equals("nickname")) {
//                textViewNickname.setText(SocialUserConfig.infoValue[i]);
//            }
//            else if (SocialUserConfig.infoItems[i].equals("gender")) {
//                if (SocialUserConfig.infoValue[i].equals("female")) {
//                    textViewGender.setText(getString(R.string.gender_female));
//                }
//                else {
//                    textViewGender.setText(getString(R.string.gender_male));
//                }
//            }
//            else if (SocialUserConfig.infoItems[i].equals("birthday")) {
//                textViewBirthday.setText(SocialUserConfig.infoValue[i]);
//            }
//            else if (SocialUserConfig.infoItems[i].equals("email")) {
//                textViewEmail.setText(SocialUserConfig.infoValue[i]);
//            }
//            else if (SocialUserConfig.infoItems[i].equals("helloMsg")) {
//                textViewHelloMsg.setText(SocialUserConfig.infoValue[i]);
//            }
//        }
//
//        String internetUrl = Setting.HTTPSSERVER + textViewUsername.getText() + ".jpg";
//        ColorDrawable cd = new ColorDrawable(0x000000);
//
//        Glide.with(getActivity())
//                .load(internetUrl)
//                .placeholder(cd)
//                .error(R.drawable.icon_manb) //load失敗的Drawable
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .into(photoCircleImageView);
//
//        photoCircleImageView.setOnClickListener(new CircleImageView.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), PhotoActivity.class);
//                intent.addFlags( Intent.FLAG_ACTIVITY_NO_ANIMATION );
//                intent.putExtra("url", internetUrl);
//                startActivity(intent);
//            }
//        });
//
//
//
//
////        sendmailImageButton.setOnClickListener(new Button.OnClickListener(){
////            @Override
////            public void onClick(View v){
////
////                String userInfo = "username=" + UserConfig.myUserName;
////                final HttpsConnection httpsConnection = new HttpsConnection(getActivity());
////                //httpsConnection.setJsonParser(jsonParser);
////                httpsConnection.setMethod("POST", userInfo);
////                //httpsConnection.setFragment(LoginFragment.this);
////                httpsConnection.setActivity(getActivity());
////                httpsConnection.execute("/mailCount");
////
////
////                Log.d("SDFAWFAWAWE", gmail);
////                Intent i = new Intent(Intent.ACTION_SEND);
////                i.setType("message/rfc822");
////                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{gmail});
////                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.emailtitle));
////                i.putExtra(Intent.EXTRA_TEXT   , "");
////                try {
////                    startActivity(Intent.createChooser(i, getString(R.string.sendemail)));
////                } catch (android.content.ActivityNotFoundException ex) {
////                    Toast.makeText(getActivity(), getString(R.string.failemail), Toast.LENGTH_SHORT).show();
////                }
////            }
////        });
//
//
//
//        addblackImageButton.setOnClickListener(new Button.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                final AlertDialog.Builder addblackDialog = new AlertDialog.Builder(getActivity());
//                addblackDialog.setMessage(getString(R.string.addintoblack))
//                        .setIcon(R.mipmap.ic_launcher)
//                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                new Thread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Post_Addblack(SocialUserConfig.infoValue[0]);
//
//                                    }
//                                }).start();
//
//
//                            }
//                        })
//                        .setNegativeButton("no", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                            }
//                        })
//                        .show();
//
//
//            }
//
//        });
//
//        textViewEmail.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                String userInfo = "username=" + UserConfig.myUserName;
//                final HttpsConnection httpsConnection = new HttpsConnection(getActivity());
//                //httpsConnection.setJsonParser(jsonParser);
//                httpsConnection.setMethod("POST", userInfo);
//                //httpsConnection.setFragment(LoginFragment.this);
//                httpsConnection.setActivity(getActivity());
//                httpsConnection.execute("/mailCount");
//
//                Log.d("SDFAWFAWAWE", gmail);
//                Intent i = new Intent(Intent.ACTION_SEND);
//                i.setType("message/rfc822");
//                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{gmail});
//                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.emailtitle));
//                i.putExtra(Intent.EXTRA_TEXT   , "");
//                try {
//                    startActivity(Intent.createChooser(i, getString(R.string.sendemail)));
//                } catch (android.content.ActivityNotFoundException ex) {
//                    Toast.makeText(getActivity(), getString(R.string.failemail), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
////        sendmoneyButton.setOnClickListener(new Button.OnClickListener(){
////            @Override
////            public void onClick(View v){
////                final View registerView =  LayoutInflater.from(getActivity()).inflate(R.layout.fragment_sendmoney,null);
////                final EditText numbercoinsEditText = (EditText) registerView.findViewById(R.id.number_of_coins);
////                final AlertDialog.Builder GetCoinDialog = new AlertDialog.Builder(getActivity());
////
////
////                alertDialog = new AlertDialog.Builder(getActivity())
////                        .setTitle(getString(R.string.sendmoneytitle))
////                        .setView(registerView)
////                        .setNegativeButton(getString(R.string.cancel),new DialogInterface.OnClickListener(){
////                            @Override
////                            public void onClick(DialogInterface dialog, int which){
////                            }
////                        })
////                        .setPositiveButton(getString(R.string.okresend), new DialogInterface.OnClickListener(){
////                            @Override
////                            public void onClick(DialogInterface dialog, int which){
////                                String numbercoins = numbercoinsEditText.getText().toString();
////                                if(numbercoins.equals("")){
////                                    ShowDialogMsg.showDialog(getString(R.string.noblank1));
////                                }
////                                else {
////                                    // one more check whether send coins
////                                    GetCoinDialog.setMessage(getString(R.string.sendmoneyrecheck1) + numbercoins + getString(R.string.sendmoneyrecheck2) + SocialUserConfig.infoValue[0] + getString(R.string.sendmoneyrecheck3) + getString(R.string.sendmoneyrecheck4))
////                                            .setIcon(R.mipmap.ic_launcher)
////                                            .setPositiveButton("yes", (dialogInterface, i) -> {
////                                                String sendmoneyInfo = "numbercoins=" + numbercoins +
////                                                        "&username=" + UserConfig.myUserName +
////                                                        "&targetname=" + SocialUserConfig.infoValue[0];
////                                                //dialogNoDismiss(dialog);
////
////                                                final HttpsConnection httpsConnection = new HttpsConnection(getActivity());
////                                                //httpsConnection.setJsonParser(jsonParser);
////                                                httpsConnection.setMethod("POST", sendmoneyInfo);
////                                                httpsConnection.setFragment(SimUserFragment.this);
////                                                httpsConnection.setActivity(getActivity());
////                                                httpsConnection.setDialogInterface(dialog);
////                                                httpsConnection.execute("/moneyTransaction");
////                                            })
////                                            .setNegativeButton("no", (dialogInterface, i) -> {
////                                            })
////                                            .show();
////                                }
////                            }
////                        }).show();
////
////
////            }
////
////        });
//
//        editNicknameImageButton.setOnClickListener(new ImageButton.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//
//                final View editNicknameView =  inflater.inflate(R.layout.dialog_edit_nickname, null);
//                final EditText nicknameEditText = editNicknameView.findViewById(R.id.edittext_edit_nickname);
//
//                new AlertDialog.Builder(getActivity())
//                        .setTitle(getString(R.string.edit_nickname))
//                        .setView(editNicknameView)
//                        .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener(){
//                            @Override
//                            public void onClick(DialogInterface dialog, int which){
//
//                                String nickname = nicknameEditText.getText().toString();
//                                if(nickname.equals("")){
//                                    ShowDialogMsg.showDialog(getString(R.string.noblank_nostar));
//                                }
//                                else{
//                                    successfulNickname = nickname;
//                                    String encodedNickName = "";
//
//                                    try {
//                                        encodedNickName = URLEncoder.encode(nickname, "UTF-8");
//                                        Log.d("testEncode", "encodedNickName = " + encodedNickName);
//                                    } catch (UnsupportedEncodingException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    String info = "username=" + UserConfig.myUserName +
//                                            "&friendname=" + SocialUserConfig.infoValue[0] +
//                                            "&nickname=" + encodedNickName;
//
//                                    connectServer("POST", "/modifyNickname", info);
//                                    Log.d("test9797", "info = " + info);
//                                }
//
//                            }
//                        })
//                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
//                            @Override
//                            public void onClick(DialogInterface dialog, int which){
//                            }
//                        }).show();
//
//            }
//
//        });
//
//
//
//
//        return  view;
//    }
//
//    public void modifyNickname() {
//        textViewNickname.setText(successfulNickname);
//    }
//
//    private void connectServer(String method, String path, String info){
//        Log.d("0612conn", info);
//        HttpsConnection httpsConnection = new HttpsConnection(getActivity());
//        httpsConnection.setActivity(getActivity());
//        httpsConnection.setFragment(this);
//        httpsConnection.setMethod(method, info);
//        httpsConnection.execute(path);
//    }
//
//    private void Post_Addblack(String blackman){
//        //String urlstring = "http://140.113.216.39/addBlacklist";
//        String Info = "username=" + UserConfig.myUserName +
//                "&blackname=" + blackman;
//        connectServer("POST", "/addBlacklist", Info);
//
//    }
//
//
//}
