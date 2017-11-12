package dev.mese.starthack.a2017.assismed;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.ciscospark.androidsdk.CompletionHandler;
import com.ciscospark.androidsdk.Result;
import com.ciscospark.androidsdk.auth.OAuthWebViewAuthenticator;

import dev.mese.starthack.a2017.assismed.controllers.SparkController;

public class HomeFragment extends Fragment implements SparkController.PhoneRegisteredCallback {
    public static final String TAG = HomeFragment.class.getSimpleName();

    private View rootview;
    private WebView webView;
    private SparkController.PhoneRegisteredCallback phoneRegisteredCallback;
    private SparkController.PhoneDoingCallCallback phoneDoingCallCallback;
    private SparkController.VideoCodeActivationCallback videoCodeActivationCallback;

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

        switchAuthMode(true);
        connectSparkOAuth();

        return rootview;
    }

    private void switchAuthMode(boolean hideAuthMode) {
        if (hideAuthMode) {
            webView.setVisibility(View.GONE);
        } else {
            webView.setVisibility(View.VISIBLE);
        }
    }

    private void connectSparkOAuth() {
        final OAuthWebViewAuthenticator authenticator = SparkController.getInstance().getOAuthAuthenticator();
        if (!authenticator.isAuthorized()) {
            switchAuthMode(false);
            authenticator.authorize(webView, new CompletionHandler<Void>() {
                @Override
                public void onComplete(Result<Void> result) {
                    if (!result.isSuccessful()) {
                        Log.e(TAG, "User not authorized: " + result.toString());
                    } else {
                        // Now we are authorized
                        Log.e(TAG, "User authorized: " + result.toString());
                        switchAuthMode(true);

                        SparkController.getInstance().getSpark(getActivity().getApplication(), authenticator);

                        // Register the phone to send and receive calls
                        SparkController.getInstance().registerPhone(phoneRegisteredCallback);
                    }
                }
            });
        }
    }

    private void setUpElements() {
        webView = (WebView) rootview.findViewById(R.id.webview);
    }

    private void setUpListeners() {

    }

    @Override
    public void onPhoneRegistered(final boolean isSuccessful) {
        Log.e(TAG, "Registration result: " + isSuccessful);

        Intent intent = new Intent(getContext(), CallActivity.class);
        startActivity(intent);

        //SparkController.getInstance().requestVideoCodecActivation(getContext(), videoCodeActivationCallback);
        //SparkController.getInstance().postMessage(postArray[postIndex], "OLA K ASE");
        //SparkController.getInstance().postMessage(postArray[postIndex], "medicalevent");
        //SparkController.getInstance().postMessage(postArray[postIndex], "new radiografia for ester.loga@gmail.com on 13/11/2017 at 1:32");
        //SparkController.getInstance().listMessages(SparkController.ROOM_HEALTH_ASSIST_ROBOT);
    }
}
