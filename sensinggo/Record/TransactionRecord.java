package edu.nctu.wirelab.sensinggo.Record;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

public class TransactionRecord {
    public class Record{
        public String kind, time, amount, id, target;
        public Record(){
            this.kind = ""; this.time = ""; this.amount = ""; this.id = ""; this.target = "";
        }
    }
    public static int recordNumbers = 0;

    //public static String [] infoItems = new String [] {"Kind", "gender", "birthday", "email", "helloMsg"};
    public static Vector<Record> infoValue = new Vector<Record>(0);


    public static void setTranrecordInfo(JSONArray inputjarray){
        try {
            recordNumbers = inputjarray.length();
            JSONObject tempobject;
            infoValue.clear();

            Log.d("sadfdfwe","fffdd"+recordNumbers);
            for (int i=0; i<recordNumbers; i++){
                Log.d("sadfdfwe","fff"+infoValue.capacity());
                tempobject = inputjarray.getJSONObject(i);
                Record temp = new TransactionRecord().new Record();
                temp.kind = tempobject.getString("Kind");
                temp.time = tempobject.getString("Time");
                temp.amount = tempobject.getString("Amount");
                temp.id = tempobject.getString("Id");
                temp.target = tempobject.getString("Target");
                infoValue.add(temp);
                Log.d("sadfdfwe","fff"+infoValue.capacity());
            }

        } catch (JSONException e){
            e.printStackTrace();
        }
    }


}
