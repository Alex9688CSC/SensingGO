package edu.nctu.wirelab.sensinggo.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Reference:
 * 1. https://gist.github.com/ishitcno1/7261765
 * 2. http://stackoverflow.com/questions/21099395/detecting-whether-screen-is-off-or-on-inside-service
 */

public class ScreenStateReceiver extends BroadcastReceiver {
    public static String screen_state = "on";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_SCREEN_ON.equals(action)) {
            screen_state = "on";
        }
        else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            screen_state = "off";
        }
    }
}
