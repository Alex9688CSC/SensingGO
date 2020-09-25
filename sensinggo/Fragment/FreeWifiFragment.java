package edu.nctu.wirelab.sensinggo.Fragment;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import edu.nctu.wirelab.sensinggo.R;

import static com.facebook.FacebookSdk.getApplicationContext;


public class FreeWifiFragment extends Fragment {

    //private Button startScanButton;
    private TextView scanningTextView;
    private TextView scanCompletedTextView;
    private TextView freeWifiTitleTextView;
    private TextView freeWifiListTextView;
    private ProgressBar scanProgressBar;

    private WifiManager wifiManager;
    private Handler mainHandler;
    private boolean inScanSuccess = false;
    private ArrayList<String> scannedWiFiList = new ArrayList<>();
    private ArrayList<String> freeWiFiList = new ArrayList<>();
    private BroadcastReceiver wifiScanReceiver;

    private boolean isDestroyed = false; // if fragment is destroyed, the thread should stop working

    Runnable scanCompletedRunnable = new Runnable() {
        @Override
        public void run() {
            String freeWiFi = "";

            if (freeWiFiList.size() == 0) {
                freeWiFi = getString(R.string.none);
            }

            for (int i = 0; i < freeWiFiList.size(); i++) {
                freeWiFi += freeWiFiList.get(i) + "\n";
            }

            freeWifiListTextView.setText(freeWiFi);

            scanProgressBar.setVisibility(View.INVISIBLE);
            scanningTextView.setVisibility(View.INVISIBLE);
            scanCompletedTextView.setVisibility(View.VISIBLE);
            freeWifiTitleTextView.setVisibility(View.VISIBLE);
            freeWifiListTextView.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_free_wifi, container, false);
        Log.d("zxc", "onCreateView");

        init();

        //startScanButton = view.findViewById(R.id.startScanButton);
        scanningTextView = view.findViewById(R.id.scanningTextView);
        scanCompletedTextView = view.findViewById(R.id.scanCompletedTextView);
        freeWifiTitleTextView = view.findViewById(R.id.freeWifiTitleTextView);
        freeWifiListTextView = view.findViewById(R.id.freeWifiListTextView);
        scanProgressBar = view.findViewById(R.id.scanProgressBar);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mainHandler = new Handler(getActivity().getMainLooper());

        wifiScanReceiver = new BroadcastReceiver() {
            @Override
            @TargetApi(Build.VERSION_CODES.M)
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success && !inScanSuccess) {
                    scanSuccess();
                } else {
                    // scan failure handling
                    scanFailure();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getActivity().registerReceiver(wifiScanReceiver, intentFilter);

        scanningTextView.setVisibility(View.VISIBLE);
        scanProgressBar.setVisibility(View.VISIBLE);
        scanCompletedTextView.setVisibility(View.INVISIBLE);
        freeWifiTitleTextView.setVisibility(View.INVISIBLE);
        freeWifiListTextView.setVisibility(View.INVISIBLE);

        wifiManager.startScan();


//        startScanButton.setOnClickListener(new Button.OnClickListener(){
//            public void onClick(View v){
//                if (!startScanning) {
//                    startScanning = true;
//                    wifiManager.startScan();
//                    scanningTextView.setVisibility(View.VISIBLE);
//                    scanProgressBar.setVisibility(View.VISIBLE);
//                    scanCompletedTextView.setVisibility(View.INVISIBLE);
//                    freeWifiTitleTextView.setVisibility(View.INVISIBLE);
//                    freeWifiListTextView.setVisibility(View.INVISIBLE);
//                }
//            }
//        });



        return view;
    }

    @Override
    public void onDestroy() {
        Log.d("zxc", "onDestroy");

        getActivity().unregisterReceiver(wifiScanReceiver);
        isDestroyed = true;

        super.onDestroy();
    }

    private void init() {
        inScanSuccess = false;
        isDestroyed = false;
    }

    private void scanSuccess() {
        inScanSuccess = true;

        List<ScanResult> results = wifiManager.getScanResults();
        Log.d("ggg", "scan success!");
        Log.d("ggg", "results.size() : " + String.valueOf(results.size()));

        scannedWiFiList.clear();
        freeWiFiList.clear();

        for(int i=0; i < results.size(); i++){
            String capabilities = results.get(i).capabilities;
            if (!TextUtils.isEmpty(capabilities)) {
                if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                    //type = WIFICIPHER_WPA;
                    Log.d("ggg", "[" + results.get(i).SSID + "] uses WPA");
                } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                    Log.d("ggg", "[" + results.get(i).SSID + "] uses WEP");
                    //type = WIFICIPHER_WEP;
                } else {
                    //type = WIFICIPHER_NOPASS;
                    Log.d("ggg", "[" + results.get(i).SSID + "] uses no password");
                    //check if the same SSID is already in list
                    boolean ssidExisted = false;
                    for (int j = 0; j < scannedWiFiList.size(); j++) {
                        if (scannedWiFiList.get(j).equals(results.get(i).SSID)) {
                            ssidExisted = true;
                        }
                    }
                    if (!ssidExisted) {
                        scannedWiFiList.add(results.get(i).SSID);
                    }

                }
            }

        }

        checkWiFiNeedAuthentication();
    }

    private void scanFailure() {
        //Log.d("ggg", "scan failure!");
    }

    private void checkWiFiNeedAuthentication() {

        new Thread(new Runnable() {
            public void run() {
                for (String ssid : scannedWiFiList) {
                    WifiConfiguration conf = new WifiConfiguration();
                    conf.SSID = "\"" + ssid + "\"";
                    conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

                    wifiManager.addNetwork(conf);

                    List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                    for( WifiConfiguration i : list ) {
                        if (i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                            wifiManager.disconnect();
                            wifiManager.enableNetwork(i.networkId, true);
                            wifiManager.reconnect();

                            int count = 0;
                            boolean isFree = false;

                            // 5 secs to check this wifi needs web login or not
                            while (count < 5) {
                                try {
                                    Thread.sleep(1000);
                                } catch (Exception e) {
                                    e.getLocalizedMessage();
                                }
                                Log.d("ggg", "This WiFi is connected or not : " + isNetworkConnected() + " (" + (count+1) + "s)");
                                if (isInternetAvailable("8.8.8.8", 53, 1000)) {

                                    // check fragment has detached
                                    if (getActivity() == null) {
                                        return;
                                    }

                                    //check it is using wifi
                                    ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                                    NetworkInfo networkINfo = connectivityManager.getActiveNetworkInfo();

                                    if(networkINfo != null
                                            && networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {
                                        Log.d("ggg", "it is using WiFi!");
                                        isFree = true;
                                        break;
                                    }
                                    else if (networkINfo != null
                                            && networkINfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                                        Log.d("ggg", "it is using Cellular!");
                                    }

                                }
                                count++;
                            }

                            if (isFree) {
                                Log.d("ggg", "This WiFi [" + ssid + "] is free");
                                freeWiFiList.add(ssid);
                            }
                            else {
                                Log.d("ggg", "This WiFi [" + ssid + "] is not free");
                            }

                            break;
                        }
                    }

                    // cehck if fragment has been destroyed
                    if (isDestroyed) {
                        Log.d("ggg", "Stop the thread because the previous fragment has been destroyed");
                        return;
                    }

                }

                //scan free wifi completed
                inScanSuccess = false;
                mainHandler.post(scanCompletedRunnable);
            }
        }).start();

    }

    private boolean isInternetAvailable(String address, int port, int timeoutMs) {
        try {
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress(address, port);

            sock.connect(sockaddr, timeoutMs); // This will block no more than timeoutMs
            sock.close();

            return true;

        } catch (IOException e) { return false; }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

}
