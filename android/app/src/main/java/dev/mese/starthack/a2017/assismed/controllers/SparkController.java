package dev.mese.starthack.a2017.assismed.controllers;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.VideoView;

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
    private final static String clientId = "C480812ef8d0564ccf4d1c681978471e78b3c97ac127419ec3abcd38808969fba";
    private final static String clientSecret = "bbbc79b98186e5be3ccb67f7b5b06a830a90eb48bb29df6684d933e6523e6047";
    private final static String scope = "spark:all";
    private final static String redirectUri = "assismedapp://responsecallback";
    private static SparkController instance;
    private Spark sparkInstance;

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
        return new OAuthWebViewAuthenticator(clientId, clientSecret, redirectUri, scope);
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
    // NOTE: Works on both ways in audio, and one-way video. As the app is currently beta it seems a 'not developed' function
    public void call(String receiverEmail, final WseSurfaceView localView, final WseSurfaceView remoteView, final PhoneDoingCallCallback phoneReceivingCallCallback) {
        Log.e(TAG, "Calling: " + receiverEmail);
        if (sparkInstance != null) {
            // Render the user (local) camera before the call starts
            sparkInstance.phone().startPreview(localView);
            sparkInstance.phone().dial(receiverEmail, MediaOption.audioVideo(localView, remoteView), new CompletionHandler<Call>() {
                @Override
                public void onComplete(Result<Call> result) {
                    Call call = result.getData();
                    if (call != null) {
                        call.setObserver(new CallObserver() {
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

    // Set a listener for incoming calls to this device
    // NOTE: The listener works, because when the device receives a call, it shows the message. And
    // just after that, the 'cisco-compiled' code stops working (Ticket sent to Spark4Dev)
    public void setListenerReceiveCall(final VideoView localView, final VideoView remoteView, final PhoneDoingCallCallback phoneReceivingCallCallback) {
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

    // Pop up an alert for the user to aprove the use of the H264 video codec
    public void requestVideoCodecActivation(Context context, final VideoCodeActivationCallback videoCodeActivationCallback) {
        if (sparkInstance != null) {
            sparkInstance.phone().requestVideoCodecActivation(new AlertDialog.Builder(context), new CompletionHandler<Boolean>() {
                @Override
                public void onComplete(Result<Boolean> result) {
                    // result == true does not mean user acceptance
                    Log.e(TAG, "requestVideoCodecActivation: " + result.getData());

                    videoCodeActivationCallback.onVideoCodecResponse(result.getData());
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

    public void listMessages(String toRoomID) {
        if (sparkInstance != null) {
            sparkInstance.messages().list(toRoomID, null, null, null, 50, new CompletionHandler<List<Message>>() {
                @Override
                public void onComplete(Result<List<Message>> results) {
                    if (results.isSuccessful()) {
                        List<Message> messagesList = results.getData();
                        if (messagesList != null) {
                            for (int i = 0; i < messagesList.size(); ++i) {
                                Log.e(TAG, "onResultSuccessful(" + i + "): " + messagesList.get(i));
                            }
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

    public interface VideoCodeActivationCallback {
        void onVideoCodecResponse(boolean isSuccessful);
    }
}
