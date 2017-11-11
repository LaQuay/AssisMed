package dev.mese.starthack.a2017.assismed;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.ciscospark.androidsdk.CompletionHandler;
import com.ciscospark.androidsdk.Result;
import com.ciscospark.androidsdk.Spark;
import com.ciscospark.androidsdk.auth.OAuthWebViewAuthenticator;

import dev.mese.starthack.a2017.assismed.controllers.SparkController;

public class HomeFragment extends Fragment {
    public static final String TAG = HomeFragment.class.getSimpleName();
    private View rootview;
    private LinearLayout container;
    private WebView webView;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_home, container, false);

        setUpElements();
        setUpListeners();

        hideWebView(true);
        connectSparkOAuth();

        return rootview;
    }

    private void hideWebView(boolean hide) {
        if (hide) {
            webView.setVisibility(View.GONE);
        } else {
            webView.setVisibility(View.VISIBLE);
        }
    }

    private void connectSparkOAuth() {
        OAuthWebViewAuthenticator authenticator = SparkController.getInstance().getOAuthAuthenticator();
        Spark spark = SparkController.getInstance().getSpark(getActivity().getApplication(), authenticator);
        if (!authenticator.isAuthorized()) {
            hideWebView(false);
            authenticator.authorize(webView, new CompletionHandler<Void>() {
                @Override
                public void onComplete(Result<Void> result) {
                    if (!result.isSuccessful()) {
                        Log.e(TAG, "User not authorized. " + result.toString());
                    } else {
                        Log.e(TAG, "User authorized." + result.toString());
                        hideWebView(true);
                    }
                }
            });
        }
    }

    private void setUpElements() {
        webView = (WebView) rootview.findViewById(R.id.webview);
        container = (LinearLayout) rootview.findViewById(R.id.fragment_home_container);
    }

    private void setUpListeners() {

    }
}
