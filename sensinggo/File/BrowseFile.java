package edu.nctu.wirelab.sensinggo.File;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import edu.nctu.wirelab.sensinggo.R;

public class BrowseFile extends Fragment {
    private static Context mContext;
    private ListView listView;
    private String filePath;
    private File[] fileList;

    public BrowseFile(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_browse_file, container, false);

        listView = (ListView) view.findViewById(R.id.listView);
        filePath = "/";

        ChangeDirectory();

        //ListView onClick
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                if( fileList[position].isDirectory() ){
                    filePath = fileList[position].getAbsolutePath();
                    ChangeDirectory();
                }
            }
        });
        return view;
    }

    private void ChangeDirectory(){
        File dir = new File(filePath);
        fileList = dir.listFiles();
        ArrayAdapter<String> adapter;

        if(dir.canRead() == false){
            String[] theNamesOfFiles = new String[0];
            adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, android.R.id.text1, theNamesOfFiles);
        }
        else {
            String[] theNamesOfFiles = new String[fileList.length];

            for (int i=0; i<theNamesOfFiles.length; i++) {
                theNamesOfFiles[i] = fileList[i].getName();
                if (fileList[i].isDirectory())
                    theNamesOfFiles[i] = theNamesOfFiles[i] + "/";
            }

            adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, android.R.id.text1, theNamesOfFiles);
        }

        listView.setAdapter(adapter);
    }

    public static String getStringFromFile (String FilePath) throws Exception {
        File fl = new File(FilePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line=reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }
}
