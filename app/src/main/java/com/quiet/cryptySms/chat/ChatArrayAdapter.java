package com.quiet.cryptySms.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quiet.cryptySms.R;

import org.apache.commons.logging.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatArrayAdapter extends ArrayAdapter {
    private TextView chatText;
    private TextView dateText;
    private List chatMessageList = new ArrayList();
    private LinearLayout singleMessageContainer;


    public void add(ChatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return (ChatMessage) this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.chat_message, parent, false);
        }
        singleMessageContainer = (LinearLayout) row.findViewById(R.id.singleMessageContainer);
        ChatMessage chatMessageObj = getItem(position);
        chatText = (TextView) row.findViewById(R.id.singleMessage);
        dateText= (TextView) row.findViewById(R.id.time);
        dateText.setText( String.valueOf(new SimpleDateFormat("MM-dd HH:mm").format(new Date(Long.valueOf(chatMessageObj.time)*1000L))));
        chatText.setText(chatMessageObj.message);
        singleMessageContainer.setBackgroundResource(chatMessageObj.left ? R.drawable.left : R.drawable.right);
        ViewGroup.LayoutParams layoutParams = singleMessageContainer.getLayoutParams();
        LinearLayout.LayoutParams castLayoutParams = (LinearLayout.LayoutParams) layoutParams;
        castLayoutParams.gravity = chatMessageObj.left ? Gravity.LEFT : Gravity.RIGHT;
        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
