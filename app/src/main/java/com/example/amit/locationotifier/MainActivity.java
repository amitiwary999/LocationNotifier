package com.example.amit.locationotifier;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    EditText address;
    TextView ans;
    Button btn,btn1;
    String addrss;
    AwarenessFence loctnfnc;
    private HeadphoneFenceBroadcastReceiver fenceReceiver;
    private static final String TAG = "Awareness";
    private GoogleApiClient mGoogleApiClient;
    double lat=0.00,lng=0.00;
     String check;
    private static final String FENCE_RECEIVER_ACTION = "FENCE_RECEIVE";
    private PendingIntent mFencePendingIntent;
    double radius=1000.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        address=(EditText) findViewById(R.id.address);
        ans=(TextView) findViewById(R.id.txta);
        btn=(Button)findViewById(R.id.button);
        btn1=(Button)findViewById(R.id.button2);
        fenceReceiver = new HeadphoneFenceBroadcastReceiver();
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(Awareness.API)
                .build();
        mGoogleApiClient.connect();
        Intent intent = new Intent(FENCE_RECEIVER_ACTION);
        mFencePendingIntent = PendingIntent.getBroadcast(MainActivity.this,
                10001,
                intent,
                0);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                addrss=address.getText().toString();
                if(addrss.length()!=0)
                    getlatlan(addrss);
                else
                    Toast.makeText(MainActivity.this,"Enter address",Toast.LENGTH_LONG).show();

            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                addrss=address.getText().toString();
                if(addrss.length()==0){
                    Toast.makeText(MainActivity.this,"Enter address",Toast.LENGTH_LONG).show();
                }
                registerFences();
               // registerReceiver(fenceReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));
            }
        });

    }

    private void registerFences() {
        // Create a fence.
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // try {
            loctnfnc = LocationFence.in(lat, lng, radius,1000);

            // }
            //catch (SecurityException e){
            //  e.printStackTrace();
            // }
            Awareness.FenceApi.updateFences(
                    mGoogleApiClient,
                    new FenceUpdateRequest.Builder()
                            .addFence("headphoneFenceKey", loctnfnc, mFencePendingIntent)
                            .build())
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                // notification();
                                Log.i(TAG, "Fence was successfully registered.and lat is" + lat);
                                registerReceiver(fenceReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));

                            } else {
                                Log.e(TAG, "Fence could not be registered: " + status);
                            }
                        }
                    });
        }
        else{
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    12345);
           // Toast.makeText(MainActivity.this,"Please on gps service",Toast.LENGTH_LONG).show();
        }
    }

    public void getlatlan(final String addr){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    Geocoder  geocoder=new Geocoder(MainActivity.this, Locale.getDefault());
                    List<Address> addresses =geocoder.getFromLocationName(addr,1);
                    Address address=addresses.get(0);
                     lat=address.getLatitude();
                    lng=address.getLongitude();

                   // check=String.valueOf(lat);
                   // ans.setText(check);
                }catch (IOException e){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,"Location not found...Write correct location",Toast.LENGTH_LONG).show();

                }
            }
        });

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:

                moveTaskToBack(true);

                return true;
        }
        return false;
    }

    public void notification(){

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        Notification n= new Notification.Builder(this).setContentTitle("Destination notice")
                .setContentText("1 km away from destination")
                .setSmallIcon(R.drawable.places_ic_search).setAutoCancel(true).build();
        notificationManager.notify(0,n);
    }

}

/*class HeadphoneFenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "Awareness";

    @Override
    public void onReceive(Context context, Intent intent) {
        FenceState fenceState = FenceState.extract(intent);
        Toast.makeText(context,"Correct",Toast.LENGTH_LONG).show();

        Log.d(TAG, "Fence Receiver Received");

        if (TextUtils.equals(fenceState.getFenceKey(), "headphoneFenceKey")) {
            switch (fenceState.getCurrentState()) {
                case FenceState.TRUE:
                    Log.i(TAG, "Fence > Headphones are plugged in.");
                    Toast.makeText(context,"Correct",Toast.LENGTH_LONG).show();
                    break;
                case FenceState.FALSE:
                    Log.i(TAG, "Fence > Headphones are NOT plugged in.");
                    Toast.makeText(context,"wrong",Toast.LENGTH_LONG).show();
                    break;
                case FenceState.UNKNOWN:
                    Log.i(TAG, "Fence > The headphone fence is in an unknown state.");
                    Toast.makeText(context,"not registered",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

}*/
