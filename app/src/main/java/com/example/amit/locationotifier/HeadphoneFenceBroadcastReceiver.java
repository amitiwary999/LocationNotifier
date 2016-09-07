package com.example.amit.locationotifier;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.awareness.fence.FenceState;

/**
 * Created by amit on 7/9/16.
 */
public class HeadphoneFenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "Awareness";
    private Context contxt;
    MediaPlayer song;
    @Override
    public void onReceive(Context context, Intent intent) {

        FenceState fenceState = FenceState.extract(intent);
        contxt=context;
      //  Toast.makeText(context,"Correct",Toast.LENGTH_LONG).show();

        Log.d(TAG, "Fence Receiver Received");

        if (TextUtils.equals(fenceState.getFenceKey(), "headphoneFenceKey")) {
            switch (fenceState.getCurrentState()) {
                case FenceState.TRUE:
                    Log.i(TAG, "Fence > Headphones are plugged in.");
                    notification();
                    Toast.makeText(context,"Correct",Toast.LENGTH_LONG).show();
                    break;
                case FenceState.FALSE:
                    Log.i(TAG, "Fence > Headphones are NOT plugged in.");
                  //  notification();
                  //  Toast.makeText(context,"wrong",Toast.LENGTH_LONG).show();
                    break;
                case FenceState.UNKNOWN:
                    Log.i(TAG, "Fence > The headphone fence is in an unknown state.");
                   // Toast.makeText(context,"not registered", Toast.LENGTH_LONG).show();
                    break;
            }
        }

    }
    public void notification(){
              song=MediaPlayer.create(contxt,R.raw.internetfriends0);
        song.start();
        NotificationManager notificationManager = (NotificationManager)
                contxt.getSystemService(contxt.NOTIFICATION_SERVICE);
        Notification n= new Notification.Builder(contxt).setContentTitle("Location notifier notice")
                .setContentText(" Just 1 km away from destination")
                .setSmallIcon(R.drawable.notification).setAutoCancel(true).build();
        notificationManager.notify(0,n);
    }
}
