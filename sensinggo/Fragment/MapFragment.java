package edu.nctu.wirelab.sensinggo.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.location.Location;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
//import edu.nctu.wirelab.measuring.Measurement.Location;

import org.json.JSONArray;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;


import edu.nctu.wirelab.sensinggo.Connect.HttpsConnection;
import edu.nctu.wirelab.sensinggo.Connect.Setting;
import edu.nctu.wirelab.sensinggo.MainActivity;
import edu.nctu.wirelab.sensinggo.R;
import edu.nctu.wirelab.sensinggo.UserConfig;
import pl.droidsonroids.gif.GifImageView;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static android.widget.Toast.LENGTH_SHORT;
import static edu.nctu.wirelab.sensinggo.MainActivity.soundeffect;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */



public class  MapFragment extends Fragment implements LocationListener {
    private MapView mMapView;
    private static Location recentlocation;
    private static GoogleMap googleMap;
    private boolean buttonbool = true;
    private boolean pressfine = false;
    private boolean presscourse = false;
    private boolean pressfriend = false;
    private static Double recentlat = 24.787574;
    private static Double recentlng = 120.997602;
    private Double lastlat = 24.594098;
    private Double lastlng = 120.455646;
    private static Double recentresolution = 0.0;
    private static Double radius = 300.0;
    private static Double accept_distance = 145.0;
//    private static Double accept_distance = 500000.0;
    private Double coinlat;
    private Double coinlng;
    private int once = 0;
    private static android.view.animation.Animation animation;
    private static android.view.animation.Animation mother_animation;
    private static TextView addOne;
    private static SoundPool soundPool;
    private int alertId1;
    private static int alertId2;
    private String freeWiFiName;
    private static FragmentActivity thiscontext;
    Button changeButton, coin_Btn, treasure_Btn, meichu_Btn;
    private Switch autoSwitch;
    private ImageView imageViewWhite;
    private TextView textViewAutoPrompt;
    private static TextView textViewCoinNumber, meichucoinsTV, meichucoinsAvatarTV;
    private ImageView imageViewBackground, meichucoinsIV, meichucoinsAvatarIV;
    private ImageView imageViewCoin, meichuimageIV, meichuimageAvatarIV, campIV;
    LocationManager lm;
    private Handler mHandler = new Handler();
    public static boolean autoIsChecked = false;

    public static ProgressBar mother_bar;
    private static ImageView giftIV;
    private static  boolean mother = false;

    public static  boolean today_upper_limit = false;

    private static int ttt = 0;
    int blood = 0;
    int original_coin = 0;
    String action = "";

    //ArrayList<Spot> spotlist = new ArrayList<Spot>();
    ArrayList<String> takencoinlist = new ArrayList<String>();
    ArrayList<SignalPoint> signalpointlist = new ArrayList<SignalPoint>();
    public static ArrayList<Double> latlist = new ArrayList<Double>();
    public static ArrayList<Double> lnglist = new ArrayList<Double>();
    public static ArrayList<Double> powerlist = new ArrayList<Double>();
    ArrayList<FreeWiFiSpot> freeWiFiList = new ArrayList<FreeWiFiSpot>();

    Runnable autoGetCoinsRunnable = new Runnable() {
        @Override
        public void run() {
            if (autoIsChecked && buttonbool) {
                autoDeleteCoins();
            }
            mHandler.postDelayed(autoGetCoinsRunnable, 10000);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {







        View view = inflater.inflate(R.layout.fragment_google_maps, null, false);
        // to initialize boolean
        buttonbool = true;
        pressfine = false;
        presscourse = false;

        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        //mMapView.onResume();
//        changeButton = (Button) view.findViewById(R.id.changeButton);
        coin_Btn = (Button) view.findViewById(R.id.coin_Btn);
        treasure_Btn = (Button) view.findViewById(R.id.treasure_Btn);
        meichu_Btn = (Button) view.findViewById(R.id.meichu_Btn);
        autoSwitch = view.findViewById(R.id.switchAuto);
        imageViewWhite = view.findViewById(R.id.imageViewWhite);
        textViewAutoPrompt = view.findViewById(R.id.textViewAutoPrompt);
        textViewCoinNumber = view.findViewById(R.id.textViewCoinNumber);
        imageViewBackground = view.findViewById(R.id.imageViewBackground);

        campIV = view.findViewById(R.id.campIV);
        imageViewCoin= view.findViewById(R.id.imageViewCoin);
        animation = AnimationUtils.loadAnimation(getActivity().getBaseContext().getApplicationContext(), R.anim.add_coin_anim);
        mother_animation = AnimationUtils.loadAnimation(getActivity().getBaseContext().getApplicationContext(), R.anim.mother_anim);
        addOne = (TextView) view.findViewById(R.id.addOne_tv);
        mother_bar = view.findViewById(R.id.mother_bar);
        giftIV = view.findViewById(R.id.giftIV);


        mother_bar.setMax(5);
        mother_bar.setVisibility(View.INVISIBLE);
        giftIV.setVisibility(View.INVISIBLE);
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);
        alertId2 = soundPool.load(getActivity(), R.raw.laugh2, 1);
        alertId1 = soundPool.load(getActivity(), R.raw.laugh1, 1);

        // hide button
        coin_Btn.setVisibility(View.INVISIBLE);
        treasure_Btn.setVisibility(View.INVISIBLE);
        meichu_Btn.setVisibility(View.INVISIBLE);
        // auto get coins
        mHandler.postDelayed(autoGetCoinsRunnable, 10000);

        getTotalMoneyFromServer();
        showCoinUI();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                double lat, lng;
                googleMap = mMap;
                UiSettings uisettings = googleMap.getUiSettings();
                uisettings.setMyLocationButtonEnabled(true);
                googleMap.setMyLocationEnabled(true);
//                googleMap.setMinZoomPreference(12.0f);
//                googleMap.setMaxZoomPreference(18.0f);
                if(autoIsChecked){
                    showAutoUI();
                    autoSwitch.setChecked(true);
                    Log.i("autoIsChecked", String.valueOf(autoIsChecked));
                }
                // Set MyLocationButton on right bottom position
                View location_button = mMapView.findViewWithTag("GoogleMapMyLocationButton");

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) location_button.getLayoutParams();
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                layoutParams.setMargins(0, 0, 30, 30);

                final AlertDialog.Builder GetCoinDialog = new AlertDialog.Builder(getActivity());
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(final Marker marker) {
                        // check fake GPS
//                        if(MainActivity.isMockSettingsON(getActivity()) || MainActivity.areThereMockPermissionApps(getActivity())){
//                            Log.d("fakegpsfakegps", "yesyes");
//                            Toast toast = Toast.makeText(getActivity(), getString(R.string.closefakegps), LENGTH_SHORT);
//                            toast.setGravity(Gravity.BOTTOM, 0, 0);
//                            toast.show();
//                        }
                        if(checkfake(recentlocation)){
                            Toast toast = Toast.makeText(getActivity(), getString(R.string.closefakegps), LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM, 0, 0);
                            toast.show();
                        }

                        // default user should login first
                        else if(UserConfig.myUserName.compareTo("DefaultUser") == 0){
                            MainActivity.navigation.setSelectedItemId(R.id.navigation_login);
                        }
                        //marker is coin
                        else if (buttonbool) {

                            // need to turn off Auto to get coins
                            if (autoIsChecked) {
                                Toast toast = Toast.makeText(getActivity(), getString(R.string.need_turn_off_auto), LENGTH_SHORT);
                                toast.show();
                                return true;
                            }


                            String temp_distance = marker.getSnippet();
                            float dis_of_coin = Float.parseFloat(temp_distance);
                            if (dis_of_coin <= accept_distance) {
                                // ask user to get coin or not
                                coinlat = marker.getPosition().latitude;
                                coinlng = marker.getPosition().longitude;
//                                GetCoinDialog.setMessage(getString(R.string.getcoin))
//                                        .setIcon(R.mipmap.ic_launcher)
//                                        .setPositiveButton("yes", (dialogInterface, i) -> {

                                Post_DeleteCoin(coinlat, coinlng);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        try {
                                            Thread.sleep(200);
                                            if(today_upper_limit==true){
                                                //不能拿金幣，因為已到今日上限
                                                Log.i("up", String.valueOf(today_upper_limit));
//                                                getCoinsFromServer(recentlat, recentlng);
                                            }
                                            else{
                                                Log.i("up", String.valueOf(today_upper_limit));
                                                MainActivity.coinlist.clear();
                                                getCoinsFromServer(recentlat, recentlng);
                                            }
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();

                            } else {
                                if(soundeffect) {
                                    soundPool.play(alertId1, 1.0F, 1.0F, 0, 0, 1.0F);
                                }

                                Toast toast = Toast.makeText(getActivity(), "不在圈圈內", LENGTH_SHORT);
                                toast.setGravity(Gravity.BOTTOM, 0, 0);
                                toast.show();

                            }
                        }
                        //marker is friends photo
                        else if(pressfriend){
                            marker.showInfoWindow();
                        }
                        else{

                        }
                        return true;
                    }
                });

                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER); // 設定定位資訊由 GPS提供
                if(location == null){
                    location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); // get location by network
                }

                // Neither GPS nor network can get location
                if(location == null) {
                    Log.d("null", "is is null");
                    new AlertDialog.Builder(getActivity())
                            .setMessage(getString(R.string.noGPSconnection))
                            .setIcon(R.mipmap.ic_launcher)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();
                }
                else {
                    lat = location.getLatitude();  // 取得經度
                    lng = location.getLongitude(); // 取得緯度
                    recentlat = location.getLatitude();
                    recentlng = location.getLongitude();

                    LatLng sydney = new LatLng(lat, lng);

                    googleMap.addCircle(new CircleOptions()
                            .center(sydney)
                            .radius(accept_distance)
                            .strokeWidth(5)
                            .strokeColor(Color.RED)
                            .fillColor(0x33ff0000));

                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(16).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }

            }
        });

        coin_Btn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                coin_Btn.setBackgroundResource(R.drawable.button_map_press);
                treasure_Btn.setBackgroundResource(R.drawable.button_map_round);
                meichu_Btn.setBackgroundResource(R.drawable.button_map_round);

                buttonbool = true;
                pressfine = false;
                presscourse = false;
                pressfriend = false;

                showCoinUI();

                if (recentlocation != null) {  // i don't know why
//                                googleMap.clear();
                    showCoin(recentlocation);
                } else {
                    Toast toast = Toast.makeText(getActivity(), getString(R.string.no_gps), LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                }
            }

        });

        treasure_Btn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                treasure_Btn.setBackgroundResource(R.drawable.button_map_press);
                coin_Btn.setBackgroundResource(R.drawable.button_map_round);
                meichu_Btn.setBackgroundResource(R.drawable.button_map_round);
                hideAutoUI();
                hideCoinUI();

                if(!pressfriend){
                    googleMap.clear();
                    getfriendsPhotoFromServer(recentlat, recentlng);
                }
                pressfriend = true;
                buttonbool = false;
                pressfine = false;
                presscourse = false;
            }

        });

        meichu_Btn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                meichu_Btn.setBackgroundResource(R.drawable.button_map_press);
                coin_Btn.setBackgroundResource(R.drawable.button_map_round);
                treasure_Btn.setBackgroundResource(R.drawable.button_map_round);

                buttonbool = false;

                hideAutoUI();
                hideCoinUI();

                // previous status is not fine-granularity
                if(!pressfine) {
                    googleMap.clear();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            latlist.clear();
                            lnglist.clear();
                            powerlist.clear();
                            recentresolution = 2.0;
                            Post_getsignallist();
                        }
                    }).start();

                    pressfine = true;
                    presscourse = false;
                    pressfriend = false;
                }

            }

        });

        giftIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation();
            }
        });

//        changeButton.setOnClickListener(new Button.OnClickListener(){
//            public void onClick(View v){
//                //Creating the instance of PopupMenu
//                PopupMenu popup = new PopupMenu(getActivity(), changeButton);
//                //Inflating the Popup using xml file
//                popup.getMenuInflater()
//                        .inflate(R.menu.popup_menu, popup.getMenu());
//
//                /* remove header of submenu */
//                MenuItem menuSignalMap = popup.getMenu().findItem(R.id.map_resolution);
//                if (menuSignalMap != null) {
//                    menuSignalMap.getSubMenu().clearHeader();
//                }
//
//                //registering popup with OnMenuItemClickListener
//                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    public boolean onMenuItemClick(MenuItem item) {
//                        //googleMap.clear();
//                        String ss = item.getTitle().toString();
//
//                        if(ss.equals(getString(R.string.coins_map))){
//                            buttonbool = true;
//                            pressfine = false;
//                            presscourse = false;
//                            pressfriend = false;
//
//                            showCoinUI();
//                            hideMonsterUI();
//
//                            if(recentlocation != null) {  // i don't know why
////                                googleMap.clear();
//                                showCoin(recentlocation);
//                            }
//                            else{
//                                Toast toast = Toast.makeText(getActivity(), getString(R.string.no_gps), LENGTH_SHORT);
//                                toast.setGravity(Gravity.BOTTOM, 0, 0);
//                                toast.show();
//                            }
//                        }
//                        else if(ss.equals(getString(R.string.highresolution))){
//                            buttonbool = false;
//
//                            hideAutoUI();
//                            hideCoinUI();
//                            hideMonsterUI();
//
//                            // previous status is not fine-granularity
//                            if(!pressfine) {
//                                googleMap.clear();
//                                new Thread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        latlist.clear();
//                                        lnglist.clear();
//                                        powerlist.clear();
//                                        recentresolution = 2.0;
//                                        Post_getsignallist();
//                                    }
//                                }).start();
//                            }
//                            // previous status is still fine-granularity
//                            else{
//                                return true;
//                            }
//                            pressfine = true;
//                            presscourse = false;
//                            pressfriend = false;
//                        }
//                        else if(ss.equals(getString(R.string.lowresolution))){
//                            buttonbool = false;
//
//                            hideAutoUI();
//                            hideCoinUI();
//                            hideMonsterUI();
//
//                            // previous status is not course-granularity
//                            if(!presscourse) {
//                                googleMap.clear();
//                                new Thread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        latlist.clear();
//                                        lnglist.clear();
//                                        powerlist.clear();
//                                        recentresolution = 1.0;
//                                        Post_getsignallist();
//                                    }
//                                }).start();
//                            }
//                            // previous status is still course-granularity
//                            else{
//                                return true;
//                            }
//                            pressfine = false;
//                            presscourse = true;
//                            pressfriend = false;
//                        }
//                        else if(ss.equals(getString(R.string.friends_map))){
//
//                            hideAutoUI();
//                            hideCoinUI();
//                            hideMonsterUI();
//
//                            if(!pressfriend){
//                                googleMap.clear();
//                                getfriendsPhotoFromServer(recentlat, recentlng);
//                            }
//                            pressfriend = true;
//                            buttonbool = false;
//                            pressfine = false;
//                            presscourse = false;
//
//                        }
//                        else if(ss.equals(getString(R.string.web_signal_map))){
//                            // Open signal map on webpage
//                            buttonbool = true;
//                            Uri webpage = Uri.parse("https://sensinggo.org/signalMap/");
//                            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
//                            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
//                                startActivity(intent);
//                            }
//                        }
//                        else if(ss.equals(getString(R.string.meichu_map))){
//                            if(UserConfig.myUserName.compareTo("DefaultUser")==0 || UserConfig.choosecamp==-1){
//                                Toast toast = Toast.makeText(getActivity(), "請先登入並且選擇陣營", LENGTH_SHORT);
//                                toast.setGravity(Gravity.BOTTOM, 0, 0);
//                                toast.show();
//                            }
//                            else {
//
//                                hideAutoUI();
//                                hideCoinUI();
//                                showMonsterUI();
//
//                                if (buttonbool || presscourse || pressfine || pressfriend) {
//                                    googleMap.clear();
//                                    getmonsterlocationFromServer();
//                                }
//                                buttonbool = false;
//                                pressfine = false;
//                                presscourse = false;
//                                pressfriend = false;
//                            }
//                        }
//                        //onResume();
//                        return true;
//                    }
//                });
//                MenuPopupHelper menuHelper = new MenuPopupHelper(getActivity(), (MenuBuilder) popup.getMenu(), changeButton);
//                menuHelper.setForceShowIcon(true);
//                menuHelper.show();
//                //popup.show(); //showing popup menu
//            }
//        });


        autoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    autoIsChecked = true;
                    showAutoUI();
                } else {
                    autoIsChecked = false;
                    hideAutoUI();
                }
            }
        });


        //spotlist = new ArrayList<Spot>();
        //spotlist.add(new Spot(24.7872268, 120.9969887, "coin1")); //小木屋鬆餅


        return view;
    }


    private void connectServer(String method, String path, String info){
        Log.d("0612conn", info);
        HttpsConnection httpsConnection = new HttpsConnection(getActivity());
        httpsConnection.setActivity(getActivity());
        httpsConnection.setFragment(this);
        httpsConnection.setMethod(method, info);
        httpsConnection.execute(path);

    }

    public static void showanimation(int tag){
        if(soundeffect) {
            soundPool.play(alertId2, 1.0F, 1.0F, 0, 0, 1.0F);
        }
//        addOne.setVisibility(View.VISIBLE);
//        if(tag==0)
//            addOne.setText("+1\n" + thiscontext.getString(R.string.remindmoney, UserConfig.totalmoney.intValue()));
//        else if(tag==1)
//            addOne.setText("+3\n" + thiscontext.getString(R.string.remindmoney, UserConfig.totalmoney.intValue()));
//        else if(tag==2)
//            addOne.setText("+5\n" + thiscontext.getString(R.string.remindmoney, UserConfig.totalmoney.intValue()));
//        addOne.startAnimation(animation);
//        new Handler().postDelayed(() -> addOne.setVisibility(View.GONE), 2500);

        updateCoinNumber();

    }

    public static void ShowModifiedMonsterAnimation(int modified_hp){
        addOne.setVisibility(View.VISIBLE);
        if(modified_hp > 0){
            addOne.setTextColor(thiscontext.getResources().getColor(R.color.green));
            addOne.setText("+" + modified_hp + " hp");
        }
        else{
            addOne.setTextColor(Color.RED);
            addOne.setText(" " + modified_hp + " hp");
        }
        addOne.startAnimation(animation);
        new Handler().postDelayed(() -> addOne.setVisibility(View.GONE), 2500);

    }

    public static void showAutoAnimation(int coins_cnt){
        if(soundeffect) {
            soundPool.play(alertId2, 1.0F, 1.0F, 0, 0, 1.0F);
        }
//        addOne.setVisibility(View.VISIBLE);
//        addOne.setText("+" + coins_cnt + "\n" + thiscontext.getString(R.string.remindmoney, UserConfig.totalmoney.intValue()));
//        addOne.startAnimation(animation);
//        new Handler().postDelayed(() -> addOne.setVisibility(View.GONE), 2500);

        updateCoinNumber();
    }

    public static void showDialog(){
//        AlertDialog.Builder GetCoinDialog = new AlertDialog.Builder(thiscontext);
//        GetCoinDialog.setMessage("")
//                .show();
        LayoutInflater inflater = LayoutInflater.from(thiscontext);
        final View v = inflater.inflate(R.layout.special_dialog, null);
        new AlertDialog.Builder(thiscontext)
                .setView(v)
                .show();
    }



    private void showCoin(Location location){

        // if user move over 200m then reget coins from server
        Location recentlocation = new Location(location);
        recentlocation.setLatitude(lastlat);
        recentlocation.setLongitude(lastlng);
        float dis = location.distanceTo(recentlocation);
        // if user walked half of radius then reget coins from server (parseCoinsListJSON will be responsible for showing coins)
        getTravelEventInfoFromServer();

        if(dis > radius/2){
            lastlat = recentlat;
            lastlng = recentlng;
            MainActivity.coinlist.clear();
            googleMap.clear();
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
            getCoinsFromServer(recentlat, recentlng);
            getTotalMoneyFromServer();

//                }
//            }).start();
        }
        //if user didn't walk half of radius then just show odd coins
        else{
            drawcoinsmap();
        }
    }

    public static void parseJSON(String data){

        try{
            JSONArray jsonMainNode = new JSONArray(data);
            int jsonArrLength = jsonMainNode.length();
            //Log.d("check", "check" + jsonArrLength);
            for(int i=0; i < jsonArrLength; i++) {
                //Log.d("check", "check" );
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                latlist.add( jsonChildNode.getDouble("Lat") );
                lnglist.add( jsonChildNode.getDouble("Lng") );
                powerlist.add( jsonChildNode.getDouble("Degree") );

            }
            drawsiganlmap();


        }catch(Exception e){
            Log.i("App", "Error parsing data" +e.getMessage());

        }
    }

    private void Post_getsignallist(){
        //String urlstring = "http://140.113.216.39/putGPS/";
        String Info = "lat=" + recentlat +
                "&lng=" + recentlng +
                "&resolution=" + recentresolution;
        connectServer("POST", "/putGPS/", Info);
    }

    private void Post_DeleteCoin(Double inputlat, Double inputlng){
        String urlstring = "http://140.113.216.39/deleteCoins/";
        String Info = "username=" + UserConfig.myUserName +
                "&coin_lat=" + inputlat +
                "&coin_lng=" + inputlng;
        connectServer("POST", "/deleteCoins/", Info);
    }

    private void getTotalMoneyFromServer(){
        //String urlstring = "http://sensinggo.org/getTotalMoney";
        String Info = "username=" + UserConfig.myUserName;
        connectServer("POST", "/getTotalMoney", Info);
    }


    private void getCoinsFromServer(Double lat, Double lng){
        //String urlstring = "http://sensinggo.org/getCoins/";
        String Info = "username=" + UserConfig.myUserName +
                "&user_lat=" + lat +
                "&user_lng=" + lng;
        connectServer("POST", "/getCoins/", Info);
    }
    private void getTravelEventInfoFromServer(){
        //String urlstring = "http://sensinggo.org/getCoins/";
        String Info = "username=" + UserConfig.myUserName ;
        Log.i("getTravelEvent",Info);
        connectServer("POST", "/getTravelEventInfo", Info);




//        mother_bar.setProgress(UserConfig.ungiftedStamp);
    }

    private void getfriendsPhotoFromServer(Double lat, Double lng){
        //String urlstring = "http://sensinggo.org/getCoins/";
        String Info = "username=" + UserConfig.myUserName +
                "&user_lat=" + lat +
                "&user_lng=" + lng;
        connectServer("POST", "/downloadFriendsProfilePhoto", Info);
    }

    private void getmonsterlocationFromServer(){
        //String urlstring = "http://sensinggo.org/getCoins/";
        connectServer("POST", "/getMonsterInfo", "");
    }

    // put the coordinate of coin into arraylist and show coins on the map
    public static void parseCoinsListJSON(String data){

        try{

            MainActivity.coinlist.clear();
            JSONObject getcoinjson = new JSONObject(data);
            JSONArray getgpsarray = getcoinjson.getJSONArray("gps");
            int jsonArrLength = getgpsarray.length();
            //public static ArrayList<CoinContent> coinlist = new ArrayList<CoinContent>();
            for(int i=0; i < jsonArrLength; i++) {
                //Log.d("check", "check" );
                JSONObject jsonChildNode = getgpsarray.getJSONObject(i);
                MainActivity.coinlist.add(new MainActivity.CoinContent(jsonChildNode.getDouble("lat"), jsonChildNode.getDouble("lng")
                        , jsonChildNode.getInt("state"), jsonChildNode.getInt("kind"), jsonChildNode.getInt("auto")));


            }
            Log.d("093030", "value "+ MainActivity.coinlist.size());
            drawcoinsmap();


        }catch(Exception e){
            Log.i("App", "Error parsing data" +e.getMessage());

        }

    }

    // put the coordinate of coin into arraylist and show coins on the map
    public static void parsefriendsPhotoListJSON(String data){

        try{

            MainActivity.photolist.clear();
            Log.d("testeste", data);

            JSONArray getphtoarray = new JSONArray(data);
            int jsonArrLength = getphtoarray.length();
            for(int i=0; i < jsonArrLength; i++) {
                //Log.d("check", "check" );
                JSONObject jsonChildNode = getphtoarray.getJSONObject(i);
                MainActivity.photolist.add(new MainActivity.PhtoContent(jsonChildNode.getDouble("lat"), jsonChildNode.getDouble("lng"), jsonChildNode.getString("name")));


            }
            Log.d("093030", "value "+ MainActivity.photolist.size());
            drawphotosmap();


        }catch(Exception e){
            Log.i("App", "Error parsing data" +e.getMessage());

        }

    }

    public static void parseMeiChuMonster(String data, boolean move_camera_bool){

        try{

            MainActivity.monsterlist.clear();

            JSONArray getphtoarray = new JSONArray(data);
            int jsonArrLength = getphtoarray.length();
            for(int i=0; i < jsonArrLength; i++) {
                //Log.d("check", "check" );
                JSONObject jsonChildNode = getphtoarray.getJSONObject(i);
                MainActivity.monsterlist.add(new MainActivity.MonsterContent(jsonChildNode.getDouble("lat"), jsonChildNode.getDouble("lng"), jsonChildNode.getString("name"), jsonChildNode.getLong("hp")));


            }
            Log.d("093030", "value "+ MainActivity.monsterlist.size());
            drawmonstermap(move_camera_bool);


        }catch(Exception e){
            Log.i("App", "Error parsing data" +e.getMessage());

        }

    }


    class SignalPoint{
        SignalPoint(double latitude, double longitude, double power) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.power = power;
        }
        double latitude;
        double longitude;
        double power;
    }

    class FreeWiFiSpot {
        double latitude;
        double longitude;
        String hotspotName;
        String address;

        FreeWiFiSpot (double latitude, double longitude, String hotspotName, String address) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.hotspotName = hotspotName;
            this.address = address;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

//        if(UserConfig.firstlogin){
//            buttonbool = true;
//            getCoinsFromServer(recentlat, recentlng);
//            UserConfig.firstlogin = false;
//        }

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                buttonbool = true;
//                getCoinsFromServer(recentlat, recentlng);
//
////                getfriendsPhotoFromServer(recentlat, recentlng);
//                //getTotalMoneyFromServer();
//            }
//        }).start();

        mMapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        lm.removeUpdates(this);
        mMapView.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void onLocationChanged(Location location) {
        if (location == null) return;
        recentlocation = location;
        recentlat = location.getLatitude();
        recentlng = location.getLongitude();


        LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());

        if(buttonbool) {
            Log.d("QQ", "this is true");
//            googleMap.addCircle(new CircleOptions()
//                    .center(sydney)
//                    .radius(accept_distance)
//                    .strokeColor(Color.RED));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//            CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(16).build();
//            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            showCoin(location);

        }
        getTravelEventInfoFromServer();
    }

    public static void drawsiganlmap(){
        Log.d("QQ", "this is false" + powerlist.size());
        int num = 0;
        for (int i = 0; i < powerlist.size(); i++) {

            Log.d("crazy", "hello" + num);
            num++;
            LatLng sigcor = new LatLng(latlist.get(i), lnglist.get(i));
            CircleOptions circle = new CircleOptions();
            circle.center(sigcor);
            if(recentresolution == 1.0)
                circle.radius(50);
            else
                circle.radius(20);
            circle.strokeWidth(0);
            if(powerlist.get(i) == 0) {
                //circle.strokeColor(0xffff0000);
                circle.fillColor(0xffff0000);
            }
            else if(powerlist.get(i) == 1.0){
                //circle.strokeColor(0x33ff0000);
                circle.fillColor(0xffff0000);
            }
            else if(powerlist.get(i) == 2.0){
                //circle.strokeColor(0x33ff0000);
                circle.fillColor(0xffff0000);
            }
            else if(powerlist.get(i) == 3.0){
                //circle.strokeColor(0x33ff0000);
                circle.fillColor(0xffffff00);
            }
            else if(powerlist.get(i) == 4.0){
                //circle.strokeColor(0x33ff0000);
                circle.fillColor(0xff00ff00);
            }
            else if(powerlist.get(i) == 5.0){
                //circle.strokeColor(0x33ff0000);
                circle.fillColor(0xff00ff00);
            }
            googleMap.addCircle(circle);
        }

    }

    private static void drawcoinsmap(){

        googleMap.clear();

        LatLng sydney = new LatLng(recentlat, recentlng);
        googleMap.addCircle(new CircleOptions()
                .center(sydney)
                .radius(accept_distance)
                .strokeWidth(5)
                .strokeColor(Color.RED)
                .fillColor(0x33ff0000));

//zoomzoom
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(16).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        for (int i = 0; i < MainActivity.coinlist.size(); i++) {
            if(MainActivity.coinlist.get(i).state == 1) continue;
            Log.d("08150815", "0815");

            Location spotloc = new Location(recentlocation);
            spotloc.setLatitude(MainActivity.coinlist.get(i).latitude);
            spotloc.setLongitude(MainActivity.coinlist.get(i).longitude);
            float dist = recentlocation.distanceTo(spotloc);

            LatLng smallwood = new LatLng(MainActivity.coinlist.get(i).latitude, MainActivity.coinlist.get(i).longitude);  // target location
            MarkerOptions mo = new MarkerOptions();
            mo.position(smallwood);
            //mo.title(spot.name);
            int height = 50;
            int width = 50;
            BitmapDrawable bitmapdraw;
            if (MainActivity.coinlist.get(i).kind == 1) {
                bitmapdraw = (BitmapDrawable) thiscontext.getResources().getDrawable(R.drawable.jewels);
            }
            else if(MainActivity.coinlist.get(i).kind == 0) {
                bitmapdraw = (BitmapDrawable) thiscontext.getResources().getDrawable(R.drawable.icon_dollar);

            }
//            else if(MainActivity.coinlist.get(i).kind == 2) {
////                bitmapdraw = (BitmapDrawable) thiscontext.getResources().getDrawable(R.drawable.flower);
//                bitmapdraw = (BitmapDrawable) thiscontext.getResources().getDrawable(R.drawable.icon_dollar);
//            }
            else{
                bitmapdraw = (BitmapDrawable) thiscontext.getResources().getDrawable(R.drawable.gift);
            }
            Bitmap b = bitmapdraw.getBitmap();

            Bitmap smallMarker = Bitmap.createScaledBitmap(b, height, width, false);
            if(MainActivity.coinlist.get(i).kind == 2)
                smallMarker = Bitmap.createScaledBitmap(b, 80, 80, false);
            mo.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

            Marker markername = googleMap.addMarker(mo.snippet(Float.toString(dist)));
            if (MainActivity.coinlist.get(i).kind == 1) {
                markername.setTag("diamond");
            }
            else if (MainActivity.coinlist.get(i).kind == 0){
                markername.setTag("coin");
            }
            else if (MainActivity.coinlist.get(i).kind == 2){
//                markername.setTag("flower");
            }
            else{
                markername.setTag("gift");
            }

            Log.d("XDXDXDX", "value "+dist);

            if (dist > radius) {
                markername.setVisible(false);
            } else {
                markername.setVisible(true);
            }
            Log.d("0816", "0816");



        }

    }

    private static void drawphotosmap(){


//        for (int i = 0; i < MainActivity.photolist.size(); i++) {
//
//            LatLng sydney = new LatLng(MainActivity.photolist.get(i).latitude, MainActivity.photolist.get(i).longitude);
//            String userimage = MainActivity.photolist.get(i).profileimage;
//            Log.d("XDXDXDX", userimage);
//
//            //adding a marker on map with image from  drawable
//            googleMap.addMarker(new MarkerOptions()
//                    .position(sydney)
//                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(userimage))));
//        }


        for( ttt = 0 ; ttt < MainActivity.photolist.size() ; ttt++) {
            // get friends' location and name

            LatLng sydney = new LatLng(MainActivity.photolist.get(ttt).latitude, MainActivity.photolist.get(ttt).longitude);
            String userimage = MainActivity.photolist.get(ttt).name;


            // call library to draw
            String internetUrl = Setting.HTTPSSERVER + userimage + ".jpg";

            Glide.with(thiscontext).asBitmap()
                    .load(internetUrl)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .error(R.drawable.icon_manb)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            Log.d("checkcheckcheck", userimage);
                            googleMap.addMarker(new MarkerOptions().position(sydney).title(userimage).icon(BitmapDescriptorFactory.fromBitmap(bitmappros(resource))));
                        }
                    });
        }


    }

    private static void drawmonstermap(boolean move_camera_bool){


        googleMap.clear();
        if(move_camera_bool) {
            LatLng tempsydney = new LatLng(24.790053,  120.995991);

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(tempsydney));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(tempsydney).zoom(15).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

//        for( ttt = 0 ; ttt < MainActivity.monsterlist.size() ; ttt++) {
//            // get friends' location and name
//
//            LatLng sydney = new LatLng(MainActivity.monsterlist.get(ttt).latitude, MainActivity.monsterlist.get(ttt).longitude);
//            String userimage = MainActivity.monsterlist.get(ttt).name;
//            long hp = MainActivity.monsterlist.get(ttt).hp;
//
//            // call library to draw
//            String internetUrl = "https://sensinggo.org/eventicons/" + userimage + ".png";
////            String internetUrl = Setting.HTTPSSERVER + "sg0009" + ".jpg";
//
//            Glide.with(thiscontext).asBitmap()
//                    .load(internetUrl)
//                    .skipMemoryCache(true)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .error(R.drawable.icon_manb)
//                    .into(new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                            Log.d("checkcheckcheck", userimage);
//                            Marker markername = googleMap.addMarker(new MarkerOptions().position(sydney).title(userimage).icon(BitmapDescriptorFactory.fromBitmap(monster_bitmappros(resource))));
//                            markername.setTag(hp);
//
//                        }
//                    });
//        }

    }

    private static Bitmap bitmappros(Bitmap res){
        LayoutInflater inflater = (LayoutInflater) thiscontext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customMarkerView = inflater.inflate(R.layout.view_custom_marker, null);
        CircleImageView markerImageView = customMarkerView.findViewById(R.id.imageView);
        markerImageView.setImageBitmap(res);

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    private static Bitmap monster_bitmappros(Bitmap res){
        LayoutInflater inflater = (LayoutInflater) thiscontext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customMarkerView = inflater.inflate(R.layout.monster_action, null);
        ImageView markerImageView = customMarkerView.findViewById(R.id.imageView);
        markerImageView.setImageBitmap(res);

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }



    private boolean checkfake(Location location){
        boolean isMock = false;
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            isMock = location.isFromMockProvider();
        } else {
            isMock = !Settings.Secure.getString(thiscontext.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
        }
        return isMock;
    }



    private void autoDeleteCoins() {
        String coin_lats = "[";
        String coin_lngs = "[";
        int coin_count = 0;

        for (int i = 0; i < MainActivity.coinlist.size(); i++) {
            if(MainActivity.coinlist.get(i).state == 1) continue;
            if(MainActivity.coinlist.get(i).auto == 0) continue;

            Location spotloc = new Location(recentlocation);
            spotloc.setLatitude(MainActivity.coinlist.get(i).latitude);
            spotloc.setLongitude(MainActivity.coinlist.get(i).longitude);
            float dist = recentlocation.distanceTo(spotloc);

            if (dist < 200) {
                coin_lats = coin_lats + MainActivity.coinlist.get(i).latitude + ", ";
                coin_lngs = coin_lngs + MainActivity.coinlist.get(i).longitude + ", ";
                coin_count++;
            }
        }

        if (coin_count == 0) {
            return;
        }

        coin_lats = coin_lats.substring(0, coin_lats.length() - 2) + "]";
        coin_lngs = coin_lngs.substring(0, coin_lngs.length() - 2) + "]";
        Log.d("99449", coin_lats);
        Log.d("99449", coin_lngs);

        String Info = "username=" + UserConfig.myUserName +
                "&coin_lats=" + coin_lats +
                "&coin_lngs=" + coin_lngs;

        connectServer("POST", "/autoDeleteCoins", Info);
        getCoinsFromServer(recentlat, recentlng);
    }

    private void showAutoUI() {
        imageViewWhite.setVisibility(View.VISIBLE);
        textViewAutoPrompt.setVisibility(View.VISIBLE);
    }

    private void hideAutoUI() {
        imageViewWhite.setVisibility(View.INVISIBLE);
        textViewAutoPrompt.setVisibility(View.INVISIBLE);
    }

    private void showCoinUI() {
        //autoSwitch.setVisibility(View.VISIBLE); //temporally remove auto function
        autoSwitch.setChecked(false);

        imageViewCoin.setVisibility(View.VISIBLE);
        imageViewBackground.setVisibility(View.VISIBLE);
        textViewCoinNumber.setVisibility(View.VISIBLE);
        textViewCoinNumber.setText(String.valueOf(UserConfig.totalmoney.intValue()));



    }

    private void hideCoinUI() {
        autoSwitch.setVisibility(View.INVISIBLE);

        imageViewCoin.setVisibility(View.INVISIBLE);
        imageViewBackground.setVisibility(View.INVISIBLE);
        textViewCoinNumber.setVisibility(View.INVISIBLE);


    }

//    private void showMonsterUI() {
//        meichucoinsTV.setVisibility(View.VISIBLE);
//        meichucoinsIV.setVisibility(View.VISIBLE);
//        meichuimageIV.setVisibility(View.VISIBLE);
//        campIV.setVisibility(View.VISIBLE);
//        meichucoinsTV.setText(UserConfig.totalcoins);
//        if(UserConfig.choosecamp==1)
//            campIV.setImageResource(R.drawable.nctu);
//        else if(UserConfig.choosecamp==0){
//            campIV.setImageResource(R.drawable.nthu);
//        }
//    }

//    private void hideMonsterUI() {
//        meichucoinsTV.setVisibility(View.INVISIBLE);
//        meichucoinsIV.setVisibility(View.INVISIBLE);
//        meichuimageIV.setVisibility(View.INVISIBLE);
//        campIV.setVisibility(View.INVISIBLE);
//    }

    public static void updateCoinNumber() {
        textViewCoinNumber.setText(String.valueOf(UserConfig.totalmoney.intValue()));
    }

//    public static void updateMeichuNumber() {
//        meichucoinsTV.setText(UserConfig.totalcoins);
//        meichucoinsAvatarTV.setText(UserConfig.totalcoins);
//    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider,
                                int status, Bundle extras) {
    }

    @Override
    public void onAttach(Activity activity) {
        thiscontext = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void  onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate( R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.sound_action);
        if (soundeffect) {
            item.setIcon(ContextCompat.getDrawable(getActivity().getBaseContext(), R.drawable.ic_speaker));
        }
        else {
            item.setIcon(ContextCompat.getDrawable(getActivity().getBaseContext(), R.drawable.ic_speaker_off));
        }
        Log.d("qqq","qqqqqqq");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.sound_action) {
            Log.d("qqq","aafafa");
            if(soundeffect){
                item.setIcon(ContextCompat.getDrawable(getActivity().getBaseContext(), R.drawable.ic_speaker_off));
                soundeffect = !soundeffect;
            }
            else{
                item.setIcon(ContextCompat.getDrawable(getActivity().getBaseContext(), R.drawable.ic_speaker));
                soundeffect = !soundeffect;
            }

            // edit colors menu
        }

        return true;
    }


    private static void testdrawmap(){

        LatLng sydney = new LatLng(recentlat, recentlng);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        LatLng smallwood = new LatLng(recentlat, recentlng);  // target location
        MarkerOptions mo = new MarkerOptions();
        mo.position(smallwood);
        mo.title("gift");
        int height = 150;
        int width = 150;
        BitmapDrawable bitmapdraw;

        bitmapdraw = (BitmapDrawable) thiscontext.getResources().getDrawable(R.drawable.gift);

        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        mo.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

        Marker markername = googleMap.addMarker(mo.snippet("100"));






    }
    @SuppressLint("ClickableViewAccessibility")
    private void pressmonster(String name, long hp, Double lat, Double lng) {
        original_coin = 0;

        final View actionView = LayoutInflater.from(getActivity()).inflate(R.layout.monster_dialog, null);
        TextView hpTV = actionView.findViewById(R.id.hpTV);
        TextView modifiedhpTV = actionView.findViewById(R.id.modifiedhpTV);
        ProgressBar hpbar = actionView.findViewById(R.id.hpbar);
        ProgressBar modifiedhpbar = actionView.findViewById(R.id.modifiedhpbar);
        GifImageView monsterIV = actionView.findViewById(R.id.monsterIV);
        ImageView addIV = actionView.findViewById(R.id.addIV);
        ImageView substractIV = actionView.findViewById(R.id.substractIV);
        TextView titleTV = actionView.findViewById(R.id.titleTV);
        TextView pay_coinTV = actionView.findViewById(R.id.pay_coinTV);

        String origin = "原血量 ", modified = "更動血量 ";
        String temp_action_name = "";
        int max_hp = 10000;

        hpbar.setProgress((int)hp);
        hpTV.setText(origin + hp +"/"+max_hp);

        modifiedhpbar.setProgress((int)hp);
        modifiedhpTV.setText(modified + hp +"/"+max_hp);
        blood = (int)hp;




        // same camp
        if((UserConfig.choosecamp == 1 && name.compareTo("fox")==0) || (UserConfig.choosecamp == 0 && name.compareTo("panda")==0)){
            temp_action_name = "救救我QQ";
            action = "only_up";
        }
        else{
            temp_action_name = "打我阿～笨蛋";
            action = "only_down";
        }
        titleTV.setText(temp_action_name);

        String internetUrl = Setting.HTTPSSERVER + name + ".gif";
        Glide.with(getActivity())
                .load(internetUrl)
                .error(R.drawable.icon_manb) //load失敗的Drawable
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(monsterIV);


        final Handler add_handler = new Handler();
        Runnable add_LongPressed = new Runnable() {
            public void run() {
                if(blood < max_hp && !((action.compareTo("only_down")==0) && blood == hp)) {
                    blood += UserConfig.proportion;
                    original_coin++;
                }
                modifiedhpbar.setProgress(blood);
                modifiedhpTV.setText(modified + blood+"/"+max_hp);
                pay_coinTV.setText(String.valueOf(Math.abs(original_coin)));
                addIV.setImageResource(R.drawable.add_after);
            }
        };

        final Handler substract_handler = new Handler();
        Runnable substract_LongPressed = new Runnable() {
            public void run() {
                if(blood > 0 && !((action.compareTo("only_up")==0) && blood == hp)) {
                    blood -= UserConfig.proportion;
                    original_coin--;
                }
                modifiedhpbar.setProgress(blood);
                modifiedhpTV.setText(modified + blood+"/"+max_hp);
                pay_coinTV.setText(String.valueOf(Math.abs(original_coin)));
                substractIV.setImageResource(R.drawable.substract_after);
            }
        };


        addIV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if(blood < max_hp && !((action.compareTo("only_down")==0) && blood == hp)) {
                            blood += UserConfig.proportion;
                            original_coin++;
                        }
                        modifiedhpbar.setProgress(blood);
                        modifiedhpTV.setText(modified + blood+"/"+max_hp);
                        pay_coinTV.setText(String.valueOf(Math.abs(original_coin)));
                        addIV.setImageResource(R.drawable.add_after);
                        break;
                    }
                    case MotionEvent.ACTION_UP:{
                        add_handler.removeCallbacks(add_LongPressed);
                        addIV.setImageResource(R.drawable.add_before);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
//                        add_handler.removeCallbacks(mLongPressed);
                        add_handler.postDelayed(add_LongPressed, 100);
                        break;
                    }

                }
                return true;
            }
        });


        substractIV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if(blood > 0 && !((action.compareTo("only_up")==0) && blood == hp)) {
                            blood -= UserConfig.proportion;
                            original_coin--;
                        }
                        modifiedhpbar.setProgress(blood);
                        modifiedhpTV.setText(modified + blood+"/"+max_hp);
                        pay_coinTV.setText(String.valueOf(Math.abs(original_coin)));
                        substractIV.setImageResource(R.drawable.substract_after);
                        break;
                    }
                    case MotionEvent.ACTION_UP:{
                        substract_handler.removeCallbacks(substract_LongPressed);
                        substractIV.setImageResource(R.drawable.substract_before);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
//                        add_handler.removeCallbacks(mLongPressed);
                        substract_handler.postDelayed(substract_LongPressed, 100);
                        break;
                    }
                }
                return true;
            }
        });

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(actionView)
                .setPositiveButton(getString(R.string.ok), null)
                .show();

        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Info = "username=" + UserConfig.myUserName +
                        "&coins_cnt=" + Math.abs(original_coin) +
                        "&monster_kind=" + name +
                        "&monster_lat=" + lat +
                        "&monster_lng=" + lng;

                connectServer("POST", "/monsterAction", Info);


                alertDialog.dismiss();
                original_coin = 0;
            }
        });


    }

    public static void Animation(){
//        RotateAnimation anim = new RotateAnimation(0f, 350f, 15f, 15f);
//        anim.setInterpolator(new LinearInterpolator());
//        anim.setRepeatCount(Animation.INFINITE);
//        anim.setDuration(700);
//        anim.setRepeatCount(1);
//
//
//        // Start animating the image
//        testIV.startAnimation(anim);
//
//        // Later.. stop the animation
//        //testIV.setAnimation(null);



        if(!mother) {
            giftIV.setVisibility(View.VISIBLE);
            giftIV.setImageResource(R.drawable.closed_gift);
            giftIV.startAnimation(mother_animation);
            mother = !mother;
        }
        else{
            updateCoinNumber();
            giftIV.setImageResource(R.drawable.opened_gift);
            mother = !mother;
            AlertDialog alertDialog = new AlertDialog.Builder(thiscontext)
                    .setMessage("恭喜您獲得 "+UserConfig.train_gift+" 個金幣")
                    .setPositiveButton("確定", null)
                    .show();

            Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    giftIV.clearAnimation();
                    giftIV.setVisibility(View.INVISIBLE);
                    alertDialog.dismiss();
                }
            });


        }
    }

}
