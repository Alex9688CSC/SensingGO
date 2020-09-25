package edu.nctu.wirelab.sensinggo.File;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.net.wifi.ScanResult;
import android.os.Build;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

import edu.nctu.wirelab.sensinggo.BroadcastReceiver.BatteryInfoReceiver;
import edu.nctu.wirelab.sensinggo.BroadcastReceiver.ScreenStateReceiver;
import edu.nctu.wirelab.sensinggo.MainActivity;
import edu.nctu.wirelab.sensinggo.Measurement.Location;
import edu.nctu.wirelab.sensinggo.Measurement.PhoneState;
import edu.nctu.wirelab.sensinggo.Measurement.SignalStrength;
import edu.nctu.wirelab.sensinggo.Record.NeighborCellInfo;
import edu.nctu.wirelab.sensinggo.Record.TrafficSnapshot;
import edu.nctu.wirelab.sensinggo.RunIntentService;

public class JsonParser implements Serializable {
    public String account = "Account";
    public String allCellInfo = "";
    public String allWiFiInfo = "";
    public String appsInfo = "";
    public String neighborWifiInfo = "";
    public String servingCellInfo = "";
    public String servingWifiInfo = "";

    public final String TESTTYPE = "1"; //1 for team test, 2 for testusers
    public final String MODEL = Build.BRAND + "_" + Build.MODEL;
    public final double dataVersion = 1.8;

    private long lastUpdateTimeG, lastUpdateTimeN; //G:GPS, N:Network

    public void setAccount(String strAccount) {
        account = strAccount;
    }

    public JsonParser() {
        lastUpdateTimeG = 0;
        lastUpdateTimeN = 0;
    }

    // All common data
    private void commonData(JSONObject obj, Context context){ //pass the context to check permission
        try{
            obj.put("AppicationType", "Android");
            obj.put("ApplicationVersion", Build.VERSION.RELEASE);
            obj.put("APPVersion", MainActivity.APPVERSION);
            obj.put("Account", account);
            obj.put("SHANSERVER", MainActivity.SHANSERVER);
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                obj.put("equipmentId", RunIntentService.tm.getDeviceId());
            }
            obj.put("Model", MODEL);
            if (BatteryInfoReceiver.lastLevel != BatteryInfoReceiver.level) {
                obj.put("BatteryLevel", BatteryInfoReceiver.electricity);
                BatteryInfoReceiver.lastElect = BatteryInfoReceiver.electricity;
            }
            obj.put("BatteryTemp", BatteryInfoReceiver.temperature);
            obj.put("Time", System.currentTimeMillis());
            obj.put("TimeStamp", RunIntentService.displayTimeMilli());
            obj.put("TimeZone(GMT)", RunIntentService.displayTimeZone());

            obj.put("DataVersion", dataVersion); //app data version

            obj.put("TestType", TESTTYPE); //for testing
            obj.put("Screen state", ScreenStateReceiver.screen_state);
            obj.put("Cpu", RunIntentService.cpuUti());
            obj.put("Ram", RunIntentService.ram_uti);
            obj.put("CallID", PhoneState.callID);
            obj.put("NetworkType", RunIntentService.networkType);
            obj.put("ConnectionState", RunIntentService.connectionState);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void gpsData(JSONObject obj){
        try{
            int gpsAlive = Location.checkGPSalive();

            if(gpsAlive == 1){
                //Writing file in new thread 2018/10/07
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileMaker.write(ErrorInfoToJson("unknownS", "checkGPSalive():"));
                    }
                }).start();
                Location.userlocationG = null;
                Location.updateTimeG = null;
            }

            else if(gpsAlive == 2) {
                //Writing file in new thread 2018/10/07
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileMaker.write(ErrorInfoToJson("unknownG", "checkGPSalive():"));
                    }
                }).start();
                Location.userlocationG = null;
                Location.updateTimeG = null;
            }
            else if(gpsAlive == 3) {
                //Writing file in new thread 2018/10/07
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileMaker.write(ErrorInfoToJson("unknownN", "checkGPSalive():"));
                    }
                }).start();
                Location.userlocationN = null;
                Location.updateTimeN = null;
            }

            //if LocationUpdate of GPS get value, update its Lat/Lng/Speed
            if (Location.userlocationG!=null && Location.updateTimeG!=null) {
                obj.put("LatG", Location.userlocationG.getLatitude());
                obj.put("LngG", Location.userlocationG.getLongitude());
                obj.put("SpeedG", Location.userlocationG.getSpeed());
            } else {
                obj.put("LatG", "Unknown");
                obj.put("LngG", "Unknown");
                obj.put("SpeedG", "Unknown");
            }
            //if LocationUpdate of Network get value, update its Lat/Lng/Speed
            if (Location.userlocationN != null&&Location.updateTimeN!=null) {
                obj.put("LatN", Location.userlocationN.getLatitude());
                obj.put("LngN", Location.userlocationN.getLongitude());
                obj.put("SpeedN", Location.userlocationN.getSpeed());
            } else {
                obj.put("LatN", "Unknown");
                obj.put("LngN", "Unknown");
                obj.put("SpeedN", "Unknown");
            }
            //the time GPS update (GPS)
            if(Location.updateTimeG != null)
                if(Location.updateTimeG != lastUpdateTimeG){
                    obj.put("GPSTimeG", Location.updateTimeG);
                    obj.put("GPSTimeStampG", Location.updateTimeStampG);
                    lastUpdateTimeG = Location.updateTimeG;
                }
            //the time GPS update (Network)
            if(Location.updateTimeN != null)
                if(Location.updateTimeN != lastUpdateTimeN){
                    obj.put("GPSTimeN", Location.updateTimeN);
                    obj.put("GPSTimeStampN", Location.updateTimeStampN);
                    lastUpdateTimeN = Location.updateTimeN;
                }
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String servingCellInfoToJson(Context context) {
        if (RunIntentService.neighborCellList.size() == 0) {
            return "";
        }

        JSONObject obj = new JSONObject();

        try {
            commonData(obj, context);
            gpsData(obj);
            obj.put("Event", "ServingCell"); // new event!
            //------------------------results
            JSONObject cellInfo = new JSONObject();
            if (SignalStrength.cellInfoType.equals("LTE")) {
                //Log.d("CellInfo", "[LTE]"+RunIntentService.AtCellID+":"+RunIntentService.AtCellPCI+":"+RunIntentService.AtCellRSRP);

                cellInfo.put("CellInfoType", RunIntentService.cellInfoType);
                cellInfo.put("CellID", RunIntentService.lteCellID);
                cellInfo.put("CellMCC", RunIntentService.lteCellMCC); //country code
                cellInfo.put("CellMNC", RunIntentService.lteCellMNC);

                cellInfo.put("CellPCI", RunIntentService.lteCellPCI); //physical cell id
                cellInfo.put("CellTAC", RunIntentService.lteCellTAC); //

                cellInfo.put("RSSI", SignalStrength.lteCellRSSI);
                cellInfo.put("SINR", "null");
                cellInfo.put("RSRQ", RunIntentService.lteCellRSRQ);
                cellInfo.put("RSRP", RunIntentService.lteCellRSRP);

            } else if (SignalStrength.cellInfoType.equals("Wcdma")) {
                //Log.d("CellInfo", "[WCDMA]"+RunIntentService.WcdmaAtCellID+":"+RunIntentService.WcdmaAtCellSignalStrength);

                cellInfo.put("CellInfoType", RunIntentService.cellInfoType);
                cellInfo.put("CellID", RunIntentService.wcdmaAtCellID);
                cellInfo.put("CellMCC", RunIntentService.wcdmaAtCellMCC);
                cellInfo.put("CellMNC", RunIntentService.wcdmaAtCellMNC);
                cellInfo.put("CellPSC", RunIntentService.wcdmaAtCellPsc);
                cellInfo.put("CellLAC", RunIntentService.wcdmaAtCellLac);
                cellInfo.put("SignalStrength", RunIntentService.wcdmaAtCellSignalStrength);
            }

            obj.put("ServingCellInfo", cellInfo);
            servingCellInfo = obj.toString(2);
            return obj.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    //---------------------------------------------------------------------------------------------

    public String NeighborCellInfoToJson(Context context) {
        if (RunIntentService.neighborCellList.size() == 0) {
            return "";
        }
        JSONObject obj = new JSONObject();
        JSONArray ary = new JSONArray();
        try {
            commonData(obj, context);
            gpsData(obj);
            obj.put("Event", "NeighborCell");
            //------------------------results

            for (int i = 0; i < RunIntentService.neighborCellList.size(); i++) {
                JSONObject cellInfo = new JSONObject();

                NeighborCellInfo neighborcellinfo = RunIntentService.neighborCellList.get(i);
                //Log.d("CellInfo", "[neighbor]"+neighborcellinfo.type+"~"+neighborcellinfo.ci+":"+neighborcellinfo.pci+":"+neighborcellinfo.RSRP);
                cellInfo.put("Type", neighborcellinfo.type);
                if (neighborcellinfo.type.compareTo("LTE") == 0) {
                    cellInfo.put("CellID", neighborcellinfo.ci);
                    cellInfo.put("CellLAC", neighborcellinfo.lac);
                    cellInfo.put("PCI", neighborcellinfo.pci);
                    cellInfo.put("RSRP", neighborcellinfo.RSRP);
                    cellInfo.put("RSRQ", neighborcellinfo.RSRQ);
                } else {
                    cellInfo.put("CellID", neighborcellinfo.ci);
                    cellInfo.put("CellLAC", neighborcellinfo.lac);
                    cellInfo.put("PCI", neighborcellinfo.pci);
                    cellInfo.put("SignalStrength", neighborcellinfo.SignalStrength);
                }

                ary.put(cellInfo);
            }
            obj.put("NeighborCellInfo", ary);
            allCellInfo = obj.toString(2);
            return obj.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String HandoverInfoToJson(Context context) {
        JSONObject obj = new JSONObject();
        try {
            commonData(obj, context);
            gpsData(obj);
            obj.put("Event", "Handover");
            obj.put("FromCellID", SignalStrength.PreAtCellID);
            obj.put("ToCellID", SignalStrength.nowCellID);
            obj.put("CellResidenceTime", SignalStrength.cellHoldTime);

            return obj.toString();

        } catch (JSONException e) {
            e.printStackTrace();
            String ErrorPath = MainActivity.logPath + "ErrorLog" + RunIntentService.recordFilePrefix;

            try {
                FileWriter writer = new FileWriter(ErrorPath, true);
                writer.write(e.toString());
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    // Record the phone state when it's changing
    public String phoneStateToJson(Context context) {
        JSONObject obj = new JSONObject();
        try {
            commonData(obj, context);
            gpsData(obj);
            obj.put("Event", "PhoneStateChanged");

            obj.put("callstarttime", PhoneState.startCallTime);
            obj.put("callendtime", PhoneState.endCallTime);
            obj.put("callstate", PhoneState.callState);

            obj.put("PhoneState", PhoneState.phoneState);

            if (SignalStrength.cellInfoType.equals("LTE")) {
                obj.put("CellID", SignalStrength.lteCellID);
            }
            else if (SignalStrength.cellInfoType.equals("Wcdma")) {
                obj.put("CellID", SignalStrength.wcdmaAtCellID);
            }

            if (PhoneState.phoneState.equals("IDLE")) {
                obj.put("CallHoldingTime", PhoneState.callHoldingTime);
                obj.put("ExcessLife", PhoneState.excessLife);
            }

            return obj.toString();

        } catch (JSONException e) {
            e.printStackTrace();
            String ErrorPath = MainActivity.logPath + "ErrorLog" + RunIntentService.recordFilePrefix;
            try {
                FileWriter writer = new FileWriter(ErrorPath, true);
                writer.write(e.toString());
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    public synchronized String appsInfoToJson(Context context) {
        JSONObject obj = new JSONObject();

        try {
            commonData(obj, context);
            obj.put("Event", "AppsInfo");

            obj.put("LastTime", System.currentTimeMillis() - MainActivity.startServiceTime);

            //Get the apps' usage
            JSONArray ary = new JSONArray();
            for (Integer appuid : MainActivity.latest.delta.keySet()) {
                if (MainActivity.latest.apps.get(appuid).tx==0 && MainActivity.latest.apps.get(appuid).rx==0) {
                    continue;
                }
                String appName = MainActivity.latest.delta.get(appuid).tag;
                TrafficSnapshot.TrafficRecord trafficRecord = MainActivity.latest.delta.get(appuid);
                
                if (trafficRecord.tx==0 && trafficRecord.rx==0) {
                    continue;
                }
                JSONObject appInfo = new JSONObject();
                appInfo.put("AppName", appName);
                appInfo.put("TotalTx", MainActivity.latest.apps.get(appuid).tx);
                appInfo.put("TotalRx", MainActivity.latest.apps.get(appuid).rx);
                appInfo.put("DeltaTx", trafficRecord.tx);
                appInfo.put("DeltaRx", trafficRecord.rx);
                ary.put(appInfo);
            }
            obj.put("AppsInfo", ary);

            obj.put("AllAppsTxBytes(MB)", RunIntentService.bytesToMega(TrafficStats.getTotalTxBytes()));
            obj.put("AllAppsRxBytes(MB)", RunIntentService.bytesToMega(TrafficStats.getTotalRxBytes()));

            appsInfo = obj.toString(2);
            return obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            String ErrorPath = MainActivity.logPath + "ErrorLog" + RunIntentService.recordFilePrefix;

            try {
                FileWriter writer = new FileWriter(ErrorPath, true);
                writer.write(e.toString());
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    public String wifiInfoToJson(Context context) {
        JSONObject obj = new JSONObject();
        JSONObject servingWifi = new JSONObject();
        JSONObject neighborWifi = new JSONObject();

        try {
            commonData(obj, context);
            gpsData(obj);
            obj.put("Event", "WiFiInfo");

            obj.put("servingSSID", RunIntentService.servingSSID);
            obj.put("servingBSSID", RunIntentService.servingBSSID);
            obj.put("servingMAC", RunIntentService.servingMAC);
            obj.put("servingLevel", RunIntentService.servingLevel);
            obj.put("servingFreq", RunIntentService.servingFreq);
            obj.put("servingChan", RunIntentService.servingChan);
            obj.put("servingIP", RunIntentService.servingIP);
            obj.put("servingSpeed", RunIntentService.servingSpeed);
            //------------------------------------------------
            servingWifi.put("servingSSID", RunIntentService.servingSSID);
            servingWifi.put("servingBSSID", RunIntentService.servingBSSID);
            servingWifi.put("servingMAC", RunIntentService.servingMAC);
            servingWifi.put("servingLevel", RunIntentService.servingLevel);
            servingWifi.put("servingFreq", RunIntentService.servingFreq);
            servingWifi.put("servingChan", RunIntentService.servingChan);
            servingWifi.put("servingIP", RunIntentService.servingIP);
            servingWifi.put("servingSpeed", RunIntentService.servingSpeed);
            servingWifiInfo = servingWifi.toString(2);
            //------------------------
            JSONArray ary = new JSONArray();

            if (RunIntentService.results != null) {
                for (int i = 0; i < RunIntentService.results.size(); i++) {
                    JSONObject wifiinfo = new JSONObject();
                    ScanResult neighborwifiinfo = RunIntentService.results.get(i);
                    int chan = RunIntentService.convertFrequencyToChannel(neighborwifiinfo.frequency);
                    wifiinfo.put("SSID", neighborwifiinfo.SSID);
                    wifiinfo.put("level", neighborwifiinfo.level);
                    wifiinfo.put("Freq", neighborwifiinfo.frequency);
                    wifiinfo.put("Chan", String.valueOf(chan));
                    wifiinfo.put("cap", neighborwifiinfo.capabilities);
                    ary.put(wifiinfo);
                }
            } else {
                JSONObject wifiinfo = new JSONObject();
                wifiinfo.put("SSID", "null");
                wifiinfo.put("level", "null");
                wifiinfo.put("Freq", "null");
                wifiinfo.put("Chan", "null");
                wifiinfo.put("cap", "null");
                ary.put(wifiinfo);
            }
            neighborWifi.put("NeighborWiFi", ary);
            neighborWifiInfo = neighborWifi.toString(2);

            obj.put("NeighborWiFi", ary);
            allWiFiInfo = obj.toString(2);
            return obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            String ErrorPath = MainActivity.logPath + "ErrorLog" + RunIntentService.recordFilePrefix;
            try {
                FileWriter writer = new FileWriter(ErrorPath, true);
                writer.write(e.toString());
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    public String sensorInfoToJson(String typeEvent, String values) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Account", account);
            obj.put("Event", typeEvent);
            obj.put("Time", System.currentTimeMillis());
            obj.put("TimeStamp", RunIntentService.displayTimeMilli());
            obj.put("Value", values);

            return obj.toString();

        } catch (JSONException e) {
            e.printStackTrace();
            String ErrorPath = MainActivity.logPath + "ErrorLog" + RunIntentService.recordFilePrefix;
            try {
                FileWriter writer = new FileWriter(ErrorPath, true);
                writer.write(e.toString());
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    public String ErrorInfoToJson(String typeEvent, String msg) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Account", account);
            obj.put("Event", typeEvent);
            obj.put("Time", System.currentTimeMillis());
            obj.put("TimeStamp", RunIntentService.displayTimeMilli());
            obj.put("MSG:", msg);

            return obj.toString();

        } catch (JSONException e) {
            e.printStackTrace();
            String ErrorPath = MainActivity.logPath + "ErrorLog" + RunIntentService.recordFilePrefix;

            try {
                FileWriter writer = new FileWriter(ErrorPath, true);
                writer.write(e.toString());
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }
}
