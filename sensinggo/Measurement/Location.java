package edu.nctu.wirelab.sensinggo.Measurement;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import androidx.core.content.ContextCompat;
import android.util.Log;

import java.lang.ref.WeakReference;

import edu.nctu.wirelab.sensinggo.File.FileMaker;
import edu.nctu.wirelab.sensinggo.File.JsonParser;
import edu.nctu.wirelab.sensinggo.R;
import edu.nctu.wirelab.sensinggo.RunIntentService;

/**
 * Location object is new by
 * 1. MainActivity.java to
 * check the location information is gained from GPS or NETWORK_PROVIDER(WLAN, Mobile Network), or neither
 *
 * 2. RunIntentService.java to
 * get the location info. by GPS or NETWORK_PROVIDER or
 * remove all location updates
 */
public class Location implements GpsStatus.Listener,LocationListener {
    private final Context mContext;
    private static WeakReference<Context> weakContext;
    private static LocationManager locationManager;

    //G: GPS, N: Network, S: Satellite
    public static android.location.Location userlocationG = null, userlocationN = null;
    public static String speedG = null,speedN = null;
    public static Long updateTimeG=0L,updateTimeN=0L,updateTimeS;
    public static String updateTimeStampG ="unknown",updateTimeStampN ="unknown";

    private LocationListener locationListener;
    //public static List<GpsSatellite> satellites = new ArrayList<GpsSatellite>();

    public static GpsStatus gpsStatus = null;

    public Location(Context context) {
        mContext = context;
        this.weakContext = new WeakReference<>(context);
        locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);

        speedG = null;
        userlocationG = null;
        updateTimeG = 0L;
        updateTimeStampG = weakContext.get().getString(R.string.info_unknown);

        speedN = null;
        userlocationN = null;
        updateTimeN = 0L;
        updateTimeStampN = weakContext.get().getString(R.string.info_unknown);

        updateTimeS = null;
    }
    private JsonParser JsonParser = null;

    public void setJsonParser(JsonParser json) {
        JsonParser = json;
    }

    public boolean isOpenGps() {
        // Get the location by GPS
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Get the location by WLAN or mobile network. It's usually used at the place which is more hidden.
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (gps || network) {
            return true;
        }
        return false;
    }


    public void startGPS(Context context) {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("connn", weakContext.get().toString());

            this.getGPS(context);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, Location.this, Looper.getMainLooper());
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, Location.this, Looper.getMainLooper());
        }

    }

    public void getGPS(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            userlocationG = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            userlocationN = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

    }

    public void stopGPS() {
        locationManager.removeUpdates(Location.this);
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        if(location.getProvider().equals(LocationManager.GPS_PROVIDER)){
            if (userlocationG == null) {
                userlocationG = new android.location.Location(location);
            }
            else {
                userlocationG.set(location);
            }
            updateTimeG = System.currentTimeMillis();

            // The time unit is year to millisecond
            updateTimeStampG = RunIntentService.displayTimeMilli();
        }

        if(location.getProvider().equals(LocationManager.NETWORK_PROVIDER)){
            if (userlocationN == null) {
                userlocationN = new android.location.Location(location);
            }
            else {
                userlocationN.set(location);
            }
            updateTimeN = System.currentTimeMillis();
            updateTimeStampN = RunIntentService.displayTimeMilli();
        }

        if (locationListener != null) {
            locationListener.onLocationChanged( location );
        }
    }

    @Override
    public void onStatusChanged(final String provider,final int status, Bundle extras) {
        if (locationListener != null) {
            //Writing file in new thread 2018/10/07
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FileMaker.write(JsonParser.ErrorInfoToJson("LOG","onStatusChanged():"+provider + " status: " + status));
                }
            }).start();
            locationListener.onStatusChanged( provider, status, extras);
        }
    }

    public static int checkGPSalive() {
        //satelliteUpdate();
        long now = System.currentTimeMillis();
        if(updateTimeS!=null && (now-updateTimeS)>10000) {
            return 1;
        }
        if(updateTimeG!=null && (now-updateTimeG)>10000) {
            return 2;
        }
        if(updateTimeN!=null && (now-updateTimeN)>30000) {
            return 3;
        }
        return 0;
    }
    /* py
    public static void satelliteUpdate(){ //never used
        satellites.clear();
        gpsStatus = locationManager.getGpsStatus(null);
        for (GpsSatellite sat : gpsStatus.getSatellites()) {
            if (sat.usedInFix()) {
                satellites.add(sat);
            }
        }
        if (satellites.size()>0 && satellites.size()<3) {
            if (updateTimeS == null) {
                updateTimeS = System.currentTimeMillis();
            }
        } else{
            updateTimeS = null;
        }
    }*/

    @Override
    public void onGpsStatusChanged(final int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                break;
        }
    }

    @Override
    public void onProviderEnabled(final String provider) {
        if (locationListener != null) {
            //Writing file in new thread 2018/10/07
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FileMaker.write(JsonParser.ErrorInfoToJson("LOG", "onProviderEnabled():"+provider));
                }
            }).start();
            locationListener.onProviderEnabled(provider);
        }
    }

    @Override
    public void onProviderDisabled(final String provider) {
        if (locationListener != null) {
            //Writing file in new thread 2018/10/07
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FileMaker.write(JsonParser.ErrorInfoToJson("LOG","onProviderDisabled():"+provider));
                }
            }).start();
            locationListener.onProviderEnabled(provider);
        }
    }
}
