package dev.mese.lauzhack.a2017.assismed;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.ciscospark.androidsdk.message.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dev.mese.lauzhack.a2017.assismed.controllers.SparkController;
import dev.mese.lauzhack.a2017.assismed.models.ChatMessage;
import dev.mese.lauzhack.a2017.assismed.utils.Utility;

public class ChatFragment extends Fragment implements SparkController.ListMessagesResultCallback {

    public static final String TAG = ChatFragment.class.getSimpleName();
    public static ArrayList<ChatMessage> chatlist;
    public static ChatAdapter chatAdapter;
    private View rootview;
    private ListView msgListView;
    private EditText msg_edittext;
    private String user1 = "khushi", user2 = "khushi1";
    private Random random;

    private SparkController.ListMessagesResultCallback listMessagesResultCallback;

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_chat, container, false);

        random = new Random();
        msg_edittext = (EditText) rootview.findViewById(R.id.messageEditText);
        msgListView = (ListView) rootview.findViewById(R.id.msgListView);
        ImageButton sendButton = (ImageButton) rootview.findViewById(R.id.sendMessageButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sendMessageButton:
                        sendTextMessage(v);
                }
            }
        });

        // ----Set autoscroll of listview when a new message arrives----//
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);

        chatlist = new ArrayList<>();
        chatAdapter = new ChatAdapter(getActivity(), chatlist);
        msgListView.setAdapter(chatAdapter);

        listMessagesResultCallback = this;
        getTextMessages(rootview);

        return rootview;
    }

    public void sendTextMessage(View v) {
        String message = msg_edittext.getEditableText().toString();
        if (!message.equalsIgnoreCase("")) {
            final ChatMessage chatMessage = new ChatMessage(user1, user2,
                    message, "" + random.nextInt(1000), true);
            chatMessage.setMsgID();
            chatMessage.body = message;
            chatMessage.Date = Utility.getCurrentDate();
            chatMessage.Time = Utility.getCurrentTime();
            msg_edittext.setText("");
            chatAdapter.add(chatMessage);
            chatAdapter.notifyDataSetChanged();
        }
    }

    public void getTextMessages(View v) {
        String roomID = "Y2lzY29zcGFyazovL3VzL1JPT00vMjJhNWFjZjUtNTM5OS0zNWQ4LWJmOTUtNTNmNDdjYTVjMDE1";
        SparkController.getInstance().listMessages(roomID, listMessagesResultCallback);
    }

    @Override
    public void onListMessagesResultReceived(List<Message> messages) {
        for (int i = 0; i < messages.size(); ++i) {
            String message = messages.get(i).getMarkdown();
            if (!message.equalsIgnoreCase("")) {
                boolean isMine = messages.get(i).getPersonEmail() == "ester.loga@gmail.com";
                final ChatMessage chatMessage = new ChatMessage(user1, user2,
                        message, "" + random.nextInt(1000), isMine);
                chatMessage.setMsgID();
                chatMessage.body = message;
                chatMessage.Date = CommonxMethods.getCurrentDate();
                chatMessage.Time = CommonMethods.getCurrentTime();
                msg_edittext.setText("");
                chatAdapter.add(chatMessage);
                chatAdapter.notifyDataSetChanged();
            }
        }
    }
}
