package edu.nctu.wirelab.sensinggo.Fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import edu.nctu.wirelab.sensinggo.Connect.HttpsConnection;
import edu.nctu.wirelab.sensinggo.R;
import edu.nctu.wirelab.sensinggo.Record.TransactionRecord;
import edu.nctu.wirelab.sensinggo.ShowDialogMsg;
import edu.nctu.wirelab.sensinggo.SocialUserConfig;
import edu.nctu.wirelab.sensinggo.UserConfig;

public class TranRecordFragment extends Fragment {
    public class Record{
        public String kind, time, amount, id, target;
        public Record(){
            this.kind = ""; this.time = ""; this.amount = ""; this.id = ""; this.target = "";
        }
    }
    private ListView listView;

    public static boolean accessrecord = false;
    public static String targetname;
    SimpleAdapter adapter;
    ArrayList<HashMap<String,String>> items;

    final String ID_TITLE = "TITLE", ID_SUBTITLE = "SUBTITLE";
    DialogInterface alertDialog;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.listview_transaction_record, container, false);
        listView = (ListView) view.findViewById(R.id.listinfo);


        items = new ArrayList<HashMap<String, String>>();
        for(int i = 0; i< TransactionRecord.recordNumbers; i++) {
            HashMap<String, String> item = new HashMap<String, String>();

            TransactionRecord.Record temp = TransactionRecord.infoValue.elementAt(i);
            Log.d("sadfdfwe",temp.target);
            item.put(ID_TITLE, temp.time);
            item.put(ID_SUBTITLE, temp.amount + " " + getString(R.string.coins) + " " + temp.kind + " " + temp.target);
            items.add(item);
        }

        adapter = new SimpleAdapter(
                getActivity(),
                items,
                android.R.layout.simple_list_item_2,
                new String[]{ID_TITLE, ID_SUBTITLE},
                new int[]{android.R.id.text1, android.R.id.text2}
        );

        listView.setAdapter(adapter);

        // see detail information of user
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public  void onItemClick(AdapterView<?> parent, View view, int position, long id){
                accessrecord = true;
                targetname = TransactionRecord.infoValue.elementAt(position).target;
                String info = "username=" + UserConfig.myUserName + "&targetname=" + targetname;
                connectServer("POST", "/checkMoneyRecords", info);
            }
        });

        //delete record
        /*
        final AlertDialog.Builder DeleteRecordDialog = new AlertDialog.Builder(getActivity());
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long id) {
                if(UserConfig.similarityArray[position][2].equals("old")){

                    DeleteRecordDialog.setMessage(getString(R.string.deleterecord)+" ?")
                            .setIcon(R.mipmap.ic_launcher)
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String recordid = TransactionRecord.infoValue.elementAt(position).id;
                                    String info = "username=" + UserConfig.myUserName +
                                            "&recordid=" + recordid;
                                    connectServer("POST", "/deleteTransactionRecords", info);
                                }
                            })
                            .setNegativeButton("no", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .show();
                }
                return true;
            }
        });
*/


        return  view;
    }
    public void connectServer(String method, String path, String info){
        Log.d("0612conn", info);
        HttpsConnection httpsConnection = new HttpsConnection(getActivity());
        httpsConnection.setActivity(getActivity());
        httpsConnection.setMethod(method, info);
        httpsConnection.execute(path);

    }
}
