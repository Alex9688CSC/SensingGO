package edu.nctu.wirelab.sensinggo.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


import edu.nctu.wirelab.sensinggo.BuildConfig;
import edu.nctu.wirelab.sensinggo.Connect.HttpsConnection;
import edu.nctu.wirelab.sensinggo.Connect.SFTPController;
import edu.nctu.wirelab.sensinggo.EventViewPagerAdapter;
import edu.nctu.wirelab.sensinggo.IntroActivity;
import edu.nctu.wirelab.sensinggo.MainActivity;
import edu.nctu.wirelab.sensinggo.R;
import edu.nctu.wirelab.sensinggo.RunIntentService;
import edu.nctu.wirelab.sensinggo.ShowDialogMsg;

public class UserFragment extends Fragment {
    public static TextView uploadProgress;//, runningText;
    public static boolean tempAutoUploadByMobile;

    //private Button uploadButton, quitButton;
    private static ProgressBar pbUpload;
    //private TextView idTextView;
    private static TextView pbText;
    //private Button appIntroButton;
    private CheckBox autoCheckBox;
    //private Button weblinkButton;
    //private Button appGuideButton;
    //private Button testButton;

    private static ImageButton appIntroButton, appGuideButton, fanPageButton, shareButton, uploadButton, websiteButton;
    private static ImageButton logoutButton;
    private static TextView textViewUploadData;
    private static TextView textViewLogout;
    private TextView textViewFeedback;
    private ViewPager viewPagerEvent;
    private TabLayout tabLayout;

    SimpleDateFormat sdf;
    SimpleDateFormat sdfMilli;
    Date LogDate;
    Context mContext;

    int currentPage = 0;
    int pagesNum = 3;
    Timer timer;
    final long DELAY_MS = 1000;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 5000; // time in milliseconds between successive task executions.
    boolean isScrolling = false;

    final Runnable update = new Runnable() {
        public void run() {
            //when user is scrolling, stop auto scrolling.
            if (isScrolling) {
                isScrolling = false;
                return;
            }

            if (currentPage == pagesNum-1) {
                currentPage = 0;
            } else {
                currentPage++;
            }
            viewPagerEvent.setCurrentItem(currentPage, true);
        }
    };

    private Handler updateViewHandler = new Handler();

    public UserFragment() {
    }

    //-------------------
    public void SetContext(Context ctx) {
        mContext = ctx;
    }

    Runnable updateView = new Runnable() {
        @Override
        public void run() {

            Activity activity = getActivity();
            if(activity != null) {
                //idTextView.setText(String.format("%s %s", getString(R.string.hi), UserConfig.myUserName));
            }
            updateViewHandler.postDelayed(updateView, 2000);

        }
    };

    Runnable updatePB = new Runnable() {
        @Override
        public void run() {
            pbUpload.setProgress(0);

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        //idTextView = (TextView) view.findViewById(R.id.IDTextView);
        //runningText = (TextView) view.findViewById(R.id.runningText);
        uploadProgress = (TextView) view.findViewById(R.id.uploadProgress); // no use in new UI
        textViewUploadData = (TextView) view.findViewById(R.id.textViewUploadData);
        textViewLogout = (TextView) view.findViewById(R.id.textViewLogout);

        autoCheckBox = (CheckBox) view.findViewById(R.id.check_settings);


        pbUpload = (ProgressBar) view.findViewById(R.id.pbUpload);
        pbText = (TextView) view.findViewById(R.id.pbText);

        appIntroButton = (ImageButton) view.findViewById(R.id.AppIntroButton);
        appGuideButton = (ImageButton) view.findViewById(R.id.AppGuideButton);
        fanPageButton = (ImageButton) view.findViewById(R.id.FanPageButton);
        shareButton = (ImageButton) view.findViewById(R.id.ShareButton);
        uploadButton = (ImageButton) view.findViewById(R.id.UploadButton);
        websiteButton = (ImageButton) view.findViewById(R.id.OfficialWebsiteButton);
//        specialEventButton = (ImageButton) view.findViewById(R.id.AppGuideButton);
        //testButton = (Button) view.findViewById(R.id.TestButton);

        textViewFeedback = view.findViewById(R.id.textViewFeedback);

        viewPagerEvent = view.findViewById(R.id.viewPagerEvent);
        PagerAdapter adapter = new EventViewPagerAdapter(getActivity());
        viewPagerEvent.setAdapter(adapter);

        tabLayout =  view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPagerEvent, true);

        sdf = new SimpleDateFormat("yyyyMMddHHmm");
        sdfMilli = new SimpleDateFormat("yyyyMMddHHmmss");
        LogDate = new Date();
        pbText.setText(getString(R.string.upload_progress_percentage, 0));
        //weblink.setText("https://sensinggo.org/");

        // I don't know why I can't use "pbUpload.setProgress(0);" directly to reset the progressbar
        //updateViewHandler.postDelayed(updatePB,100);
        //updateViewHandler.post(updateView);

//        showHideLogoutBtn();

//        startAutoSwipe();

        hideProgressBar();


        viewPagerEvent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.d("7476","onPageScrolled");
                isScrolling = true;
            }

            @Override
            public void onPageSelected(int position) {
//                Log.d("7476","onPageSelected, position = " + position);
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                Log.d("7476","onPageScrollStateChanged, state = " + state);
                // scrolling is end
                if (state == 0) {
                    isScrolling = false;
                }
            }
        });


        uploadButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (RunIntentService.runFlag) {
                    if (checkNetworkStatus()) {
                        //-------------
                        /*String variables = "5";
                        HttpsConnection httpsconnection = new HttpsConnection(getActivity());
                        httpsconnection.setActivity(getActivity());
                        httpsconnection.setMethod("GET", variables);
                        //httpsconnection.execute("/signal/api/appversion");
                        httpsconnection.execute("/appversion");

                        variables = "code=" + "psTc8qWfbyYfOknpJd92DoYE";
                        HttpsConnection httpsPostFKey = new HttpsConnection(getActivity());
                        httpsPostFKey.setActivity(getActivity());
                        httpsPostFKey.setMethod("POST", variables);
                        //httpsPostFKey.execute("/signal/api/upload");
                        httpsPostFKey.execute("/upload");
                        Log.d("FTP56", "result: "+httpsPostFKey.getResultKey());*/



                        SFTPController ftpController = new SFTPController(getActivity());
                        ftpController.setProgressBar(pbUpload, pbText, uploadProgress);
//                        ftpController.execute();
                        // Use "executeOnExecutor" to let multiple AsyncTasks execute in parallel
                        ftpController.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        showProgressBar();
                    } else {
                        ShowDialogMsg.showDialog(getString(R.string.connectionerror));
                    }
                }
            }
        });

        appIntroButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), IntroActivity.class);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

//        quitButton.setOnClickListener(new Button.OnClickListener() {
//            public void onClick(View v) {
//                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
//                        .setMessage(getString(R.string.closeAPP))
//                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
////                            ShowDialogMsg.showDialog("Cancel");
//                            }
//                        })
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                                Intent intent = new Intent(getActivity(), MainActivity.class);
//
//                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                intent.putExtra("LOGOUT", true);
//                                startActivity(intent);
//                                getActivity().finish();
//
//                            }
//                        }).show();
//            }
//        });
//        autoCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
//                if(isChecked) {
//                    UserConfig.setAutoUploadByMobile(true);
//                } else {
//                    UserConfig.setAutoUploadByMobile(false);
//                }
//                UserConfig.saveConfigTo(MainActivity.configPath);
//            }
//        });

//        logoutButton.setOnClickListener(new Button.OnClickListener() {
//            public void onClick(View v) {
//                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
//                        .setMessage(getString(R.string.logoutApp))
//                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
////                            ShowDialogMsg.showDialog("Cancel");
//                            }
//                        })
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                                try {
//                                    File file = new File(MainActivity.configPath);
//                                    boolean deleted = file.delete();
//                                    Log.d("deletelog", String.valueOf(deleted));
//                                    Intent intent = new Intent(getActivity(), MainActivity.class);
//                                    startActivity(intent);
//                                    getActivity().finish();
//
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//                        }).show();
//            }
//        });

        fanPageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Uri webpage = Uri.parse("https://www.facebook.com/SensingGO/");
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        websiteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Uri webpage = Uri.parse("https://sensinggo.org/");
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        appGuideButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Uri webpage = Uri.parse("https://sensing-go.gitbook.io/guide/");
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "SensingGO");
                String shareMessage = getString(R.string.share_msg) + "\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
            }
        });

        textViewFeedback.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Uri webpage = Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLSfgOfGbhXub8rSIR2_dbOv8F3jNc_xHlA_3JhgWEZ-_R28VYw/viewform");
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

//        specialEventButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                String specialInfo = "username=" + UserConfig.myUserName;
//                Log.d("sdfwf",UserConfig.myUserName);
//                connectServer("POST", "/specialEvent", specialInfo);
//            }
//        });


        if (RunIntentService.runFlag == true) {
            //setRunningText(getString(R.string.app_is_running));
        } else {
            //setRunningText(((MyApplication) getActivity().getApplication()).appRunningStatus);
        }

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        startAutoSwipe();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAutoSwipe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public boolean checkNetworkStatus() {
        NetworkInfo info = (NetworkInfo) ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (info!=null && info.isConnected()) {
            return true;
        }
        return false;
    }

    public static void showUploadSuccess(Context context, int type) {
        //type 1: manually press button
        //type 2: auto upload
//        if(uploadProgress != null) {
//            if (type == 1) {
//                //    uploadProgress.setText("volume:" + SFTPController.uploadBytes / 1024 + " KB");
//            } else if (type == 2) {
//                uploadProgress.setText(context.getString(R.string.auto_upload_message, SFTPController.uploadBytes / 1024));
//            }
//        }
    }

    public static void showHideLogoutBtn(){
        File configF = new File(MainActivity.configPath);
        boolean exists = configF.exists();
        if(exists){     // user has logged in
            textViewLogout.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
        }
        else{
            textViewLogout.setVisibility(View.INVISIBLE);
            logoutButton.setVisibility(View.INVISIBLE);
        }
    }

    public static void hideProgressBar() {
        if (textViewUploadData != null && pbText != null && pbUpload !=null) {
            textViewUploadData.setVisibility(View.VISIBLE);
            pbText.setVisibility(View.INVISIBLE);
            pbUpload.setVisibility(View.INVISIBLE);
            uploadButton.setEnabled(true);
        }
    }

    public static void showProgressBar() {
        textViewUploadData.setVisibility(View.INVISIBLE);
        pbText.setVisibility(View.VISIBLE);
        pbText.setText("0%");
        pbUpload.setProgress(0);
        pbUpload.setVisibility(View.VISIBLE);
        uploadButton.setEnabled(false);
    }

    private void startAutoSwipe() {
        pagesNum = 3;

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateViewHandler.post(update);
            }
        }, DELAY_MS, PERIOD_MS);
    }

    private void stopAutoSwipe() {
        timer.cancel();
    }

    public void connectServer(String method, String path, String info){
        Log.d("0612conn", info);
        HttpsConnection httpsConnection = new HttpsConnection(getActivity());
        httpsConnection.setActivity(getActivity());
        httpsConnection.setMethod(method, info);
        httpsConnection.execute(path);


    }

}