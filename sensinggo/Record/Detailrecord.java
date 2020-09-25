package edu.nctu.wirelab.sensinggo.Record;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

public class Detailrecord {
    public class Record{
        public String kind, time, amount, id, target;
        public Record(){
            this.kind = ""; this.time = ""; this.amount = ""; this.id = ""; this.target = "";
        }
    }

    public static int detailrecordN = 0;
    public static int totalto = 0;
    public static int totalfrom = 0;

    //public static String [] infoItems = new String [] {"Kind", "gender", "birthday", "email", "helloMsg"};
    public static Vector<Detailrecord.Record> infoValue = new Vector<Detailrecord.Record>(0);


    public static void setDetailTranrecordInfo(JSONArray inputjarray){
        try {
            int recordNumbers = 0;
            recordNumbers = inputjarray.length();
            JSONObject mainobject = inputjarray.getJSONObject(0);
            JSONArray transactionarray;
            JSONObject tempobject;
            infoValue.clear();

            // get totalto and totalfrom
            if(recordNumbers > 0) {
                totalto = mainobject.getInt("To");
                totalfrom = mainobject.getInt("From");
                transactionarray = mainobject.getJSONArray("TransactionRecords");
                detailrecordN = transactionarray.length();


                Log.d("sadfdfwe", "fffdd" + recordNumbers);
                for (int i = 0; i < detailrecordN; i++) {
                    Log.d("sadfdfwe", "fff" + infoValue.capacity());
                    tempobject = transactionarray.getJSONObject(i);
                    Detailrecord.Record temp = new Detailrecord().new Record();
                    temp.kind = tempobject.getString("Kind");
                    temp.time = tempobject.getString("Time");
                    temp.amount = tempobject.getString("Amount");
                    temp.id = tempobject.getString("Id");
                    temp.target = tempobject.getString("Target");
                    infoValue.add(temp);
                    Log.d("sadfdfwe", "fff" + infoValue.capacity());
                }
            }

        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}
