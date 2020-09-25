package edu.nctu.wirelab.sensinggo.Connect;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Properties;

import edu.nctu.wirelab.sensinggo.Fragment.UserFragment;
import edu.nctu.wirelab.sensinggo.MainActivity;
import edu.nctu.wirelab.sensinggo.R;
import edu.nctu.wirelab.sensinggo.RunIntentService;
import edu.nctu.wirelab.sensinggo.ShowDialogMsg;
import edu.nctu.wirelab.sensinggo.UserConfig;

import static edu.nctu.wirelab.sensinggo.Connect.Setting.SFTP_HOST;
import static edu.nctu.wirelab.sensinggo.Connect.Setting.SFTP_PASSWORD;
import static edu.nctu.wirelab.sensinggo.Connect.Setting.SFTP_USERNAME;
import static edu.nctu.wirelab.sensinggo.Connect.Setting.TEST_UPLOADDATA_PATH;
import static edu.nctu.wirelab.sensinggo.Connect.Setting.UPLOADDATA_PATH;
import static edu.nctu.wirelab.sensinggo.Connect.Setting.UPLOADSIG_PATH;

/**
 * Use JSch to establish ssh(sftp) connection
 */

public class SFTPController extends AsyncTask<Void, String, Integer> { // Params, Progress, Result
    private final String tagName = "SFTPController";

    /**
     * JSch Session
     */
    private Session mSession;


    private ProgressBar pbUpload = null;
    private TextView pbText=null;
    private TextView  uploadProgressText=null;
    public static int uploadBytes = 0;
    public static String logFile = "";

    private WeakReference<Context> weakContext;

    public SFTPController(Context context) {
        this.weakContext = new WeakReference<>(context);
    }

    public boolean connectSFTPServer(){
        try {
            JSch jsch = new JSch();
            mSession = null;
            Log.d("hostohst", SFTP_HOST+MainActivity.ipipip.substring(6,10)+ UserConfig.ipipip.substring(4,6));
            mSession = jsch.getSession(SFTP_USERNAME, SFTP_HOST+MainActivity.ipipip.substring(6,10)+ UserConfig.ipipip.substring(4,6), 22); // port 22
            mSession.setPassword(SFTP_PASSWORD+MainActivity.passpass.substring(7,10)+UserConfig.passpass.substring(4,7));
            Properties properties = new Properties();
            properties.setProperty("StrictHostKeyChecking", "no");
            mSession.setConfig(properties);
            mSession.connect();
        } catch (JSchException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void setProgressBar(ProgressBar pb, TextView pbT, TextView upT){
        pbUpload=pb; pbText=pbT; uploadProgressText=upT;
    }

    /**
     * Upload the data to sftp server
     * if there isn't "upload" word in the logs directory
     * @param params
     * @return
     */
    @Override
    protected Integer doInBackground(Void... params) {

        Log.d("0612", "sftp doInBackground start!");

        if(connectSFTPServer()){
            try {
                Channel channel = mSession.openChannel("sftp");
                channel.connect();
                ChannelSftp sftp = (ChannelSftp) channel;
                File folder = new File(MainActivity.logPath);
                String[] fileList = folder.list();
                if(fileList == null) {
                    return 0;
                }
                publishProgress("0");
                for(int i=0; i<fileList.length; i++){
                    boolean wrongFormatFile = false;
                    if(!fileList[i].contains("uploaded") && !fileList[i].contains(RunIntentService.recordFilePrefix)){
                        File f = new File(MainActivity.logPath, fileList[i]);

                        if (!fileList[i].contains("Error") && !fileList[i].contains(".sig")) {
                            int length = (int) f.length();
                            byte[] bytes = new byte[length];
                            try {

                                FileInputStream in = new FileInputStream(f);
                                in.read(bytes);
                                in.close();
                                String contents = new String(bytes);

                                // if file is not JSON format, add "]" to the end of file
                                if (!isJSONValid(contents)) {
                                    FileWriter fileWriter = new FileWriter(MainActivity.logPath + fileList[i], true);
                                    fileWriter.write("\n]\n");
                                    fileWriter.close();

                                    length = (int) f.length();
                                    bytes = new byte[length];
                                    in = new FileInputStream(f);
                                    in.read(bytes);
                                    in.close();
                                    contents = new String(bytes);

                                    //if file is still not, rename. And record the error files
                                    if (!isJSONValid(contents)) {
//                                        String ErrorPath = MainActivity.logPath + "ErrorFiles";
//                                        try {
//                                            java.io.FileWriter writer = new java.io.FileWriter(ErrorPath, true);
//                                            writer.write("Username & Date: " + RunIntentService.recordFilePrefix + " , Error Filename: " + f.toString() + "\n");
//                                            writer.close();
//                                        } catch (IOException e2) {
//                                            e2.printStackTrace();
//                                        }
                                        f.renameTo(new File(MainActivity.logPath, "Error" + fileList[i]));
                                        fileList[i] = "Error" + fileList[i];
                                        wrongFormatFile = true;
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                        if (fileList[i].contains(".sig")) {
                            sftp.put(MainActivity.logPath+fileList[i], UPLOADSIG_PATH+fileList[i]);
                        } else {
                            sftp.put(MainActivity.logPath+fileList[i], UPLOADDATA_PATH+fileList[i]);
                        }


                        uploadBytes += f.length();
                        f.renameTo(new File(MainActivity.logPath, "uploaded" + fileList[i]));
                        logFile = fileList[i];
                    }
                    float percent = (i/(float)fileList.length)*100;
                    publishProgress("" + (int)percent);
                }
                publishProgress("100");
                channel.disconnect();
                mSession.disconnect();
                Log.d("0612", "sftp doInBackground end!");
            } catch (JSchException e) {
                e.printStackTrace();
                Log.e("MYAPP", "exception01", e);
//                ShowDialogMsg.showDialog(e.getMessage());
                return -1;
            }
            catch (SftpException e) {
                e.printStackTrace();
                Log.e("MYAPP", "exception02", e);
//                ShowDialogMsg.showDialog(e.getMessage());
                return -1;
            }
            return 0;
        }
        else {
            Log.e("MYAPP", "connect err");
        }
        return -1;
    }

    @Override
    protected void onPreExecute(){
    }

    @Override
    protected void onProgressUpdate(String... progress){
        if(pbUpload != null && MainActivity.isInUserFragment){
            pbUpload.setProgress(Integer.parseInt(progress[0]));
            pbText.setText(weakContext.get().getString(R.string.upload_progress_percentage,Integer.parseInt(progress[0])) );
            //uploadProgressText.setText(weakContext.get().getString(R.string.uploading));
        }
    }

    @Override
    protected void onPostExecute(Integer result){
        if(result == 0){
            Log.d("0612", "sftp onPostExecute start!");
            removeUploadedInFolder(MainActivity.logPath);
            //ShowDialogMsg.showDialogLong("Upload succeed");
            ShowDialogMsg.showDialog(weakContext.get().getString(R.string.uploadingfinish));
//            if(uploadProgressText != null)
//                //uploadProgressText.setText(weakContext.get().getString(R.string.uploadingfinish));
//            if(pbUpload != null) {
//                UserFragment.showUploadSuccess(weakContext.get(),1);
//            }
//            else {
//                UserFragment.showUploadSuccess(weakContext.get(),2);
//            }
        }
        else {
            ShowDialogMsg.showDialog("上傳失敗");
        }
        if (MainActivity.isInUserFragment) {
            UserFragment.hideProgressBar();
        }
        Log.d("0612", "sftp onPostExecute end!");
    }
    //remove all files containing "uploaded" in folderPath
    public void removeUploadedInFolder(String folderPath){
        File folder = new File(folderPath);
        String[] fileList = folder.list();
        for(int i=0; i<fileList.length; i++){
            if (fileList[i].contains("uploaded")){
                File file = new File(folderPath+fileList[i]);
                file.delete();
            }
        }
    }

    //Remove all files in folderPath
    public void removeFolder(String folderPath){
        File folder = new File(folderPath);
        String[] fileList = folder.list();
        for(int i=0; i<fileList.length; i++){
            File file = new File(folderPath + fileList[i]);
            file.delete();
        }
    }

    //Remove the FileName
    public void removeFile(String fileName){
        File file = new File(fileName);
        file.delete();
    }

    //check file is JSON or not
    private boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}
