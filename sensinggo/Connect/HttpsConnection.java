package edu.nctu.wirelab .sensinggo.Connect;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import edu.nctu.wirelab.sensinggo.Fragment.FriendFragment;
import edu.nctu.wirelab.sensinggo.Fragment.InfoFragment;
import edu.nctu.wirelab.sensinggo.Fragment.MapFragment;
import edu.nctu.wirelab.sensinggo.Friend;
import edu.nctu.wirelab.sensinggo.LoginActivity;
import edu.nctu.wirelab.sensinggo.R;
import edu.nctu.wirelab.sensinggo.Record.Detailrecord;
import edu.nctu.wirelab.sensinggo.Record.TransactionRecord;
import edu.nctu.wirelab.sensinggo.SocialUserConfig;
import edu.nctu.wirelab.sensinggo.UserConfig;
import edu.nctu.wirelab.sensinggo.File.JsonParser;
import edu.nctu.wirelab.sensinggo.MainActivity;
import edu.nctu.wirelab.sensinggo.ShowDialogMsg;


import static edu.nctu.wirelab.sensinggo.Connect.Setting.HTTPS_SERVER;
import static java.lang.Integer.parseInt;

//import static edu.nctu.wirelab.sensinggo.Connect.Setting.HTTPS_SERVER_PORT;

/**
 * Send the signal data to specified server path by GET or POST method
 * It's newed in
 * 1. MainActivity: when an user login, he/she send a http request
 * 2. UserFragment: when the user upload the signal data
 */

public class HttpsConnection extends AsyncTask<String, Void, String>{
    private static final String tagName = "HttpsConnection";
    public static String ftpKey = "";
    private Activity mActivity = null;
    private Fragment mFragment;

    private Certificate ca;
    private Context myContext;
    private JsonParser JsonParser = null;
    private SSLContext sslContext;
    private String configPath;
    private String sendMethod = null; //sendMethod can be "GET" or "POST"
    private String getVariables = null, postVariables = null;
    private String myUserName = null;
    private String mCode = null;
    private String resultKey = "";
    public DialogInterface dialog;
    public static boolean isdefaultuser = false;
    private boolean isFreeWiFi = false;
    private String urlString;

    //ProgressDialog progressDialog;

    public void setJsonParser(JsonParser json){
        JsonParser=json;
//        if (JsonParser == MainActivity.JsonParser) {
//            Log.d("9898", "H:the same");
//        }
//        else {
//            Log.d("9898", "H:not the same");
//        }
    }

    public void setDialogInterface(DialogInterface dialogInterface){
        dialog = dialogInterface;
    }
    public HttpsConnection(Context context){
        myContext = context;
        //progressDialog = new ProgressDialog(myContext);

        if(myContext == null){
            return;
        }
        configPath = "/data/data/" + myContext.getPackageName() + "/config";

    }

    @Override
    protected void onPreExecute() {
//        progressDialog.setMessage("Loading...");
//        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d("0612", "https doInBackground start!");
        URL url = null;
        String urlFile = "";
        String responseStr="";
        try {
            if(sendMethod!=null && sendMethod.equals("GET")){
                urlFile = params[0].concat("?" + getVariables);
            }
            else{
                urlFile = params[0];
            }

            //Distinguish what API is called
            urlString = params[0];

            // Tell the URLConnection to use a SocketFactory from our SSLContext
//            url = new URL("https", HTTPS_SERVER, 9990, urlFile); //for test server
            url = new URL("https", HTTPS_SERVER, urlFile); // ("https", "140.113.216.37", "/abc/def") -> https://140.113.216.37/abc/def
            Log.d(tagName, "urlFile:" + url);

            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            if(sendMethod!=null && sendMethod.equals("GET")){
                urlConnection.setRequestMethod("GET");
            }
            else if(sendMethod!=null && sendMethod.equals("POST")){
                urlConnection.setRequestMethod("POST");
                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(postVariables);
                wr.flush();
                wr.close();
            }

            InputStream in = urlConnection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));



            String line;
            while ((line = reader.readLine()) != null) {
                //Log.d(tagName, "line:" + line);
                responseStr = responseStr.concat(line);
            }
            Log.d("0612", "https doInBackground end!");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "connection failed";
        } catch (IOException e) {
            //Log.d(tagName, "The website may be crashed");
            e.printStackTrace();
            return "connection failed";
        }
        return responseStr;
    }



    public void setMethod(String method, String variables){
        sendMethod = method;
        Log.d("split1", variables);
        splitAndGetVariables(variables);
        Log.d("split2", variables);

        if(sendMethod.equals("GET")){
            getVariables = variables;
            postVariables = null;
        }
        else if(sendMethod.equals("POST")){
            postVariables = variables;
            getVariables = null;
            Log.d("picturePathpicturePath","value2: "+postVariables.length());
        }
        else{
            sendMethod = null;
            postVariables = null;
            getVariables = null;
        }
    }

    public void splitAndGetVariables(String variables){
        String[] token = variables.split("&");

        for(String t : token){
            //Log.d(tagName, "t: " + t);
            if(t.contains("username")){
                Log.d(tagName, "t:"+t);
                String[] subtoken = t.split("=");
                myUserName = subtoken[1];
                Log.d(tagName, "myUserName:"+myUserName);
            }
            else if (t.contains("code")){
                String[] subtoken = t.split("=");
                mCode = subtoken[1];
                Log.d(tagName, "t: "+mCode);
            }
        }
    }

    public void setActivity(Activity activity){
        mActivity = activity;
    }

    public void setFragment(Fragment fragment){
        mFragment = fragment;
    }


    public String getResultKey(){
        return resultKey;
    }

    private void login(){
        LoginActivity.activity.finish();
        UserConfig.setUserName(myUserName);
        UserConfig.saveConfigTo(configPath);
        MainActivity.recordPrefix = new String (UserConfig.myUserName+"RECORD");
        Log.d("0523", "userName:"+myUserName);
        //JsonParser.setAccount("USER" + UserConfig.myUserid);
        if(mActivity instanceof MainActivity){
            ((MainActivity) mActivity).loginSuccess();
            //((MainActivity) mActivity).updateConfigMoney();
        }
    }

    public void unpackJson(JSONArray jsonArray){

        try {
            //Log.d("0717","unpackJson in Https");

            JSONObject jsonObject = jsonArray.getJSONObject(0);
            Log.d("0717","in Https"+ jsonObject.toString());

            if (urlString.equals("/getUID")) {
                Log.d("testUID","uid = " + jsonObject.getString("uid"));
                UserConfig.setUserID(jsonObject.getString("uid"));
                UserConfig.saveConfigTo(configPath);
                JsonParser.setAccount("USER" + UserConfig.myUserid);

                return;
            }
            else if (urlString.equals("/test")) {
                Log.d("9877","jsonArray: " + jsonArray.toString());
                Iterator<String> keys = jsonObject.keys();

                while(keys.hasNext()) {
                    String key = keys.next();
                    Log.d("9877","key: " + key);
                    String nickname = jsonObject.getString(key);
                    Log.d("9877","nickname: " + nickname);
                }
            }

            else if (urlString.equals("/addFriendByID")) {
                //Log.d("9878","jsonArray: " + jsonArray.toString());

                Log.d("addFriendByID","jsonArray: " + jsonArray.toString());
                Log.d("addFriendByID","jsonArray: " + jsonObject);
//                if(jsonObject.has("successfully")){
//                    Toast toast = Toast.makeText(myContext, myContext.getString(R.string.add_friend_successfully), Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.BOTTOM, 0, 0);
//                    toast.show();
//                }
                if(jsonObject.has("addFriendByID_failState")){
                    Log.d("addFriendByID","jsonArray: " + "addFriendByID_failState");
                    String result = jsonObject.getString("addFriendByID_failState");
                    Log.d("addFriendByID","jsonArray: " + result);
                    if (result.compareTo("0")==0) {
                        Toast toast = Toast.makeText(myContext, myContext.getString(R.string.cannot_add_yourself), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.show();
                    }
                    else if (result.compareTo("1")==0) {
                        Toast toast = Toast.makeText(myContext, myContext.getString(R.string.friend_name_not_exist), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.show();
                    }
                    else if (result.compareTo("2")==0) {
                        Toast toast = Toast.makeText(myContext, myContext.getString(R.string.friend_id_is_in_your_blacklist), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.show();
                    }
                    else if (result.compareTo("3")==0) {
                        Toast toast = Toast.makeText(myContext, myContext.getString(R.string.already_friend), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.show();
                    }
                    else if (result.compareTo("4")==0) {
                        Toast toast = Toast.makeText(myContext, myContext.getString(R.string.add_friend_already), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.show();
                    }
                    else if (result.compareTo("5")==0) {
                        Toast toast = Toast.makeText(myContext, myContext.getString(R.string.something_wrong), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.show();
                    }
                }
                else{
                    Log.i("addFriendByID","haha");
                }
            }

            else if (urlString.equals("/getToBeConfirmdFriend")) {
                Log.d("9878","jsonArray: " + jsonArray.toString());
                List<Friend> friendList = new ArrayList<>();

                JSONArray frineds = jsonObject.getJSONArray("toBeConfirmdFriends");
                Log.d("9878","frineds: " + frineds.toString());

                for(int i = 0; i < frineds.length(); i++) {
                    friendList.add(new Friend(frineds.getString(i), frineds.getString(i)));
                }

//                if(mActivity instanceof MainActivity){
//                    ((MainActivity) mActivity).createFriendRequest();
//                    ((MainActivity) mActivity).mFriendRequest.setFriendList(friendList);
//                    ((MainActivity) mActivity).transferFriendRequest();
//                }

                if(mActivity instanceof MainActivity){
                    ((MainActivity) mActivity).mSocial.setRequestFriendList(friendList);
                    ((MainActivity) mActivity).transferSocial();
                }

            }

            else if (urlString.equals("/getFriendInfo")) {
                SocialUserConfig.setSocialUserInfo(jsonObject);
//                if(mActivity instanceof MainActivity){
//                    ((MainActivity) mActivity).transferSimUser();
//                }
                if(mActivity instanceof MainActivity){
                    ((MainActivity) mActivity).transferFriend();
                }
            }

            else if (urlString.equals("/coinsTopUser")) {
                UserConfig.setCoinMasters(jsonArray);
            }




            // set progressbar INVISIBLE
            if(mActivity instanceof LoginActivity){
                Log.d("0ddfef612", "setSocialJson");
                LoginActivity.progressbar.setVisibility(View.INVISIBLE);
            }

            ///donate ticket
            if(jsonObject.has("is_get_ticket")){
                Log.i("is_get_ticket" , jsonObject.getString("is_get_ticket"));
                Boolean is_get_ticket = jsonObject.getBoolean("is_get_ticket");
                if(is_get_ticket){
                    String place = "random";
                    Log.i("is_get_ticket" , place);
                    InfoFragment.is_get_ticket_dialog( );
                }
            }
            else{}
            if(jsonObject.has("tickets_info")){
                Log.i("tickets_info" , String.valueOf(jsonObject.getJSONObject("tickets_info")));
                InfoFragment.parseTicketInfoJSON(jsonObject.getJSONObject("tickets_info").toString());
            }else{}
            if(jsonObject.has("current_phase")){
                Log.i("current_phase" , String.valueOf(jsonObject.getInt("current_phase")));
            }else{}
            if(jsonObject.has("current_coins")){
                UserConfig.current_coins = jsonObject.getInt("current_coins");

                InfoFragment.button_coin_to_ticket.setText(UserConfig.current_coins+"/250");

                Log.i("current_coins" , String.valueOf(jsonObject.getInt("current_coins")));
            }else{}
            if(jsonObject.has("coins")){
//                UserConfig.current_coins = jsonObject.getInt("current_coins");
                Log.i("wwwggg_coin" , String.valueOf(jsonObject.getJSONObject("coins")));
                InfoFragment.parseFacilityCoinsJSON(jsonObject.getJSONObject("coins").toString());
//                InfoFragment.parseFacilityJSON(jsonObject.toString());
//                InfoFragment.button_coin_to_ticket.setText(UserConfig.current_coins+"/500");
//                Log.i("wwwggg_animal" , "fff");
//                Log.i("wwwggg_animal" , String.valueOf(jsonObject.getInt("animal")));
            }else{}
            if(jsonObject.has("ch")){
//                UserConfig.current_coins = jsonObject.getInt("current_coins");
                Log.i("wwwggg_ch" , String.valueOf(jsonObject.getJSONObject("ch")));
                InfoFragment.parseFacilityChJSON(jsonObject.getJSONObject("ch").toString());
//                InfoFragment.button_coin_to_ticket.setText(UserConfig.current_coins+"/500");
//                Log.i("wwwggg_animal" , "fff");
//                Log.i("wwwggg_animal" , String.valueOf(jsonObject.getInt("animal")));
            }else{}
            if(jsonObject.has("en")){
//                UserConfig.current_coins = jsonObject.getInt("current_coins");
                Log.i("wwwggg_coin" , String.valueOf(jsonObject.getJSONObject("en")));
                InfoFragment.parseFacilityEnJSON(jsonObject.getJSONObject("en").toString());
//                InfoFragment.button_coin_to_ticket.setText(UserConfig.current_coins+"/500");
//                Log.i("wwwggg_animal" , "fff");
//                Log.i("wwwggg_animal" , String.valueOf(jsonObject.getInt("animal")));
            }else{}
            if(jsonObject.has("url")){
//                UserConfig.current_coins = jsonObject.getInt("current_coins");
                Log.i("wwwggg_coin" , String.valueOf(jsonObject.getJSONObject("url")));
                InfoFragment.parseFacilityURLJSON(jsonObject.getJSONObject("url").toString());
//                InfoFragment.button_coin_to_ticket.setText(UserConfig.current_coins+"/500");
//                Log.i("wwwggg_animal" , "fff");
//                Log.i("wwwggg_animal" , String.valueOf(jsonObject.getInt("animal")));
            }else{}
            if (jsonObject.has("friends")) { // get social list (name)
                Log.d("0612", "setSocialJson");
                UserConfig.setSocialInfo(jsonArray);
//                if(mActivity instanceof MainActivity){
//                    ((MainActivity) mActivity).transferSocial();
//                }

                List<Friend> friendList = new ArrayList<>();
//                JSONObject frineds = jsonObject.getJSONObject("friends");
//                Log.d("98789","frineds: " + frineds.toString());

//                Iterator<String> keys = frineds.keys();
//
//                while(keys.hasNext()) {
//                    String key = keys.next();
//                    Log.d("98789","key: " + key);
//                    String friendName = frineds.getString(key);
//                    Log.d("98789","friendName: " + friendName);
//
//                    friendList.add(new Friend(frineds.getString(key)));
//                }

                for (int i = 0; i < UserConfig.similarityArray.length; i++) {
                    if (UserConfig.similarityArray[i][2].equals("old")) {
                        Log.d("98789","friendName: " + UserConfig.similarityArray[i][0]);
                        Log.d("98789","nickName: " + UserConfig.similarityArray[i][1]);
                        friendList.add(new Friend(UserConfig.similarityArray[i][0], UserConfig.similarityArray[i][1]));
                    }
                }

                if(mActivity instanceof MainActivity){
                    ((MainActivity) mActivity).mSocial.setFriendList(friendList);

                    String userInfo = "username=" + UserConfig.myUserName;
                    ((MainActivity) mActivity).connectServer("POST", "/getToBeConfirmdFriend", userInfo); // transfer to social after response
                }

            }

            else if (jsonObject.has("username")){ // userinfo (gender, email)
                Log.d("chenorange", String.valueOf(jsonObject));
                Log.d("chenorange", String.valueOf(jsonObject.getString("emailLocked")));

                UserConfig.emailLocked = jsonObject.getString("emailLocked");

                //these information are mine
                if(jsonObject.getString("username").toString().compareTo(UserConfig.myUserName)==0) {
                    UserConfig.setUserInfo(jsonObject);
                    if(UserConfig.loginned){
                        //if(mActivity instanceof MainActivity){
                        ((MainActivity) mActivity).transferLoginUser();
                    }
                    else {
                        UserConfig.loginned = true;
                    }
                    //}
                }

                /* changed to use above "/getFriendInfo" */
//                //these information are my friends
//                else { // get similar user (detail date)
//                    SocialUserConfig.setSocialUserInfo(jsonObject);
//                    if(mActivity instanceof MainActivity){
//                        ((MainActivity) mActivity).transferSimUser();
//                    }
//                }
            }
            else if (jsonObject.has("resendemail")) {
                String email = jsonObject.getString("resendemail").toString();
                Toast toast = Toast.makeText(myContext, myContext.getString(R.string.gotocheckemail), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
            }
            else if (jsonObject.has("fb_username")) {
                myUserName = jsonObject.getString("fb_username");
                ShowDialogMsg.showDialog(myContext.getString(R.string.loginok));
                login();
            }
            else if (jsonObject.has("fb_login_fail_state")) {
                if(jsonObject.getInt("fb_login_fail_state")==0){
                    ShowDialogMsg.showDialog(myContext.getString(R.string.email_not_exist));
                }
                if(jsonObject.getInt("fb_login_fail_state")==1){
                    if(mActivity instanceof LoginActivity){
                        ((LoginActivity) mActivity).RegisterByFB();
                    }
                }
                if(jsonObject.getInt("fb_login_fail_state")==2){
                    ShowDialogMsg.showDialog(myContext.getString(R.string.not_fb_register));
                }
            }
            else if (jsonObject.has("fb_regist_fail_state")) {
                if(jsonObject.getInt("fb_regist_fail_state")==0){
                    ShowDialogMsg.showDialog(myContext.getString(R.string.useridisused));
                }
                if(jsonObject.getInt("fb_regist_fail_state")==1){
                    ShowDialogMsg.showDialog(myContext.getString(R.string.emailisused));
                }
                if(jsonObject.getInt("fb_regist_fail_state")==3){
                    ShowDialogMsg.showDialog(myContext.getString(R.string.email_not_exist));
                }
            }
            else if(jsonObject.has("detailtranrecord")){
                Detailrecord.setDetailTranrecordInfo(jsonObject.getJSONArray("detailtranrecord"));
                if(mActivity instanceof MainActivity){
                    ((MainActivity) mActivity).transferDetailTranRecord();
                }
            }
            else if(jsonObject.has("TransactionRecords")){
                TransactionRecord.setTranrecordInfo(jsonObject.getJSONArray("TransactionRecords"));
                if(mActivity instanceof MainActivity){
                    ((MainActivity) mActivity).transferTranRecord();
                }
            }
            else if(jsonObject.has("signalmessage")){
                MapFragment.parseJSON(jsonObject.getJSONArray("signalmessage").toString());
            }
            else if(jsonObject.has("coinlist")){
                MapFragment.parseCoinsListJSON(jsonObject.getJSONObject("coinlist").toString());
            }

            else if(jsonObject.has("friends photo")){
                MapFragment.parsefriendsPhotoListJSON(jsonObject.getJSONArray("friends photo").toString());
            }

            else if(jsonObject.has("regist_fail_state")){
                int flag = jsonObject.getInt("regist_fail_state");
                if(flag == 0){
                    LoginActivity.loadingDialog.dismissDialog();
                    ShowDialogMsg.showDialog(myContext.getString(R.string.useridisused));
                }
                else if(flag == 1){
                    ShowDialogMsg.showDialog(myContext.getString(R.string.emailisused));
                }
            }
            else if(jsonObject.has("newUserPromo")){
                int flag = jsonObject.getInt("newUserPromo");
                if(flag == 1){
                    ShowDialogMsg.showDialog(myContext.getString(R.string.recommendfriend_response1));
                }
                else if(flag == 2){
                    ShowDialogMsg.showDialog(myContext.getString(R.string.recommendfriend_response2));
                }
                else if(flag == 3){
                    ShowDialogMsg.showDialog(myContext.getString(R.string.recommendfriend_response3));
                }
                else if(flag == 4){
                    ShowDialogMsg.showDialog(myContext.getString(R.string.recommendfriend_response4));
                }
                else if(flag == 5){
                    ShowDialogMsg.showDialog(myContext.getString(R.string.recommendfriend_response5));
                }
            }

            else if(jsonObject.has("totalmoneymessage")){
                Log.i("totalmoneymessage","totalmoneymessage");
                String message = jsonObject.getString("totalmoneymessage");
                if(message.compareTo("successfully")==0) {
                    Double tempmoney = UserConfig.totalmoney;
                    if (jsonObject.has("total_money")) {
                        UserConfig.totalmoney = jsonObject.getDouble("total_money");
                    }
                    if (jsonObject.has("actMoney")) {
//                        Log.d("asdffe",jsonObject.getString("actMoney"));
                        if(jsonObject.has("kind")){
                            Log.d("asdffe","this is mother event");
//                            Log.d("asdffe",jsonObject.getString("actMoney"));
                        }
                        else{
                            Log.d("asdffe",jsonObject.getString("actMoney"));
                            UserConfig.ungiftedStamp = parseInt(jsonObject.getString("actMoney"));
                            MapFragment.mother_bar.setProgress(UserConfig.ungiftedStamp );
                        }

                    }
                    if (jsonObject.has("camp")) {
                        UserConfig.choosecamp = jsonObject.getInt("camp");
                    }
                    if (jsonObject.has("proportion")) {
                        UserConfig.proportion = jsonObject.getInt("proportion");
                    }


                    //update totalmoney on map fragment
                    if(mFragment instanceof MapFragment){
                        MapFragment.updateCoinNumber();
                    }

                    if(((UserConfig.totalmoney.intValue())/100 - (tempmoney.intValue())/100) == 1){

                        SFTPController ftpController = new SFTPController(myContext);
                        ftpController.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }

                    // normal delete coins
                    if (jsonObject.has("kind")) {
                        Log.d("sfawf","awef");
                        MapFragment.showanimation(jsonObject.getInt("kind"));
                    }
                    // auto delete coins
                    else if (jsonObject.has("coins_cnt")) {
                        MapFragment.showAutoAnimation(jsonObject.getInt("coins_cnt"));
                    }
                }
                else if(message.compareTo("lottery qualified get")==0){
                    MapFragment.showDialog();
                }
                else if(message.compareTo("The coin/diamond has been got")==0){
                    Toast toast = Toast.makeText(myContext, myContext.getString(R.string.totalmoneyerror1), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                }
                else if(message.compareTo("Has reached the money upper limit")==0){
                    Toast toast = Toast.makeText(myContext, myContext.getString(R.string.totalmoneyerror3), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                }
                else if(message.compareTo("the speed is too fast")==0){
                    Toast toast = Toast.makeText(myContext, myContext.getString(R.string.totalmoneyerror3), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                }
                else if(message.compareTo("get the gift")==0){
                    if (jsonObject.has("total_money")) {
                        UserConfig.totalmoney = jsonObject.getDouble("total_money");
                    }
                    if (jsonObject.has("actMoney")) {
                        if(jsonObject.has("kind")){
                            Log.d("asdffe","this is mother event");
//                            Log.d("asdffe",jsonObject.getString("actMoney"));
                        }
                        else{
                            Log.d("asdffe",jsonObject.getString("actMoney"));
                            UserConfig.ungiftedStamp = parseInt(jsonObject.getString("actMoney"));
                            MapFragment.mother_bar.setProgress(UserConfig.ungiftedStamp );
                        }

                    }



                    MapFragment.Animation();
                }

            }
            else if(jsonObject.has("appupdate")){
                String app_update = jsonObject.getString("appupdate");
                //0:latest version 1:could update 2:need update
                if(app_update.compareTo("1")==0){
                    if(mActivity instanceof MainActivity){
                        ((MainActivity) mActivity).couldupdate();
                    }
                }
                if(app_update.compareTo("2")==0){
                    if(mActivity instanceof MainActivity){
                        ((MainActivity) mActivity).needupdate();
                    }
                }

            }
            else if(jsonObject.has("personalupdate")){
                JSONObject updatedjsonObject = jsonObject.getJSONObject("personalupdate");
                UserConfig.setUserInfo(updatedjsonObject);
            }
            else if(jsonObject.has("specialEvent")){
                JSONObject specialjsonObject = jsonObject.getJSONObject("specialEvent");
                UserConfig.setspecialInfo(specialjsonObject);
                if(mActivity instanceof MainActivity){
                    ((MainActivity) mActivity).transferSpecial();
                }
            }
            else if(jsonObject.has("upload profile photo")){
                int ack = jsonObject.getInt("upload profile photo");
                if(ack == 1){
                    Toast toast = Toast.makeText(myContext, myContext.getString(R.string.uploadprofileimage1), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                }
                else{
                    Toast toast = Toast.makeText(myContext, myContext.getString(R.string.uploadprofileimage2), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                }

            }
            else if(jsonObject.has("chooseCamp_result")){
                String result = jsonObject.getString("chooseCamp_result");
                if(result.compareTo("sucessfully")==0){
                    Toast toast = Toast.makeText(myContext, "選定成功", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                }
                else{
                    Toast toast = Toast.makeText(myContext, "選定失敗", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                }

            }
        }

        catch (JSONException e){
            e.printStackTrace();
        }
    }

    // result: response message from server
    protected void onPostExecute(String result) {
        Log.d("0612API", urlString);
        Log.d("0612json", result);
//        if (progressDialog.isShowing()) {inaries

//            progressDialog.dismiss();
//        }


        try {
            //JSONObject json = new JSONObject(result);
            Log.d("wanttotest", "sdfwefwe");
            JSONArray json = new JSONArray(result);
            Log.d("0523jsons", json.toString());

            unpackJson(json);
            return;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // set progressbar INVISIBLE
        if(mActivity instanceof LoginActivity){
            Log.d("0ddfef612", "setSocialJson");
            LoginActivity.progressbar.setVisibility(View.INVISIBLE);
        }

        if (urlString.equals("/modifyNickname")) {
            if(result.compareTo("successfully")==0) {
                if (mFragment instanceof FriendFragment) {
                    ((FriendFragment) mFragment).modifyNickname();
                }
            }
        }

        if (urlString.equals("/addFriendByID")) {
            Log.i("aa","aa");
            if(result.compareTo("successfully")==0) {
                Toast toast = Toast.makeText(myContext, myContext.getString(R.string.add_friend_successfully), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
            }

        }


        if (urlString.equals("/confirmAddFriend")) {
            if(result.compareTo("successfullly")==0) {
//                ((MainActivity) mActivity).connectServer("POST", "/getSimInfo", UserConfig.myUserName);
                ((MainActivity) mActivity).socialInfo();
            }
        }

        Log.d("0508 from server: ",result );
        if(result.compareTo("create a user successfully")==0){
        }
        else if(result.compareTo("login successfully")==0){
            ShowDialogMsg.showDialog(myContext.getString(R.string.loginok));
            login();
        }
        else if(result.compareTo("error")==0){
            Toast toast = Toast.makeText(myContext, myContext.getString(R.string.loginerror), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        else if(result.compareTo("userID is error")==0){
            Toast toast = Toast.makeText(myContext, myContext.getString(R.string.useriderror), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        else if(result.compareTo("password is error")==0){
            Toast toast = Toast.makeText(myContext, myContext.getString(R.string.passwordiserror), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        else if(result.compareTo("fb login error")==0){
            Toast toast = Toast.makeText(myContext, myContext.getString(R.string.fbloginerror), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        else if(result.compareTo("fb no email")==0){
            Toast toast = Toast.makeText(myContext, myContext.getString(R.string.fbnoemail), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        else if(result.compareTo("send money successfully")==0){
            Toast toast = Toast.makeText(myContext, myContext.getString(R.string.sendmoneysuccess), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        else if(result.compareTo("money is not enough")==0){
            Toast toast = Toast.makeText(myContext, myContext.getString(R.string.moneynotenough), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        else if(result.compareTo("sender or receiver does not exist")==0 || result.compareTo("moneyTransaction error")==0){
            Toast toast = Toast.makeText(myContext, myContext.getString(R.string.sendmoneyerror), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        else if(result.compareTo("change password successfully")==0){
            Toast toast = Toast.makeText(myContext, myContext.getString(R.string.changepwdsuccess), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        else if (result.compareTo("register successfully")==0){
            //((LoginFragment) mFragment).waitForRegister(0, dialog);
//            Toast toast = Toast.makeText(myContext, myContext.getString(R.string.gotocheckemail), Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.BOTTOM, 0, 0);
//            toast.show();

            //login();
            LoginActivity.loadingDialog.dismissDialog();
            LoginActivity.success_registDialog.start_success_registDialog();
//            ShowDialogMsg.showDialog(myContext.getString(R.string.gotocheckemail));
        }
//        else if (result.compareTo("register unsuccessfully")==0){
//            //((LoginFragment) mFragment).waitForRegister(1, dialog);
//            Log.d("0523", "post unsuccess");
//            result = "The userid has been used";
//            ShowDialogMsg.showDialog(result);
//        }
        else if (result.compareTo("send ID and new password successfully")==0){
            Toast toast = Toast.makeText(myContext, myContext.getString(R.string.get_new_ID_and_password), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        else if (result.compareTo("username is error")==0){
            Toast toast = Toast.makeText(myContext, myContext.getString(R.string.username_not_exist), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        else if (result.compareTo("no record")==0){
            Toast toast = Toast.makeText(myContext, myContext.getString(R.string.norecord), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        else if (result.compareTo("consolidation successfully")==0){
            Toast toast = Toast.makeText(myContext, myContext.getString(R.string.consolidationsuccessful), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        else if (result.compareTo("delete record successfully")==0){
            Toast toast = Toast.makeText(myContext, myContext.getString(R.string.deleterecordsuccess), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        else if (result.compareTo("Email does not exist")==0){
            Toast toast = Toast.makeText(myContext, myContext.getString(R.string.email_not_exist), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        else if (result.compareTo("friend id does not exist")==0){
            Toast toast = Toast.makeText(myContext, myContext.getString(R.string.friend_name_not_exist), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        else if (result.compareTo("connection failed")==0){
            Log.i("chenorange", String.valueOf(result));
            Toast toast = Toast.makeText(myContext, result, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        else{ // for app version
            Log.d("0504 from server: ",result );
            Log.d(tagName,"APPVERSION: " + MainActivity.APPVERSION);
            if (result.compareTo(MainActivity.APPVERSION)==1){
                //http://www.mysamplecode.com/2013/05/android-update-application.html

                Log.d(tagName,"APPVERSION: " + MainActivity.APPVERSION);

                AlertDialog alertDialog = new AlertDialog.Builder(myContext)
                        .setTitle("Warning")
                        .setMessage("You have to update the latest app version!")
                        .setPositiveButton("Update", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=edu.nctu.wirelab.measuring"));
                                marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                mActivity.startActivity(marketIntent);
                            }
                        }).show();
                result = "";
            }
            if(sendMethod!=null && sendMethod.equals("POST")){
                resultKey = result;
                Log.d(tagName,"key: " + result);
                ftpKey = result;
                result = "";
            }
            else{
                //ShowDialogMsg.showDialog(result);
            }
        }


    }

    public void setFreeWiFi() {
        isFreeWiFi = true;
    }
}
