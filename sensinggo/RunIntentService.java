package edu.nctu.wirelab.sensinggo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.app.NotificationCompat;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import edu.nctu.wirelab.sensinggo.BroadcastReceiver.BatteryInfoReceiver;
import edu.nctu.wirelab.sensinggo.Connect.HttpsConnection;
import edu.nctu.wirelab.sensinggo.Connect.SFTPController;
import edu.nctu.wirelab.sensinggo.File.BrowseFile;
import edu.nctu.wirelab.sensinggo.File.FileMaker;
import edu.nctu.wirelab.sensinggo.File.JsonParser;
import edu.nctu.wirelab.sensinggo.File.OutCypher;
import edu.nctu.wirelab.sensinggo.Measurement.Location;
import edu.nctu.wirelab.sensinggo.Measurement.PhoneState;
import edu.nctu.wirelab.sensinggo.Measurement.SensorList;
import edu.nctu.wirelab.sensinggo.Measurement.SignalStrength;
import edu.nctu.wirelab.sensinggo.Record.NeighborCellInfo;
import edu.nctu.wirelab.sensinggo.Record.TrafficSnapshot;

import static androidx.core.app.NotificationCompat.PRIORITY_LOW;
import static java.lang.System.currentTimeMillis;

public class RunIntentService extends Service {
    WifiManager wifi;

    public static List<ScanResult> results;
    private edu.nctu.wirelab.sensinggo.File.JsonParser JsonParser = null;
    //SimpleAdapter adapter;

    public static String networkType = "null", connectionState = "null";

    //----serving wifi
    private static ConnectivityManager connManager;
    private static NetworkInfo mWifi;
    public static String servingBSSID = "null",
            servingChan = "null",
            servingFreq = "null",
            servingIP = "null",
            servingLevel = "null",
            servingMAC = "null",
            servingSSID = "null",
            servingSpeed = "null";

    public static boolean wifiState = false;
    //----------------
    public static int lteCellRSRP = 0, lteCellID = 0, lteCellMCC = 0,
            lteCellMNC = 0, lteCellPCI = 0, lteCellTAC = 0,
            lteCellRSSI = 0, lteCellRSRQ = 0;

    //Wcdma info
    public static int wcdmaAtCellPsc = 0, wcdmaAtCellLac = 0, wcdmaAtCellID = 0,
            wcdmaAtCellMCC = 0, wcdmaAtCellMNC = 0, wcdmaAtCellSignalStrength = 0;

    public static String cellInfoType = "null";
    //----------------
    public static double ram_uti;

    private static final String TagName = "RunIntentService";
    public static boolean runFlag = false;
    public static boolean errorFlag = false;
    public static boolean stopServiceFlag = false;
    public static boolean firstRoundFlag = false;
    public static int uploadCount = 0;

    public static ArrayList<NeighborCellInfo> neighborCellList = null;

    public static int servingCellId = 0;

    public static String filePrefix = "", recordFilePrefix = "";


    //the variables here changed by Runnable "SenseAllCellRunnable"
    private Handler senseHandler;
    public static String allCellInfo;
    public static String allWiFiInfo;
    public static String servingWifiInfo;
    public static String servingCellInfo;

    //senseAppUsageHandler is used for recording the APP's traffic usage every 10s
    private Handler senseAppUsageHandler;
    private static int senseAppUsageInterval = 10000; // Unit: milli sec

    //the variable used for stop then start the service every bigInterval
    private Handler bigRoundHandler;
    private static int bigInterval = 600000; // 10 min for one record data file

    private Thread SenseAppUsageThread = null;
    private Thread SenseThread = null;
    private Thread WriteFileThread = null;
    private int second_count = 0;
//    private static int bigInterval = 10000;//10s

    private BatteryInfoReceiver batteryInfoReceiver;

    public static java.io.FileWriter recordWriter;
    public static File file;
    public static String logFileSheerPath, recordFileSheerPath;

    SimpleDateFormat sdf;
    Date LogDate;

    final int notifyID = 199;
    final int notifyErrorID = 200;
    private int closeFlag = 0;

    public static int LocationCount=1;
    public static TelephonyManager tm;

    public static OutCypher moutCypher;

    public static Location locationUpdater;
    private SensorManager sensorManager;
    private SensorList slListener;
    //the variables here changed by SignalStrength
    private SignalStrength ssListener;
    //the variables here changed by PhoneState(inherit from PhoneStateListener)
    private PhoneState psListener;


    public static Date logTime;
    public static SimpleDateFormat logTimeSdf;


    PowerManager powerManager;
    PowerManager.WakeLock globalWakeLock;


    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        JsonParser = (JsonParser) intent.getSerializableExtra("JsonParser");
        moutCypher = (OutCypher) intent.getSerializableExtra("moutcypher");
        Log.d("9487", "name:" + JsonParser.account);
        Log.d("9487", "run:" + RunIntentService.runFlag);
        ssListener.setJsonParser(JsonParser);
        psListener.setJsonParser(JsonParser);
        slListener.setJsonParser(JsonParser);
        locationUpdater.setJsonParser(JsonParser);
        notifyMsg();
        bigRoundHandler.postDelayed(SenseRunnable, bigInterval); // run SenseRunnable / 10 min
        createLogAndWriter();

        whichShouldStart();
        firstRoundFlag = false;
//
//        if (JsonParser == MainActivity.JsonParser) {
//            Log.d("9898", "R:the same");
//        }
//        else {
//            Log.d("9898", "R:not the same");
//        }

        //START_REDELIVER_INTENT:Service被系統殺掉, 重啟且Intent會重傳
        //解決 java.lang.NullPointerException: Attempt to invoke virtual method 'java.io.Serializable android.content.Intent.getSerializableExtra(java.lang.String)' on a null object reference
        return START_REDELIVER_INTENT;
    }

    public void notifyMsg() {
//        Log.d("9487", "notifyMsg:");
//        Intent intent = new Intent();
//        intent.setClass(this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
//                Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        int flags = PendingIntent.FLAG_CANCEL_CURRENT; // ONE_SHOT：PendingIntent只使用一次；CANCEL_CURRENT：PendingIntent執行前會先結束掉之前的；NO_CREATE：沿用先前的PendingIntent，不建立新的PendingIntent；UPDATE_CURRENT：更新先前PendingIntent所帶的額外資料，並繼續沿用
//        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, flags);
//
//        //notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // Get the notification service of system
//        NotificationCompat.Action actiont = new NotificationCompat.Action.Builder(R.drawable.icon_sg, "Start App", pendingIntent).build();
//        Notification notification = new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(R.drawable.icon_sg).setContentTitle("SensingGo").setContentText("Running").addAction(actiont).setOngoing(true).build(); // Create a notification
//        startForeground(notifyID, notification);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground();
        }
        else {
            startForeground(1, new Notification());
        }
//        getNotification();

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationCompat.Action actiont = new NotificationCompat.Action.Builder(R.drawable.icon_cmb, "開啟App", pendingIntent).build();
            NotificationCompat.Action actionf = new NotificationCompat.Action.Builder(android.R.drawable.ic_menu_close_clear_cancel, "關閉App", pendingCancelIntent).build();
            Notification notification = new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(R.drawable.icon_cmb).setContentTitle("CM").setContentText("Running").addAction(actiont).addAction(actionf).setOngoing(true).build(); // 建立通知
            startForeground(notifyID, notification);
        } else {

            Notification notification = new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(R.drawable.icon_cmb).setContentTitle("CM").setContentText("Running").setOngoing(true).addAction(R.drawable.icon_cmb, "開啟App", pendingIntent).addAction(android.R.drawable.ic_menu_close_clear_cancel, "關閉App", pendingCancelIntent).build(); // 建立通知
            startForeground(notifyID, notification);
        }*/

    }

    public Notification getNotification() {
        String channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channel = createChannel();
        else {
            channel = "";
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channel).setSmallIcon(android.R.drawable.ic_menu_mylocation).setContentTitle("snap map fake location");
        Notification notification = mBuilder
                .setPriority(PRIORITY_LOW)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();


        return notification;
    }

    @NonNull
    @TargetApi(26)
    private synchronized String createChannel() {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        String name = "snap map fake location ";
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel mChannel = new NotificationChannel("snap map channel", name, importance);

        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            stopSelf();
        }
        return "snap map channel";
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.icon_sg)
                .setContentTitle("App is running")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();

        startForeground(2, notification);

    }



    public void notifyError() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Notification notification = new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(R.drawable.icon_cmb).setContentTitle("SensingGo").setContentText("錯誤 請將sim卡裝在sim 1").setOngoing(true).build(); // 建立通知
        startForeground(notifyErrorID, notification);

    }

    public void onCreate() {
        //Log.d(TagName, "onCreate");
        super.onCreate();
//        notifyMsg();
        initVar();

        registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onDestroy() {
        Log.d("9487", "onDestroy: RUN");
        globalWakeLock.release();
        closeFlag = 0;
        this.stopForeground(true);
        stopService();
        unregisterReceiver(batteryInfoReceiver);
        //stop();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return (null);
    }

    // Record the time, and write into the file
    public void createLogAndWriter() {
        logTime = null;
        LogDate = new Date();

        //deal with java.lang.NullPointerException
        Log.d("nullf", sdf.toString());
        Log.d("nullf", LogDate.toString());
        Log.d("nullf", sdf.format(LogDate).toString());
        //Log.d("nullf", MainActivity.logPrefix);  // null!

        //解決某些時候(可能Service意外重啟)MainActivity.logPrefix和MainActivity.recordPrefix都為NULL
        String logPrefix = new String("NCTU");
        //change file name with userID 2019/5-30
        String recordPrefix = new String("USER" + UserConfig.myUserid + "RECORD");

        filePrefix = logPrefix.concat((sdf.format(LogDate))); //NCTU'Date'
        recordFilePrefix = recordPrefix.concat((sdf.format(LogDate))); // 'userName'Record'Date'
        //Log.d(TagName, "FilePrefix:" + filePrefix + " RecordFilePrefix:" + recordFilePrefix);

        String logPath = new String("/data/data/" + getPackageName()) + "/logs/";

        file = new File(logPath);
        if (!file.exists()) {
            if (file.mkdir()) {
                //Log.d(TagName, "create logs dir");
            }
        }

        //Now fileWriter is created in FileMaker class -2018/10/07
        recordFileSheerPath = logPath + recordFilePrefix;
        File file = new File(logPath + recordFilePrefix);
        //If file has already existed, don't write "[" again
        if (file.exists()) {
            FileMaker.setFileNotFirstWrite();
        }
        FileMaker.setFilePath(recordFileSheerPath);

        //fix for .sig file path
        recordFileSheerPath = MainActivity.logPath + recordFilePrefix;

//        try {
//            logFileSheerPath = MainActivity.logPath + filePrefix;
//            recordFileSheerPath = MainActivity.logPath + recordFilePrefix;
//            recordWriter = new java.io.FileWriter(recordFileSheerPath, true); //append mode
//
//            Log.d("0807record", recordFileSheerPath);
//        } catch (IOException e) {
//            e.printStackTrace();
//            String ErrorPath = MainActivity.logPath + "ErrorLog" + RunIntentService.recordFilePrefix;
//            try {
//                java.io.FileWriter writer = new java.io.FileWriter(ErrorPath, true);
//                writer.write(e.toString());
//                writer.close();
//            } catch (IOException e2) {
//                e2.printStackTrace();
//            }
//        }
    }

    public void whichShouldStart() {
        locationUpdater.startGPS(this);

        ssListener.startService(tm);
        slListener.startService(sensorManager);
        senseHandler.post(SenseAllCellRunnable);
        psListener.startService(tm);

        MainActivity.previous = new TrafficSnapshot(RunIntentService.this);
        senseAppUsageHandler.postDelayed(SenseAppUsageRunnable, senseAppUsageInterval);

        //registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public boolean checkWifiStatus() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getActiveNetworkInfo();

        if (mWifi!=null && mWifi.getType()==ConnectivityManager.TYPE_WIFI && mWifi.isConnected()) {
            return true;
        }
        return false;
    }

    public boolean checkNetworkStatus() {
        if (UserConfig.autoUploadByMobile == false) {
            return checkWifiStatus();
        } else {
            NetworkInfo info = (NetworkInfo) ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

            if (info!=null && info.isConnected()) {
                return true;
            }
        }
        return false;
    }

    public void autoWhichShouldStart() {
        locationUpdater.startGPS(this);

        ssListener.autoStartService(tm);
        slListener.startService(sensorManager);
        senseHandler.post(SenseAllCellRunnable);
        psListener.startService(tm);

        MainActivity.previous = new TrafficSnapshot(RunIntentService.this);
        senseAppUsageHandler.postDelayed(SenseAppUsageRunnable, senseAppUsageInterval);

        //registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    // Listeners stop listening for the updates of specified managers
    public void stopRun() {
        ssListener.stopService(tm);
        slListener.stopService(sensorManager);
        psListener.stopService(tm);
        senseHandler.removeCallbacks(SenseAllCellRunnable);
        senseAppUsageHandler.removeCallbacks(SenseAppUsageRunnable);
//        if (batteryInfoReceiver!=null) {
//            unregisterReceiver(batteryInfoReceiver);
//            //batteryInfoReceiver=null;
//        }

        locationUpdater.stopGPS();
    }

    // New all the listeners and handlers that will be used later
    public void initVar() {
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
//        //PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | SCREEN_DIM_WAKE_LOCK, "MyWakelockTag");
//        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "myapp:mywakelocktag");
//        //PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
//        wakeLock.acquire();
//        //wakeLock.release();
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        globalWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:mywakelocktag0");
        globalWakeLock.acquire();

        ssListener = new SignalStrength(this);
        psListener = new PhoneState(this);
        slListener = new SensorList();
        //sensor
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        locationUpdater = new Location(RunIntentService.this);

        senseHandler = new Handler(Looper.getMainLooper());
        bigRoundHandler = new Handler(Looper.getMainLooper());
        senseAppUsageHandler = new Handler(Looper.getMainLooper());

        allCellInfo = null;
        allWiFiInfo = null;
        servingWifiInfo = "null";

        batteryInfoReceiver = new BatteryInfoReceiver();

        sdf = new SimpleDateFormat("yyyyMMddHHmm");
        logTimeSdf = new SimpleDateFormat("HH:mm:ss:SSS");

        neighborCellList = new ArrayList<>();
    }

    // For wifi frequency to channel
    public static int convertFrequencyToChannel(int freq) {
        if (freq >= 2412 && freq <= 2484) {
            return (freq - 2412) / 5 + 1;
        } else if (freq >= 5170 && freq <= 5825) {
            return (freq - 5170) / 5 + 34;
        } else {
            return -1;
        }
    }

    /**
     * Get the cpu usage information from /proc/stat
     * @return the usage time
     */
    // Ref: http://www.linuxhowtos.org/System/procstat.htm
    private static float readUsage() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" ");

            // Log.d("/proc", "/stat: " + load);
            // Log.d("0202***", "file tokens: " + "1: " + toks[1] + "," + " 2: " + toks[2] + ","
            //        + " 3: " + toks[3] + "," + " 4: " + toks[4] + "," + " 5: " + toks[5] + ","
            //        + " 6: " + toks[6] + " 7: " + toks[7] + "," + " 8: " + toks[8]);


            long idle1 = Long.parseLong(toks[5]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            try {
                Thread.sleep(360);
            } catch (Exception e) {
            }

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" ");

            long idle2 = Long.parseLong(toks[5]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            if (((cpu2 + idle2) - (cpu1 + idle1)) <= 0)
                return (float) 0;
            else
                return (float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    }


    // Show time zone
    // Ref: http://stackoverflow.com/questions/15068113/how-to-get-the-timezone
    // -offset-in-gmtlike-gmt700-from-android-device
    public static String displayTimeZone() {
        TimeZone tz = TimeZone.getDefault();

        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());

        String offset = String.format("%02d:%02d",
                Math.abs(offsetInMillis / 3600000),
                Math.abs((offsetInMillis / 60000) % 60));

        return (offsetInMillis >= 0 ? "+" : "-") + offset;
    }

    //----------------------------------------------------------


    // Show time in milliseconds
    public static String displayTimeMilli() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        return sdf.format(currentTimeMillis());
    }


    /**
     * Show phone info to user
     * Used in InfoFragment class
     * @return CPU, RAM info
     */
    public static String displayPhoneState() {

        double cpu_uti = readUsage();
        DecimalFormat df = new DecimalFormat("##.00");
        cpu_uti = Double.parseDouble(df.format(cpu_uti));
        return "\t\"CPU\": " + cpu_uti
                + "\n\t\"RAM\": " + ram_uti;
    }

    public static String cpuUti() {
        double cpu_uti = readUsage();
        DecimalFormat df = new DecimalFormat("##.00");
        cpu_uti = Double.parseDouble(df.format(cpu_uti));
        return String.valueOf(cpu_uti);
    }

    //Ref: https://developer.android.com/reference/android/app/ActivityManager.MemoryInfo.html#totalMem
    public void ram() {
        ActivityManager actManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        long totalMemory = memInfo.totalMem;
        long Free = memInfo.availMem;
        long Busy = totalMemory - Free;
        DecimalFormat df = new DecimalFormat("##.00");
        ram_uti = (double) Busy / (double) totalMemory;
        ram_uti = Double.parseDouble(df.format(ram_uti));
        /*Log.d("LINYIHAO", "Total memory: " + bytesToHuman(totalMemory)
                                + " Free: " + bytesToHuman(Free)
                                + " Busy: " + bytesToHuman(Busy));*/
    }
    //----------------------------------------------

    private Runnable SenseAllCellRunnable = new Runnable() { // Use SenseThread
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
//        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void run() {
            if(closeFlag==1) {
                return;
            }
            if (SenseThread==null || (SenseThread!=null && !SenseThread.isAlive())) {
                SenseThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("threadId:", "SenseAllCellRunnable thread id = " + String.valueOf(Thread.currentThread().getId()));
//                        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
//                        //PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | SCREEN_DIM_WAKE_LOCK, "MyWakelockTag");
//                        //PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
//                        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:mywakelocktag");
//                        wakeLock.acquire();
//                        //wakeLock.release();
                        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                        //Log.d("Model","Brand: " + Build.BRAND + " Model: " + Build.MODEL + " Proc.: " + Build.PRODUCT + " Man.: " + Build.MANUFACTURER );

                        // For handling management of wifi access
                        //wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);


                        if (wifi.isWifiEnabled() == false) {
                            //Log.d("LINYIHAO", " wifi is disabled");
                            wifiState = false;
                        } else {
                            wifiState = true;
                            if (mWifi.isConnected()) {
                                WifiInfo wifiInfo = wifi.getConnectionInfo();
                                //networkType = mWifi.getTypeName();

                                int ipAddress = wifiInfo.getIpAddress();
                                String ip = String.format("%d.%d.%d.%d",
                                        (ipAddress & 0xff),
                                        (ipAddress >> 8 & 0xff),
                                        (ipAddress >> 16 & 0xff),
                                        (ipAddress >> 24 & 0xff));
                                servingIP = ip;
                                servingSSID = wifiInfo.getSSID();
                                servingBSSID = wifiInfo.getBSSID();
                                servingMAC = wifiInfo.getMacAddress();

                                // need checked , pei-yu 0206/2018
                                servingLevel = String.valueOf(wifiInfo.getRssi()); // Returns the received signal strength indicator of the current 802.11 network, in dBm.

                                int version = Build.VERSION.SDK_INT;
                                if (version >= 20) {
                                    servingFreq = String.valueOf(wifiInfo.getFrequency());
                                    servingChan = String.valueOf(convertFrequencyToChannel(wifiInfo.getFrequency()));
                                } else {
                                    servingFreq = "Null";
                                    servingChan = "Null";
                                }

                                servingSpeed = String.valueOf(wifiInfo.getLinkSpeed());

                                Log.d("LINYIHAO", " wifi: SSID: " + wifiInfo.getSSID()
                                                + " signal: " + wifiInfo.getRssi() + "dBm"
                                                + " speed: " + wifiInfo.getLinkSpeed() + "Mbps"
                                        // + " frequency: " + wifiInfo.getFrequency()+"GHz"
                                );

                                //---------Hank: 2016/08/23  add scan wifi function---------------
                                //ref_1: http://dean-android.blogspot.tw/2013/08/androidwi-fiip.html
                                //ref_2: http://www.theappguruz.com/blog/access-wi-fi-data-android
                                //ref_3: http://bryceknowhow.blogspot.tw/2014/02/android-wifimanagerget-wifissid-power.html

                            } else {
                                servingIP = "Null";
                                servingSSID = "Null";
                                servingBSSID = "Null";
                                servingMAC = "Null";
                                servingLevel = "Null";
                                servingFreq = "Null";
                                servingChan = "Null";
                                servingSpeed = "Null";
                            }

                            //this.adapter = new SimpleAdapter(MainActivity.this, arraylist, R.layout.list,
                            //        new String[] {"ssid","power","freq"}, new int[] {R.id.ssid, R.id.power, R.id.freq});

                            new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                            wifi.startScan();

                            results = wifi.getScanResults();
                            //mWifiConfigurationList = wifi.getConfiguredNetworks();

                            for (int i = 0; i < results.size(); i++) {
                                // The current wifi environment around the phone
//                    Log.d("LINYIHAO", "SSID-"+ i+ ": "
//                            + results.get(i).SSID
//                            + "\tlevel: " + results.get(i).level + "dBm"
//                            +"\tFreq: "+ results.get(i).frequency+ "MHz"
//                            +"\tChan: " + convertFrequencyToChannel(results.get(i).frequency)
//                            +"\tcap: "+ results.get(i).capabilities
//                            );
                            }

                            allWiFiInfo = JsonParser.neighborWifiInfo;
                            servingWifiInfo = JsonParser.servingWifiInfo;

                            //Log.d("LINYIHAO",getInfo());
                            //Log.d("LINYIHAO","CPU Uti: " + readUsage());
                        }
                        //connectionState =  mWifi.getState().toString();

                        //----List<ScanResult> results = wifi.getScanResults();

                        //RAM memory
                        ram();


                        //-------------Hank: 2016/08/29 add Monitor screen on/off-------------
                        //ref_1: http://stackoverflow.com/questions/8317331/detecting-when-screen-is-locked


                        KeyguardManager myKM = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

                        if (myKM.inKeyguardRestrictedInputMode()) {
                            //it is locked
                            //Log.d("LINYIHAO", "Lock");
                        } else {
                            //it is not locked
                            //Log.d("LINYIHAO", "Not Lock!!");
                        }

                        // Monitor network state
                        Log.d("www", setCurrentNetworkState());

                        //-------------------------------------------

                        //Log.d("LINCYU", "SenseAllCellRunnable (1)");

                        logTime = new Date();
                        //Log.d(TagName, "SenseAllCellRunnable");

                        /*List<CellInfo> cellInfoList;
                        cellInfoList = tm.getAllCellInfo();*/
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                            SubscriptionManager subManager = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                            if (ContextCompat.checkSelfPermission(RunIntentService.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                                List<SubscriptionInfo> subInfoList = subManager.getActiveSubscriptionInfoList();

                                if (subInfoList != null) {
                                    for (int i = 0; i < subInfoList.size(); i++) {
                                        int subID = subInfoList.get(i).getSubscriptionId();
                                        int simPosition = subInfoList.get(i).getSimSlotIndex();

                                        if (subManager.isNetworkRoaming(subID)) {
                                            Log.d("9487", "Simcard in slot " + simPosition + " has subID == " + subID + " and it is in ROAMING");
                                        } else {
                                            Log.d("9487", "Simcard in slot " + simPosition + " has subID == " + subID + " and it is HOME");
                                        }
                                    }
                                }
                            }
                        }

                        int lac = 0;
                        if (ContextCompat.checkSelfPermission(RunIntentService.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            GsmCellLocation loc = (GsmCellLocation) tm.getCellLocation();

                            if (loc == null) { //sim card 1 don't detect
                                stopService();
                                errorFlag = true;
                                notifyError();
                                return;
                            }

                            int cellid = loc.getCid();
                            lac = loc.getLac();
                            servingCellId = cellid;
                            neighborCellList.clear();
                        }

                        List<CellInfo> cellInfoList = null;
                        if (ContextCompat.checkSelfPermission(RunIntentService.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            cellInfoList = tm.getAllCellInfo();
                        }
                        if (cellInfoList == null) {
                            return;
                        }

                        for (CellInfo cellInfo : cellInfoList) {
                            int pci, rsrp, rsrq, signalstrength = -1;
                            int ci = 0;
                            NeighborCellInfo neighborCellInfo = new NeighborCellInfo();
                            String type;
                            
                            if (cellInfo instanceof CellInfoLte) {
                                type = "LTE";
                                pci = ((CellInfoLte) cellInfo).getCellIdentity().getPci();
                                ci = ((CellInfoLte) cellInfo).getCellIdentity().getCi();

                                /**
                                 * Ref:
                                 * 1. http://stackoverflow.com/questions/16224684/how-to-get-lte-cell-location-in-android
                                 * 2. http://www.it610.com/article/2582329.htm
                                 */

                    /*Log.d("test5566", "RunService PCI: " + ((CellInfoLte)cellInfo).getCellIdentity().getPci()
                            + " dBm: " + ((CellInfoLte)cellInfo).getCellSignalStrength().getDbm()
                            + " CI: " + ((CellInfoLte)cellInfo).getCellIdentity().getCi()
                            + " TAC: " + ((CellInfoLte)cellInfo).getCellIdentity().getTac()
                            + " cellid:" + cellid
                            + " lac: " + lac
                            + " SSL: " + SignalStrength.AtCellRSRP);*/

                                if (cellInfo.isRegistered()) {

                                    String onecellinfo = cellInfo.toString();
                                    Log.d("7887", "gg: " + onecellinfo);

                                    cellInfoType = "LTE";
                                    // cast to CellInfoLte and call all the CellInfoLte methods you need

                                    // Gets RSRP cell signal strength: (the unit is dbm)
                                    lteCellRSRP = ((CellInfoLte) cellInfo).getCellSignalStrength().getDbm();

                                    Log.d("7887", "yy: " + lteCellRSRP);

                                    // Gets the LTE cell identity: (returns 28-bit Cell Identity, Integer.MAX_VALUE if unknown)
                                    lteCellID = ((CellInfoLte) cellInfo).getCellIdentity().getCi();

                                    // Gets the LTE MCC: (returns 3-digit Mobile Country Code, 0..999, Integer.MAX_VALUE if unknown)
                                    lteCellMCC = ((CellInfoLte) cellInfo).getCellIdentity().getMcc();

                                    // Gets the LTE MNC: (returns 2 or 3-digit Mobile Network Code, 0..999, Integer.MAX_VALUE if unknown)
                                    lteCellMNC = ((CellInfoLte) cellInfo).getCellIdentity().getMnc();

                                    // Gets the LTE PCI: (returns Physical Cell Id 0..503, Integer.MAX_VALUE if unknown)
                                    lteCellPCI = ((CellInfoLte) cellInfo).getCellIdentity().getPci();

                                    // Gets the LTE TAC: (returns 16-bit Tracking Area Code, Integer.MAX_VALUE if unknown)
                                    lteCellTAC = ((CellInfoLte) cellInfo).getCellIdentity().getTac();

                                    //Log.d("test1117",  " RunSer(CellInfoLte): " + AtCellRSRP);
                                }
                            } else if (cellInfo instanceof CellInfoWcdma) {
                                type = "Wcdma";
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                    pci = ((CellInfoWcdma) cellInfo).getCellIdentity().getPsc();
                                } else {
                                    pci = Integer.MAX_VALUE;
                                }
                                //add wcdma cell id
                                ci = ((CellInfoWcdma) cellInfo).getCellIdentity().getCid();
                                if (cellInfo.isRegistered()) {
                                    //Log.d(TagName, "3G cellinfo");

                                    String onecellinfo = cellInfo.toString();
                                    Log.d("7887", "wcdma: " + onecellinfo);

                                    cellInfoType = "Wcdma";
                                    wcdmaAtCellSignalStrength = ((CellInfoWcdma) cellInfo).getCellSignalStrength().getDbm();
                                    wcdmaAtCellID = ((CellInfoWcdma) cellInfo).getCellIdentity().getCid();
                                    wcdmaAtCellMCC = ((CellInfoWcdma) cellInfo).getCellIdentity().getMcc();
                                    wcdmaAtCellMNC = ((CellInfoWcdma) cellInfo).getCellIdentity().getMnc();
                                    wcdmaAtCellPsc = ((CellInfoWcdma) cellInfo).getCellIdentity().getPsc();
                                    wcdmaAtCellLac = ((CellInfoWcdma) cellInfo).getCellIdentity().getLac();
                        /*Log.d(TagName, "WcdmaAtCellSignalStrength:" + wcdmaAtCellSignalStrength +
                                " WcdmaAtCellID:" + wcdmaAtCellID +
                                " WcdmaAtCellMCC:" + wcdmaAtCellMCC +
                                " WcdmaAtCellMNC:" + wcdmaAtCellMNC +
                                " WcdmaAtCellPsc:" + wcdmaAtCellPsc +
                                " WcdmaAtCellLac:" + wcdmaAtCellLac);*/
                                }
                            } else if (cellInfo instanceof CellInfoGsm) {
                                type = "Gsm";
                                pci = ((CellInfoGsm) cellInfo).getCellIdentity().getPsc();
                            } else {
                                type = "unknown";
                                pci = Integer.MAX_VALUE;
                            }

                            neighborCellInfo.type = type;
                            neighborCellInfo.pci = pci;
                            neighborCellInfo.ci = ci;
                            neighborCellInfo.lac = lac;


                            String onecellinfo = cellInfo.toString();
                            //Log.d(TagName, "onecellinfo:"+onecellinfo);
                            String[] cellInfoParts = onecellinfo.split(" ");

                            //Log.d("test5566", "Info " + onecellinfo);


                            for (String str : cellInfoParts) {
                                if (!cellInfo.isRegistered()) {
                                    Log.d("LTECell", "1"+str);
                                } else {
                                    Log.d("LTECell", str);
                                }

                                String[] parts = str.split("=");

                                //治標不治本
                                if (parts.length ==0) {
                                    continue;
                                }

                                if (parts[0].compareTo("rsrp") == 0) {
                                    rsrp = Integer.valueOf(parts[1]);
                                    neighborCellInfo.RSRP = rsrp;

                                    if (cellInfo.isRegistered()) {
                                        lteCellRSRP = (lteCellRSRP > 0 ? rsrp : lteCellRSRP);
                                    }

                                    //Log.d("test5566", "In if RSRP: " + rsrp);
                                } else if (parts[0].compareTo("rsrq") == 0) {
                                    rsrq = Integer.valueOf(parts[1]);
                                    neighborCellInfo.RSRQ = rsrq;
                                    //Log.d("test5566", "In if RSRQ: " + rsrq);

                                    if (cellInfo.isRegistered()) {
                                        lteCellRSRQ = (lteCellRSRQ > 0 ? rsrq : lteCellRSRQ);
                                    }

                                } else if (parts[0].compareTo("ss") == 0) {
                                    if (type.equals("LTE")) {

                                    } else if (type.equals("Wcdma")) {
                                        int level = Integer.valueOf(parts[1]);
                                        int dBm = 0;
                                        int asu = (level == 99 ? Integer.MAX_VALUE : level);
                                        if (asu != Integer.MAX_VALUE) {
                                            dBm = -113 + (2 * asu);
                                        } else {
                                            dBm = Integer.MAX_VALUE;
                                        }

                                        if (dBm == Integer.MAX_VALUE) {
                                            signalstrength = Integer.MAX_VALUE;
                                            neighborCellInfo.SignalStrength = signalstrength;
                                        } else {
                                            signalstrength = dBm;
                                            neighborCellInfo.SignalStrength = signalstrength;
                                        }

                                        //Log.d("LINCYU", "PCI: " + pci + "\tdBm: " + dBm);

                                    } else if (type.equals("Gsm")) {
                                        int level = Integer.valueOf(parts[1]);
                                        int dBm = 0;
                                        int asu = (level == 99 ? Integer.MAX_VALUE : level);
                                        if (asu != Integer.MAX_VALUE) {
                                            dBm = -113 + (2 * asu);
                                        } else {
                                            dBm = Integer.MAX_VALUE;
                                        }

                                        if (dBm == Integer.MAX_VALUE) {
                                            signalstrength = Integer.MAX_VALUE;
                                            neighborCellInfo.SignalStrength = signalstrength;
                                        } else {
                                            signalstrength = dBm;
                                            neighborCellInfo.SignalStrength = signalstrength;
                                        }
                                    }
                                }
//                    allCellInfo = allCellInfo.concat(str + "\n");
                            }

                            if (pci==Integer.MAX_VALUE || signalstrength==Integer.MAX_VALUE) {
                                continue;
                            } else {
                                if (!cellInfo.isRegistered()){
                                    neighborCellList.add(neighborCellInfo);
                                }
                            }
                        }
//            FileMaker.write(JsonParser.NeighborCellInfoToJson());
                        allCellInfo = JsonParser.allCellInfo;

                        /*if (ScreenStateReceiver.screen_state == "off") {
                            FileMaker.write(JsonParser.servingCellInfoToJson());
                        }*/
                        if(second_count >= 3){
                            FileMaker.write(JsonParser.servingCellInfoToJson(RunIntentService.this));
                            FileMaker.write(JsonParser.NeighborCellInfoToJson(RunIntentService.this));
                            second_count=0;
                        }

                        senseHandler.postDelayed(SenseAllCellRunnable, MainActivity.flashInterval);
//                        wakeLock.release();
                        second_count++;
                        //thread end
                    }
                });
                SenseThread.start();
            }
        }
    };

    private Runnable SenseAppUsageRunnable = new Runnable() {
        public void run() { // Use SenseAppUsageThread
            if (closeFlag == 1) {
                return;
            }
            if (SenseAppUsageThread==null || (SenseAppUsageThread!=null && !SenseAppUsageThread.isAlive())) {
                SenseAppUsageThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("threadId:","SenseAppUsageRunnable thread id = " + String.valueOf(Thread.currentThread().getId()));
                            MainActivity.latest = new TrafficSnapshot(RunIntentService.this);
                            MainActivity.latest.calculateUsage(MainActivity.previous);
                            //write the avg value to the file
                            //Log.d(TagName, JsonParser.CallInfoToJson());

                            FileMaker.write(JsonParser.appsInfoToJson(RunIntentService.this));

//                      //------
                            if (wifiState) {
                                FileMaker.write(JsonParser.wifiInfoToJson(RunIntentService.this));
                            }

                            // Write neighborCell info to upload file!
                            //FileMaker.write(JsonParser.NeighborCellInfoToJson());

                            MainActivity.previous = MainActivity.latest;
                            pingMethod();
                            senseAppUsageHandler.postDelayed(SenseAppUsageRunnable, senseAppUsageInterval);
                            //thread end-----------
                    }
                });
                SenseAppUsageThread.start();
            }
//            Log.d(TagName, "SenseAppUsageRunnable postDelayed "+String.valueOf(senseAppUsageInterval/1000)+"s");
            //senseAppUsageHandler.postDelayed(SenseAppUsageRunnable, senseAppUsageInterval);
        }
    };

    public void startService() {
        FileMaker.fileFirstWrite = true;

        MainActivity.previous = new TrafficSnapshot(RunIntentService.this);
        //MainActivity.latest = null;
        MainActivity.startServiceTime = currentTimeMillis();

        RunIntentService.firstRoundFlag = true;
        RunIntentService.runFlag = true;
    }

    public void stopService() {
        stopRun();
        //calculate the Avg values
        SignalStrength.calculateAvg();
        PhoneState.calculateAvg();
        FileMaker.closeWriter();
        try {
            //get the signature of the data and write it to the file "file.sig"
            moutCypher.openAndWriteFileInBytes(moutCypher.signString(BrowseFile.getStringFromFile(RunIntentService.recordFileSheerPath)), RunIntentService.recordFileSheerPath + ".sig");
        } catch (Exception e) {
            e.printStackTrace();
            String ErrorPath = MainActivity.logPath + "ErrorLog" + RunIntentService.recordFilePrefix;
            try {
                java.io.FileWriter writer = new java.io.FileWriter(ErrorPath, true);
                writer.write(e.toString());
                writer.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
Log.d("getgeteget", "false");
        RunIntentService.runFlag = false;
    }

    private Runnable SenseRunnable = new Runnable() {
        public void run() {
            Log.d("threadId:", "SenseRunnable thread id:" + String.valueOf(Thread.currentThread().getId()));
            stopService();
            try {
                uploadCount++;
                if (uploadCount>=5 && checkNetworkStatus()){
                    uploadCount = 0;
                    SFTPController ftpcontroller = new SFTPController(RunIntentService.this);
//                    ftpcontroller.execute();
                    ftpcontroller.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    Thread.sleep(4000);
                }
                Thread.sleep(MainActivity.flashInterval); // 1 sec
            } catch (InterruptedException e) {
                e.printStackTrace();
                String ErrorPath = MainActivity.logPath + "ErrorLog" + RunIntentService.recordFilePrefix;
                try {
                    java.io.FileWriter writer = new java.io.FileWriter(ErrorPath, true);
                    writer.write(e.toString());
                    writer.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            if (MainActivity.isLoginSuccess) {
                getUserID();
                MainActivity.isLoginSuccess = false;
            }

            startService();
            createLogAndWriter();
            autoWhichShouldStart();

            senseHandler.postDelayed(SenseRunnable, bigInterval);
        }
    };

    public static String floatForm(double d) {
        return new DecimalFormat("#.##").format(d);
    }

    public static String bytesToMega(long size) {
        long Kb = 1 * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        return floatForm((double) size / Mb);
    }

    public static String pingMethod() {
        String str = "null";
        //-------Hank: 2016/10/28 add Ping function
        try {
            //Log.d("w5566", "info is available");
            // 不像 Windows 的 ping 預設執行 4 次
            // Linux 的 ping 預設是一直執行的，所以一定要下參數指定次數
            // 不然會造成您的 app 無回應
            Log.d("ping", "pingMethod: ");
            // 試著 ping 168.95.1.1, 4 次, 參數 -c 4
            Process process = new ProcessBuilder().command("/system/bin/ping", "-c 4", "www.google.com.tw")
                    .redirectErrorStream(true)
                    .start();
            try {
                String strPing = "";
                InputStream in = process.getInputStream();
                OutputStream out = process.getOutputStream();
                InputStreamReader reader = new InputStreamReader(in, "utf-8");
                int i;
                //Log.d("w5566", "read instream");
                while ((i = in.read()) != -1) { // -1: reach the end of the stream
                    strPing = strPing + (char) i;
                }
                out.close();
                in.close();
                reader.close();
                Log.d("ping", "strPing ==== "+strPing);
                if (strPing.indexOf("ttl") >= 0) {
                    //Log.d("w5566", "strPing 字串中有 ttl");
                    //result = true;
                } else {
                    //Log.d("w5566", "Ping 沒回應");
                    //result = false;
                }
            } catch (Exception e) {
                //result = false;
            } finally {
                // Notice: remember to release process!
                process.destroy();
            }
        } catch (Exception e) {
            //Log.d("w5566", "Exception--> " + e.getMessage());
            //result = false;
        }

        return str;
    }

    public String setCurrentNetworkState() { // Ref: https://dotblogs.com.tw/pou/2011/07/22/31982
        StringBuilder tBuilder = new StringBuilder();

        // Get the current network connection mode
        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo tInfo = connManager.getActiveNetworkInfo();
        if (tInfo != null) {
            tBuilder.append(String.format("Network Type: %s\n", tInfo.getTypeName()));
            tBuilder.append(String.format("Network State: %s\n", tInfo.getState().toString()));
            tBuilder.append(String.format("Network is Connecting: %s\n", tInfo.isConnectedOrConnecting()));
            tBuilder.append(String.format("Network is Connected: %s\n", tInfo.isConnected()));
            tBuilder.append(String.format("Network is Available: %s\n", tInfo.isAvailable()));
            tBuilder.append(String.format("Network is Roaming: %s\n", tInfo.isRoaming()));
            tBuilder.append(String.format("Network is Failover: %s\n", tInfo.isFailover()));
//                     switch (netType) {
//                        case TelephonyManager.NETWORK_TYPE_GPRS:
//                        case TelephonyManager.NETWORK_TYPE_EDGE:
//                        case TelephonyManager.NETWORK_TYPE_CDMA:
//                        case TelephonyManager.NETWORK_TYPE_1xRTT:
//                        case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
//                            resultType = "2G";
//                        case TelephonyManager.NETWORK_TYPE_UMTS:
//                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
//                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
//                        case TelephonyManager.NETWORK_TYPE_HSDPA:
//                        case TelephonyManager.NETWORK_TYPE_HSUPA:
//                        case TelephonyManager.NETWORK_TYPE_HSPA:
//                        case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
//                        case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
//                        case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
//                            resultType = "3G";
//                        case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
//                            resultType = "4G";
//                        default:
//                            resultType = "null";
            networkType = tInfo.getTypeName().toString();
            connectionState = tInfo.getState().toString();

        } else {
            networkType = "null";
            connectionState = "DISCONNECTED";
            tBuilder.append("No use Network Mode!!");
        }
        return tBuilder.toString();
    }

    public void connectServer(String method, String path, String info){
        HttpsConnection httpsConnection = new HttpsConnection(this);
        httpsConnection.setJsonParser(JsonParser);
        httpsConnection.setMethod(method, info);
        httpsConnection.execute(path);
        Log.d("1003conn", info);
    }

    public void getUserID() {
        Log.d("1003info",UserConfig.myUserName);
        if (!UserConfig.myUserName.equals("DefaultUser")) {
            String info = "username=" + UserConfig.myUserName;
            connectServer("POST", "/getUID", info);
        }
    }
}
