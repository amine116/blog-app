package com.amine.blog.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.amine.blog.interfaces.OnWaitListener;
import com.amine.blog.model.ChatMessage;
import com.amine.blog.repositories.CurrentNode;
import com.amine.blog.repositories.Retrieve;
import com.amine.blog.repositories.Save;
import com.amine.blog.viewmodel.DataModel;

public class ChatBoxFrag extends Fragment implements View.OnClickListener, OnReadSingleMessage {

    private EditText edtMessage;
    private ImageButton btnSendMessage;

    private LinearLayout layoutMessages;
    private ProgressBar pBar;
    private ScrollView sView;
    private TextView txtChatHead;

    private Context context;

    private String receiverUsername, receiverProfileName, myUsername;

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

        edtMessage = view.findViewById(R.id.edtMessage);
        btnSendMessage = view.findViewById(R.id.btnSendMessage);
        btnSendMessage.setOnClickListener(this);
        layoutMessages = view.findViewById(R.id.layout_messages);
        pBar = view.findViewById(R.id.progress_chatBox);
        sView = view.findViewById(R.id.scroll_messages);

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
                        new DataModel().getCurrentMyTime());
                new Save().saveMyMessage(receiverUsername, myUsername, chatMessage);
            }
            edtMessage.setText("");
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
        Retrieve retrieve = new Retrieve("false");
        retrieve.setOnSingleMessageListener(this);
        retrieve.readSingleMessage(myUsername, receiverUsername);


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

    @Override
    public void onReadMessage(ChatMessage chatMessage) {
        makeChatViewVisible();
        if(chatMessage != null){
            layoutMessages.addView(getChatMessageView(chatMessage));
            sView.post(() -> sView.fullScroll(ScrollView.FOCUS_DOWN));
        }
    }
}
