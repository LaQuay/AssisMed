package dev.mese.starthack.a2017.assismed;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ciscospark.androidsdk.SparkError;
import com.webex.wseclient.WseSurfaceView;

import dev.mese.starthack.a2017.assismed.controllers.SparkController;

import static dev.mese.starthack.a2017.assismed.controllers.SparkController.callArray;
import static dev.mese.starthack.a2017.assismed.controllers.SparkController.callIndex;

public class CallActivity extends AppCompatActivity
        implements SparkController.VideoCodeActivationCallback, SparkController.PhoneDoingCallCallback, SparkController.HangUpCallCallback {
    public static final String TAG = CallActivity.class.getSimpleName();

    private TextView hangButton;
    private WseSurfaceView localView;
    private WseSurfaceView remoteView;
    private SparkController.VideoCodeActivationCallback videoCodeActivationCallback;
    private SparkController.PhoneDoingCallCallback phoneDoingCallCallback;
    private SparkController.HangUpCallCallback onHangupCallCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        setElements();
        setListeners();

        videoCodeActivationCallback = this;
        phoneDoingCallCallback = this;
        onHangupCallCallback = this;

        SparkController.getInstance().requestVideoCodecActivation(this, videoCodeActivationCallback);
    }

    public void setElements() {
        localView = (WseSurfaceView) findViewById(R.id.local_view);
        remoteView = (WseSurfaceView) findViewById(R.id.remote_view);
        hangButton = (TextView) findViewById(R.id.button_hangup_activity);
    }

    public void setListeners() {
        hangButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SparkController.getInstance().hangupCall(onHangupCallCallback);
            }
        });
    }

    @Override
    public void onVideoCodecResponse(boolean isSuccessful) {
        Log.e(TAG, "onVideoCodecResponse call: " + isSuccessful);

        if (isSuccessful) {
            // Set up Listener to receive a call
            //SparkController.getInstance().setListenerReceiveCall(spark, localView, remoteView, phoneDoingCallCallback);

            // Call someone
            SparkController.getInstance().call(callArray[callIndex], localView, remoteView, phoneDoingCallCallback);
        }
    }

    @Override
    public void onDoingCall(boolean isSuccessful, SparkError error) {
        Log.e(TAG, "Inside call: " + isSuccessful);

        if (error != null) {
            Log.e(TAG, "Inside call error: " + error.toString());
        }
    }

    @Override
    public void onHangUpCall() {
        Log.e(TAG, "Hangup Call");

        onBackPressed();
    }
}
