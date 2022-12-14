package com.amine.blog;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amine.blog.fragments.ChatBoxFrag;
import com.amine.blog.interfaces.OnReadChatList;
import com.amine.blog.model.ArticlesUnderTag;
import com.amine.blog.repositories.Retrieve;
import com.amine.blog.repositories.Save;
import com.amine.blog.viewmodel.DataModel;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity{

    // constant values are from 41-50

    public static final int CHAT_BOX = 41, CHAT_LIST = 42;

    private int intentType;
    private String receiverUsername, receiverProfileName, senderUsername, senderProfileName;
    private boolean inOtherActivity = false;
    //private Map<String, String> listedUsernames = new HashMap<>();

    private TextView txtChatHead;
    private ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        hideActionBar();
        getIntentInfo();

        if(intentType == CHAT_BOX){
            makeChatBoxView();
            ChatBoxFrag chatBoxFrag = new ChatBoxFrag();
            chatBoxFrag.setReceiverUsername(receiverUsername);
            chatBoxFrag.setReceiverName(receiverProfileName);
            chatBoxFrag.setTxtChatHead(txtChatHead);
            chatBoxFrag.setContext(this);
            addFragment(chatBoxFrag);
        }
        else if(intentType == CHAT_LIST){
            pBar = findViewById(R.id.progress_chatList);
            makeChatListView();
            setListenerForOldChatList();
            setListenerForNewChatList();
        }
    }

    private void hideActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }
    }

    private void getIntentInfo(){
        intentType = getIntent().getIntExtra("INTENT_TYPE", 0);
        receiverUsername = getIntent().getStringExtra("RECEIVE_USER_NAME");
        receiverProfileName = getIntent().getStringExtra("RECEIVER_NAME");
        senderUsername = MainActivity.userBasicInfo.getUserName();
        senderProfileName = MainActivity.userBasicInfo.getProfileName();
    }

    private void makeChatBoxView(){
        FrameLayout layout_for_frag = findViewById(R.id.layout_for_fragments);
        LinearLayout layout_chatList = findViewById(R.id.layout_chatList);
        txtChatHead = findViewById(R.id.txtChatHead);
        txtChatHead.setText(receiverProfileName);

        layout_chatList.setVisibility(View.GONE);
        layout_for_frag.setVisibility(View.VISIBLE);
    }

    private void makeChatListView(){
        pBar.setVisibility(View.VISIBLE);
        FrameLayout layout_for_frag = findViewById(R.id.layout_for_fragments);
        LinearLayout layout_chatList = findViewById(R.id.layout_chatList);
        TextView txtChatHead = findViewById(R.id.txtChatHead);

        txtChatHead.setText(R.string.chats);
        layout_for_frag.setVisibility(View.GONE);
        layout_chatList.setVisibility(View.VISIBLE);
    }

    private void addFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.layout_for_fragments, fragment).commit();
    }

    private void setListenerForNewChatList(){
        Retrieve.readNewChatList(senderUsername, new OnReadChatList() {
            @Override
            public void onReadChatList(ArrayList<ArticlesUnderTag> chatList, boolean isAdded) {

            }

            @Override
            public void onReadChatList(DataSnapshot snapshot, boolean isAdded) {
                if(pBar.getVisibility() == View.VISIBLE){
                    pBar.setVisibility(View.GONE);
                }
                if(snapshot != null){
                    if(snapshot.exists()){
                        String username = snapshot.getKey(),
                                lastMessage = snapshot.getValue(String.class);
                        if(username != null){
                            setChatList(new ArticlesUnderTag(username, lastMessage), true, isAdded);
                        }
                    }
                }
            }
        });
    }

    private void setListenerForOldChatList(){

        Retrieve.readOldChatList(MainActivity.userBasicInfo.getUserName(), new OnReadChatList() {
            @Override
            public void onReadChatList(ArrayList<ArticlesUnderTag> chatList, boolean isAdded) {
                if(pBar.getVisibility() == View.VISIBLE){
                    pBar.setVisibility(View.GONE);
                }

                for(int i = 0; i < chatList.size(); i++){
                    // articleId = username
                    // headLine = last message
                    setChatList(chatList.get(i), false, isAdded);
                }
            }

            @Override
            public void onReadChatList(DataSnapshot snapshot, boolean isAdded) {

            }
        });
    }

    private void setChatList(ArticlesUnderTag friend, boolean isNew, boolean isAdded){
        //listedUsernames.put(friend.getArticleId(), friend.getArticleId());
        LinearLayout layoutChatList = findViewById(R.id.layout_chatList);

        if(isAdded){
            if(isNew){
                layoutChatList.removeView(layoutChatList.findViewWithTag(friend.getArticleId()));
                layoutChatList.addView(getChatListView(friend, true), 0);
            }
            else{
                layoutChatList.addView(getChatListView(friend, false));
            }
        }

    }

    private View getChatListView(ArticlesUnderTag friend, boolean isNew){

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.model_chat_list, null);
        TextView txtName = view.findViewById(R.id.txtProfileName),
                txtLastMessage = view.findViewById(R.id.txtLastMessage),
                txtIsOnline = view.findViewById(R.id.imgIsOnline),
                txtNew = view.findViewById(R.id.txtNewMessageHint);
        txtName.setText(friend.getArticleId());
        String s;
        int lastMessageSize = 30;
        if(friend.getHeadLine().length() > lastMessageSize){
            s = friend.getHeadLine().substring(0, lastMessageSize) + "...";
        }
        else{
            s = friend.getHeadLine();
        }
        txtLastMessage.setText(s);
        if(isNew){
            txtNew.setVisibility(View.VISIBLE);
            txtLastMessage.setTextColor(getResources().getColor(R.color.head_line_color));
        }
        else{
            txtNew.setVisibility(View.GONE);
            txtLastMessage.setTextColor(getResources().getColor(R.color.text_color));
        }
        Retrieve.getActivityStatus(friend.getArticleId(), task -> {
            if(task == DataModel.YES){
                if(txtIsOnline.getParent() != null){
                    txtIsOnline.setCompoundDrawablesWithIntrinsicBounds(
                            0, 0, R.drawable.round_shape_chat_blue, 0);
                }
            }
            else{
                if(txtIsOnline.getParent() != null){
                    txtIsOnline.setCompoundDrawablesWithIntrinsicBounds(
                            0, 0, 0, 0);
                }
            }
        });

        view.setOnClickListener(getListenerForChatListView(txtName, txtNew));
        view.setTag(friend.getArticleId());

        return view;
    }

    private View.OnClickListener getListenerForChatListView(TextView txtUsername, TextView txtNew){
        return view -> {
            txtNew.setVisibility(View.GONE);
            String username = txtUsername.getText().toString();
            Intent intent = new Intent(ChatActivity.this, ChatActivity.class);
            intent.putExtra("INTENT_TYPE", ChatActivity.CHAT_BOX);
            intent.putExtra("RECEIVE_USER_NAME", username);
            intent.putExtra("RECEIVER_NAME", username);
            startActivity(intent);
        };
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment fragment = fm.findFragmentById(R.id.activity_main_frameLayout);
        if(fragment != null){
            ft.remove(fragment);
            //fm.popBackStack();
            ft.commit();

            // Go to home if the last fragment is removed
            /*
            ft = getSupportFragmentManager().beginTransaction();
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.activity_main_frameLayout);
            if(f == null){
                makeViewsInvisible();
                readArticles();
            }
             */

        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        if (!inOtherActivity){
            Save.activeStatus(MainActivity.userBasicInfo.getUserName(), false);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        inOtherActivity = false;
        Save.activeStatus(MainActivity.userBasicInfo.getUserName(), true);
        super.onResume();
    }
}