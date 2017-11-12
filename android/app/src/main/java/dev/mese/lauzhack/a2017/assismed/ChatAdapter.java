package dev.mese.lauzhack.a2017.assismed;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import dev.mese.lauzhack.a2017.assismed.models.ChatMessage;

public class ChatAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    ArrayList<ChatMessage> chatMessageList;
    private Activity activity;

    public ChatAdapter(Activity activity, ArrayList<ChatMessage> list) {
        this.activity = activity;
        chatMessageList = list;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage message = chatMessageList.get(position);
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.chat_bubble, null);

        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();

        TextView msg = (TextView) vi.findViewById(R.id.message_text);
        msg.setText(message.body);
        msg.setTextSize(18);

        msg.setPadding((int) (metrics.density * 20 + 0.5f), (int) (metrics.density * 7 + 0.5f),
                (int)(metrics.density*20+0.5f), (int)(metrics.density*7+0.5f));
        
        LinearLayout layout = (LinearLayout) vi
                .findViewById(R.id.bubble_layout);
        LinearLayout parent_layout = (LinearLayout) vi
                .findViewById(R.id.bubble_layout_parent);


        // if message is mine then align to right
        if (message.isMine) {
            layout.setBackgroundResource(R.drawable.shape_bg_outgoing_bubble);
            parent_layout.setGravity(Gravity.RIGHT);
        }
        // If not mine then align to left
        else {
            layout.setBackgroundResource(R.drawable.shape_bg_incoming_bubble);
            parent_layout.setGravity(Gravity.LEFT);
        }

        msg.setTextColor(Color.BLACK);
        return vi;
    }

    public void add(ChatMessage object) {
        chatMessageList.add(object);
    }
}
