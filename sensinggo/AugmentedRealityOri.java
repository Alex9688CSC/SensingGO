package edu.nctu.wirelab.sensinggo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AugmentedRealityOri extends Activity implements LocationListener {

    SurfaceHolder sh;
    TextView spotinfo;
    LocationManager lmgr;
    Location userLocation;
    Camera camera;
    float[] values = new float[3];
    boolean blocation = false;
    private ImageView imageView;
    private Double coinlat;
    private Double coinlng;
    private boolean hasdeleted = false;
    private int deletecoin_i;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_augmented_reality);
        imageView = (ImageView) findViewById(R.id.image_money);

        spotinfo = new TextView(this);
        spotinfo.setTextSize((float)24.0);
        spotinfo.setTextColor(Color.parseColor("#FF4081"));

        //spotinfo.setText("Total money: " + UserConfig.getTotalMoney() +"\n");
        spotinfo.setText(getString(R.string.totalmoney) + UserConfig.totalmoney +"\n");


        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        addContentView(spotinfo, params);

        SurfaceView sv = (SurfaceView)findViewById(R.id.sv);
        sh = sv.getHolder();
        sh.addCallback(new MySHCallback());

        lmgr = (LocationManager)getSystemService(LOCATION_SERVICE);
        lmgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this); // 0ms, 0m

        final AlertDialog.Builder GetCoinDialog = new AlertDialog.Builder(this);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageView.getDrawable() != null) {
                    GetCoinDialog.setMessage(getString(R.string.getcoin))
                            .setIcon(R.mipmap.ic_launcher)
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    MainActivity.coinlist.clear();
                                    imageView.setImageDrawable(null);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("coodinatex:", "values " + coinlat);
                                            Post_DeletMessage(coinlat, coinlng);
//                                            MainActivity.getTotalMoneyFromServer();
//                                            MainActivity.getCoinsFromServer();
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

            }

        });

        //spotlist.add(new Spot(24.7872268, 120.9969887, "Shine Mood NCTU"));
        Log.d("0809","on Create");
        //update();

        /*if (!blocation) { // show no gps msg
            Log.d("XXX", "show no gps at beginning");
            spotinfo.setText("No GPS");
        }*/


    }

    class MySHCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            camera = Camera.open();

            if (camera == null) {
                finish();
                return;
            }

            try  {
                camera.setPreviewDisplay(holder);
            } catch (Exception e) {
                finish();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceholder) {
            if (camera == null) return;
            camera.stopPreview();
            camera.release();
            camera = null;
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceholder,
                                   int format, int w, int h) {
            camera.startPreview();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        //update();

        Log.d("0809", "onresume ar " + UserConfig.spotlist.size());
//
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        lmgr.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 1000, 1, this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        lmgr.removeUpdates(this);lmgr.removeUpdates(this);
        //spotinfo.setText(moneyInfo+ "");
    }


    @Override
    public void onLocationChanged(Location location) {
        if (location == null) return;
        //userLocation = new Location(location);
        //blocation = true;
//        detectCoin(location);
    }

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

//    private boolean showMoney() {
//        Random ran = new Random();
//        int ranNum = ran.nextInt(10);
//        if (ranNum<3) {
//            return false;
//        }
//        else
//            return true;
//    }


    public class Spot {
        Spot(double latitude, double longitude, String name) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.name = name;
        }
        Spot(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
        public double latitude;
        public double longitude;
        String name;
    }

//    void update() {
//        String info = "";
//
//        if (UserConfig.firstMoney && blocation) { // the first time to get the money
//            imageView.setImageResource(R.drawable.img01);
//
//            UserConfig.firstMoney = false;
//
//            imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    imageView.setImageDrawable(null);
//                    UserConfig.addMoney(1);
//                    UserConfig.setFirstMoney(false);
//                    UserConfig.saveConfigTo(MainActivity.configPath);
//                    UserConfig.spotlist.add(new Spot(userLocation.getLatitude(), userLocation.getLongitude()));
//                    spotinfo.setText("Total money: " + UserConfig.getTotalMoney() +"\n");
//                    imageView.setOnClickListener(null);
//                }
//            });
//            return;
//        }
//
//        if (!blocation) {
//            Log.d("0809", "no gps");
//            spotinfo.setText("Total money: " + UserConfig.getTotalMoney() +"\n"+ "No GPS");
//            return;
//        }
//        else {
//            spotinfo.setText("Total money: " + UserConfig.getTotalMoney() +"\n" + "");
//
//        }
//
//
//    }
/*
    private void detectCoin(Location location){
        String info = "";
        Log.d("CDERFAWERF", "sss");
        for (int index = 0; index < MainActivity.coinlist.size(); index++) {
            spotinfo.setText(getString(R.string.totalmoney) + MainActivity.totalmoney +"\n" + "");
            if(MainActivity.coinlist.get(index).state == 1){
                Log.d("indexindex", "values "+ index);
                Log.d("hastaken", "DDDDDDD");
                continue;
            }
            Location recentlocation = new Location(location);
            recentlocation.setLatitude(MainActivity.coinlist.get(index).latitude);
            recentlocation.setLongitude(MainActivity.coinlist.get(index).longitude);
            float dis = location.distanceTo(recentlocation);



            if(dis < 100){
                //final AlertDialog.Builder GetCoinDialog = new AlertDialog.Builder(this);
                coinlat = MainActivity.coinlist.get(index).latitude;
                coinlng = MainActivity.coinlist.get(index).longitude;
                if(MainActivity.coinlist.get(index).kind == 1)
                    imageView.setImageResource(R.drawable.diamond);
                else
                    imageView.setImageResource(R.drawable.img01);
                break;
            }
        }


    }
*/
    public static void Post_DeletMessage(Double inputlat, Double inputlng){
        String urlstring = "http://140.113.216.39/deleteCoins/";

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
            JsonObj.put("coin_lat", inputlat);
            JsonObj.put("coin_lng", inputlng);
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

            parseDeletMessage(result);

            br.close();

            //System.out.println("WEB return value is : " + sb);
            Log.d("Post_Deletresponse", "Post_Message: " + result);
            // Toast.makeText(getApplicationContext(),"Sending 'POST' request to URL : " + url + "\nPost parameters : " + test + "\nResponse Code : " + responseCode + "\nWEB return value is : " + sb, Toast.LENGTH_LONG).show();
        }
        catch(IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void parseDeletMessage(String data){
        try{

            JSONArray jsonMainNode = new JSONArray(data);
            int jsonArrLength = jsonMainNode.length();
            //Log.d("check", "check" + jsonArrLength);
            for(int i=0; i < jsonArrLength; i++) {
                //Log.d("check", "check" );
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                UserConfig.totalmoney = jsonChildNode.getDouble("total_money");
                Log.d("backmessage", jsonChildNode.getString("message"));

            }


        }catch(Exception e){
            Log.i("App", "Error parsing data" +e.getMessage());

        }
    }

}
