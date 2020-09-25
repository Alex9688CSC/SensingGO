package edu.nctu.wirelab.sensinggo.Record;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.util.Log;

import java.util.HashMap;

/**
 * Record what app is used and
 * calculate the usage of its network traffic
 */
public class TrafficSnapshot{
    String tagName = "TrafficSnapshot";
    public HashMap<Integer, TrafficRecord> apps = new HashMap<Integer, TrafficRecord>(); // key: uid of the app, value: traffic record
    public HashMap<Integer, TrafficRecord> delta; //AppName <-> delta usage (calculate by function CalculateUsage)

    public TrafficSnapshot(Context ctxt) {
        HashMap<Integer, String> appNames = new HashMap<Integer, String>();

        PackageManager pm = ctxt.getPackageManager();
        //http://stackoverflow.com/questions/8167343/how-to-get-application-labels-of-installed-applications
        //http://www.fx114.net/qa-126-101131.aspx

        for (ApplicationInfo app : ctxt.getPackageManager().getInstalledApplications(0)) {
            appNames.put(app.uid, app.packageName);

            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(ctxt.getApplicationInfo().packageName, 0);
            } catch (final PackageManager.NameNotFoundException e) {
            }

            if((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
/*
                Log.d(tagName, "Non system uid:" + app.uid
                                + "  AppName:" + app.packageName
                                + " label:" + app.loadLabel(pm)
                        //+ " res:" + pm.getApplicationLabel(app)
                );*/
            }

            else{
                /*Log.d(tagName, "System uid:" + app.uid
                                + "  AppName:" + app.packageName
                                + " label:" + app.loadLabel(pm)
                        //+ " res:" + pm.getApplicationLabel(app)
                );*/
            }
        }

        for (Integer uid : appNames.keySet()) {
            apps.put(uid, new TrafficRecord(uid, appNames.get(uid)));
//            Log.d(tagName, "uid:"+uid+"  AppName:"+appNames.get(uid));
        }
    }

    /**
     * Record the network transmitted and received traffic
     */
    public class TrafficRecord{
        public long tx = 0;
        public long rx = 0;
        public String tag ="";

        TrafficRecord() {
            tx = TrafficStats.getTotalTxBytes();
            rx = TrafficStats.getTotalRxBytes();
        }

        TrafficRecord(int uid, String tag) {
            tx = TrafficStats.getUidTxBytes(uid);
            rx = TrafficStats.getUidRxBytes(uid);
            this.tag = tag;
            this.tag = tag;
        }
    }

    public void calculateUsage(TrafficSnapshot previous){
        delta = new HashMap<Integer, TrafficRecord>();

        if(apps!=null && previous.apps!=null){
            for(Integer uid : apps.keySet()){
                String appName = apps.get(uid).tag;
                TrafficRecord tr = new TrafficRecord();
                if(previous.apps.get(uid) != null) {
                    if (previous.apps.get(uid).tag != null) {
                        if (!previous.apps.get(uid).tag.equals("")) { // prevent crashed
                            Log.d(tagName, previous.apps.get(uid).tag);
                        }
                    }
                }
                if(previous.apps.containsKey(uid) && previous.apps.get(uid).tag!=null && previous.apps.get(uid).tag.equals(appName)){
                    tr.tx = apps.get(uid).tx - previous.apps.get(uid).tx;
                    tr.rx = apps.get(uid).rx - previous.apps.get(uid).rx;
                    tr.tag = new String(appName);
                    delta.put(uid, tr);
                } else{
                    tr.tx = apps.get(uid).tx;
                    tr.rx = apps.get(uid).rx;
                    tr.tag = new String(appName);
                    delta.put(uid, tr);
                }
            }
        }
    }
}
