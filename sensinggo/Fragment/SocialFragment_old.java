package edu.nctu.wirelab.sensinggo.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.nctu.wirelab.sensinggo.Connect.HttpsConnection;
import edu.nctu.wirelab.sensinggo.Connect.Setting;
import edu.nctu.wirelab.sensinggo.File.JsonParser;
import edu.nctu.wirelab.sensinggo.R;
import edu.nctu.wirelab.sensinggo.ShowDialogMsg;
import edu.nctu.wirelab.sensinggo.UserConfig;

/**
 * Created by py on 4/9/18.
 */

public class SocialFragment_old extends Fragment{
    private ListView newlistView, oldlistView;

    //Context mContext;
    SimpleAdapter newadapter, oldadapter;
    List<Map<String, Object>> olditems, newitems;
    List<Object> imageList;
    private int makefriend_index = 0;
    private int deletefriend_index = 0;
    private Button blackButton, trasarecordButton, addFriendButton, friendRequestButton;
    private String[] blackperson;

    private JsonParser jsonParser = null;
    public void setJsonParser(JsonParser json) {
        jsonParser = json;
    }
    //DialogInterface alertDialog;

    @Override
    public void onResume() {
        super.onResume();
        // update transaction record
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Log.d("0715", "arraylen");
//        if(HttpsConnection.isdefaultuser){
//            View view = inflater.inflate(R.layout.textview_nodata, container, false);
//            TextView tv = (TextView) view.findViewById(R.id.info_text) ;
//            tv.setText(getString(R.string.defaultusernodata));
//            return view;
//        }
        if(UserConfig.myUserName.compareTo("DefaultUser") == 0){
            View view = inflater.inflate(R.layout.textview_nodata, container, false);
            TextView tv = (TextView) view.findViewById(R.id.info_text) ;
            tv.setText(getString(R.string.defaultusernodata));
            return view;
        }

        if(UserConfig.similarityArray == null){

            View view = inflater.inflate(R.layout.textview_nodata, container, false);
            TextView tv = (TextView) view.findViewById(R.id.info_text) ;
            tv.setText(getString(R.string.serverfail));
            return view;
        }

//        Log.d("0715len", Integer.toString(UserConfig.similarityArray.length));
//        if (UserConfig.similarityArray.length==0) {
//            View view = inflater.inflate(R.layout.textview_nodata, container, false);
//            TextView tv = (TextView) view.findViewById(R.id.info_text) ;
//            tv.setText(getString(R.string.newusernodata));
//            return view;
//        }

        else{
            View view = inflater.inflate(R.layout.listview_social, container, false);
            blackButton = (Button) view.findViewById(R.id.blackbutton);
            //trasarecordButton = (Button) view.findViewById(R.id.transaction_record_button); // hide this button
            addFriendButton = (Button) view.findViewById(R.id.add_friend_button);
            friendRequestButton = (Button) view.findViewById(R.id.friend_request_button);

            newlistView = (ListView) view.findViewById(R.id.newfriendlist);
            oldlistView = (ListView) view.findViewById(R.id.oldfriendlist);

            newitems = new ArrayList<Map<String, Object>>();
            olditems = new ArrayList<Map<String, Object>>();
            imageList = new ArrayList<Object>();
            imageList.add(R.drawable.icon_manb);

//            imageList.add(R.drawable.icon_mano);
//            imageList.add(R.drawable.icon_many);
            imageList.add(R.drawable.icon_manr);
            imageList.add(R.drawable.icon_black);

            blackperson = new String[UserConfig.numberblacks];

            // classify oldfriend, newfriend, blackperson
            for (int i=0, j = 0; i< UserConfig.similarityArray.length; i++){
                Map<String, Object> item = new HashMap<String, Object>();

                Log.d("0613", UserConfig.similarityArray[i][0]);
                if(UserConfig.similarityArray[i][2].equals("new")){
                    item.put("userIcon", imageList.get(1));
                    item.put("userid", UserConfig.similarityArray[i][0]);
                    newitems.add(item);
                }
                else if(UserConfig.similarityArray[i][2].equals("old")){
                    item.put("userIcon", imageList.get(0));
                    item.put("userid", UserConfig.similarityArray[i][1]);
                    olditems.add(item);
                }
                else{
                    blackperson[j] = UserConfig.similarityArray[i][0];
                    j++;
                }

            }




            newadapter = new SimpleAdapter(
                    getActivity(),
                    newitems,
                    R.layout.fragment_social,
                    new String[]{"userIcon", "userid"},
                    new int[]{R.id.similarIcon, R.id.UserId}
                    //new String[]{"userIcon", "userName", "userEmail"},
                    //new int[]{R.id.similarIcon, R.id.UserId, R.id.UserEmail}
            );
//            oldadapter = new SimpleAdapter(
//                    getActivity(),
//                    olditems,
//                    R.layout.fragment_social,
//                    new String[]{"userIcon", "userid"},
//                    new int[]{R.id.similarIcon, R.id.UserId}
//                    //new String[]{"userIcon", "userName", "userEmail"},
//                    //new int[]{R.id.similarIcon, R.id.UserId, R.id.UserEmail}
//            );

            oldadapter = new SimpleAdapter(
                    getActivity(),
                    olditems,
                    R.layout.fragment_social,
                    new String[]{"userIcon", "userid"},
                    new int[]{R.id.similarIcon, R.id.UserId}) {

                @Override
                public View getView(final int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    ImageView imageView = (ImageView) view.findViewById(R.id.similarIcon);

                    ColorDrawable cd = new ColorDrawable(0x000000);

                    String internetUrl = Setting.HTTPSSERVER + UserConfig.similarityArray[position+UserConfig.newfriend_len][0] + ".jpg";
                    Glide.with(getActivity())
                            .load(internetUrl)
                            .placeholder(cd)
                            .error(R.drawable.icon_manb) //load失敗的Drawable
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(imageView);
                    return view;
                }
            };

            /* old inefficient way for photo */
//            //use to set bitmap
//            oldadapter.setViewBinder(new SimpleAdapter.ViewBinder(){
//
//                @Override
//                public boolean setViewValue(View view, Object data,
//                                            String textRepresentation) {
//                    if( (view instanceof ImageView) & (data instanceof Bitmap) ) {
//                        ImageView iv = (ImageView) view;
//                        Bitmap bm = (Bitmap) data;
//                        iv.setImageBitmap(bm);
//                        return true;
//                    }
//                    return false;
//
//                }
//
//            });


            newlistView.setAdapter(newadapter);
            oldlistView.setAdapter(oldadapter);
            // set listview height
            ListUtils.setDynamicHeight(newlistView);
            ListUtils.setDynamicHeight(oldlistView);

            final AlertDialog.Builder AddFriendDialog = new AlertDialog.Builder(getActivity());
            // once pressing goldfriend, show personal information
            oldlistView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public  void onItemClick(AdapterView<?> parent, View view, int position, long id){
                    String friendname = UserConfig.similarityArray[position + UserConfig.newfriend_len][0];
                    String info = "username=" + UserConfig.myUserName +
                            "&friendname=" + friendname;
                    Log.d("0614pos", Integer.toString(position) + '\t' + friendname);
                    connectServer("POST", "/getFriendInfo", info);

                }
            });

            newlistView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public  void onItemClick(AdapterView<?> parent, View view, int position, long id){
                    makefriend_index = position;

                    String makeFriendMessage = String.format(getString(R.string.makefriend), UserConfig.similarityArray[position][0]);

                    AddFriendDialog.setMessage(makeFriendMessage)
                            .setIcon(R.mipmap.ic_launcher)
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Post_MakefriendsMessage(UserConfig.similarityArray[makefriend_index][0]);
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


            final AlertDialog.Builder DeleteFriendDialog = new AlertDialog.Builder(getActivity());
            oldlistView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long id) {
                    Log.d("enterdelere1","enter");
                    deletefriend_index = position;
                    //if(UserConfig.similarityArray[position][2].equals("old")){

                    // user cannot unfriend "sensinggo"
                    if (UserConfig.similarityArray[position + UserConfig.newfriend_len][0].equals("sensinggo")) {
                        return true;
                    }

                    DeleteFriendDialog.setMessage(getString(R.string.unfriend)+ UserConfig.similarityArray[position + UserConfig.newfriend_len][0]+" ?")
                            .setIcon(R.mipmap.ic_launcher)
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("enterdelere2","enter");
                                            Post_DeletefriendsMessage(UserConfig.similarityArray[deletefriend_index+ UserConfig.newfriend_len][0]);

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
                    //}
                    return true;
                }
            });

            // to show blacklist
            blackButton.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View v){
                    final AlertDialog.Builder blackDialog = new AlertDialog.Builder(getActivity());
                    blackDialog.setTitle(getString(R.string.blacklist));
                    blackDialog.setItems(blackperson, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, final int which) {

                            final AlertDialog.Builder removeblackDialog = new AlertDialog.Builder(getActivity());
                            removeblackDialog.setMessage(getString(R.string.removeblack))
                                    .setIcon(R.mipmap.ic_launcher)
                                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Post_Deleteblack(blackperson[which]);

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

                    blackDialog.show();

                }
            });

            // to show transaction record // hide this button
//            trasarecordButton.setOnClickListener(new Button.OnClickListener(){
//
//
//                @Override
//                public void onClick(View v){
//                    String Info = "username=" + UserConfig.myUserName;
//                    //dialogNoDismiss(dialog);
//                    connectServer("POST", "/moneyTransactionRecords", Info);
////                    final View recordView =  LayoutInflater.from(getActivity()).inflate(R.layout.fragment_get_transactionrecord,null);
////                    final EditText passwordEditText = (EditText) recordView.findViewById(R.id.password);
////
////                    AlertDialog.Builder alertDialog= new AlertDialog.Builder(getActivity());
////                    alertDialog.setTitle(getString(R.string.transactionrecord))
////                            .setView(recordView)
////                            .setNegativeButton(getString(R.string.cancel),new DialogInterface.OnClickListener(){
////                                @Override
////                                public void onClick(DialogInterface dialog, int which){
////                                }
////                            })
////                            .setPositiveButton(getString(R.string.okresend), new DialogInterface.OnClickListener(){
////                                @Override
////                                public void onClick(DialogInterface dialog, int which){
////                                    String pwd = passwordEditText.getText().toString();
////                                    if(pwd.equals("")){
////                                        ShowDialogMsg.showDialog(getString(R.string.noblank1));
////                                    }
////                                    else {
////
////                                        String sendmoneyInfo = "username=" + UserConfig.myUserName +
////                                                "&password=" + pwd;
////                                        //dialogNoDismiss(dialog);
////                                        connectServer("POST", "/", sendmoneyInfo);
////                                    }
////                                }
////                            });
////                    alertDialog.show();
//
//                }
//            });

            addFriendButton.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View v) {

                    final View addFriendView =  inflater.inflate(R.layout.dialog_add_friend, null);
                    final EditText usernameEditText = addFriendView.findViewById(R.id.edittext_add_friend_username);

                    new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.add_friend))
                            .setView(addFriendView)
                            .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which){

                                    String friendUsername = usernameEditText.getText().toString();
                                    if(friendUsername.equals("")){
                                        ShowDialogMsg.showDialog(getString(R.string.noblank_nostar));
                                    }
                                    else{
                                        String info = "username=" + UserConfig.myUserName +
                                                "&friendname=" + friendUsername +
                                                "&fromSimilar=" + 0;
                                        connectServer("POST", "/addFriendByID", info);
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

            friendRequestButton.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String info = "username=" + UserConfig.myUserName;
                    connectServer("POST", "/getToBeConfirmdFriend", info);
                    //((MainActivity)getActivity()).transferFriendRequest();
                }
            });

            /* old inefficient way for photo */
//            // download friends profile photo
//            String info = "username=" + UserConfig.myUserName;
//            connectServer("POST", "/downloadFriendsProfilePhoto", info);


            return  view;
        }

    }

    public void reStart(){

        Log.d("ASDF", "asdf " + UserConfig.similarityArray.length);
        SocialFragment_old fragment = (SocialFragment_old)
                getFragmentManager().findFragmentById(this.getId());

        getFragmentManager().beginTransaction()
                .detach(fragment)
                .attach(fragment)
                .commit();
    }

    // dynamically to fit length of listview
    public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }
            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    public void connectServer(String method, String path, String info){
        Log.d("0612conn", info);
        HttpsConnection httpsConnection = new HttpsConnection(getActivity());
        httpsConnection.setJsonParser(jsonParser);
        httpsConnection.setActivity(getActivity());
        httpsConnection.setFragment(this);
        httpsConnection.setMethod(method, info);
        httpsConnection.execute(path);


    }
/*
    private void Post_MakefriendsMessage(String newfriend){
        String urlstring = "http://sensinggo.org/addFriend/";

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
            JsonObj.put("friendname", newfriend);
            JsonObj.put("username", UserConfig.myUserName);
            JSONArray Json_send = new JSONArray();
            Json_send.put(JsonObj);


            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            dos.writeBytes(Json_send.toString());
            dos.flush();
            dos.close();
            System.out.println(Json_send.toString());
            int responseCode = connection.getResponseCode();
            Log.d("response", "Post_Message: " + responseCode);
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            String result = "";
            while((line = br.readLine()) != null)
                result += line;

            parseuserfriendlist(result);
            br.close();

            //System.out.println("WEB return value is : " + sb);
            Log.d("addfriendresult", "Post_Message: " + result);
            // Toast.makeText(getApplicationContext(),"Sending 'POST' request to URL : " + url + "\nPost parameters : " + test + "\nResponse Code : " + responseCode + "\nWEB return value is : " + sb, Toast.LENGTH_LONG).show();
        }
        catch(IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    private void Post_MakefriendsMessage(String newfriend){
        //String urlstring = "http://sensinggo.org/addFriend/";
        String Info = "friendname=" + newfriend +
                "&username=" + UserConfig.myUserName +
                "&fromSimilar=" + 1; // make friend from similar
        connectServer("POST", "/addFriendByID", Info);
    }

    private void Post_DeletefriendsMessage(String unfriend){
        //String urlstring = "http://140.113.216.39/unfriend/";
        String Info = "friendname=" + unfriend +
                "&username=" + UserConfig.myUserName;
        connectServer("POST", "/unfriend", Info);

    }

    private void Post_Deleteblack(String blackman){
        //String urlstring = "http://140.113.216.39/removeFromBlacklist";
        String Info = "username=" + UserConfig.myUserName +
                "&blackname=" + blackman;
        connectServer("POST", "/removeFromBlacklist", Info);
    }

    /* old inefficient way for photo */
//    public void downloadPhotoCallback(JSONArray result){
//        try {
//            JSONObject jsonObject = result.getJSONObject(0);
//            JSONArray friends = jsonObject.getJSONArray("friends photo");
//
//            for (int i = 0; i < friends.length(); i++) {
//                JSONObject friend = friends.getJSONObject(i);
//                String name = friend.getString("name");
//                String photo = friend.getString("profile photo");
//                Log.d("9912test", "name: " + name + ", photo: " + photo);
//
//                if (photo.isEmpty()) {
//                    continue;
//                }
//
//                byte[] decodedString = Base64.decode(photo, Base64.URL_SAFE|Base64.NO_WRAP);
//                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//
//                int bitmapByteCount= BitmapCompat.getAllocationByteCount(bitmap);
//                double b = bitmapByteCount;
//                double k = b/1024.0;
//                double m = ((b/1024.0)/1024.0);
//                Log.d("9912test", "size of bitmap: " + bitmapByteCount + "Byte; = " + m + "MB");
//
//                int newLength = 0;
//                if (UserConfig.similarityArray[0][2].equals("new")) {
//                    newLength = 1;
//                }
//
//                for (int j = newLength; j < UserConfig.similarityArray.length; j++) {
//                    if (UserConfig.similarityArray[j][2].equals("old")) {
//                        if (UserConfig.similarityArray[j][0].equals(name)) {
////                            if (olditems.get(j-newLength).get("userid").equals(name)){
//                            olditems.get(j-newLength).put("userIcon", bitmap);
//                            Log.d("9912test", "photo!! , name = " + name);
////                            }
////                            else {
////                                Log.d("9912test", "array & olditems are not match??");
////                            }
//
//                        }
//                    }
//                }
//
//            }
//
//            oldadapter.notifyDataSetChanged();
//
//        }
//        catch (JSONException e){
//            e.printStackTrace();
//        }
//    }






}
