package com.amine.blog.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amine.blog.MainActivity;
import com.amine.blog.R;
import com.amine.blog.interfaces.OnReadSingleMessage;
import com.amine.blog.model.ChatMessage;
import com.amine.blog.repositories.Retrieve;
import com.amine.blog.repositories.Save;
import com.amine.blog.viewmodel.DataModel;

import java.util.ArrayList;

public class ChatBoxFrag extends Fragment implements View.OnClickListener, OnReadSingleMessage {

    private EditText edtMessage;
    private ImageButton btnSendMessage;

    private LinearLayout layoutMessages, layout_showMoreMessage;
    private ProgressBar pBar, prShowMoreMessage;
    private ScrollView sView;
    private TextView txtChatHead, txtShowMoreMessage;

    private Context context;

    private String receiverUsername, receiverProfileName, myUsername;
    private long lastMessageTimeInMill = 1;

    public ChatBoxFrag(){}

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public void setReceiverName(String receiverProfileName) {
        this.receiverProfileName = receiverProfileName;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setTxtChatHead(TextView txtChatHead) {
        this.txtChatHead = txtChatHead;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat_box_frag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        txtShowMoreMessage = view.findViewById(R.id.txtShowMoreMessage);
        edtMessage = view.findViewById(R.id.edtMessage);
        btnSendMessage = view.findViewById(R.id.btnSendMessage);

        layoutMessages = view.findViewById(R.id.layout_messages);
        layout_showMoreMessage = view.findViewById(R.id.layout_showMoreMessage);
        pBar = view.findViewById(R.id.progress_chatBox);
        prShowMoreMessage = view.findViewById(R.id.prShowMoreMessage);
        sView = view.findViewById(R.id.scroll_messages);

        txtShowMoreMessage.setOnClickListener(this);
        btnSendMessage.setOnClickListener(this);

        myUsername = MainActivity.userBasicInfo.getUserName();

        //readAllMessagesOnce();
        makeChatViewInvisible();
        setListenerForSingleMessageReceived();

        Save.removeNewMessages(myUsername, receiverUsername);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btnSendMessage.getId()){
            String message = edtMessage.getText().toString();
            if(!message.isEmpty()){
                String messageId = new Save().getMessageId(receiverUsername, myUsername);

                ChatMessage chatMessage = new ChatMessage(message, messageId, myUsername, receiverUsername,
                        new DataModel().getCurrentMyTime(), -DataModel.calenderTimeInMill());
                new Save().saveMyMessage(receiverUsername, myUsername, chatMessage);
            }
            edtMessage.setText("");
        }
        else if(view.getId() == txtShowMoreMessage.getId()){
            txtShowMoreMessage.setVisibility(View.GONE);
            prShowMoreMessage.setVisibility(View.VISIBLE);
            readMoreMessage();
        }
    }

    private void readMoreMessage(){
        if(lastMessageTimeInMill != 1){
            Retrieve.readMessages(myUsername, receiverUsername, lastMessageTimeInMill, false,
                    (chatMessages, task) -> {
                if(chatMessages.size() > 0){
                    lastMessageTimeInMill = chatMessages.get(chatMessages.size() - 1).getTimeInMill();
                }
                setMessagesToChatBox(chatMessages, true);

                prShowMoreMessage.setVisibility(View.GONE);
                txtShowMoreMessage.setVisibility(View.VISIBLE);
            });
        }
        else{
            prShowMoreMessage.setVisibility(View.GONE);
            txtShowMoreMessage.setVisibility(View.VISIBLE);
        }
    }

    /*
    private void readAllMessagesOnce(){
        makeChatViewInvisible();
        ArrayList<ChatMessage> chatMessages = new ArrayList<>();
        Retrieve retrieve = new Retrieve("false");
        retrieve.setOnReadListener(snapshot -> {
            if(snapshot != null){
                for(DataSnapshot item : snapshot.getChildren()){
                    assert item.getKey() != null;
                    ChatMessage chatMessage = snapshot.child(item.getKey()).getValue(ChatMessage.class);
                    chatMessages.add(chatMessage);
                }
            }

            makeChatViewVisible();
            setAllMessagesInChatBox(chatMessages);
        });
        retrieve.readAllMessagesOnce(myUsername, receiverUsername);

    }
     */

    private void makeChatViewInvisible(){
        sView.setVisibility(View.GONE);
        pBar.setVisibility(View.VISIBLE);
    }
    private void makeChatViewVisible(){
        pBar.setVisibility(View.GONE);
        sView.setVisibility(View.VISIBLE);
    }

    /*
    private void setAllMessagesInChatBox(ArrayList<ChatMessage> chatMessages){
        layoutMessages.removeAllViews();

        for(int i = 0; i < chatMessages.size(); i++){
            View view = getChatMessageView(chatMessages.get(i));
            layoutMessages.addView(view);
        }
        sView.post(() -> sView.fullScroll(ScrollView.FOCUS_DOWN));
    }
     */

    private View getChatMessageView(ChatMessage chatMessage){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.model_chat_message, null);

        TextView txtMessageDate = view.findViewById(R.id.txtMessageDate),
                txtSenderName = view.findViewById(R.id.txtSenderName),
                txtMessageText = view.findViewById(R.id.txtMessageText);

        txtMessageText.setTag("text");
        txtMessageText.setOnClickListener(getClickListenerForMessageText(txtMessageDate));

        txtMessageDate.setText(chatMessage.getTime().toString());
        txtSenderName.setVisibility(View.GONE);
        txtMessageText.setText(chatMessage.getText());

        if(chatMessage.getSenderUsername().equals(myUsername)){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) txtMessageText.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            txtMessageText.setBackgroundResource(R.drawable.rectangular_shape_my_message_color);

        }
        else{
            txtMessageText.setGravity(Gravity.START);
        }

        return view;
    }

    private View.OnClickListener getClickListenerForMessageText(TextView txtDate){
        return view -> {
            String tag = view.getTag().toString();
            if(tag.equals("text")){
                if(txtDate.getVisibility() == View.VISIBLE){
                    txtDate.setVisibility(View.GONE);
                }
                else if(txtDate.getVisibility() == View.GONE){
                    txtDate.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    private void setListenerForSingleMessageReceived(){

        Retrieve.readInstantMessageSent(myUsername, receiverUsername, this);

//        Retrieve.readLastMessageTimeInMill(myUsername, receiverUsername, value -> {
//            if(value != 1){
//                Retrieve.readMessages(myUsername, receiverUsername, value, true, (chatMessages, task) -> {
//                    if(chatMessages.size() > 0){
//                        lastMessageTimeInMill = chatMessages.get(chatMessages.size() - 1).getTimeInMill();
//                    }
//                    makeChatViewVisible();
//                    setMessagesToChatBox(chatMessages, false);
//                });
//            }
//            else{
//                makeChatViewVisible();
//            }
//        });

        Retrieve.readMessages(myUsername, receiverUsername, 0, true, (chatMessages, task) -> {
            if(chatMessages.size() > 0){
                lastMessageTimeInMill = chatMessages.get(chatMessages.size() - 1).getTimeInMill();
            }
            if(chatMessages.size() < DataModel.MAXIMUM_DATA_QUERY_FIREBASE){
                txtShowMoreMessage.setVisibility(View.GONE);
            }
            else{
                txtShowMoreMessage.setVisibility(View.VISIBLE);
            }
            makeChatViewVisible();
            setMessagesToChatBox(chatMessages, false);
        });

        Retrieve.getActivityStatus(receiverUsername, task -> {
            if(task == DataModel.YES){
                txtChatHead.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.round_shape_chat_blue, 0);
            }
            else{
                txtChatHead.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, 0, 0);
            }
        });
    }

    private void setMessagesToChatBox(ArrayList<ChatMessage> chatMessages, boolean isReadingMore){
        for(int i = 0; i < chatMessages.size(); i++){
            layoutMessages.addView(getChatMessageView(chatMessages.get(i)), 0);
        }
        if (!isReadingMore) {
            sView.post(() -> sView.fullScroll(ScrollView.FOCUS_DOWN));
        }
    }

    @Override
    public void onReadMessage(ChatMessage chatMessage) {
        if(chatMessage != null){
            layoutMessages.addView(getChatMessageView(chatMessage));
            sView.post(() -> sView.fullScroll(ScrollView.FOCUS_DOWN));
        }
    }
}
