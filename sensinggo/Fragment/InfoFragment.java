package edu.nctu.wirelab.sensinggo.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Message;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.nctu.wirelab.sensinggo.Connect.HttpsConnection;
import edu.nctu.wirelab.sensinggo.File.JsonParser;
import edu.nctu.wirelab.sensinggo.Measurement.Location;
import edu.nctu.wirelab.sensinggo.MainActivity;
import edu.nctu.wirelab.sensinggo.Measurement.SensorList;
import edu.nctu.wirelab.sensinggo.Measurement.SignalStrength;
import edu.nctu.wirelab.sensinggo.R;
import edu.nctu.wirelab.sensinggo.RunIntentService;
import edu.nctu.wirelab.sensinggo.UserConfig;
import edu.nctu.wirelab.sensinggo.donate_coin_to_ticket_card_view;
import edu.nctu.wirelab.sensinggo.ticket_card_view;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static androidx.recyclerview.widget.LinearLayoutManager.*;
import static edu.nctu.wirelab.sensinggo.MainActivity.ch;
import static edu.nctu.wirelab.sensinggo.MainActivity.ch_type;
import static edu.nctu.wirelab.sensinggo.MainActivity.coins;
import static edu.nctu.wirelab.sensinggo.MainActivity.en;
import static edu.nctu.wirelab.sensinggo.MainActivity.facilityURL;
import static edu.nctu.wirelab.sensinggo.MainActivity.logoURL;
import static edu.nctu.wirelab.sensinggo.MainActivity.mContext;
//import static edu.nctu.wirelab.sensinggo.MainActivity.memberlist;
import static edu.nctu.wirelab.sensinggo.MainActivity.ticketlist;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoFragment extends Fragment {
    private Button button_elder, button_animal,button_woman_child,
            button_people, button_disease, button_random;
    public static Button button_coin_to_ticket;

    private SeekBar sb_normal;
    private ImageView plus, minus;
    private float num_of_coin_in_the_pool;
    private TextView txt_cur;

    private float coin_num;
    private float donate_coin_total_amount;
    private ImageView coin, green_coin, pool;
    private float xDelta;
    private float yDelta;
    private String[] wish = {"                         A","                         B"};
    AlertDialog alertDialog;





    private class coinAnimationListener implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Animation scaleAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.fountain_scale_anim);
            pool.startAnimation(scaleAnim);
//            ScaleAnimation fountain_scale = new ScaleAnimation(1f,1.2f,1f,1.2f,pool.getWidth()/2 ,pool.getWidth()/2);
//            fountain_scale.setRepeatCount(2);
////            fountain_scale.setStartTime(2);
//            fountain_scale.setDuration(500);
//            pool.startAnimation(fountain_scale);
            coin.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            Animation scaleAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.fountain_scale_anim);
            pool.startAnimation(scaleAnim);
            coin.setVisibility(View.INVISIBLE);
        }
    }

    private class green_coinAnimationListener implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Animation scaleAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.fountain_scale_anim);
            pool.startAnimation(scaleAnim);
//            ScaleAnimation fountain_scale = new ScaleAnimation(1f,1.2f,1f,1.2f,pool.getWidth()/2 ,pool.getWidth()/2);
//            fountain_scale.setRepeatCount(2);
////            fountain_scale.setStartTime(2);
//            fountain_scale.setDuration(500);
//            pool.startAnimation(fountain_scale);
            green_coin.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            Animation scaleAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.fountain_scale_anim);
            pool.startAnimation(scaleAnim);
            green_coin.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        logoURL.clear();
        logoURL.add("https://sensinggo.org/charity_icons/animal.png");
        logoURL.add("https://sensinggo.org/charity_icons/elder.png");
        logoURL.add("https://sensinggo.org/charity_icons/people.png");
        logoURL.add("https://sensinggo.org/charity_icons/woman_child.png");

        ch_type.add("動物");
        ch_type.add("老人");
        ch_type.add("弱勢");
        ch_type.add("婦幼");
        String Info = "username=" + UserConfig.myUserName ;
        Log.i("onCreate", String.valueOf(logoURL.size()));
        connectServer("POST", "/getAllUsersDonate","");
        connectServer("POST", "/getTicketsInfo", Info);
        connectServer("POST", "/getRequiredDonatedCoins", Info);

        coin =  view.findViewById(R.id.poolCoin);
        coin.setVisibility(View.INVISIBLE);
        button_coin_to_ticket = view.findViewById(R.id.coin_to_ticket);


        button_coin_to_ticket.setEnabled(false);

        pool =  view.findViewById(R.id.wish_pool);
//            pool.getLocationInWindow();
        coin_num = UserConfig.totalmoney.intValue();
//            Drawable drawable = ContextCompat.getDrawable(getActivity(),R.drawable.people);
//            drawable.setBounds(0, 0, (int)(drawable.getIntrinsicWidth()*0.5),
//                    (int)(drawable.getIntrinsicHeight()*0.5));
//            ScaleDrawable sd = new ScaleDrawable(drawable, 0, 10, 10);
        button_elder = view.findViewById(R.id.elder);
//            button_a.setCompoundDrawables(sd.getDrawable(), null, null, null);
        //        coin.setOnTouchListener(onTouchListener());
        button_elder.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String place = "elder";
                dialogShow2(v, place);

            }
        });

        button_animal = view.findViewById(R.id.animal);
        //        coin.setOnTouchListener(onTouchListener());
        button_animal.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String place = "animal";
                dialogShow2(v, place);

            }
        });
        button_woman_child = view.findViewById(R.id.woman_child);
        button_woman_child.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String place = "woman_child";
                dialogShow2(v, place);
            }



        });
        button_people = view.findViewById(R.id.people);
        button_people.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String place = "people";
                dialogShow2(v, place);

            }
        });
//        button_disease = view.findViewById(R.id.disease);
//        button_disease.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String place = "disease";
//                dialogShow2(v, place);
//            }
//        });
//        button_random= view.findViewById(R.id.random);
//        button_random.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String place = "random";
//                dialogShow2(v, place);
//            }
//        });
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                .getLayoutParams();
        layoutParams.bottomMargin = 0;

        return view;

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        String Info = "username=" + UserConfig.myUserName ;
        Log.i("onCreate", "onCreate");

        connectServer("POST", "/getTicketsInfo", Info);

        connectServer("POST", "/getRequiredDonatedCoins", Info);
    }
    @Override
    public void  onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate( R.menu.menu_info, menu);
        MenuItem item = menu.findItem(R.id.ticket_red);
        item.setIcon(ContextCompat.getDrawable(getActivity().getBaseContext(), R.drawable.ticket_red));

        Log.d("popo","qqqqqqq");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ticket_red) {
            Log.i("jjj","hahaha");
            String TicketsInfo = "username=" + UserConfig.myUserName;
            connectServer("POST", "/getTicketsInfo", TicketsInfo);
            dialog_coin_to_ticket();

        }
        return true;
    }
    public void dialog_coin_to_ticket() {

//        Log.i("View view", String.valueOf(view));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v = inflater.inflate(R.layout.donate_coin_to_ticket, null);
        TextView dialog_title = v.findViewById(R.id.dialog_title);

        List<donate_coin_to_ticket_card_view> memberList = new ArrayList<>();
//        https://sensinggo.org/charity_icons/elder.png  woman_child  people  animal
        for(int i=MainActivity.ticketlist.size()-1; i>=0;i--){
            if(i==MainActivity.ticketlist.size()-1){
                memberList.add(new donate_coin_to_ticket_card_view("第"+MainActivity.ticketlist.get(i).phase+"期", R.drawable.ticket_red, "X"+MainActivity.ticketlist.get(i).number));
            }
            else{
                memberList.add(new donate_coin_to_ticket_card_view("第"+MainActivity.ticketlist.get(i).phase+"期", R.drawable.ticket, "X"+MainActivity.ticketlist.get(i).number));
            }

        }
//        memberList.add(new donate_coin_to_ticket_card_view(1, R.drawable.new_sg, "白沙屯海灘1"));
//        memberList.add(new donate_coin_to_ticket_card_view(2, R.drawable.new_sg, "白沙屯海灘2"));

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.ticket_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),VERTICAL, false));
// MemberAdapter 會在步驟7建立
        recyclerView.setAdapter(new MemberAdapter(getActivity(), memberList));

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
//    public static void parseFacilityJSON(String data){
//
//        try{
//            memberlist.clear();
//            JSONObject facilityjsonObject = new JSONObject(data);
////            JSONObject ticketjsonObject = new JSONObject("{'1': 0, '2': 0, '3': 0, '4': 0, '5': 0, '6': 0, '7': 0, '8': 0, '9': 0, '10': 11}");
//            Log.i("facilityjsonObject",facilityjsonObject.toString());
//            Log.i("facilityjsonObject", String.valueOf(facilityjsonObject.length()));
//            Iterator<String> iter = facilityjsonObject.keys(); //This should be the iterator you want.
//            int coinslistLength = 0;
//            while(iter.hasNext()){
//                String key = iter.next();
//                Log.i("facilityjsonObject_key", key);
////                int value = facilityjsonObject.getInt(key);
////                Log.i("facilityjsonObject", String.valueOf(value));
////                coins.add(facilityjsonObject.getInt(key));
//                if(key.equals("coins")){
//                    JSONObject coinsObject = facilityjsonObject.getJSONObject(key);
//                    Log.i("facilityjsonObject", String.valueOf(coinsObject));
//                    Iterator<String> coinsObjectiter = coinsObject.keys(); //This should be the iterator you want.
//                    while(coinsObjectiter.hasNext()) {
//                        String coinskey = coinsObjectiter.next();
//                        Log.i("facilityjsonObject_key", coinskey);
//                        int value = coinsObject.getInt(coinskey);
//                        Log.i("facilityjsonObject", String.valueOf(value));
////                        coins.add(facilityjsonObject.getInt(key));
//                    }
//                }
//                else if(key.equals("ch")){
//                    JSONObject chObject = facilityjsonObject.getJSONObject(key);
//                    Log.i("facilityjsonObject", String.valueOf(ch));
//                    Iterator<String> chObjectiter = chObject.keys();
//                    while(chObjectiter.hasNext()) {
//                        String chkey = chObjectiter.next();
//                        Log.i("facilityjsonObject_key", chkey);
//                        String value = chObject.getString(chkey);
//                        Log.i("facilityjsonObject", String.valueOf(value));
////                        coins.add(facilityjsonObject.getInt(key));
//                    }
//                }
//                else if(key.equals("en")){
//                    JSONObject enObject = facilityjsonObject.getJSONObject(key);
//                    Log.i("facilityjsonObject", String.valueOf(en));
//                    Iterator<String> enObjectiter = enObject.keys();
//                    while(enObjectiter.hasNext()) {
//                        String enkey = enObjectiter.next();
//                        Log.i("facilityjsonObject_key", enkey);
//                        int value = enObject.getInt(enkey);
//                        Log.i("facilityjsonObject", String.valueOf(value));
////                        coins.add(facilityjsonObject.getInt(key));
//                    }
//                }
//                else{
//                    Log.i("facilityjsonObject_key", "error");
//                }
//
//            }
//
//        }catch(Exception e){
//            Log.i("gettrainjsonError", "Error parsing data" +e.getMessage());
//
//        }
//
//    }
    public static void parseFacilityCoinsJSON(String data){

        try{
            coins.clear();
            JSONObject coinsjsonObject = new JSONObject(data);
//            JSONObject ticketjsonObject = new JSONObject("{'1': 0, '2': 0, '3': 0, '4': 0, '5': 0, '6': 0, '7': 0, '8': 0, '9': 0, '10': 11}");
            Log.i("getticketjson",coinsjsonObject.toString());
            Log.i("getticketjson", String.valueOf(coinsjsonObject.length()));
            Iterator<String> iter = coinsjsonObject.keys(); //This should be the iterator you want.
            int coinslistLength = 0;
            while(iter.hasNext()){
                String key = iter.next();
                Log.i("ggticketlist", key);
                int value = coinsjsonObject.getInt(key);
                Log.i("ggticketlist", String.valueOf(value));
                coins.add(coinsjsonObject.getInt(key));

            }

        }catch(Exception e){
            Log.i("gettrainjsonError", "Error parsing data" +e.getMessage());

        }

    }
    public static void parseFacilityChJSON(String data){

        try{
            ch.clear();
            JSONObject chjsonObject = new JSONObject(data);
//            JSONObject ticketjsonObject = new JSONObject("{'1': 0, '2': 0, '3': 0, '4': 0, '5': 0, '6': 0, '7': 0, '8': 0, '9': 0, '10': 11}");
            Log.i("getticketjson",chjsonObject.toString());
            Log.i("getticketjson", String.valueOf(chjsonObject.length()));
            Iterator<String> iter = chjsonObject.keys(); //This should be the iterator you want.
            int coinslistLength = 0;
            while(iter.hasNext()){
                String key = iter.next();
                Log.i("chggticketlist", key);
                String value = chjsonObject.getString(key);
                Log.i("chggticketlist", String.valueOf(value));
                ch.add(chjsonObject.getString(key));

            }

        }catch(Exception e){
            Log.i("gettrainjsonError", "Error parsing data" +e.getMessage());

        }

    }
    public static void parseFacilityEnJSON(String data){

        try{
            en.clear();
            JSONObject enjsonObject = new JSONObject(data);
//            JSONObject ticketjsonObject = new JSONObject("{'1': 0, '2': 0, '3': 0, '4': 0, '5': 0, '6': 0, '7': 0, '8': 0, '9': 0, '10': 11}");
            Log.i("enjsonObject",enjsonObject.toString());
            Log.i("enjsonObject", String.valueOf(enjsonObject.length()));
            Iterator<String> iter = enjsonObject.keys(); //This should be the iterator you want.
            int coinslistLength = 0;
            while(iter.hasNext()){
                String key = iter.next();
                Log.i("enjsonObject", key);
                String value = enjsonObject.getString(key);
                Log.i("enjsonObject", String.valueOf(value));
                en.add(enjsonObject.getString(key));

            }

        }catch(Exception e){
            Log.i("gettrainjsonError", "Error parsing data" +e.getMessage());

        }

    }
    public static void parseFacilityURLJSON(String data){

        try{
            facilityURL.clear();
            JSONObject enjsonObject = new JSONObject(data);
//            JSONObject ticketjsonObject = new JSONObject("{'1': 0, '2': 0, '3': 0, '4': 0, '5': 0, '6': 0, '7': 0, '8': 0, '9': 0, '10': 11}");
            Log.i("facilityURL",enjsonObject.toString());
            Log.i("facilityURL", String.valueOf(enjsonObject.length()));
            Iterator<String> iter = enjsonObject.keys(); //This should be the iterator you want.
            int coinslistLength = 0;
            while(iter.hasNext()){
                String key = iter.next();
                Log.i("facilityURL", key);
                String value = enjsonObject.getString(key);
                Log.i("facilityURL", String.valueOf(value));
                facilityURL.add(enjsonObject.getString(key));

            }

        }catch(Exception e){
            Log.i("gettrainjsonError", "Error parsing data" +e.getMessage());

        }

    }

    public static void parseTicketInfoJSON(String data){

        try{

            ticketlist.clear();
            JSONObject ticketjsonObject = new JSONObject(data);
//            JSONObject ticketjsonObject = new JSONObject("{'1': 0, '2': 0, '3': 0, '4': 0, '5': 0, '6': 0, '7': 0, '8': 0, '9': 0, '10': 11}");
            Log.i("getticketjson",ticketjsonObject.toString());
            Log.i("getticketjson", String.valueOf(ticketjsonObject.length()));
            Iterator<String> iter = ticketjsonObject.keys(); //This should be the iterator you want.
            int ticketlistLength = 0;
            while(iter.hasNext()){
                String key = iter.next();
                Log.i("ticketlist", key);
                int value = ticketjsonObject.getInt(key);
                Log.i("ticketlist", String.valueOf(value));
                ticketlist.add(new MainActivity.TicketContent(Integer.parseInt(key),value));
                Log.i("ticketphase", String.valueOf(ticketlist.get(ticketlistLength).phase));
                ticketlistLength++;
            }
//            for(int i=1; i < ticketjsonObject.length(); i++) {
//                Log.i("getticketjson", "\""+i+"\"");
////                MainActivity.ticketlist.add(new MainActivity.TicketContent(i,ticketjsonObject.getInt("\""+i+"\"")));
////                Log.i("ticketlist", String.valueOf(ticketjsonObject.getInt("'"+i+"'")));
//
////                Log.i("ticketlist", String.valueOf(MainActivity.ticketlist.get(i).phase));
//            }
//                    //                        , jsonChildNode.getInt("collected")));
//            JSONArray getgpsarray = getcoinjson.getJSONArray("gps");
//            int jsonArrLength = gettrainjsonArray.length();
////            Log.i("lentrainjson", String.valueOf(jsonArrLength));
////            //public static ArrayList<CoinContent> coinlist = new ArrayList<CoinContent>();
//            for(int i=0; i < jsonArrLength; i++) {
//                //Log.d("check", "check" );
//                JSONObject jsonChildNode = gettrainjsonArray.getJSONObject(i);
//                MainActivity.trainlist.add(new MainActivity.TrainContent(jsonChildNode.getInt("train_id"),jsonChildNode.getDouble("lat"), jsonChildNode.getDouble("lng")
//                        , jsonChildNode.getInt("collected")));
//
//
//            }
//            Log.d("093030", "value "+ MainActivity.coinlist.size());
//            drawcoinsmap();
//            setTrainStamp();


        }catch(Exception e){
            Log.i("gettrainjsonError", "Error parsing data" +e.getMessage());

        }

    }
    @SuppressLint("ClickableViewAccessibility")
    private void dialogShow2(View view, String place) {

        Log.i("View view", String.valueOf(view));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v = inflater.inflate(R.layout.donate_alertdialog, null);
        TextView dialog_title = v.findViewById(R.id.dialog_title);
        coin_num = UserConfig.totalmoney.intValue();
        Log.i("coin_num", String.valueOf(coin_num));
        dialog_title.setText("總共有"+(int)coin_num+"枚金幣\n要投入0到許願池");
//            TextView content = (TextView) v.findViewById("haha");
        Button btn_sure = (Button) v.findViewById(R.id.dialog_btn_sure);
        Button btn_cancel = (Button) v.findViewById(R.id.dialog_btn_cancel);
        sb_normal = (SeekBar) v.findViewById(R.id.sb_normal);
        plus = (ImageView) v.findViewById(R.id.plus);
        plus.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

//                        donate_coin_total_amount = donate_coin_total_amount+1;
//                        sb_normal.setProgress(sb_normal.getProgress() + 1);
//                        Log.i("inin","plustonto");
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 100);
                        Log.i("inin","down");
//                        break;
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        Log.i("inin","upup");
//                        break;
                        return true; // if you want to handle the touch event
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override public void run() {
                    Log.i("inin","runrun");
                    num_of_coin_in_the_pool = num_of_coin_in_the_pool+1;
                    sb_normal.setProgress(sb_normal.getProgress() + 1);
//                    System.out.println("Performing action...");
                    mHandler.postDelayed(this, 100);
                }
            };


        });
        minus = (ImageView) v.findViewById(R.id.minus);
        minus.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

//                        donate_coin_total_amount = donate_coin_total_amount+1;
//                        sb_normal.setProgress(sb_normal.getProgress() + 1);
//                        Log.i("inin","plustonto");
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 100);
                        Log.i("inin","down");
//                        break;
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        Log.i("inin","upup");
//                        break;
                        return true; // if you want to handle the touch event
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override public void run() {
                    Log.i("inin","runrun");
                    num_of_coin_in_the_pool = num_of_coin_in_the_pool-1;
                    sb_normal.setProgress(sb_normal.getProgress() - 1);
//                    System.out.println("Performing action...");
                    mHandler.postDelayed(this, 100);
                }
            };


        });
//        txt_cur = (TextView) v.findViewById(R.id.txt_cur);

        if(coin_num<250){
            sb_normal.setMax((int) coin_num);
        }
        else{
            sb_normal.setMax(250);
        }
        sb_normal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                txt_cur.setText("当前进度值:" + progress + "  / 100 ");
                dialog_title.setText("總共有"+(int)coin_num+"枚金幣\n要投入"+progress+"到許願池");
                num_of_coin_in_the_pool = progress;
                Log.i("ttt","progress");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i("ttt","触碰SeekBar");

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("ttt","放开SeekBar");

            }
        });
        //builer.setView(v);//这里如果使用builer.setView(v)，自定义布局只会覆盖title和button之间的那部分
        final Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        dialog.getWindow().setContentView(v);//自定义布局应该在这里添加，要在dialog.show()的后面
        //dialog.getWindow().setGravity(Gravity.CENTER);//可以设置显示的位置
        btn_sure.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
//                Toast.makeText(getActivity(), "ok").show();
                Log.i("ttt","ok");

                try {
                    coin.setVisibility(View.VISIBLE);
                    TranslateAnimation coin_move = new TranslateAnimation(0.0f,
                            0f, 0.0f, 280.0f);
//

                    coin_move.setDuration(100);
//                    donate_coin_total_amount = num_of_coin_in_the_pool;

                    String Info = "username=" + UserConfig.myUserName +
                            "&place=" + place +
                            "&coin=" + num_of_coin_in_the_pool ;
                    Log.i("elder_click", String.valueOf(num_of_coin_in_the_pool));

                    connectServer("POST", "/donateCoin", Info);
                    String Info_current_coin = "username=" + UserConfig.myUserName;
                    connectServer("POST", "/getRequiredDonatedCoins", Info_current_coin);
                    String TicketsInfo = "username=" + UserConfig.myUserName;
                    connectServer("POST", "/getTicketsInfo", TicketsInfo);
                    Log.i("coin:",String.valueOf(num_of_coin_in_the_pool));
                    if(num_of_coin_in_the_pool>8){
                        coin_move.setRepeatCount(7);
                        coin_move.setAnimationListener((Animation.AnimationListener)
                                new coinAnimationListener());
                        coin.startAnimation(coin_move);
                    }
                    else if(num_of_coin_in_the_pool==0){
                        //no action
                        coin.setVisibility(View.INVISIBLE);
                    }
                    else{
                        coin_move.setRepeatCount((int) num_of_coin_in_the_pool-1);
                        coin_move.setAnimationListener((Animation.AnimationListener)
                                new coinAnimationListener());
                        coin.startAnimation(coin_move);
                    }
                    num_of_coin_in_the_pool=0;



                } catch (Exception e) {
                    e.printStackTrace();
//                                                                   donate_coin_total_amount =
//                                                                           Integer.parseInt(edit_donate_coin_amount.getText().toString());

                    Log.i("coin_except:",String.valueOf(num_of_coin_in_the_pool));
                }
            }

        });

        btn_cancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                String TicketsInfo = "username=" + UserConfig.myUserName;
                connectServer("POST", "/getTicketsInfo", TicketsInfo);
                Log.i("ttt","no");
//                Toast.makeText(getActivity(), "no").show();
            }

        });
    }

    public static void is_get_ticket_dialog(){



        AlertDialog.Builder congratulations_Dialog =
                new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);


        final View v =  inflater.inflate(R.layout.info_ticket_congratulations_dialog, null);
        congratulations_Dialog.setView(v);
        Button OK = (Button)v.findViewById(R.id.button_ok);

//        congratulations_Dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface congratulations_Dialog, int id) {
//                congratulations_Dialog.dismiss();
//           }
//        });
        final AlertDialog dialog = congratulations_Dialog.create();
//        final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
//        positiveButtonLL.gravity = Gravity.CENTER;
//        positiveButton.setLayoutParams(positiveButtonLL);
        OK.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Log.i("ttt","no");
//                Toast.makeText(getActivity(), "no").show();
            }

        });
        dialog.show();
    }
    private void connectServer(String method, String path, String info){
        Log.d("coinInfo", info);
        Log.d("coinPath", path);
        HttpsConnection httpsConnection = new HttpsConnection(getActivity());
        httpsConnection.setActivity(getActivity());
        httpsConnection.setFragment(this);
        httpsConnection.setMethod(method, info);
        httpsConnection.execute(path);

    }
    @Override
    public void onDestroy() {

        super.onDestroy();
    }



    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause(){
        super.onPause();

    }



}
class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
    private Context context;
    private List<donate_coin_to_ticket_card_view> memberList;

    MemberAdapter(Context context, List<donate_coin_to_ticket_card_view> memberList) {
        this.context = context;
        this.memberList = memberList;
    }

    @Override
    public MemberAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.donate_coin_to_ticket_card_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MemberAdapter.ViewHolder holder, int position) {
        final donate_coin_to_ticket_card_view member = memberList.get(position);
        holder.imageId.setImageResource(member.getImage());
        holder.textId.setText(String.valueOf(member.getPhase()));
        holder.textName.setText(String.valueOf(member.getNumber()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ImageView imageView = new ImageView(context);
//                imageView.setImageResource(member.getImage());
//                Toast toast = new Toast(context);
//                toast.setView(imageView);
//                toast.setDuration(Toast.LENGTH_SHORT);
//                toast.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    //Adapter 需要一個 ViewHolder，只要實作它的 constructor 就好，保存起來的view會放在itemView裡面
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageId;
        TextView textId, textName;
        ViewHolder(View itemView) {
            super(itemView);
            imageId = (ImageView) itemView.findViewById(R.id.imageId);
            textId = (TextView) itemView.findViewById(R.id.textId);
            textName = (TextView) itemView.findViewById(R.id.textName);
        }
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
        return new ViewHolder(view);
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
//                ImageView imageView = new ImageView(context);
//                imageView.setImageResource(member.getImage());
//                Toast toast = new Toast(context);
//                toast.setView(imageView);
//                toast.setDuration(Toast.LENGTH_SHORT);
//                toast.show();
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
