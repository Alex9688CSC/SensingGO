package edu.nctu.wirelab.sensinggo.Fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import edu.nctu.wirelab.sensinggo.Connect.HttpsConnection;
import edu.nctu.wirelab.sensinggo.R;
import edu.nctu.wirelab.sensinggo.ShowDialogMsg;
import edu.nctu.wirelab.sensinggo.SocialUserConfig;
import edu.nctu.wirelab.sensinggo.UserConfig;

/**
 * Created by py on 6/17/18.
 */

public class SimUserFragment extends Fragment{
    private ListView listView;
    SimpleAdapter adapter;
    ArrayList<HashMap<String,String>> items;
    private Button sendmailButton, addblackButton, editNicknameButton; //, sendmoneyButton;
    private String gmail = "";

    final String ID_TITLE = "TITLE", ID_SUBTITLE = "SUBTITLE";
    private String [] infoItems;// = new String [] {"UserID", "Gender", "Birthday", "E-mail", "Introduction"};
    DialogInterface alertDialog;

    String successfulNickname = ""; // use for updating the nickname when get "successfully"


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.listview2, container, false);
        sendmailButton = (Button) view.findViewById(R.id.gmailbutton);
        addblackButton = (Button) view.findViewById(R.id.addblackbutton);
        //sendmoneyButton = (Button) view.findViewById(R.id.sendmoneybutton);
        editNicknameButton = (Button) view.findViewById(R.id.editNicknameButton);
        listView = (ListView) view.findViewById(R.id.listinfo);

        if(TranRecordFragment.accessrecord){
            sendmailButton.setVisibility(View.INVISIBLE);
            addblackButton.setVisibility(View.INVISIBLE);
            editNicknameButton.setVisibility(View.INVISIBLE);
            //sendmoneyButton.setVisibility(View.INVISIBLE);
        }



        infoItems = new String[] {getString(R.string.username_friend),
                getString(R.string.nickname_friend),
                getString(R.string.gender_friend),
                getString(R.string.birthday_friend),
                getString(R.string.email_friend),
                getString(R.string.personal_profile_friend)};

        items = new ArrayList<HashMap<String, String>>();
        for(int i=0; i<SocialUserConfig.infoValue.length; i++) {
            HashMap<String, String> item = new HashMap<String, String>();
            if(infoItems[i].equals("E-mail")){
                gmail = SocialUserConfig.infoValue[i];
            }
            item.put(ID_TITLE, infoItems[i]);
            item.put(ID_SUBTITLE, SocialUserConfig.infoValue[i]);
            items.add(item);
        }

       adapter = new SimpleAdapter(
                getActivity(),
                items,
                android.R.layout.simple_list_item_2,
                new String[]{ID_TITLE, ID_SUBTITLE},
                new int[]{android.R.id.text1, android.R.id.text2}
       );


        listView.setAdapter(adapter);

        sendmailButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){

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

        addblackButton.setOnClickListener(new Button.OnClickListener(){
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

        editNicknameButton.setOnClickListener(new Button.OnClickListener(){
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
        int position = 0;
        for (int i = 0; i < SocialUserConfig.infoItems.length; i++) {
            if (SocialUserConfig.infoItems[i].equals("nickname")) {
                position = i;
                break;
            }
        }

        items.get(position).put(ID_SUBTITLE, successfulNickname);
        adapter.notifyDataSetChanged();
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

/*
    private void Post_Addblack(String blackman){
        String urlstring = "http://140.113.216.39/addBlacklist";

        HttpURLConnection connection = null;
        try{
            //initial connection
            URL url = new URL(urlstring);
            //get connection object
            connection = (HttpURLConnection) url.openConnection();
            //set request
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type","application/json");
            connection.setRequestProperty("charset","utf-8");
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            connection.setDoOutput(true);
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(100000);


            JSONObject JsonObj = new JSONObject();
            Log.d("XDDDD", "value");
            JsonObj.put("username", UserConfig.myUserName);
            JsonObj.put("blackname", blackman);
            JSONArray Json_send = new JSONArray();
            Json_send.put(JsonObj);


            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            dos.writeBytes(Json_send.toString());
            dos.flush();
            dos.close();
            System.out.println(Json_send.toString());
            int responseCode = connection.getResponseCode();
            Log.d("responseaddblack", "Post_Message: " + responseCode);
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            String result = "";
            while((line = br.readLine()) != null)
                result += line;

            br.close();
            //parseuserfriendlist(result);

            //System.out.println("WEB return value is : " + sb);
            Log.d("addfriendresult", "Post_Message: " + result);
            // Toast.makeText(getApplicationContext(),"Sending 'POST' request to URL : " + url + "\nPost parameters : " + test + "\nResponse Code : " + responseCode + "\nWEB return value is : " + sb, Toast.LENGTH_LONG).show();
        }
        catch(IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    */


}
