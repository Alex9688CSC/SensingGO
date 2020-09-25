package edu.nctu.wirelab.sensinggo.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.nctu.wirelab.sensinggo.CheckFormat;
import edu.nctu.wirelab.sensinggo.Connect.HttpsConnection;
import edu.nctu.wirelab.sensinggo.File.JsonParser;
import edu.nctu.wirelab.sensinggo.MainActivity;
import edu.nctu.wirelab.sensinggo.R;
import edu.nctu.wirelab.sensinggo.ShowDialogMsg;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by py on 4/17/18.
 */


public class LoginFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    private Button forgetBtn;
    private Button loginBtn, registerBtn ,resendBtn, prefbBtn;
    private Button logoutButton;
    private Button fb_loginBtn ,fb_registerBtn;
    private SignInButton mSignInButton;
    private EditText userNameEditText, pwdEditText;
    public Context mContext;
    private String facebookmail = "";
    private static int RC_SIGN_IN = 100;



    //DialogInterface alertDialog;
    AlertDialog alertDialog;
    GoogleApiClient mGoogleApiClient;

    // fb
    private LoginManager loginManager;
    private CallbackManager callbackManager;



    private JsonParser jsonParser = null;
    public void setJsonParser(JsonParser json) {
        jsonParser = json;
    }

    public void dialogNoDismiss(DialogInterface dialog){
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, false);
        }
        catch (Exception e) {
        }
    }

    public void dialogDismiss(DialogInterface dialog){
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, true);
        }
        catch (Exception e) {
        }
    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        loginBtn = (Button) view.findViewById(R.id.LoginBtn);
        registerBtn = (Button) view.findViewById(R.id.RegisterBtn);
        forgetBtn = view.findViewById(R.id.ForgetPasswordBtn);
        resendBtn = (Button) view.findViewById(R.id.ResendEmailBtn);
        fb_loginBtn = view.findViewById(R.id.fb_login_button);
        fb_registerBtn = view.findViewById(R.id.fb_register_button);
        //fbloginBtn = (Button) view.findViewById(R.id.login_button);
        //prefbBtn = (Button) view.findViewById(R.id.PreFBBtn);

        //fb
        FacebookSdk.sdkInitialize(getApplicationContext());
        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();


//        if(mGoogleApiClient == null)
//            configureSignIn();


//        fbloginBtn.setOnClickListener(new Button.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                loginFB();
//            }
//        });

//        loginBtn.setOnClickListener(new Button.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                try {
//                    String userName = URLEncoder.encode(userNameEditText.getText().toString(), "UTF-8");
//                    String pwd = pwdEditText.getText().toString();
//                    if(!userName.equals("") && !pwd.equals("")) {
//
//                        String loginInfo = "username=" + userName +"&password=" + pwd;
//                        HttpsConnection httpsConnection = new HttpsConnection(getActivity());
//                        httpsConnection.setJsonParser(jsonParser);
//                        httpsConnection.setActivity(getActivity());
//                        httpsConnection.setMethod("POST", loginInfo);
//                        httpsConnection.execute("/login");//exec with the url, such as https://140.113.216.37/login;
//                    }
//                    else{
//                        ShowDialogMsg.showDialog(getString(R.string.noblank));
//                    }
//                }
//                catch(IOException e){
//                    e.printStackTrace();
//                }
//                //String userName = userNameEditText.getText().toString();
//
//            }
//        });

        // change login btn
        loginBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                final View loginView =  LayoutInflater.from(getActivity()).inflate(R.layout.login_pop,null);
                final EditText usernameEditText = (EditText) loginView.findViewById(R.id.UserNameEditText);
                final EditText pwdEditText = (EditText) loginView.findViewById(R.id.PwdEditText);

                alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.login))
                        .setView(loginView)
                        .setNegativeButton(getString(R.string.cancel),new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                dialogDismiss(dialog);
                            }
                        })
                        .setPositiveButton(getString(R.string.login), null)
                        .show();

                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        try {

                            String userName = URLEncoder.encode(usernameEditText.getText().toString(), "UTF-8");
                            String pwd = pwdEditText.getText().toString();
                            if(!userName.equals("") && !pwd.equals("")) {

                                String loginInfo = "username=" + userName +"&password=" + pwd;
                                HttpsConnection httpsConnection = new HttpsConnection(getActivity());
                                httpsConnection.setJsonParser(jsonParser);
                                httpsConnection.setActivity(getActivity());
                                httpsConnection.setMethod("POST", loginInfo);
                                httpsConnection.execute("/login");//exec with the url, such as https://140.113.216.37/login;
                                alertDialog.dismiss();
                            }
                            else{
                                ShowDialogMsg.showDialog(getString(R.string.noblank));
                            }
                        }
                        catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                });



                //String userName = userNameEditText.getText().toString();

            }
        });


        // for hot dismiss
        registerBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                final View registerView =  LayoutInflater.from(getActivity()).inflate(R.layout.fragment_register,null);
                //mSignInButton = registerView.findViewById(R.id.login_with_google);
                //logoutButton = (Button) registerView.findViewById(R.id.logout_with_google);

                final EditText userIdEditText = (EditText) registerView.findViewById(R.id.registerID);
                final EditText pwdEditText = (EditText) registerView.findViewById(R.id.registerPwd);
                final EditText pwdEditText2 = (EditText) registerView.findViewById(R.id.registerPwd2);
                //EditText mailEditText = (EditText) registerView.findViewById(R.id.registerMail);
                final EditText birthEditText = (EditText) registerView.findViewById(R.id.registerBirth);
                final EditText greetEditText = (EditText) registerView.findViewById(R.id.hello);
                final RadioGroup rg = (RadioGroup) registerView.findViewById(R.id.genderGroup);
                final RadioGroup friendRg= (RadioGroup) registerView.findViewById(R.id.friendGender);
                final EditText emailEditText = (EditText) registerView.findViewById(R.id.writeemail);
                final EditText emailEditText2 = (EditText) registerView.findViewById(R.id.writeemail2);

                // recover previous register message
                userIdEditText.setText(MainActivity.loginname);
                birthEditText.setText(MainActivity.loginbirth);
                greetEditText.setText(MainActivity.loginmsg);
                emailEditText.setText(MainActivity.loginemail);


                birthEditText.setFocusable(false);

                birthEditText.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Log.d("dcwe" , "sadff");
                        final Calendar m_Calendar = Calendar.getInstance();
                        Calendar c = Calendar.getInstance();
                        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // TODO Auto-generated method stub
                                m_Calendar.set(Calendar.YEAR, year);
                                m_Calendar.set(Calendar.MONTH, monthOfYear);
                                m_Calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                String myFormat = "yyyy/MM/dd";
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.TAIWAN);
                                birthEditText.setText(sdf.format(m_Calendar.getTime()));
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });


                alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.register))
                        .setView(registerView)
                        .setNegativeButton(getString(R.string.cancel),new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                dialogDismiss(dialog);
                            }
                        })
                        .setPositiveButton(getString(R.string.register), null)
                        .show();
                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        String userId = userIdEditText.getText().toString();
                        String pwd = pwdEditText.getText().toString();
                        String pwd2 = pwdEditText2.getText().toString();
//                            String mail = mailEditText.getText().toString();
                        String birth = birthEditText.getText().toString();
                        String msg = greetEditText.getText().toString();
                        String gender = "", frGender ="";
                        String writeemail = emailEditText.getText().toString();
                        String writeemail2 = emailEditText2.getText().toString();

                        MainActivity.loginname = userId;
                        MainActivity.loginbirth = birth;
                        MainActivity.loginmsg = msg;
                        MainActivity.loginemail = writeemail;


                        switch (rg.getCheckedRadioButtonId()){
                            case R.id.male:
                                gender = "male";
                                break;
                            case R.id.female:
                                gender = "female";
                                break;
                        }
                        switch (friendRg.getCheckedRadioButtonId()) {
                            case R.id.frMale:
                                frGender = "male";
                                break;
                            case R.id.frFemale:
                                frGender = "female";
                                break;
                            case R.id.frBoth:
                                frGender = "both";
                                break;
                        }



                        if(userId.equals("") || pwd.equals("") || birth.equals("") || gender.equals("") || writeemail.equals("") || writeemail2.equals("")){
                            ShowDialogMsg.showDialog(getString(R.string.noblank1));
                        }
                        else if (!CheckFormat.checkEmail(writeemail) && !CheckFormat.checkEmail(writeemail)){
                            ShowDialogMsg.showDialog(getString(R.string.noblank2));
                        }
                        else if (!CheckFormat.checkBirth(birth)){
                            ShowDialogMsg.showDialog(getString(R.string.noblank3));
                        }else if(invalidbirthday(birth)){
                            ShowDialogMsg.showDialog(getString(R.string.noblank3));
                        }
                        else if(!pwd.equals(pwd2)){
                            ShowDialogMsg.showDialog(getString(R.string.passwordnotsame));
                        }else if(!writeemail.equals(writeemail2)){
                            ShowDialogMsg.showDialog(getString(R.string.emailnotsame));
                        }
                        else {
                            String registerInfo = "username=" + userId +
                                    "&password=" + pwd +
                                    "&gender=" + gender +
                                    "&birthday=" + birth +
                                    "&email=" + writeemail +
                                    "&helloMsg=" + msg +
                                    "&genderPrefer=" + frGender;
                            //dialogNoDismiss(dialog);

                            final HttpsConnection httpsConnection = new HttpsConnection(getActivity());
                            httpsConnection.setJsonParser(jsonParser);
                            httpsConnection.setMethod("POST", registerInfo);
                            httpsConnection.setFragment(LoginFragment.this);
                            httpsConnection.setActivity(getActivity());
                            httpsConnection.execute("/register");
//                            ShowDialogMsg.showDialog(getString(R.string.gotocheckemail));
                            alertDialog.dismiss();
                        }
                    }
                });



//                mSignInButton.setOnClickListener(new Button.OnClickListener(){
//                    @Override
//                    public void onClick(View v){
//                        signIn();
//                    }
//                });
//
//                logoutButton.setOnClickListener(new Button.OnClickListener(){
//                    @Override
//                    public void onClick(View v){
//                        signOut();
//
//                    }
//                });
            }
        });


        //for alternative layout
//        prefbBtn.setOnClickListener(new Button.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                final View registerView =  LayoutInflater.from(getActivity()).inflate(R.layout.fragment_fb_login,null);
//                final Button secondloginBtn = registerView.findViewById(R.id.fbsecondButton);
//                final Button firstloginBtn = registerView.findViewById(R.id.fbfirstButton);
//
//                alertDialog = new AlertDialog.Builder(getActivity())
//                        .setView(registerView)
//                        .show();
//
//
//
//                secondloginBtn.setOnClickListener(new Button.OnClickListener(){
//                    @Override
//                    public void onClick(View v){
//                        alertDialog.dismiss();
//                        loginFB("fbuser", "", "", "", "", null);
//                    }
//                });
//
//                firstloginBtn.setOnClickListener(new Button.OnClickListener(){
//                    @Override
//                    public void onClick(View v){
//                        alertDialog.dismiss();
//                        firsttime_FBlogin();
//                    }
//                });
//            }
//        });


        resendBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                final View registerView =  LayoutInflater.from(getActivity()).inflate(R.layout.fragment_resendmail,null);
                //mSignInButton = registerView.findViewById(R.id.login_with_google);
                //logoutButton = (Button) registerView.findViewById(R.id.logout_with_google);

                final EditText userIdEditText = (EditText) registerView.findViewById(R.id.resendname);
                final EditText pwdEditText = (EditText) registerView.findViewById(R.id.resendPwd);
                // recover previous register message
                alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.resend))
                        .setView(registerView)
                        .setNegativeButton(getString(R.string.cancel),new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                dialogDismiss(dialog);
                            }
                        })
                        .setPositiveButton(getString(R.string.okresend), new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){

                                String userId = userIdEditText.getText().toString();
                                String pwd = pwdEditText.getText().toString();
                                if(userId.equals("") || pwd.equals("")){
                                    ShowDialogMsg.showDialog(getString(R.string.noblank1));
                                }
                                else{
                                    String registerInfo = "username=" + userId +
                                            "&password=" + pwd;
                                    //dialogNoDismiss(dialog);

                                    final HttpsConnection httpsConnection = new HttpsConnection(getActivity());
                                    httpsConnection.setJsonParser(jsonParser);
                                    httpsConnection.setMethod("POST", registerInfo);
                                    httpsConnection.setFragment(LoginFragment.this);
                                    httpsConnection.setActivity(getActivity());
                                    httpsConnection.setDialogInterface(dialog);
                                    httpsConnection.execute("/getVerificationLetter");

                                }
                            }
                        }).show();
            }
        });

        forgetBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Log.d("click" , "forget");

                final View forgotPasswordView =  inflater.inflate(R.layout.dialog_forgot_password, null);
                final EditText emailEditText = forgotPasswordView.findViewById(R.id.edittext_forgot_email);

                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.forget))
                        .setView(forgotPasswordView)
                        .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){

                                String email = emailEditText.getText().toString();
                                if(email.equals("")){
                                    ShowDialogMsg.showDialog(getString(R.string.noblank_nostar));
                                }
                                else{
                                    String userInfo = "email=" + email;
                                    final HttpsConnection httpsConnection = new HttpsConnection(getActivity());
                                    httpsConnection.setJsonParser(jsonParser);
                                    httpsConnection.setMethod("POST", userInfo);
                                    httpsConnection.setFragment(LoginFragment.this);
                                    httpsConnection.setActivity(getActivity());
                                    httpsConnection.execute("/getIdAndPassword");
                                }

                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                dialogDismiss(dialog);
                            }
                        }).show();

            }
        });

        fb_loginBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("facebookfacebook", "fb_loginBtn");
                loginFB("login");
            }
        });

        fb_registerBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d("facebookfacebook", "fb_registerBtn");
                loginFB("register");
            }
        });


        return  view;
    }

    private boolean invalidbirthday(String birth){
        String temp_birth = "";
        String temp_now = "";
        for(int i = 0; i < birth.length(); i++){
            if(birth.charAt(i) != '/')
                temp_birth += birth.charAt(i);
        }
        Date now = new Date();
        Date alsoNow = Calendar.getInstance().getTime();
        String nowAsString = new SimpleDateFormat("yyyy-MM-dd").format(now);
        for(int i = 0; i < nowAsString.length(); i++){
            if(nowAsString.charAt(i) != '-')
                temp_now += nowAsString.charAt(i);
        }
        if(temp_birth.compareTo(temp_now) > 0)
            return true;
        else
            return false;
    }

//    private void firsttime_FBlogin(){
//        final View registerView =  LayoutInflater.from(getActivity()).inflate(R.layout.firsttime_fblogin,null);
//        final EditText userIdEditText = (EditText) registerView.findViewById(R.id.registerID);
//        final EditText birthEditText = (EditText) registerView.findViewById(R.id.registerBirth);
//        final EditText greetEditText = (EditText) registerView.findViewById(R.id.hello);
//        final RadioGroup rg = (RadioGroup) registerView.findViewById(R.id.genderGroup);
//        final RadioGroup friendRg= (RadioGroup) registerView.findViewById(R.id.friendGender);
//
//
//        // recover previous register message
//        userIdEditText.setText(MainActivity.loginname);
//        birthEditText.setText(MainActivity.loginbirth);
//        greetEditText.setText(MainActivity.loginmsg);
//
//
//        birthEditText.setFocusable(false);
//
//        birthEditText.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                final Calendar m_Calendar = Calendar.getInstance();
//                Calendar c = Calendar.getInstance();
//                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
//
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                        // TODO Auto-generated method stub
//                        m_Calendar.set(Calendar.YEAR, year);
//                        m_Calendar.set(Calendar.MONTH, monthOfYear);
//                        m_Calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                        String myFormat = "yyyy/MM/dd";
//                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.TAIWAN);
//                        birthEditText.setText(sdf.format(m_Calendar.getTime()));
//                    }
//                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
//            }
//        });
//
//
//        alertDialog = new AlertDialog.Builder(getActivity())
//                .setView(registerView)
//                .setNegativeButton(getString(R.string.cancel),new DialogInterface.OnClickListener(){
//                    @Override
//                    public void onClick(DialogInterface dialog, int which){
//                        dialogDismiss(dialog);
//                    }
//                })
//                .setPositiveButton(getString(R.string.login), new DialogInterface.OnClickListener(){
//                    @Override
//                    public void onClick(DialogInterface dialog, int which){
//
//                        String userId = userIdEditText.getText().toString();
//                        String birth = birthEditText.getText().toString();
//                        String msg = greetEditText.getText().toString();
//                        String gender = "", frGender ="";
//
//
//                        MainActivity.loginname = userId;
//                        MainActivity.loginbirth = birth;
//                        MainActivity.loginmsg = msg;
//
//                        switch (rg.getCheckedRadioButtonId()){
//                            case R.id.male:
//                                gender = "male";
//                                break;
//                            case R.id.female:
//                                gender = "female";
//                                break;
//                        }
//                        switch (friendRg.getCheckedRadioButtonId()) {
//                            case R.id.frMale:
//                                frGender = "male";
//                                break;
//                            case R.id.frFemale:
//                                frGender = "female";
//                                break;
//                            case R.id.frBoth:
//                                frGender = "both";
//                                break;
//                        }
//
//                        if(userId.equals("")){
//                            userId = "fbuser";
//                        }
//
//
//                        loginFB();
//                    }
//                }).show();
//    }

    private void loginFB(String type){
        loginManager.setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
        ArrayList<String> permissions = new ArrayList<>();

        permissions.add("email");
//        permissions.add("user_gender");
//        permissions.add("user_birthday");
        // 設定要讀取的權限
        loginManager.logInWithReadPermissions(this, permissions);
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                // 登入成功
                // 透過GraphRequest來取得用戶的Facebook資訊
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        try {
                            Log.d("facebookfacebook", "sdffwfw"+response.getConnection().getResponseCode());
                            if (response.getConnection().getResponseCode() == 200) {
                                long id = object.getLong("id");
//                                String name = object.getString("name");
                                facebookmail = object.getString("email");
                                Log.d("facebookfacebook", object.toString());

                                if(type.compareTo("login")==0){
                                    String Info = "email=" + facebookmail;

                                    HttpsConnection httpsConnection = new HttpsConnection(getActivity());
                                    httpsConnection.setJsonParser(jsonParser);
                                    httpsConnection.setActivity(getActivity());
                                    httpsConnection.setMethod("POST", Info);
                                    httpsConnection.execute("/fb_login");//exec with the url, such as https://140.113.216.37/login;
                                }
                                else{
                                    final View fb_register_view =  LayoutInflater.from(getActivity()).inflate(R.layout.fragment_fb_login,null);
                                    EditText username = fb_register_view.findViewById(R.id.registerID);

                                    alertDialog = new AlertDialog.Builder(getActivity())
                                            .setView(fb_register_view)
                                            .setPositiveButton(getString(R.string.register), null)
                                            .show();

                                    Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                    positiveButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            if(username.getText().toString().compareTo("")==0){
                                                Toast.makeText(getActivity(), getString(R.string.noblank), Toast.LENGTH_SHORT).show();
                                            }

                                            String Info = "username=" + username.getText() +
                                                    "&email=" + facebookmail;

                                            HttpsConnection httpsConnection = new HttpsConnection(getActivity());
                                            httpsConnection.setJsonParser(jsonParser);
                                            httpsConnection.setActivity(getActivity());
                                            httpsConnection.setMethod("POST", Info);
                                            httpsConnection.execute("/fb_register");//exec with the url, such as https://140.113.216.37/login;
                                            alertDialog.dismiss();
                                        }
                                    });
                                }
//                                String registerInfo = "username=" + userid +
//                                        "&gender=" + gender +
//                                        "&birthday=" + birth +
//                                        "&helloMsg=" + msg +
//                                        "&genderPrefer=" + frGender +
//                                        "&facebookemail=" + facebookmail;
                                //dialogNoDismiss(dialog);

//                                final HttpsConnection httpsConnection = new HttpsConnection(getActivity());
//                                httpsConnection.setJsonParser(jsonParser);
//                                httpsConnection.setMethod("POST", registerInfo);
//                                httpsConnection.setFragment(LoginFragment.this);
//                                httpsConnection.setActivity(getActivity());
//                                httpsConnection.setDialogInterface(dialog);
//                                httpsConnection.execute("/fb_loginAndRegister");
                                // 此時如果登入成功，就可以順便取得用戶大頭照

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                // https://developers.facebook.com/docs/android/graph?locale=zh_TW
                // 如果要取得email，需透過添加參數的方式來獲取(如下)
                // 不添加只能取得id & name
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                // 用戶取消
                Log.d("TAG", "Facebook onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                // 登入失敗
                Log.d("FBerror", "Facebook onError:" + error.toString());
            }
        });
    }


//    private void postForgotPassword(String username){
//        String urlString = "http://140.113.216.39/getPassword";
//
//        HttpURLConnection connection = null;
//        try{
//            //initial connection
//            URL url = new URL(urlString);
//            //get connection object
//            connection = (HttpURLConnection) url.openConnection();
//            //set request
//            connection.setRequestMethod("POST");
//            connection.setRequestProperty("Content-Type","application/json");
//            connection.setRequestProperty("charset","utf-8");
//            connection.setUseCaches(false);
//            connection.setAllowUserInteraction(false);
//            connection.setDoOutput(true);
//            connection.setReadTimeout(5000);
//            connection.setConnectTimeout(100000);
//
//
//            JSONObject JsonObj = new JSONObject();
//            Log.d("XDDDD", "value");
//            //JsonObj.put("coin_lat", inputlat);
//            //JsonObj.put("coin_lng", inputlng);
//            JsonObj.put("username", username);
//            JSONArray Json_send = new JSONArray();
//            Json_send.put(JsonObj);
//
//
//            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
//            dos.writeBytes(Json_send.toString());
//            dos.flush();
//            dos.close();
//            Log.d("forgot_send", "forgot_send message: " + Json_send.toString());
//            int responseCode = connection.getResponseCode();
//            Log.d("forgot_response", "forgot_response code: " + responseCode);
//            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            String line = "";
//            String result = "";
//            while((line = br.readLine()) != null)
//                result += line;
//
//            //parseDeletMessage(result);
//
//            br.close();
//
//            Log.d("forgot_response", "forgot_response message: " + result);
//        }
//        catch(IOException e){
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }



//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            if (result.isSuccess()) {
//                // Google Sign In was successful, save Token and a state then authenticate with Firebase
//                GoogleSignInAccount account = result.getSignInAccount();
//
////                idToken = account.getIdToken();
////
////                name = account.getDisplayName();
//                gmail = account.getEmail();
//
//                Log.d("emailemail", gmail);
//                Toast.makeText(getActivity(), getString(R.string.youremailis) + gmail, Toast.LENGTH_SHORT)
//                        .show();
//
//
//            } else {
//                // Google Sign In failed, update UI appropriately
//                Log.e("result_login", "Login Unsuccessful. ");
//                Toast.makeText(getActivity(), getString(R.string.loginunsuccessful), Toast.LENGTH_SHORT)
//                        .show();
//            }
//        }
//    }
//
//    public void configureSignIn(){
//        // Configure sign-in to request the user’s basic profile like name and email
//
//        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
//        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
//                .enableAutoManage((FragmentActivity)getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
//                .build();
//        mGoogleApiClient.connect();
//    }



    // wait for the background process finish, if register is not success, not dismiss the dialog, o.w. dismiss it
    /*public void waitForRegister(int status, DialogInterface dialog){ // wait for 3 cycles
        if(status==0){ // register successfully
            Log.d("0523", "wait success");
            dialogDismiss(dialog);
        }
        else if (status==1){
            Log.d("0523", "wait unsuccess");
            dialogNoDismiss(dialog);
        }
    }*/

//    private void signIn() {
//        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }
//
//    private void signOut(){
//
//
//        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//                new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(@NonNull Status status) {
//                        Log.d("successfullylogout", "YES");
//                    }
//                }
//        );
//        Toast.makeText(getActivity(), getString(R.string.anotheraccount), Toast.LENGTH_SHORT)
//                .show();
//    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
//    @Override
//    public void onPause() {
//        super.onPause();
//        mGoogleApiClient.stopAutoManage((FragmentActivity)getActivity());
//        mGoogleApiClient.disconnect();
//    }
//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // Call GoogleApiClient connection when starting the Activity
//        mGoogleApiClient.connect();
//    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage((FragmentActivity)getActivity());
            mGoogleApiClient.disconnect();
        }
    }


}
