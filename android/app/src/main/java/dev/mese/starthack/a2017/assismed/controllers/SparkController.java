package dev.mese.starthack.a2017.assismed.controllers;


import android.app.Application;

import com.ciscospark.androidsdk.Spark;
import com.ciscospark.androidsdk.auth.Authenticator;
import com.ciscospark.androidsdk.auth.OAuthWebViewAuthenticator;

public class SparkController {
    private static SparkController instance;
    private final static String clientId = "C480812ef8d0564ccf4d1c681978471e78b3c97ac127419ec3abcd38808969fba";
    private final static String clientSecret = "bbbc79b98186e5be3ccb67f7b5b06a830a90eb48bb29df6684d933e6523e6047";
    private final static String scope = "spark:all";
    private final static String redirectUri = "assismedapp://responsecallback";

    private SparkController() {
    }

    public static SparkController getInstance() {
        if (instance == null) {
            createInstance();
        }
        return instance;
    }

    private synchronized static void createInstance() {
        if (instance == null) {
            instance = new SparkController();
        }
    }

    public OAuthWebViewAuthenticator getOAuthAuthenticator() {
        //FIX Bug in SDK, redirectUri and scope are switched
        return new OAuthWebViewAuthenticator(clientId, clientSecret, redirectUri, scope);
    }

    public Spark getSpark(Application application, Authenticator authenticator) {
        return new Spark(application, authenticator);
    }

}
