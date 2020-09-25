package edu.nctu.wirelab.sensinggo;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import edu.nctu.wirelab.sensinggo.AugmentedRealityOri.Spot;

import edu.nctu.wirelab.sensinggo.Fragment.LoginUserFragment;

public class UserConfig {

    public static String myUserName = null;
    public static String myUserid = null;
    public static String userType = null;
    public static final String ipipip = "132137216.1635";
    public static final String passpass = "asdf020labsdfvg";
    public static Double totalmoney = 0.0;

    public static Boolean firstMoney = true;
    public static int numberblacks = 0;
    public static int newfriend_len = 0;
    public static int choosecamp = -1;

    // for special event
    public static String content = null;
    public static String date = null;
    public static String totalcoins = null;
    public static boolean showmoney = false;
    public static boolean qualified = false;
    public static int proportion = 1;
    // mother day
    public static String mother_gift = null;

    //train
    public static String train_gift = null;
    public static int ungiftedStamp = 0;

    //donate fountain
    public static int current_coins = 0;

    // once user login and he/she wants to back to mapFraqment app should call getCoinList again
    public static  boolean loginned = false;



    public static String userGender = null, userBirthday = null, userEmail = null, emailLocked= null, userMsg = null;

    public static String [][] similarityArray ;

    public static String [] coinMasters  = new String[4];

    public static ArrayList<Spot> spotlist = new ArrayList<>();


    public static boolean autoUploadByMobile = false;

    public static void setUserName(String username){
        myUserName = username;
    }

    public static void setUserID(String userID){
        myUserid = userID;
    }

    public static void setUserInfo(JSONObject obj){
        try {

            userGender = obj.getString("gender");
            userBirthday = obj.getString("birthday");
            userEmail = obj.getString("email");
//            if(userEmail.compareTo("")==0){
//                userEmail= "Please enter email";
//            }
            //emailLocked= "0";
            emailLocked= obj.getString("emailLocked"); // added this line
            userMsg = obj.getString("helloMsg");
            myUserid = obj.getString("uid");
            userType = obj.getString("userType");
            totalmoney = obj.getDouble("total_money");

            totalcoins = obj.getString("actmoney");


            if(obj.has("camp")){
                choosecamp = obj.getInt("camp");
            }


        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public static void setspecialInfo(JSONObject obj){
        try {
            content = obj.getString("content");
            date = obj.getString("time");
            totalcoins = obj.getString("money");
            showmoney = obj.getBoolean("showmoney");
            choosecamp = obj.getInt("camp");
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public static void setSocialInfo(JSONArray jsonArray){

        try {


            Log.d("0316018", "SDFAWEFWE");
            JSONObject obj = jsonArray.getJSONObject(0);
            JSONArray newfriendJSONarray = obj.getJSONArray("stranger");
            //JSONArray oldfriendarray = obj.getJSONArray("friends");
            JSONObject oldfriendObject = obj.getJSONObject("friends");
            JSONArray blacklistarray = obj.getJSONArray("blacklist");

            int newlen = newfriendJSONarray.length();
            //int oldlen = oldfriendarray.length();
            JSONArray oldfriendKeys = oldfriendObject.names();
            int oldlen = oldfriendObject.names().length();
            int blacklen = blacklistarray.length();

            Log.d("testLen9456", "oldlen = " + oldlen);

            newfriend_len = newlen;
            similarityArray = new String[newlen + oldlen + blacklen][3];
            for(int index = 0; index < newlen; index++){
                //String tmp = "Email: ";
                String tmp = "Nickname";
                String id =  newfriendJSONarray.getString(index);
                Log.d("15121512", "ll" +newlen);

                similarityArray[index][0] = id;
                similarityArray[index][1] = tmp;
                similarityArray[index][2] = "new";
            }
            for(int index = 0; index < oldlen; index++){
                //String id = oldfriendObject.getString(oldfriendKeys.getString(index));
                String id = oldfriendKeys.getString(index);
                Log.d("testLen9456", "id = " + id);
                similarityArray[index+newlen][0] = id;
                //similarityArray[index+newlen][1] = "no";
                similarityArray[index+newlen][1] = oldfriendObject.getString(oldfriendKeys.getString(index));
                similarityArray[index+newlen][2] = "old";
            }
            for(int index = 0; index < blacklen; index++){
                String id = blacklistarray.getString(index);
                similarityArray[index+newlen+oldlen][0] = id;
                similarityArray[index+newlen+oldlen][1] = "no";
                similarityArray[index+newlen+oldlen][2] = "black";
            }
            numberblacks = blacklen;




        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public static void setCoinMasters(JSONArray jsonArray){

        try {
            JSONObject obj = jsonArray.getJSONObject(0);
            JSONArray coinsTopUserJSONarray = obj.getJSONArray("coinsTopUser");
            for (int i = 0; i < coinsTopUserJSONarray.length(); i++){
                coinMasters[i] = coinsTopUserJSONarray.getString(i);
                Log.d("aavav", coinsTopUserJSONarray.getString(i));

            }

        } catch (JSONException e){
            coinMasters = null;
            e.printStackTrace();
        }
    }

    public static void setAutoUploadByMobile(boolean autouploadbymobile){
        autoUploadByMobile = autouploadbymobile;
    }

//    public static double getTotalMoney(){
//        loadConfigFrom(MainActivity.configPath);
//        Log.d("0807", "money from file");
//        Log.d("0807", Double.toString(totalMoney));
//        return totalMoney;
//    }
//
//    public static void addMoney(int money) {
//        totalMoney = getTotalMoney();
//        totalMoney += money;
//    }

    public static void setFirstMoney(boolean isfirst) {
        firstMoney = isfirst;
    }

    public static void saveConfigTo(String path){

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(path));

            out.write("username=" + myUserName + "\n");
            out.write("AutoUploadByMobile=" + autoUploadByMobile + "\n");
//            out.write("totalMoney=" + String.valueOf(totalMoney)+ "\n");
            out.write("firstMoney=" + String.valueOf(firstMoney) + "\n");
            out.write("userID=" + String.valueOf(myUserid));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean loadConfigFrom(String path){
        //Log.d(TagName, "LoadConfigFrom: " + path);
        File file = new File(path);
        int flag = 0;

        if(!file.exists()){
            return false;
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;
            while ((line = br.readLine()) != null) {
                Log.d("0521", line);
                //Log.d(TagName, "line:"+line);
                String[] token = line.split("=");
                if (token[0].equals("username") && token.length>=2) {
                    //Log.d(TagName, "token[0].equals(\"email\")");
                    myUserName = token[1];
                    flag = flag | 1;
                    //Log.d(TagName, "myEmail:"+myEmail);
                } else if (token[0].equals("AutoUploadByMobile") && token.length>=2) {
                    if (token[1].compareTo("false") == 0) {
                        autoUploadByMobile = false;
                        flag = flag | 2;
                    } else if (token[1].compareTo("true") == 0) {
                        autoUploadByMobile = true;
                        flag = flag | 2;
                    }
                } else if (token[0].equals("totalMoney") && token.length>=2) {
                    Log.d("0807", "read config file, total money= "  + token[1]);
//                    totalMoney = Double.parseDouble(token[1]);
                } else if (token[0].equals("firstMoney") && token.length>=2) {
                    Log.d("0808", "read config file, first money= "  + token[1]);
                    firstMoney = Boolean.valueOf(token[1]);
                } else if (token[0].equals("userID") && token.length>=2) {
                    Log.d("0809", "read config file, userID= " + token[1]);
                    myUserid = token[1];
                }


            }

            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(flag == 3) {
            return true;
        }
        else{
            //Log.d(TagName, "lack of email in the config or AutoUploadByMobile");
            return false;
        }
    }
}
