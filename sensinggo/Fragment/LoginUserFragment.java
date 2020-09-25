package edu.nctu.wirelab.sensinggo.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListPopupWindow;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import edu.nctu.wirelab.sensinggo.CheckFormat;
import edu.nctu.wirelab.sensinggo.Connect.HttpsConnection;
import edu.nctu.wirelab.sensinggo.File.JsonParser;
import edu.nctu.wirelab.sensinggo.MainActivity;
import edu.nctu.wirelab.sensinggo.R;
import edu.nctu.wirelab.sensinggo.ShowDialogMsg;
import edu.nctu.wirelab.sensinggo.UserConfig;

/**
 * Created by py on 4/24/18.
 */

public class LoginUserFragment extends Fragment {
    private ConstraintLayout constrain;

    //////////
    //change theme
    private int theme_color ;
    private TextView changeTheme;
    ///////



    // editable
    public static EditText gender_ET, birthday_ET, helloMsg_ET, email_ET; //add email edit text
    private TextView slash_TV;
    private ImageButton done_IB;
    private TextView done_TV;

    static String temp_gender = "";
    // non-editable
    public TextView username_TV, userid_TV, age_TV, gender_TV, email_TV, intro_TV, yearsold_TV;
    private int ACTION_PICK_FROM_GALLERY = 1;
    Calendar m_Calendar = Calendar.getInstance();

    // private Button changepwdBtn, recommendBtn;//,
    private CircleImageView profile_image;
    private TextView edit_profile_TV, changePwd_TV;

    //logout textview
    private TextView logout_TV;

    private JsonParser jsonParser = null;
    public void setJsonParser(JsonParser json) {
        jsonParser = json;
    }
    AlertDialog alertDialog;
    AlertDialog sumitalertDialog;

    private void showDatePickerDialog() {
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
                birthday_ET.setText(sdf.format(m_Calendar.getTime()));
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();

    }

    // Ref: https://blog.csdn.net/Crab0314/article/details/79608705
    private void showListPopWindow () {
        final String [] list = {getString(R.string.gender_male),getString(R.string.gender_female)};
        final ListPopupWindow listpopW = new ListPopupWindow(getActivity());
        listpopW.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list));
        listpopW.setAnchorView(gender_ET);
        listpopW.setWidth(200);
        listpopW.setModal(true);

        listpopW.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                gender_ET.setText(list[i]); // show the list content on the editText
                listpopW.dismiss(); // dismiss after selecting
            }
        });
        listpopW.show();
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_login_user, container, false);
        constrain = view.findViewById(R.id.constrain);

        changeTheme = view.findViewById(R.id.changeTheme);
        // editable
        gender_ET = view.findViewById(R.id.gender_ET);
        birthday_ET = view.findViewById(R.id.birthday_ET);
        helloMsg_ET = view.findViewById(R.id.intro_ET);
        email_ET = view.findViewById(R.id.email_ET);
        done_IB = view.findViewById(R.id.done_IB);
        done_TV = view.findViewById(R.id.done_TV);
        slash_TV = view.findViewById(R.id.slash_TV);

        // non-editable
        username_TV = view.findViewById(R.id.username_TV);
        userid_TV = view.findViewById(R.id.userid_TV);
        age_TV = view.findViewById(R.id.age_TV);
        gender_TV = view.findViewById(R.id.gender_TV);
        email_TV = view.findViewById(R.id.email_TV);
        intro_TV = view.findViewById(R.id.intro_TV);
        profile_image = view.findViewById(R.id.imageView);
        yearsold_TV = view.findViewById(R.id.textView7);

        edit_profile_TV = view.findViewById(R.id.edit_profile_TV);
        changePwd_TV = view.findViewById(R.id.changePwd_TV);
        logout_TV = view.findViewById(R.id.logout_TV);
        edit_profile_TV = view.findViewById(R.id.edit_profile_TV);


        // initial
        username_TV.setText(UserConfig.myUserName);
        userid_TV.setText(UserConfig.myUserid);
        email_TV.setText(UserConfig.userEmail);
        intro_TV.setText(UserConfig.userMsg);
        age_TV.setText(Integer.toString(CalculateAge(UserConfig.userBirthday)));
        //initial emailLocked




        if(UserConfig.userGender.compareTo("male")==0){
            gender_TV.setText(getString(R.string.gender_male));
        }
        if(UserConfig.userGender.compareTo("female")==0){
            gender_TV.setText(getString(R.string.gender_female));
        }

        gender_ET.setVisibility(View.INVISIBLE);
        birthday_ET.setVisibility(View.INVISIBLE);
        helloMsg_ET.setVisibility(View.INVISIBLE);
        email_ET.setVisibility(View.INVISIBLE);
        done_IB.setVisibility(View.INVISIBLE);
        done_TV.setVisibility(View.INVISIBLE);
        slash_TV.setVisibility(View.INVISIBLE);



        setProfilePhoto();

        if(UserConfig.userType != null && UserConfig.userType.compareTo("1")==0){
            changePwd_TV.setVisibility(View.INVISIBLE);
            changePwd_TV.setVisibility(View.INVISIBLE);
        }

        constrain.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                hideKeyboard(v);
            }
        });

        edit_profile_TV.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                EditProfile();
            }
        });

        edit_profile_TV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditProfile();
            }
        });

        done_IB.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Done();
            }
        });

        done_TV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Done();
            }
        });

        logout_TV.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setMessage(getString(R.string.logoutApp))
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                            ShowDialogMsg.showDialog("Cancel");
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                try {
                                    File file = new File(MainActivity.configPath);
                                    boolean deleted = file.delete();
                                    Log.d("deletelog", String.valueOf(deleted));
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }).show();
            }
        });
        changeTheme.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View v = inflater.inflate(R.layout.change_theme, null);
                TextView dialog_title = v.findViewById(R.id.dialog_title);



                Button pink = (Button) v.findViewById(R.id.pink);
                Button yellow = (Button) v.findViewById(R.id.yellow);
                Button purple = (Button) v.findViewById(R.id.purple);
                Button origin = (Button) v.findViewById(R.id.origin);

                AppCompatActivity act = ((AppCompatActivity) getActivity());
                BottomNavigationView nav = getActivity().findViewById(R.id.navigation);
                SharedPreferences spref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor sprefEditor = spref.edit();
                //builer.setView(v);//这里如果使用builer.setView(v)，自定义布局只会覆盖title和button之间的那部分
                final Dialog dialog = builder.create();
                dialog.show();
                dialog.getWindow().setContentView(v);//自定义布局应该在这里添加，要在dialog.show()的后面
                //dialog.getWindow().setGravity(Gravity.CENTER);//可以设置显示的位置
                pink.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        theme_color = R.color.theme_pink;
                        sprefEditor.putInt("theme_color", theme_color);
                        sprefEditor.commit();
                        Log.i("user_theme_color: ",String.valueOf(theme_color));
                        ChangeTheme(act, nav, theme_color);
                        dialog.dismiss();
                    }

                });
                yellow.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        theme_color = R.color.theme_yellow;
                        sprefEditor.putInt("theme_color", theme_color);
                        sprefEditor.commit();
                        Log.i("user_theme_color: ",String.valueOf(theme_color));
                        ChangeTheme(act, nav, theme_color);
                        dialog.dismiss();
                    }

                });
                purple.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        theme_color = R.color.theme_purple;
                        sprefEditor.putInt("theme_color", theme_color);
                        sprefEditor.commit();
                        Log.i("user_theme_color: ",String.valueOf(theme_color));
                        ChangeTheme(act, nav, theme_color);
                        dialog.dismiss();
                    }

                });
                origin.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        theme_color = R.color.new_background;
                        sprefEditor.putInt("theme_color", theme_color);
                        sprefEditor.commit();
                        Log.i("user_theme_color: ",String.valueOf(theme_color));
                        ChangeTheme(act, nav, theme_color);
                        dialog.dismiss();

                    }

                });


            }
        });
        changeTheme.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View v = inflater.inflate(R.layout.change_theme, null);
                TextView dialog_title = v.findViewById(R.id.dialog_title);



                Button pink = (Button) v.findViewById(R.id.pink);
                Button yellow = (Button) v.findViewById(R.id.yellow);
                Button purple = (Button) v.findViewById(R.id.purple);
                Button origin = (Button) v.findViewById(R.id.origin);

                AppCompatActivity act = ((AppCompatActivity) getActivity());
                BottomNavigationView nav = getActivity().findViewById(R.id.navigation);
                SharedPreferences spref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor sprefEditor = spref.edit();
                //builer.setView(v);//这里如果使用builer.setView(v)，自定义布局只会覆盖title和button之间的那部分
                final Dialog dialog = builder.create();
                dialog.show();
                dialog.getWindow().setContentView(v);//自定义布局应该在这里添加，要在dialog.show()的后面
                //dialog.getWindow().setGravity(Gravity.CENTER);//可以设置显示的位置
                pink.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        theme_color = R.color.theme_pink;
                        sprefEditor.putInt("theme_color", theme_color);
                        sprefEditor.commit();
                        Log.i("user_theme_color: ",String.valueOf(theme_color));
                        ChangeTheme(act, nav, theme_color);
                        dialog.dismiss();
                    }

                });
                yellow.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        theme_color = R.color.theme_yellow;
                        sprefEditor.putInt("theme_color", theme_color);
                        sprefEditor.commit();
                        Log.i("user_theme_color: ",String.valueOf(theme_color));
                        ChangeTheme(act, nav, theme_color);
                        dialog.dismiss();
                    }

                });
                purple.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        theme_color = R.color.theme_purple;
                        sprefEditor.putInt("theme_color", theme_color);
                        sprefEditor.commit();
                        Log.i("user_theme_color: ",String.valueOf(theme_color));
                        ChangeTheme(act, nav, theme_color);
                        dialog.dismiss();
                    }

                });
                origin.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        theme_color = R.color.new_background;
                        sprefEditor.putInt("theme_color", theme_color);
                        sprefEditor.commit();
                        Log.i("user_theme_color: ",String.valueOf(theme_color));
                        ChangeTheme(act, nav, theme_color);
                        dialog.dismiss();

                    }

                });


            }
        });

        changePwd_TV.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                ChangePWD();
            }
        });

        changePwd_TV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePWD();
            }
        });



        profile_image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, ACTION_PICK_FROM_GALLERY);
            }
        });
        Log.d("emailLocked_oncreate", UserConfig.emailLocked);
        return view;


    }
    private void ChangeTheme(AppCompatActivity act, BottomNavigationView nav, int color_theme) {
        act.getSupportActionBar()
                .setBackgroundDrawable(new ColorDrawable(
                        ContextCompat.getColor(getActivity() ,color_theme)
                ));

        nav.setItemBackground(
                new ColorDrawable(
                        ContextCompat.getColor(getActivity(),color_theme))
        );
    }
    private void setProfilePhoto(){
        Log.d("asgaerg","sdf");
        String internetUrl = "https://sensinggo.org/icon/" + UserConfig.myUserName + ".jpg";
        ColorDrawable cd = new ColorDrawable(0x000000);

        Glide.with(getActivity())
                .load(internetUrl)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(cd)
                .error(R.drawable.icon_manb) //load失敗的Drawable
                .into(profile_image);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ACTION_PICK_FROM_GALLERY && resultCode == Activity.RESULT_OK && data != null){
            // Pick image from gallery
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            // get image from gallery
            Bitmap loadedBitmap = BitmapFactory.decodeFile(picturePath);

            try {
                ExifInterface exif = new ExifInterface(picturePath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                }
                loadedBitmap = Bitmap.createBitmap(loadedBitmap, 0, 0, loadedBitmap.getWidth(), loadedBitmap.getHeight(), matrix, true); // rotating bitmap
            }
            catch(Exception e){
                Log.d("adfarfa","adf");
            }

            // set scaled image into imageView
            profile_image.setImageBitmap(loadedBitmap);


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            loadedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);

            byte[] array = stream.toByteArray();
            String encImage = Base64.encodeToString(array, Base64.URL_SAFE|Base64.NO_WRAP);
            String Info = "username=" + UserConfig.myUserName +
                    "&profile_photo=" + encImage;

            connectServer("POST", "/uploadProfilePhoto", Info);

            Log.d("picturePathpicturePath",encImage);
            Log.d("picturePathpicturePath","value : " + encImage.length());
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        new Thread(new Runnable(){
            @Override
            public void run(){
                getTotalMoneyFromServer();
            }
        }).start();
    }

    private void getTotalMoneyFromServer(){
        //String urlstring = "http://sensinggo.org/getTotalMoney";
        String Info = "username=" + UserConfig.myUserName ;
        connectServer("POST", "/getTotalMoney", Info);
    }

    private void connectServer(String method, String path, String info){
        Log.d("0612conn", info);
        HttpsConnection httpsConnection = new HttpsConnection(getActivity());
        httpsConnection.setActivity(getActivity());
        httpsConnection.setMethod(method, info);
        httpsConnection.execute(path);

    }

    private int CalculateAge(String birthday){
        if(birthday.compareTo("Unknown")==0){
            return 0;
        }
        Log.d("071sdf7",birthday);
        String[] split_birth = birthday.split("/");
        int age=0;
        if(split_birth.length>0 && split_birth[0].compareTo("")!=0) {
            int user_yser = Integer.valueOf(split_birth[0]);

            int current_year = Calendar.getInstance().get(Calendar.YEAR);
            age = current_year-user_yser;
        }
        return age;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void EditProfile(){
        Log.d("adfweffa", "asdf"+UserConfig.userBirthday);
        Log.d("adfweffa", "asdf");

        //list of visible
        gender_ET.setVisibility(View.VISIBLE);
        birthday_ET.setVisibility(View.VISIBLE);
        helloMsg_ET.setVisibility(View.VISIBLE);

        if (UserConfig.emailLocked.compareTo("0")==0) {
            email_ET.setVisibility(View.VISIBLE);
            email_TV.setVisibility(View.INVISIBLE);
        }
        else{
            email_ET.setVisibility(View.INVISIBLE);
            email_TV.setVisibility(View.VISIBLE);
        }

        done_IB.setVisibility(View.VISIBLE);
        done_TV.setVisibility(View.VISIBLE);
        slash_TV.setVisibility(View.VISIBLE);
        //list of invisible
        age_TV.setVisibility(View.INVISIBLE);
        gender_TV.setVisibility(View.INVISIBLE);
        intro_TV.setVisibility(View.INVISIBLE);
        yearsold_TV.setVisibility(View.INVISIBLE);
        edit_profile_TV.setVisibility(View.INVISIBLE);
        changePwd_TV.setVisibility(View.INVISIBLE);
        edit_profile_TV.setVisibility(View.INVISIBLE);
        changePwd_TV.setVisibility(View.INVISIBLE);


        gender_ET.setFocusableInTouchMode(false);
        birthday_ET.setFocusableInTouchMode(false);


        if(UserConfig.userGender.compareTo("male")==0){
            gender_ET.setText(getString(R.string.gender_male));
        }
        if(UserConfig.userGender.compareTo("female")==0){
            gender_ET.setText(getString(R.string.gender_female));
        }
        Log.d("adfweffa", UserConfig.userBirthday);
        birthday_ET.setText(UserConfig.userBirthday);
        helloMsg_ET.setText(UserConfig.userMsg);
        // set user email
        email_ET.setText(UserConfig.userEmail);

        gender_ET.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard(v);
                    showListPopWindow();
                }
            }
        });
        gender_ET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                showListPopWindow();
            }
        });

        birthday_ET.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    hideKeyboard(v);
                    showDatePickerDialog();
                }
            }
        });

        birthday_ET.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                showDatePickerDialog();
            }
        });
        Log.d("emailLocked_edprofile", UserConfig.emailLocked);

    }


    private void Done(){

        if(gender_ET.getText().toString().compareTo(getString(R.string.gender_male))==0){
            temp_gender = "male";
        }
        if(gender_ET.getText().toString().compareTo(getString(R.string.gender_female))==0){
            temp_gender = "female";
        }


        if(email_ET.getText().toString().compareTo(email_TV.getText().toString())!=0){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getResources().getString(R.string.email_confirm))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.email_yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {


                            if (!CheckFormat.checkEmail(email_ET.getText().toString()) && !CheckFormat.checkEmail(email_ET.getText().toString())){

                                ShowDialogMsg.showDialog(getString(R.string.noblank2));
                            }else{
                                email_TV.setText(email_ET.getText().toString());
                                String reviseInfo = "username=" + UserConfig.myUserName +
                                        "&gender=" + temp_gender +
                                        "&birthday=" + birthday_ET.getText().toString() +
                                        "&email=" + email_TV.getText().toString()+
                                        "&helloMsg=" + helloMsg_ET.getText().toString();
                                Log.d("email_tv_yes", email_TV.toString());
                                connectServer("POST", "/updateUserInfo", reviseInfo);
                            }

                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.email_no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String reviseInfo = "username=" + UserConfig.myUserName +
                                    "&gender=" + temp_gender +
                                    "&birthday=" + birthday_ET.getText().toString() +
                                    "&email=" + email_TV.getText().toString()+
                                    "&helloMsg=" + helloMsg_ET.getText().toString();
                            Log.d("email_tv_no", email_TV.toString());
                            connectServer("POST", "/updateUserInfo", reviseInfo);
                        }
                    });
            builder.show();
        }




        intro_TV.setText(helloMsg_ET.getText().toString());
        age_TV.setText(Integer.toString(CalculateAge(birthday_ET.getText().toString())));
        gender_TV.setText(gender_ET.getText().toString());
        //email_TV.setText("Please enter email");


        gender_ET.setVisibility(View.INVISIBLE);
        birthday_ET.setVisibility(View.INVISIBLE);
        helloMsg_ET.setVisibility(View.INVISIBLE);
        email_ET.setVisibility(View.INVISIBLE);
        done_IB.setVisibility(View.INVISIBLE);
        done_TV.setVisibility(View.INVISIBLE);
        slash_TV.setVisibility(View.INVISIBLE);

        age_TV.setVisibility(View.VISIBLE);
        gender_TV.setVisibility(View.VISIBLE);
        email_TV.setVisibility(View.VISIBLE);
        intro_TV.setVisibility(View.VISIBLE);
        yearsold_TV.setVisibility(View.VISIBLE);
        edit_profile_TV.setVisibility(View.VISIBLE);
        changePwd_TV.setVisibility(View.VISIBLE);
        edit_profile_TV.setVisibility(View.VISIBLE);
        changePwd_TV.setVisibility(View.VISIBLE);

        //email_TV.setText(UserConfig.userEmail);

        Log.d("emailLocked_after_s", UserConfig.emailLocked);
        hideKeyboard(getView().getRootView());
    }
    // new Alert Dialog asking the user to confirm new email.
//    private Dialog onCreateDialog(Bundle savedInstanceState) {
//        // Use the Builder class for convenient dialog construction
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("Email Confirmation")
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        if(emailLocked){
//                            ShowDialogMsg.showDialog("Sorry you can change your email.");
//                        }
//                        else{
//
//                        }
//
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                    }
//                });
//        // Create the AlertDialog object and return it
//        return builder.create();
//    }


    private void ChangePWD(){
        final View registerView =  LayoutInflater.from(getActivity()).inflate(R.layout.fragment_changepwd,null);
        //mSignInButton = registerView.findViewById(R.id.login_with_google);
        //logoutButton = (Button) registerView.findViewById(R.id.logout_with_google);

        final EditText newpwdEditText = (EditText) registerView.findViewById(R.id.newchangePwd);
        final EditText pwdEditText = (EditText) registerView.findViewById(R.id.changePwd);
        final EditText newpwdEditText2 = (EditText) registerView.findViewById(R.id.newchangePwd2);
        // recover previous register message
        alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.changepwd))
                .setView(registerView)
                .setNegativeButton(getString(R.string.cancel),new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                    }
                })
                .setPositiveButton(getString(R.string.okresend), null).show();

        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String newpwd = newpwdEditText.getText().toString();
                String newpwd2 = newpwdEditText2.getText().toString();
                String pwd = pwdEditText.getText().toString();

                if(newpwd.equals("") || pwd.equals("") || newpwd2.equals("")){
                    ShowDialogMsg.showDialog(getString(R.string.noblank1));
                }
                else if(newpwd.compareTo(newpwd2) != 0){
                    ShowDialogMsg.showDialog(getString(R.string.passwordnotsame));
                }
                else{
                    String changedInfo = "username=" + UserConfig.myUserName +
                            "&password=" + pwd +
                            "&newpassword=" + newpwd;
                    //dialogNoDismiss(dialog);
                    connectServer("POST", "/modifyPassword", changedInfo);
                    alertDialog.dismiss();
                }
            }
        });
    }



}
