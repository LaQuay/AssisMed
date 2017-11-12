package dev.mese.lauzhack.a2017.assismed;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ciscospark.androidsdk.CompletionHandler;
import com.ciscospark.androidsdk.Result;
import com.ciscospark.androidsdk.SparkError;
import com.ciscospark.androidsdk.auth.OAuthWebViewAuthenticator;
import com.webex.wseclient.WseSurfaceView;

import dev.mese.lauzhack.a2017.assismed.controllers.SparkController;

import static dev.mese.lauzhack.a2017.assismed.controllers.SparkController.callArray;
import static dev.mese.lauzhack.a2017.assismed.controllers.SparkController.callIndex;

public class HomeFragment extends Fragment implements SparkController.PhoneRegisteredCallback,
        SparkController.PhoneDoingCallCallback, SparkController.HangUpCallCallback {
    public static final String TAG = HomeFragment.class.getSimpleName();

    private View rootview;
    private WebView webView;

    private LinearLayout optionsCallLayout;
    private LinearLayout insideCallLayout;
    private TextView callDoctorTextView;
    private TextView hangTextView;
    private WseSurfaceView localView;
    private WseSurfaceView remoteView;

    private SparkController.PhoneRegisteredCallback phoneRegisteredCallback;
    private SparkController.PhoneDoingCallCallback phoneDoingCallCallback;
    private SparkController.HangUpCallCallback onHangupCallCallback;

    private FlowState currentFlowState;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_home, container, false);

        setUpElements();
        setUpListeners();

        phoneRegisteredCallback = this;

        currentFlowState = FlowState.LOGIN;
        changeFlowState(currentFlowState);

        connectSparkOAuth();

        return rootview;
    }

    private void changeFlowState(FlowState state) {
        switch (state) {
            case LOGIN:
                webView.setVisibility(View.VISIBLE);
                optionsCallLayout.setVisibility(View.GONE);
                insideCallLayout.setVisibility(View.GONE);
                break;

            case WAITING:
                webView.setVisibility(View.GONE);
                optionsCallLayout.setVisibility(View.VISIBLE);
                insideCallLayout.setVisibility(View.GONE);
                break;

            case CALLING:
                webView.setVisibility(View.GONE);
                optionsCallLayout.setVisibility(View.GONE);
                insideCallLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void connectSparkOAuth() {
        final OAuthWebViewAuthenticator authenticator = SparkController.getInstance().getOAuthAuthenticator();
        if (!authenticator.isAuthorized()) {
            authenticator.authorize(webView, new CompletionHandler<Void>() {
                @Override
                public void onComplete(Result<Void> result) {
                    if (!result.isSuccessful()) {
                        Log.e(TAG, "User not authorized: " + result.toString());
                    } else {
                        // Now we are authorized
                        Log.e(TAG, "User authorized: " + result.toString());
                        changeFlowState(FlowState.WAITING);

                        SparkController.getInstance().getSpark(getActivity().getApplication(), authenticator);

                        // Register the phone to send and receive calls
                        SparkController.getInstance().registerPhone(phoneRegisteredCallback);
                    }
                }
            });
        } else {
            changeFlowState(FlowState.WAITING);
        }
    }

    private void setUpElements() {
        webView = (WebView) rootview.findViewById(R.id.webview);

        optionsCallLayout = (LinearLayout) rootview.findViewById(R.id.activity_call_options_view);
        insideCallLayout = (LinearLayout) rootview.findViewById(R.id.activity_call_call_view);
        localView = (WseSurfaceView) rootview.findViewById(R.id.local_view);
        remoteView = (WseSurfaceView) rootview.findViewById(R.id.remote_view);
        hangTextView = (TextView) rootview.findViewById(R.id.button_hangup_activity);
        callDoctorTextView = (TextView) rootview.findViewById(R.id.activity_call_call_doctor_text);
    }

    private void setUpListeners() {
        callDoctorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Ringing...", Toast.LENGTH_SHORT).show();

                SparkController.getInstance().call(callArray[callIndex], localView, remoteView, phoneDoingCallCallback);
            }
        });

        hangTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparkController.getInstance().hangupCall(onHangupCallCallback);
            }
        });
    }

    @Override
    public void onPhoneRegistered(final boolean isSuccessful) {
        Log.e(TAG, "Registration result: " + isSuccessful);

        // Set up Listener to receive a call
        SparkController.getInstance().setListenerReceiveCall(localView, remoteView, phoneDoingCallCallback);

        //SparkController.getInstance().requestVideoCodecActivation(getContext(), videoCodeActivationCallback);
        //SparkController.getInstance().postMessage(postArray[postIndex], "OLA K ASE");
        //SparkController.getInstance().postMessage(postArray[postIndex], "medicalevent");
        //SparkController.getInstance().postMessage(postArray[postIndex], "new radiografia for ester.loga@gmail.com on 13/11/2017 at 1:32");
        //SparkController.getInstance().listMessages(SparkController.ROOM_HEALTH_ASSIST_ROBOT);
    }

    @Override
    public void onDoingCall(boolean isSuccessful, SparkError error) {
        Log.e(TAG, "Inside call: " + isSuccessful);

        changeFlowState(FlowState.CALLING);
        if (error != null) {
            Log.e(TAG, "Inside call error: " + error.toString());
        }
    }

    @Override
    public void onHangUpCall() {
        Log.e(TAG, "Hangup Call");

        changeFlowState(FlowState.WAITING);
    }

    private enum FlowState {
        LOGIN,
        WAITING,
        CALLING
    }
}
