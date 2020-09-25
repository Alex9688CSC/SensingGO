package edu.nctu.wirelab.sensinggo.Fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import edu.nctu.wirelab.sensinggo.Connect.HttpsConnection;
import edu.nctu.wirelab.sensinggo.R;
import edu.nctu.wirelab.sensinggo.Record.Detailrecord;
import edu.nctu.wirelab.sensinggo.Record.TransactionRecord;
import edu.nctu.wirelab.sensinggo.UserConfig;

public class DetailTranRecordFragment extends Fragment {

    private ListView listView;
    private TextView textView1, textView2, textView3;

    public static boolean accessperson = false;
    private Button totaltoButton, totalfromButton, personinfoButton;
    SimpleAdapter adapter;
    ArrayList<HashMap<String,String>> items;

    final String ID_TITLE = "TITLE", ID_SUBTITLE = "SUBTITLE";
    DialogInterface alertDialog;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.listview_detailrecord, container, false);
        listView = (ListView) view.findViewById(R.id.listinfo);
        textView1 = view.findViewById(R.id.textView1);
        textView2 = view.findViewById(R.id.targetname);
        textView3 = view.findViewById(R.id.textView3);

        textView2.setText(TranRecordFragment.targetname);
        textView2.setPaintFlags(textView2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textView3.setText(getString(R.string.totalto) +" : "+ Detailrecord.totalto +" "+ getString(R.string.coins) + "\n" + getString(R.string.totalfrom) +" : "+ Detailrecord.totalfrom +" "+ getString(R.string.coins));



        items = new ArrayList<HashMap<String, String>>();
        for(int i = 0; i< Detailrecord.detailrecordN; i++) {
            HashMap<String, String> item = new HashMap<String, String>();

            Detailrecord.Record temp = Detailrecord.infoValue.elementAt(i);
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

        textView2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                accessperson = true;
                String info = "username=" + TranRecordFragment.targetname;
                connectServer("POST", "/getUserInfo", info);
            }
        });

        // see detail information of user
        /*
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public  void onItemClick(AdapterView<?> parent, View view, int position, long id){
                accessrecord = true;
                String name = Detailrecord.infoValue.elementAt(position).target;
                String info = "username=" + name;
                Log.d("0614pos", Integer.toString(position) + '\t' + name);
                connectServer("POST", "/getUserInfo", info);
            }
        });
        */

        //show totalto coins
        /*
        totaltoButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                final AlertDialog.Builder showtotaltoDialog = new AlertDialog.Builder(getActivity());
                showtotaltoDialog.setMessage(Detailrecord.totalto+" "+getString(R.string.coins))
                        .setIcon(R.mipmap.ic_launcher)
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .show();

            }
        });

        //show totalfrom coins
        totalfromButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                final AlertDialog.Builder showtotalfromDialog = new AlertDialog.Builder(getActivity());
                showtotalfromDialog.setMessage(Detailrecord.totalfrom+" "+getString(R.string.coins))
                        .setIcon(R.mipmap.ic_launcher)
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .show();

            }
        });

        personinfoButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                accessperson = true;
                String info = "username=" + TranRecordFragment.targetname;
                connectServer("POST", "/getUserInfo", info);

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
