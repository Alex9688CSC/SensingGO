package edu.nctu.wirelab.sensinggo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import java.io.IOException;
import java.util.ArrayList;

import edu.nctu.wirelab.sensinggo.Connect.HttpsConnection;

public class LoginActivity extends AppCompatActivity {

    public static ConstraintLayout container2, container1, container0;
    public static boolean press_register = false;
    public static Activity activity = null;
    AlertDialog alertDialog;
    public static ProgressBar progressbar;

    // fb
    private LoginManager loginManager;
    private CallbackManager callbackManager;
    String facebookmail = "";

    // container1
    private EditText username_ET, password_ET;
    private ImageButton login_Btn;
    private ImageButton FBlogin_IB;
    private TextView register_TV;
    private Button forgetBtn;
    //private Button resendBtn;



    // container2
    private EditText email_ET, email_again_ET, username_ET_2, password_ET_2, password_again_ET_2;
    private ImageButton register_Btn;
    private ImageButton FBregister_IB;
    private TextView login_TV;


    public static android.view.animation.Animation animation_regi;
    private android.view.animation.Animation animation_login;



    public static LoadingDialog loadingDialog;
    public static success_registDialog success_registDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        //fb
        FacebookSdk.sdkInitialize(getApplicationContext());

        loadingDialog = new LoadingDialog(LoginActivity.this);
        success_registDialog = new success_registDialog(LoginActivity.this);
        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();

        activity = this;
        container2 = findViewById(R.id.container2);
        container1 = findViewById(R.id.container1);
        container0 = findViewById(R.id.container0);
        progressbar = findViewById(R.id.loading);

        // container1
        username_ET = findViewById(R.id.username);
        password_ET = findViewById(R.id.password);
        login_Btn = findViewById(R.id.login);
        FBlogin_IB = findViewById(R.id.FBlogin);
        register_TV = findViewById(R.id.register);
        forgetBtn = findViewById(R.id.ForgetPasswordBtn);

        // container2
        //email_ET = findViewById(R.id.email);
        //email_again_ET = findViewById(R.id.email_again);
        username_ET_2 = findViewById(R.id.username_2);
        password_ET_2 = findViewById(R.id.password_2);
        password_again_ET_2 = findViewById(R.id.password_again);
        register_Btn = findViewById(R.id.go_Btn);
        FBregister_IB = findViewById(R.id.FBregister);
        login_TV = findViewById(R.id.login_TV);
        //resendBtn = findViewById(R.id.ResendEmailBtn);

        animation_regi = AnimationUtils.loadAnimation(this, R.anim.slide_in_regi);
        animation_login = AnimationUtils.loadAnimation(this, R.anim.slide_in_login);

        // set initial

        container2.setVisibility(View.INVISIBLE);
        progressbar.setVisibility(View.INVISIBLE);
        FBregister_IB.setVisibility(View.INVISIBLE);
        FBlogin_IB.setVisibility(View.INVISIBLE);
        login_Btn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){

                String username = username_ET.getText().toString();
                String password = password_ET.getText().toString();
                if(!username.equals("") && !password.equals("")) {
                    hideKeyboard(v);
                    progressbar.setVisibility(View.VISIBLE);
                    String loginInfo = "username=" + username + "&password=" + password;
                    connectServer("POST", "/login", loginInfo);
                }
                else{
                    ShowDialogMsg.showDialog(getString(R.string.noblank));
                }
            }
        });

        register_Btn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){

                String userId = username_ET_2.getText().toString();
                String pwd = password_ET_2.getText().toString();
                String pwd2 = password_again_ET_2.getText().toString();
                String birth = "";
                String msg = "";
                String gender = "", frGender ="";
                String writeemail = "";
                String writeemail2 = "";

                MainActivity.loginname = userId;
                MainActivity.loginbirth = birth;
                MainActivity.loginmsg = msg;
                MainActivity.loginemail = writeemail;

//                LoginActivity.success_registDialog.start_success_registDialog();
//                loadingDialog.startLoadingDialog();
                if(userId.equals("") || pwd.equals("") || pwd2.equals("")){
                    ShowDialogMsg.showDialog(getString(R.string.noblank1));
                }
                else if(!pwd.equals(pwd2)) {
                    ShowDialogMsg.showDialog(getString(R.string.passwordnotsame));
                }
                else {
                    hideKeyboard(v);
                    progressbar.setVisibility(View.VISIBLE);
                    String registerInfo = "username=" + userId +
                            "&password=" + pwd ;
                            //"&gender=" + gender +
                            //"&birthday=" + birth +
                            //"&email=" + writeemail +
                            //"&helloMsg=" + msg +
                            //"&genderPrefer=" + frGender;
                    //dialogNoDismiss(dialog);
                    loadingDialog.startLoadingDialog();
                    connectServer("POST", "/register", registerInfo);

                    ///delete the animation where asking the user to verified email
                }

            }
        });

        register_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                press_register = true;
                container1.setVisibility(View.INVISIBLE);
                container2.startAnimation(animation_regi);
                container2.setVisibility(View.VISIBLE);
            }
        });

        login_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                press_register = false;
                container2.setVisibility(View.INVISIBLE);
                container1.startAnimation(animation_login);
                container1.setVisibility(View.VISIBLE);

            }
        });

        FBlogin_IB.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                loginFB();
            }
        });



        FBregister_IB.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                loginFB();

            }
        });

        // hind keyboard once tapping the view except editview
        container1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
            }
        });

        container2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
            }
        });

//        resendBtn.setOnClickListener(new Button.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                final View registerView =  LayoutInflater.from(LoginActivity.this).inflate(R.layout.fragment_resendmail,null);
//                //mSignInButton = registerView.findViewById(R.id.login_with_google);
//                //logoutButton = (Button) registerView.findViewById(R.id.logout_with_google);
//
//                final EditText userIdEditText = (EditText) registerView.findViewById(R.id.resendname);
//                final EditText pwdEditText = (EditText) registerView.findViewById(R.id.resendPwd);
//                // recover previous register message
//                alertDialog = new AlertDialog.Builder(LoginActivity.this)
//                        .setTitle(getString(R.string.resend))
//                        .setView(registerView)
//                        .setNegativeButton(getString(R.string.cancel),new DialogInterface.OnClickListener(){
//                            @Override
//                            public void onClick(DialogInterface dialog, int which){
//                                alertDialog.dismiss();
//                            }
//                        })
//                        .setPositiveButton(getString(R.string.okresend), new DialogInterface.OnClickListener(){
//                            @Override
//                            public void onClick(DialogInterface dialog, int which){
//
//                                String userId = userIdEditText.getText().toString();
//                                String pwd = pwdEditText.getText().toString();
//                                if(userId.equals("") || pwd.equals("")){
//                                    ShowDialogMsg.showDialog(getString(R.string.noblank1));
//                                }
//                                else{
//                                    String registerInfo = "username=" + userId +
//                                            "&password=" + pwd;
//
//                                    connectServer("POST", "/getVerificationLetter", registerInfo);
//                                }
//                            }
//                        }).show();
//            }
//        });


        forgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("click" , "forget");
                final View forgotPasswordView =  LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_forgot_password,null);
                final EditText emailEditText = forgotPasswordView.findViewById(R.id.edittext_forgot_email);

                alertDialog = new AlertDialog.Builder(LoginActivity.this)
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
                                    connectServer("POST", "/getIdAndPassword", userInfo);

                                }

                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                alertDialog.dismiss();
                            }
                        }).show();

            }
        });



    }

    @Override
    public void onBackPressed() {
        if(press_register){
            press_register = false;
            container1.setVisibility(View.VISIBLE);
            container2.setVisibility(View.INVISIBLE);
        }

    }

    private void loginFB(){
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

                                facebookmail = object.getString("email");
                                Log.d("ferfgrg", "df"+facebookmail);
                                String Info = "email=" + facebookmail;
                                connectServer("POST", "/fb_login", Info);

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
                Log.d("FBTAG", "Facebook onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                // 登入失敗
                Log.d("FBerror", "Facebook onError:" + error.toString());
            }
        });

    }

    public void RegisterByFB(){

        final View fb_register_view =  LayoutInflater.from(activity).inflate(R.layout.fragment_fb_login,null);
        EditText username = fb_register_view.findViewById(R.id.registerID);

        alertDialog = new AlertDialog.Builder(activity)
                .setView(fb_register_view)
                .setPositiveButton(getString(R.string.register), null)
                .show();

        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().toString().compareTo("")==0){
                    ShowDialogMsg.showDialog(getString(R.string.noblank));
                }
                else {
                    String Info = "username=" + username.getText() +
                            "&email=" + facebookmail;
                    connectServer("POST", "/fb_register", Info);
                    alertDialog.dismiss();
                }
            }
        });

    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void connectServer(String method, String path, String info){
        Log.d("0612conn", info);
        HttpsConnection httpsConnection = new HttpsConnection(this);
        httpsConnection.setActivity(this);
        httpsConnection.setMethod(method, info);
        httpsConnection.execute(path);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }



}
