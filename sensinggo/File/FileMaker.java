package edu.nctu.wirelab.sensinggo.File;

import android.os.Looper;
import android.util.Log;

import java.io.FileWriter;
import java.io.IOException;

import edu.nctu.wirelab.sensinggo.MainActivity;
import edu.nctu.wirelab.sensinggo.RunIntentService;

/**
 * Write the RunIntentService's data in a file by append mode
 */
public class FileMaker {
    public static boolean fileFirstWrite = true;
    private static String filePath;
    private static FileWriter fileWriter;

    public static void setFilePath(String path){
        filePath = path;
    }

    public static void setFileNotFirstWrite(){
        fileFirstWrite = false;
    }

    public static synchronized void write(String msg) {

        //Create fileWriter
        try {
            fileWriter = new FileWriter(filePath,true);
        } catch (IOException e) {
            e.printStackTrace();
            String ErrorPath = MainActivity.logPath + "ErrorLog" + RunIntentService.recordFilePrefix;
            try {
                java.io.FileWriter writer = new java.io.FileWriter(ErrorPath, true);
                writer.write(e.toString());
                writer.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }

        //Write the file
        try {
            //Log.d("threadID", "writing file thread id:" + Thread.currentThread().getId());
            if (fileFirstWrite && msg!=null && msg.length()>0) {
                fileFirstWrite = false;
                fileWriter.write("[\n" + msg);
            } else if (msg!=null && msg.length()>0) {
                fileWriter.write("\n,\n" + msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
            String ErrorPath = MainActivity.logPath + "ErrorLog" + RunIntentService.recordFilePrefix;
            try {
                java.io.FileWriter writer = new java.io.FileWriter(ErrorPath, true);
                writer.write(e.toString());
                writer.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }

        //Close the writer
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            String ErrorPath = MainActivity.logPath + "ErrorLog" + RunIntentService.recordFilePrefix;
            try {
                java.io.FileWriter writer = new java.io.FileWriter(ErrorPath, true);
                writer.write(e.toString());
                writer.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }

//        try {
//            if (RunIntentService.recordWriter != null) {
//                    if (fileFirstWrite && msg!=null && msg.length()>0) {
//                        fileFirstWrite = false;
//
//                        RunIntentService.recordWriter.write("[\n" + msg);
//                    } else if (msg!=null && msg.length()>0) {
//                        RunIntentService.recordWriter.write("\n,\n" + msg);
//                    }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            String errorPath = MainActivity.logPath + "ErrorLog" + RunIntentService.recordFilePrefix;
//            try {
//                java.io.FileWriter writer = new java.io.FileWriter(errorPath, true);
//                writer.write(e.toString());
//                writer.close();
//            } catch (IOException e2) {
//                e2.printStackTrace();
//            }
//        }
    }

    public static synchronized void pureWrite(String msg) {
        try {
            if (RunIntentService.recordWriter != null) {
                RunIntentService.recordWriter.write(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
            String ErrorPath = MainActivity.logPath + "ErrorLog" + RunIntentService.recordFilePrefix;
            try {
                java.io.FileWriter writer = new java.io.FileWriter(ErrorPath, true);
                writer.write(e.toString());
                writer.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    public static synchronized void closeWriter() {
//        try {
//            if (RunIntentService.recordWriter != null) {
//                pureWrite("\n]\n");
//                RunIntentService.recordWriter.close();
//            }
//            RunIntentService.recordWriter = null;
//        } catch (IOException e) {
//            e.printStackTrace();
//            String ErrorPath = MainActivity.logPath + "ErrorLog" + RunIntentService.recordFilePrefix;
//            try {
//                java.io.FileWriter writer = new java.io.FileWriter(ErrorPath, true);
//                writer.write(e.toString());
//                writer.close();
//            } catch (IOException e2) {
//                e2.printStackTrace();
//            }
//        }
    }
}
