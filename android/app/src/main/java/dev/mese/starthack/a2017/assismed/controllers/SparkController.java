package dev.mese.starthack.a2017.assismed.controllers;

import android.app.Application;
import android.util.Log;

import com.ciscospark.androidsdk.CompletionHandler;
import com.ciscospark.androidsdk.Result;
import com.ciscospark.androidsdk.Spark;
import com.ciscospark.androidsdk.SparkError;
import com.ciscospark.androidsdk.auth.Authenticator;
import com.ciscospark.androidsdk.auth.OAuthWebViewAuthenticator;
import com.ciscospark.androidsdk.message.Message;
import com.ciscospark.androidsdk.phone.Call;
import com.ciscospark.androidsdk.phone.CallObserver;
import com.ciscospark.androidsdk.phone.MediaOption;
import com.ciscospark.androidsdk.phone.Phone;
import com.webex.wseclient.WseSurfaceView;

import java.util.List;

public class SparkController {
    public static final String TAG = SparkController.class.getSimpleName();
    public final static String ROOM_HEALTH_ASSIST_ROBOT = "f1836441-08bb-3cda-9daa-2e33cb061b89";
    public static final String[] callArray = {"ester.lorente@est.fib.upc.edu", "dx80_lauzhack@ch.room.ciscospark.com"};
    public static final int callIndex = 0;
    public static final String[] postArray = {"ester.lorente@est.fib.upc.edu"};
    private static final int postIndex = 0;
    private final static String clientId = "C480812ef8d0564ccf4d1c681978471e78b3c97ac127419ec3abcd38808969fba";
    private final static String clientSecret = "bbbc79b98186e5be3ccb67f7b5b06a830a90eb48bb29df6684d933e6523e6047";
    private final static String scope = "spark:all";
    private final static String redirectUri = "assismedapp://responsecallback";
    private static SparkController instance;

    private Spark sparkInstance;
    private OAuthWebViewAuthenticator oAuthWebViewAuthenticator;
    private Call activeCall;

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

    // Authenticates the Android app in the SparkApp, through OAuth2
    public OAuthWebViewAuthenticator getOAuthAuthenticator() {
        //FIX Bug in SDK, redirectUri and scope are switched
        if (oAuthWebViewAuthenticator == null) {
            oAuthWebViewAuthenticator = new OAuthWebViewAuthenticator(clientId, clientSecret, redirectUri, scope);
        }
        return oAuthWebViewAuthenticator;
    }

    // Get the Spark element
    public Spark getSpark(Application application, Authenticator authenticator) {
        if (sparkInstance == null) {
            sparkInstance = new Spark(application, authenticator);
        }
        return sparkInstance;
    }

    // Registers the device to Spark
    public void registerPhone(final PhoneRegisteredCallback phoneRegisteredCallback) {
        if (sparkInstance != null) {
            sparkInstance.phone().register(new CompletionHandler<Void>() {
                @Override
                public void onComplete(Result<Void> result) {
                    if (result.isSuccessful()) {
                        // Device registered
                        phoneRegisteredCallback.onPhoneRegistered(true);
                    } else {
                        // Device not registered, and calls will not be sent or received
                        phoneRegisteredCallback.onPhoneRegistered(false);
                    }
                }
            });
        }
    }

    // VideoCall receiverEmail person with audio and video both ways enabled
    // NOTE: Works when the receiver accepts the call
    public void call(String receiverEmail, final WseSurfaceView localView, final WseSurfaceView remoteView, final PhoneDoingCallCallback phoneReceivingCallCallback) {
        Log.e(TAG, "Calling: " + receiverEmail);
        if (sparkInstance != null) {
            // Render the user (local) camera before the activeCall starts
            sparkInstance.phone().startPreview(localView);
            sparkInstance.phone().dial(receiverEmail, MediaOption.audioVideo(localView, remoteView), new CompletionHandler<Call>() {
                @Override
                public void onComplete(Result<Call> result) {
                    activeCall = result.getData();
                    if (activeCall != null) {
                        activeCall.setObserver(new CallObserver() {
                            @Override
                            public void onRinging(Call call) {
                                Log.e(TAG, "onRinging");
                            }

                            @Override
                            public void onConnected(Call call) {
                                Log.e(TAG, "onConnected");
                                phoneReceivingCallCallback.onDoingCall(true, null);
                            }

                            @Override
                            public void onDisconnected(CallDisconnectedEvent callDisconnectedEvent) {
                                Log.e(TAG, "onDisconnected");
                            }

                            @Override
                            public void onMediaChanged(MediaChangedEvent mediaChangedEvent) {
                                Log.e(TAG, "onMediaChanged" + mediaChangedEvent.toString());
                            }
                        });
                    } else {
                        SparkError error = result.getError();
                        if (error != null) {
                            Log.e(TAG, "onError: " + error.toString());
                        }
                    }
                }
            });
        }
    }

    public void hangupCall(final HangUpCallCallback onHangupCallCallback) {
        if (activeCall != null) {
            activeCall.hangup(new CompletionHandler<Void>() {
                @Override
                public void onComplete(Result<Void> result) {
                    onHangupCallCallback.onHangUpCall();
                }
            });
        }
    }

    // Set a listener for incoming calls to this device
    public void setListenerReceiveCall(final WseSurfaceView localView, final WseSurfaceView remoteView, final PhoneDoingCallCallback phoneReceivingCallCallback) {
        if (sparkInstance != null) {
            sparkInstance.phone().setIncomingCallListener(new Phone.IncomingCallListener() {
                @Override
                public void onIncomingCall(Call call) {
                    call.answer(MediaOption.audioVideo(localView, remoteView), new CompletionHandler<Void>() {
                        @Override
                        public void onComplete(Result<Void> result) {
                            if (result.isSuccessful()) {
                                // success
                                SparkError error = result.getError();
                                phoneReceivingCallCallback.onDoingCall(true, error);
                            } else {
                                SparkError error = result.getError();
                                phoneReceivingCallCallback.onDoingCall(false, error);
                            }
                        }
                    });
                }
            });
        }
    }

    public void postMessage(String toPersonEmail, String messageToSend) {
        if (sparkInstance != null) {
            sparkInstance.messages().post(null, null, toPersonEmail, messageToSend, null, null, new CompletionHandler<Message>() {
                @Override
                public void onComplete(Result<Message> result) {
                    if (result.isSuccessful()) {
                        Message message = result.getData();
                        Log.e(TAG, "onResultSuccessful: " + message);
                    } else {
                        SparkError error = result.getError();
                        Log.e(TAG, "onResultError: " + error);
                    }
                }
            });
        }
    }

    public void listMessages(String toRoomID, final ListMessagesResultCallback callback) {
        if (sparkInstance != null) {
            sparkInstance.messages().list(toRoomID, null, null, null, 50, new CompletionHandler<List<Message>>() {
                @Override
                public void onComplete(Result<List<Message>> results) {
                    if (results.isSuccessful()) {
                        List<Message> messagesList = results.getData();
                        if (messagesList != null) {
                            callback.onListMessagesResultReceived(messagesList);
                        }
                    } else {
                        SparkError error = results.getError();
                        Log.e(TAG, "onResultError: " + error);
                    }
                }
            });
        }
    }

    public void newMessageReceived() {

    }

    public interface PhoneRegisteredCallback {
        void onPhoneRegistered(boolean isSuccessful);
    }

    public interface PhoneDoingCallCallback {
        void onDoingCall(boolean isSuccessful, SparkError error);
    }

    public interface HangUpCallCallback {
        void onHangUpCall();
    }

    public interface ListMessagesResultCallback {
        void onListMessagesResultReceived(List<Message> messages);
    }
}
