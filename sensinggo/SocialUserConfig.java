package edu.nctu.wirelab.sensinggo;

import android.util.Log;

//import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by py on 6/15/18.
 */

public class SocialUserConfig {
    private static int userNumbers = 7;

    public static String [] infoItems = new String [] {"username", "nickname", "gender", "birthday", "email", "helloMsg", "distance"};
    public static String [] infoValue = new String [userNumbers];

    public static void setSocialUserInfo(JSONObject obj){
        try {
            for (int i=0; i<infoValue.length; i++){
                infoValue[i] = obj.getString(infoItems[i]);
            }

        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}
