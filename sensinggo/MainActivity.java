package edu.nctu.wirelab.sensinggo;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import android.net.Uri;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import edu.nctu.wirelab.sensinggo.Fragment.FriendFragment;
import edu.nctu.wirelab.sensinggo.Fragment.FriendRequestFragment;

import edu.nctu.wirelab.sensinggo.Fragment.SocialFragment;
import edu.nctu.wirelab.sensinggo.Fragment.SpecialEventFragment;
import io.fabric.sdk.android.Fabric;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.nctu.wirelab.sensinggo.BroadcastReceiver.ScreenStateReceiver;
import edu.nctu.wirelab.sensinggo.Connect.HttpsConnection;
import edu.nctu.wirelab.sensinggo.File.FileMaker;
import edu.nctu.wirelab.sensinggo.File.JsonParser;
import edu.nctu.wirelab.sensinggo.File.OutCypher;
import edu.nctu.wirelab.sensinggo.Fragment.DetailTranRecordFragment;
import edu.nctu.wirelab.sensinggo.Fragment.FreeWifiFragment;
import edu.nctu.wirelab.sensinggo.Fragment.InfoFragment;
import edu.nctu.wirelab.sensinggo.Fragment.LoginFragment;
import edu.nctu.wirelab.sensinggo.Fragment.LoginUserFragment;
import edu.nctu.wirelab.sensinggo.Fragment.MapFragment;
import edu.nctu.wirelab.sensinggo.Fragment.SimUserFragment;
import edu.nctu.wirelab.sensinggo.Fragment.TranRecordFragment;
import edu.nctu.wirelab.sensinggo.Fragment.UserFragment;
import edu.nctu.wirelab.sensinggo.Measurement.Location;
import edu.nctu.wirelab.sensinggo.Record.TrafficSnapshot;

import static androidx.recyclerview.widget.LinearLayoutManager.VERTICAL;
import static edu.nctu.wirelab.sensinggo.MainActivity.facilityURL;
import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {
    public static Context mContext;

    private static final int REQUEST_PERMISSION = 1000;
    private final String TagName = "MainActivity";
    public static String VERSION = "v1.0.52_2020/09/23";
    public static final String ipipip = "132156216.1635";
    public static final String passpass = "asdfefwlabsdfvg";
    public static String APPVERSION = "52";
    public static String SHANSERVER = "1";
    //Button quitButton;
    private CheckBox autoCheckBox;
    //MainContentFragment mMain = null;
    UserFragment mUser = null;
    InfoFragment mInfo = null;
    public SocialFragment mSocial = null;
    SimUserFragment mSim = null;
    FriendFragment mFriend = null; // new simuser
    TranRecordFragment mTranRecord = null;
    DetailTranRecordFragment mDetailTranRecord = null;
    FreeWifiFragment mFreeWifi = null;
    MapFragment mMap = null;
    Fragment previousfragment = null;
    SpecialEventFragment mSpecial = null;
    public FriendRequestFragment mFriendRequest = null;
    private Fragment selectedFragment;
    public static LoginFragment mLogin = null;
    public static LoginUserFragment mLoginUser = null;


    private edu.nctu.wirelab.sensinggo.File.JsonParser JsonParser = new JsonParser();

    public static String logPath, configPath;
    public static String logPrefix, recordPrefix;

    public static String loginname = "", loginbirth = "", loginmsg = "", loginemail = "";

    //public static Double totalmoney = 0.0;
    public static Double faketotalmoney = 0.0;

    //storage of coin's coordinate
    public static ArrayList<CoinContent> coinlist = new ArrayList<CoinContent>();
    //storage of friends photo message
    public static ArrayList<PhtoContent> photolist = new ArrayList<PhtoContent>();
    public static ArrayList<MonsterContent> monsterlist = new ArrayList<MonsterContent>();
//    public static ArrayList<Double> coinlatlist = new ArrayList<Double>();
//    public static ArrayList<Double> coinlnglist = new ArrayList<Double>();


    public static long startServiceTime;

    public static boolean tempAutoUploadByMobile;

    public static int originalScreenOffTime = 15000; //ms

    //a delay control all the sense info delay, such as all cell info, location update, traffic throughput
    public static int flashInterval = 1000; //in ms

    public static Location lu;

    //the traffic monitor that monitors all apps data usage
    public static TrafficSnapshot latest, previous;


    //the cypher for signing the data that is used to upload to server
    private OutCypher mOutCypher;

    //public int removeLogFlag = 0;

    private ScreenStateReceiver mReceiver; // for screen monitor

    //private NotificationManager notificationManager = null; //notify 2017/1/17
    //final int notifyID = 1;

    public static BottomNavigationView navigation;

    private boolean dialogExisted = false;

    // for turnon or turnoff sound effect
    private ImageView soundimage;
    private ImageView soundoffimage;
    public static boolean soundeffect = false;

    public static boolean isInUserFragment = true;
    public static boolean isLoginSuccess = false;

    // toolbar
    private Toolbar toolbar;

    private int theme_color;

    // Navigation listener
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            isInUserFragment = false;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    toolbar.setNavigationIcon(null);
                    isInUserFragment = true;
                    if (mUser == null) {
                        mUser = new UserFragment();
                        mUser.SetContext(MainActivity.this);
                    }
                    //hideToFragment(selectedFragment, mUser);
                    //selectedFragment = mUser;
                    replaceToFragment(mUser);

                    return true;


                case R.id.navigation_info:
                    Log.d("getgeteget","out");
//                    Drawable drawable= getResources().getDrawable(R.drawable.wish_list);
//                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
//                    Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 60, 60, true));
                    toolbar.setNavigationIcon(R.drawable.wish_list_resized);
                    toolbar.setNavigationOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {
                            Log.i("jjj","hahaha");
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                            View v = inflater.inflate(R.layout.ticket, null);
                            TextView dialog_title = v.findViewById(R.id.dialog_title);

                            List<ticket_card_view> ticketList = new ArrayList<>();
                            for(int i=0; i<coins.size(); i++){
                                ticketList.add(new ticket_card_view(R.drawable.icon_dollar, "單位 : "+ ch_type.get(i), coins.get(i),logoURL.get(i),240));
                            }

//            ticketList.add(new ticket_card_view(R.drawable.icon_dollar, "單位 : "+"弘道", 0,"https://sensinggo.org/charity_icons/animal.png",240));
//            ticketList.add(new ticket_card_view( R.drawable.icon_dollar, "單位 : "+"導盲犬", 0,"https://sensinggo.org/charity_icons/elder.png",240));
//            ticketList.add(new ticket_card_view( R.drawable.icon_dollar, "單位 : "+"勵馨", 0,"https://sensinggo.org/charity_icons/people.png",225));
//            ticketList.add(new ticket_card_view( R.drawable.icon_dollar, "單位 : "+"陽光", 0,"https://sensinggo.org/charity_icons/woman_child.png",220));
//            ticketList.add(new ticket_card_view(2, R.drawable.new_sg, "白沙屯海灘2"));

                            RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.ticket_recycler_view);
                            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this,VERTICAL, false));
// MemberAdapter 會在步驟7建立
                            recyclerView.setAdapter(new ticketAdapter(MainActivity.this, ticketList));

//            TextView content = (TextView) v.findViewById("haha");
//        Button btn_sure = (Button) v.findViewById(R.id.dialog_btn_sure);
                            Button btn_cancel = (Button) v.findViewById(R.id.dialog_btn_cancel);

                            //builer.setView(v);//这里如果使用builer.setView(v)，自定义布局只会覆盖title和button之间的那部分
                            final Dialog dialog = builder.create();
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            dialog.show();
                            dialog.getWindow().setContentView(v);//自定义布局应该在这里添加，要在dialog.show()的后面
                            //dialog.getWindow().setGravity(Gravity.CENTER);//可以设置显示的位置
//        btn_sure.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//
//        });

                            btn_cancel.setOnClickListener(new Button.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    Log.i("ttt","no");
//                Toast.makeText(getActivity(), "no").show();
                                }

                            });

                        }
                    });
                    if (RunIntentService.runFlag) {
                        Log.d("getgeteget","in");
                        //removeLogFlag = 0;
                        if (mInfo == null) {
                            Log.d("0523", "info1");
                            mInfo = new InfoFragment();

                        }
                        //hideToFragment(selectedFragment, mInfo);
                        //selectedFragment = mInfo;
                        replaceToFragment(mInfo);
                    }
                    else{
                        ShowDialogMsg.showDialog(getString(R.string.simcarderror));
                    }

                    return true;
                case R.id.navigation_login:
                    toolbar.setNavigationIcon(null);
                    if(UserConfig.myUserName.compareTo("DefaultUser")==0){
                        if(mLogin==null){
                            mLogin = new LoginFragment();
                            mLogin.setJsonParser(JsonParser);
                        }
                        //hideToFragment(selectedFragment, mLogin);
                        //selectedFragment = mLogin;
                        replaceToFragment(mLogin);
                    }

                    else{ // the user has loginned

                        // for general user
                        if (mLoginUser == null) {
                            Log.d("0523", "loginUser1");
                            mLoginUser = new LoginUserFragment();
                            mLoginUser.setJsonParser(JsonParser);
                            loginSuccess();
                        }


                        if (!mLoginUser.isAdded()) {
                            loginSuccess();
                        }



                    }
                    return true;

                case R.id.navigation_social:
                    toolbar.setNavigationIcon(null);
                    if (mSocial == null) {
                        mSocial = new SocialFragment();
                        socialInfo();
                    }

                    else if (!mSocial.isAdded()) {
                        socialInfo();
                    }

                        /*if (selectedFragment != mSocial) {
                            socialInfo();
                        }*/

                    return true;

                case R.id.navigation_map:
                    toolbar.setNavigationIcon(null);
//                    new Thread(new Runnable(){
//                        @Override
//                        public void run(){
//                            Post_Message();
//                        }
//                    }).start();
                    if (mMap == null) {
                        mMap = new MapFragment();
                    }
                    replaceToFragment(mMap);
                    return true;

            }
            return false;
        }

    };

    // setting spinner listener
//    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
//        @Override
//        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//            switch (position){
//                case 0:
//                    Toast.makeText(MainActivity.this, parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
//                    break;
//                case 1:
//                    Toast.makeText(MainActivity.this, parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
//                    break;
//                case 2:
//                    Toast.makeText(MainActivity.this, parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
//                    break;
//                case 3:
//                    Toast.makeText(MainActivity.this, parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
//                    break;
//                case 4:
//                    Toast.makeText(MainActivity.this, parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
//                    break;
//
//            }
//        }
//        @Override
//        public void onNothingSelected(AdapterView<?> adapterView) {
//
//        }
//    };


    @Override
    protected void onResume() {
        super.onResume();
        checkappversion();

        ((MyApplication) getApplication()).appRunningStatus = "APP STOP";
        if (RunIntentService.errorFlag == true) {
            Toast.makeText(MainActivity.this, getString(R.string.simcarderror), Toast.LENGTH_LONG)
                    .show();
        }
        else {
            if (checkGpsStatus() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.System.canWrite(this)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // redundant? peiyu
                            if (!dialogExisted) {
                                initPermission();
                            }
                        }
                    } else {
                        showPermissionSettingMsg();
                    }
                } else {//version under 6.0
                    ((MyApplication) getApplication()).appRunningStatus = getString(R.string.app_is_running);
                    startServices();
                }
            } else {
                showGPSClosedMsg();
                //  startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }

            //If no connection , prompt user to connect
            if(!isNetWorkConnected()){
                showNoNetworkDialog();
            }

        }

    }




    public void onDestroy() { // need check 01/25/18 peiyu
        //if running stop Services
        //if (RunIntentService.RunFlag)
        //StopServices();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }

    public boolean checkGpsStatus() {
        lu = new Location(MainActivity.this);
        if (!lu.isOpenGps()) {
//            ShowDialogMsg.showDialogLong("Please turn your gps on");
            lu = null;
            return false;
        }
        if (RunIntentService.runFlag) {
            lu.getGPS(this);
        }
        lu = null;
        return true;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Fragment f = getFragmentManager().findFragmentById(R.id.FragmentContent);
        if (f instanceof FreeWifiFragment && mInfo != null) {
            replaceToFragment(mInfo);
            return;
        }

        else if (f instanceof MapFragment || f instanceof SocialFragment || f instanceof InfoFragment
                || f instanceof LoginFragment || f instanceof LoginUserFragment || f instanceof UserFragment) {

            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.leaveapp))
                    .setPositiveButton(getString(R.string.ok), null)
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show();

            Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("LOGOUT", true);
                    startActivity(intent);
                    finish();

                    return;
                }
            });


        }

        if(DetailTranRecordFragment.accessperson){
            transferDetailTranRecord();
            DetailTranRecordFragment.accessperson = false;
            return;
        }

        if(TranRecordFragment.accessrecord){
            transferTranRecord();
            TranRecordFragment.accessrecord = false;
            return;
        }

        if(mSim != null || mTranRecord != null) {
            socialInfo();
            mSim = null;
            mTranRecord = null;
        }

        if (mFriend != null) {
            socialInfo();
            mFriend = null;
        }

        if(mSpecial != null) {
            mSpecial = null;
            transferUser();
        }

        if(mFriendRequest != null) {
            socialInfo();
            mFriendRequest = null;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        /*setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);*/

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setItemIconTintList(null);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = token;
                        Log.i("haha", msg);
//                        c3wqiqvcHUpCbbu415dlXt:APA91bFwJom4twDm8nkmoHpE76kNuNrVYD7ZRndqNB1yX_C6khS7rq7H-gx-XP4E9HwnF0NabvypNLA3jRxz8cg41pbLIQY0VGMRTwokz-pkl5FGYWiDhHC4yI9iCn2KIS_U7lPPFPLv
                    }
                });
        SharedPreferences spref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor sprefEditor = spref.edit();


        sprefEditor.commit();
        theme_color = spref.getInt("theme_color", -1);
        Log.i("theme_color: ",String.valueOf(theme_color));
        if (theme_color == -1){
            theme_color =  R.color.new_background;
        }

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setItemIconTintList(null);

        navigation.setItemBackground(
                new ColorDrawable(
                        ContextCompat.getColor(this,theme_color))
        );


        toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(this,theme_color));
//        Drawable drawable= getResources().getDrawable(R.drawable.ticket_red);
//        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
//        Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 40, 40, true));

//        toolbar.setNavigationIcon(newdrawable);




        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if(getIntent().getBooleanExtra("LOGOUT",false)){
            Intent intent = new Intent(MainActivity.this, RunIntentService.class);
            stopService(intent);
            RunIntentService.runFlag = false;
            finish();
            return;
        }

        //  Show the app intro at first time
        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    final Intent i = new Intent(MainActivity.this, IntroActivity.class);
                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            startActivity(i);
                        }
                    });

//                    //  Launch app login
//                    final Intent login_intent = new Intent(MainActivity.this, LoginActivity.class);
//                    runOnUiThread(new Runnable() {
//                        @Override public void run() {
//                            startActivity(login_intent);
//                        }
//                    });



                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
                }
            }
        });

        t.start();


        // Add screen monitor
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenStateReceiver();
        registerReceiver(mReceiver, intentFilter);
        //--------------------------------------------

        initVar();
        //mainButton.setBackgroundResource(R.drawable.icon_svg_home);

//        new Thread(new Runnable(){
//            @Override
//            public void run(){
//                getTotalMoneyFromServer();
//            }
//        }).start();


//        if (mInfo == null) {
//            mInfo = new InfoFragment();
//            mInfo.setJsonParser(JsonParser);
//        }
//        replaceToFragment(mInfo);
        showStartingFragment();
        checkappversion();
        getUserID();
        getCoinsTopUser();
    }

    public void connectServer(String method, String path, String info){
        HttpsConnection httpsConnection = new HttpsConnection(MainActivity.this);
        httpsConnection.setJsonParser(JsonParser);
        httpsConnection.setActivity(MainActivity.this);
        httpsConnection.setMethod(method, info);
        httpsConnection.execute(path);
        // Use "executeOnExecutor" to let multiple AsyncTasks execute in parallel
//        httpsConnection.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path);
        Log.d("0612conn", info);
    }

    private void checkappversion(){
        String versionInfo = "appversion=" + APPVERSION;
        connectServer("POST", "/checkVersion", versionInfo);
    }

    public void socialInfo(){

        String userInfo = "username=" + UserConfig.myUserName;
        if(UserConfig.myUserName.compareTo("DefaultUser")!=0) {
            //transferSocial();
            connectServer("POST", "/getSimInfo", userInfo);

//            connectServer("POST", "/getToBeConfirmdFriend", userInfo); // transfer to social after response

        }
        else{
            transferSocial();
        }
//        transferSocial();
//        connectServer("POST", "/getSimInfo", userInfo);
    }



    public void loginSuccess() { //After server responds login successfully msg, call getUserInfo api
        Log.d("0523", "loginSuccess()");
        String loginInfo = "username=" + UserConfig.myUserName;
        connectServer("POST", "/getUserInfo",loginInfo);
        Log.d("0523info",UserConfig.myUserName);
//        LoginActivity.activity.finish();
        isLoginSuccess = true;
    }

    /*public void updateConfigMoney(){
        String moneyInfo = "username=" + UserConfig.myUserName;
        connectServer("POST", "/getMoney", moneyInfo);

    }*/

    public void transferSocial(){
        //hideToFragment(selectedFragment, mSocial);
        //selectedFragment = mSocial;
        Log.d("0807", "trnasfer social");
        replaceToFragment(mSocial);
    }

    public void transferSpecial(){
        if (mSpecial == null) {
            mSpecial = new SpecialEventFragment();
        }
        replaceToFragment(mSpecial);
    }

    public void transferLoginUser(){
        //hideToFragment(selectedFragment, mLoginUser);
        //selectedFragment = mLoginUser;
        Log.d("0807", "trnasfer login");
        if (mLoginUser == null) {
            mLoginUser = new LoginUserFragment();
        }

        replaceToFragment(mLoginUser);
    }


    public void transferSimUser(){

        if(mSim == null){
            mSim = new SimUserFragment();
        }
        //hideToFragment(selectedFragment, mSim);
        //selectedFragment = mSim;
        replaceToFragment(mSim);

    }

    public void transferFriend(){

        if(mFriend == null){
            mFriend = new FriendFragment();
        }
        replaceToFragment(mFriend);

    }

    public void transferTranRecord(){

        if(mTranRecord == null){
            mTranRecord = new TranRecordFragment();
        }
        //hideToFragment(selectedFragment, mSim);
        //selectedFragment = mSim;
        replaceToFragment(mTranRecord);

    }

    public void transferLogin(){

        if(mLogin==null){
            mLogin = new LoginFragment();
            mLogin.setJsonParser(JsonParser);
        }
        replaceToFragment(mLogin);

    }


    public void transferDetailTranRecord(){

        if(mDetailTranRecord == null){
            mDetailTranRecord = new DetailTranRecordFragment();
        }
        //hideToFragment(selectedFragment, mSim);
        //selectedFragment = mSim;
        replaceToFragment(mDetailTranRecord);

    }

    public void createFriendRequest(){
        if (mFriendRequest == null) {
            mFriendRequest = new FriendRequestFragment();
        }
    }

    public void transferFriendRequest(){
        replaceToFragment(mFriendRequest);
    }

    public void transferUser(){

        if(mUser == null){
            mUser = new UserFragment();
        }
        replaceToFragment(mUser);

    }

    public void transferFreeWifi(){

        if(mFreeWifi == null){
            mFreeWifi = new FreeWifiFragment();
        }
        replaceToFragment(mFreeWifi);

    }

    public void replaceToFragment(Fragment fragment) {
        Log.d("sdffawew","wefawfe");

//        FragmentManager fm = getFragmentManager();
//        FragmentTransaction transaction = fm.beginTransaction();
//        if(previousfragment != fragment){
//            previousfragment = fragment;
//            transaction.replace(R.id.FragmentContent, fragment);
//        }
//        if(previousfragment == fragment){
//            transaction.detach(fragment);
//            transaction.attach(fragment);
//        }
//        transaction.commit();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            transaction.setReorderingAllowed(false);
        }
        if(previousfragment != fragment){
            previousfragment = fragment;
            transaction.replace(R.id.FragmentContent, fragment);
        }
        if(previousfragment == fragment){
            transaction.detach(fragment);
            transaction.attach(fragment);
        }
        transaction.commit();
    }


    public void hideToFragment(Fragment origin, Fragment to) {
        Log.d("0731hideto",origin.toString() + "----- " +  to.toString());
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (origin!=to) {
            if (!to.isAdded()) {
                Log.d("0731Added", "button1");
                transaction.hide(origin).add(R.id.FragmentContent, to).commit();
            } else {
                Log.d("0731Added", "button2");
                transaction.hide(origin).show(to).commit();
            }
        }
        else {
            Log.d("0731", "the same");
        }
    }

    public void initBfRun() {
        selectedFragment = mUser;
        FileMaker.fileFirstWrite = true;

        latest = null;
        previous = new TrafficSnapshot(this);


        startServiceTime = System.currentTimeMillis();

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        APPVERSION = valueOf(pInfo.versionCode);
        //Log.d("APPVSERSION",""+APPVERSION);
    }

    public void initVar() {
        logPath = new String("/data/data/" + getPackageName()) + "/logs/";
        configPath = "/data/data/" + getPackageName() + "/config";

        //----
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        APPVERSION = valueOf(pInfo.versionCode);
        ///Log.d("APPVSERSION",""+APPVERSION);



        if (UserConfig.loadConfigFrom(configPath) == false) {
            //  Launch app login
            final Intent login_intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(login_intent);
            UserConfig.setUserName("DefaultUser");
        }
        else {
            JsonParser.setAccount("USER" + UserConfig.myUserid);
        }
//        JsonParser.setAccount(UserConfig.myUserName);

        logPrefix = new String("NCTU");
        recordPrefix = new String(UserConfig.myUserName + "RECORD");

        mOutCypher = new OutCypher();
        ShowDialogMsg.mcontext = getApplicationContext();
//
//        if (mUser == null) {
//            mUser = new UserFragment();
//            mUser.SetContext(MainActivity.this);
//        }
//        replaceToFragment(mUser);

        dialogExisted = false;


    }

    private boolean isAllPermissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void showStartingFragment() {
        if (isAllPermissionsGranted()) {
            navigation.setSelectedItemId(R.id.navigation_map);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initPermission() {
        dialogExisted = true;
        List<String> permissionsNeeded = new ArrayList<>();

        final List<String> permissionsList = new ArrayList<>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION)
                || !addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            permissionsNeeded.add("LOCATION");
        }

        if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE)
                || !addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionsNeeded.add("STORAGE");
        }

        if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE)) {
            permissionsNeeded.add("PHONE");
        }

        if (!addPermission(permissionsList, Manifest.permission.CAMERA)) {
            permissionsNeeded.add("CAMERA");
        }

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = getString(R.string.grantaccess) + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++) {
                    message = message + ", " + permissionsNeeded.get(i);
                }
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_PERMISSION);
                                dialogExisted = false;
                            }
                        });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_PERMISSION);
            return;
        }
        ((MyApplication) getApplication()).appRunningStatus = getString(R.string.app_is_running);
        startServices();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);

            /**
             * This case means either 1 or 2:
             * 1. The user turned down the permission request in the past and
             *      choose the Don't ask again option in the permission request system dialog
             * 2. A device policy prohibits the app from having that permission
             *
             * Ref: https://developer.android.com/training/permissions/requesting.html
             */
            if (!shouldShowRequestPermissionRationale(permission)) {
                return false;
            }
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ShowDialogMsg.showDialog(getString(R.string.withoutpermission));
                        dialogExisted = false;
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    public void startServices() {

        if (RunIntentService.runFlag == false) {
            initBfRun();
            try {
                originalScreenOffTime = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            //Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 86400000 ); //86400000ms = 24hr

            RunIntentService.runFlag = true;
            RunIntentService.stopServiceFlag = false;
            RunIntentService.firstRoundFlag = true;
            Intent Intent = new Intent(MainActivity.this, RunIntentService.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("JsonParser", JsonParser);
            bundle.putSerializable("moutcypher", mOutCypher);
            Intent.putExtras(bundle);
            startService(Intent);
        }
    }

    //move below to MapFragment
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate( R.menu.menu_main, menu);
//        this.menu = menu;
//
//        return super.onCreateOptionsMenu(menu);
//    }

//    private void hideOption(int id) {
//        if (menu != null) {
//            MenuItem item = menu.findItem(id);
//            item.setVisible(false);
//        }
//    }
//
//    private void showOption(int id) {
//        if (menu != null) {
//            MenuItem item = menu.findItem(id);
//            item.setVisible(true);
//        }
//    }
//
//    private void setOptionTitle(int id, String title) {
//        if (menu != null) {
//            MenuItem item = menu.findItem(id);
//            item.setTitle(title);
//        }
//    }
//
//    private void setOptionIcon(int id, int iconRes) {
//        if (menu != null) {
//            MenuItem item = menu.findItem(id);
//            item.setIcon(iconRes);
//        }
//    }
//
//    public void showSpeakerOption() {
//            if (soundeffect) {
//                setOptionIcon(R.id.menu_item, R.drawable.ic_speaker);
//            }
//            else {
//                setOptionIcon(R.id.menu_item, R.drawable.ic_speaker_off);
//            }
//            showOption(R.id.menu_item);
//    }
//
//    public void showRecommendFriendEventOption() {
//            setOptionIcon(R.id.menu_item, R.drawable.icon_friend_recommend_event);
//            showOption(R.id.menu_item);
//    }


    // handle sound effect
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu){
////        MenuItem action_select_item = menu.findItem(R.id.action_select);
////        View rootview = action_select_item.getActionView();
////        soundimage = rootview.findViewById(R.id.sound);
////        soundoffimage = rootview.findViewById(R.id.soundoff);
////        soundimage.setVisibility(View.INVISIBLE);
////
////        // press to turn off
////        soundimage.setOnClickListener(new View.OnClickListener(){
////            @Override
////            public void onClick(View v){
////                soundoffimage.setVisibility(View.VISIBLE);
////                soundimage.setVisibility(View.INVISIBLE);
////                soundeffect = false;
////            }
////        });
////
////        // press to turn on
////        soundoffimage.setOnClickListener(new View.OnClickListener(){
////            @Override
////            public void onClick(View v){
////                soundoffimage.setVisibility(View.INVISIBLE);
////                soundimage.setVisibility(View.VISIBLE);
////                soundeffect = true;
////            }
////        });
//        return super.onPrepareOptionsMenu(menu);
//    }

    //move below to MapFragment
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if (item.getItemId() == R.id.sound_action) {
//            if(soundeffect){
//                item.setIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_speaker_off));
//                soundeffect = !soundeffect;
//            }
//            else{
//                item.setIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_speaker));
//                soundeffect = !soundeffect;
//            }
//
//            // edit colors menu
//        }
//
//        return super.onOptionsItemSelected(item);
//    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    break;
                }
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);

                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_DENIED);
                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_DENIED);
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_DENIED);
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_DENIED);
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
                    perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_DENIED);
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_DENIED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }

                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    navigation.setSelectedItemId(R.id.navigation_map);
                    startServices();
                    ((MyApplication) getApplication()).appRunningStatus = getString(R.string.app_is_running);
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, getString(R.string.openpermission), Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void showGPSClosedMsg() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(getString(R.string.GPSchecker))
                .setMessage(getString(R.string.turnGPSon))
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).show();
    }

    public void showPermissionSettingMsg() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(getString(R.string.systemsetting))
                .setMessage(getString(R.string.grantsystem))
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).show();
    }
/*
    public static void getCoinsFromServer(){
        String urlstring = "http://sensinggo.org/getCoins/";
        //String urlstring = "http://wirelab.nctucs.net:8000/hello/welcome_post_json/";

        Log.d("entergetcoins", "1223");
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
            if(LocationLocation.userlocationG == null){
                Log.d("nullnull", "null");
                JsonObj.put("user_lat", "null");
                JsonObj.put("user_lng", "null");
            }
            else {
                JsonObj.put("user_lat", Location.userlocationG.getLatitude());
                JsonObj.put("user_lng", Location.userlocationG.getLongitude());
            }
            JsonObj.put("username", UserConfig.myUserName);
            JSONArray Json_send = new JSONArray();
            Json_send.put(JsonObj);



            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            dos.writeBytes(Json_send.toString());
            dos.flush();
            dos.close();
            int responseCode = connection.getResponseCode();
            Log.d("getcoinsresponse", "Post_Message: " + responseCode);
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            String result = "";
            while((line = br.readLine()) != null)
                result += line;

            Log.d("entergetcoins", "122333");
            if(!result.equals("GPS should be float")) {
                Log.d("successfullygetcoins", "20000");
                parseJSON(result);
            }
            else{
                Log.d("unsuccessfullygetcoins", "20000");
            }


            br.close();

            //System.out.println("WEB return value is : " + sb);
            Log.d("stringofgetcoins", "Post_Message: " + result);
            // Toast.makeText(getApplicationContext(),"Sending 'POST' request to URL : " + url + "\nPost parameters : " + test + "\nResponse Code : " + responseCode + "\nWEB return value is : " + sb, Toast.LENGTH_LONG).show();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }*/



/*

    // put the coordinate of coin into arraylist
    public static void parseJSON(String data){

        try{

            coinlist.clear();
            Log.d("testeste", data);
            JSONObject getcoinjson = new JSONObject(data);
            JSONArray getgpsarray = getcoinjson.getJSONArray("gps");
            Log.d("testeste", getgpsarray.toString());
            int jsonArrLength = getgpsarray.length();
            //public static ArrayList<CoinContent> coinlist = new ArrayList<CoinContent>();
            for(int i=0; i < jsonArrLength; i++) {
                //Log.d("check", "check" );
                JSONObject jsonChildNode = getgpsarray.getJSONObject(i);
                coinlist.add(new CoinContent(jsonChildNode.getDouble("lat"), jsonChildNode.getDouble("lng"), jsonChildNode.getInt("state"), jsonChildNode.getInt("kind")));


            }
            Log.d("093030", "value "+ coinlist.size());


        }catch(Exception e){
            Log.i("App", "Error parsing data" +e.getMessage());

        }

    }*/

    private boolean isNetWorkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();

        if (nInfo != null) {
            if (nInfo.isConnected()) {
                Log.v("connectt", "connected ");
                return true;
            }
        }
        Log.v("connectt", "No connection ");
        return false;
    }

    public void showNoNetworkDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.turnonnetwork))
                .setTitle(getString(R.string.unableconnect))
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                                startActivity(i);
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        }
                );
        AlertDialog alert = builder.create();
        alert.show();
    }
    public static ArrayList<TicketContent> ticketlist = new ArrayList<TicketContent>();

//    public static ArrayList<memberContent> memberlist = new ArrayList<memberContent>();
    public static ArrayList<Integer> coins = new ArrayList<Integer>();
    public static ArrayList<String> ch = new ArrayList<String>();
    public static ArrayList<String> ch_type = new ArrayList<String>();
    public static ArrayList<String> en = new ArrayList<String>();
    public static ArrayList<String> facilityURL = new ArrayList<String>();
    public static ArrayList<String> logoURL = new ArrayList<String>();

    public static class TicketContent{
        public TicketContent(int phase, int number) {
            this.phase = phase;
            this.number = number;


        }
        public int phase = 0;
        public int number = 0;

    }

    public static class memberContent{
        public memberContent(int coins, String ch, String en, String membertype, String logoURL, String facilityURL) {
            this.coins = coins;
            this.ch = ch;
            this.en = en;
            this.membertype = membertype;
            this.logoURL = logoURL;
            this.facilityURL = facilityURL;
        }
        public int coins = 0;
        public String ch = "not show";
        public String en = "not show";
        public String membertype = "not show";
        public String logoURL = null;
        public String facilityURL = null;
    }
    public static ArrayList<TrainContent> trainlist = new ArrayList<TrainContent>();
    public static class TrainContent{
        public TrainContent(int TrainId, double latitude, double longitude, int collected) {
            this.TrainId = TrainId;
            this.latitude = latitude;
            this.longitude = longitude;
            this.collected = collected;

        }
        public int TrainId = 0;
        public double latitude = 0.0;
        public double longitude = 0.0;
        public int collected = 0;

    }
    public static class CoinContent{
        public CoinContent(double latitude, double longitude, int state, int kind, int auto) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.state = state;
            this.kind = kind;
            this.auto = auto;
        }
        public double latitude = 0.0;
        public double longitude = 0.0;
        public int state = 0;
        public int kind = 0;
        public int auto = 0;
    }

    public static class PhtoContent{
        public PhtoContent(double latitude, double longitude, String name) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.name = name;
        }
        public double latitude = 0.0;
        public double longitude = 0.0;
        public String name = "";
    }

    public static class MonsterContent{
        public MonsterContent(double latitude, double longitude, String name, long hp) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.name = name;
            this.hp = hp;
        }
        public double latitude = 0.0;
        public double longitude = 0.0;
        public String name = "";
        public long hp = 0;
    }

//    private void getTotalMoneyFromServer(){
//        //String urlstring = "http://sensinggo.org/getTotalMoney";
//        String Info = "username=" + UserConfig.myUserName ;
//        connectServer("POST", "/getTotalMoney", Info);
//    }

    public void couldupdate(){
        final AlertDialog.Builder removeblackDialog = new AlertDialog.Builder(this);
        removeblackDialog.setMessage(getString(R.string.couldupdate))
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }

    public void needupdate(){
        final AlertDialog.Builder removeblackDialog = new AlertDialog.Builder(this);
        removeblackDialog.setMessage(getString(R.string.needupdate))
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        launchAppDetail(getPackageName());
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();
    }

    public void launchAppDetail(String appPkg) {
        try {

            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.android.vending");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getUserID() {
        Log.d("0626info",UserConfig.myUserName);
        if (!UserConfig.myUserName.equals("DefaultUser")) {
            String info = "username=" + UserConfig.myUserName;
            connectServer("POST", "/getUID", info);
        }
    }

    public void getCoinsTopUser() {
        String info = "";
        connectServer("POST", "/coinsTopUser", info);
    }

    // if GPSmock is enable
    public static boolean isMockSettingsON(Context context) {
        // returns true if mock location enabled, false if not enabled.
        if (Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")) {
            Log.d("fakegpsfakegps", "111112222");
            return false;
        }
        else {
            Log.d("fakegpsfakegps", "11111");
            return true;
        }


    }



    public static boolean areThereMockPermissionApps(Context context) {
        int count = 0;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,
                        PackageManager.GET_PERMISSIONS);

                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        if (requestedPermissions[i]
                                .equals("android.permission.ACCESS_MOCK_LOCATION")
                                && !applicationInfo.packageName.equals(context.getPackageName())) {
                            count++;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.d("Got exception " , e.getMessage());
            }
        }

        if (count > 0) {
            Log.d("fakegpsfakegps", "22222");
            return true;
        }
        return false;
    }
}
class ticketAdapter extends RecyclerView.Adapter<ticketAdapter.ViewHolder> {
    private Context context;
    private List<ticket_card_view> ticketList;
    //    private String type;
    ticketAdapter(Context context, List<ticket_card_view> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
//        this.type = type;
    }

    @Override
    public ticketAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ticket_card_view, parent, false);
        return new ticketAdapter.ViewHolder(view);
    }

    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        context.startActivity(launchBrowser);
    }

    @Override
    public void onBindViewHolder(ticketAdapter.ViewHolder holder, int position) {
        final ticket_card_view member = ticketList.get(position);
//        holder.logo.setImageResource(member.getLogo());
        holder.coin_logo.setImageResource(member.getCoin_logo());
        holder.name.setText(member.getName());
        holder.money.setText(String.valueOf(member.getMoney()));
//        "https://sensinggo.org/charity_icons/"+type+".png "
        Picasso.get().load(member.getlogoURL()).networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).resize(member.getWidth(),50)
                .into(holder.logo);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("yyy",String.valueOf(position));
                switch (position){
                    case 0:
                        goToUrl (facilityURL.get(0) );
                        break;
                    case 1:
                        goToUrl ( facilityURL.get(1));
                        break;
                    case 2:
                        goToUrl ( facilityURL.get(2));
                        break;
                    case 3:
                        goToUrl ( facilityURL.get(3));
                        break;
                    default:
                        Log.i("yyy","error");
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    //Adapter 需要一個 ViewHolder，只要實作它的 constructor 就好，保存起來的view會放在itemView裡面
    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView logo, coin_logo;
        TextView name, money;
        ViewHolder(View itemView) {
            super(itemView);
            logo = (ImageView) itemView.findViewById(R.id.logo);
            coin_logo = (ImageView) itemView.findViewById(R.id.coin_logo);
            name  = (TextView) itemView.findViewById(R.id.name);
            money = (TextView) itemView.findViewById(R.id.money);


        }
    }
}