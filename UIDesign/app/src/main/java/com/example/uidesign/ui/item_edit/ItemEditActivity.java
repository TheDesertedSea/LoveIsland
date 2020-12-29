package com.example.uidesign.ui.item_edit;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.uidesign.data.LogginedUser;
import com.example.uidesign.net.NetSendConfession;
import com.example.uidesign.net.NetSendDiscussion;
import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivityItemEditBinding;

import java.util.Date;

public class ItemEditActivity extends BaseActivity {

    private final String TAG = "ItemEditActivity";
    private ActivityItemEditBinding binding;
    private final Context thisContext = this;
    private LogginedUser Me = LogginedUser.getInstance();

    private String identifyString;
    private final String ConfessionString = "confession";
    private final String DiscussionString = "discussion";

    private Button sendButton;

    private long nowDate = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityItemEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        identifyString = intent.getStringExtra("name");
        Log.v(TAG, identifyString);

        sendButton = binding.button;
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "点击按钮");
                if (identifyString.equals("confession")) {
                    Log.v(TAG, "进来了");
                    //发送表白贴
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.v(TAG, "run");
                            NetSendConfession netSendConfession = new NetSendConfession();

                            String result = netSendConfession.sendConfession(Me.getUid(), binding.commentInput.getText().toString(), nowDate);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.v(TAG, "修改UI");
                                    Log.v(TAG, result);
                                    switch(result)
                                    {
                                        case NetSendConfession.SUCCESS:
                                            Toast.makeText(thisContext, NetSendConfession.SUCCESS_INFO, Toast.LENGTH_SHORT).show();
                                            ItemEditActivity.this.finish();
                                            break;
                                        case NetSendConfession.FAIL:
                                            Toast.makeText(thisContext, NetSendConfession.FAIL_INFO, Toast.LENGTH_SHORT).show();
                                            break;
                                    }
                                }
                            });
                        }
                    }).start();
                } else if (identifyString.equals(DiscussionString)) {
                    //发送讨论贴
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            NetSendDiscussion netSendDiscussion = new NetSendDiscussion();
                            String result = netSendDiscussion.sendDiscussion(Me.getUid(), binding.commentInput.getText().toString(), nowDate);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    switch(result)
                                    {
                                        case NetSendDiscussion.SUCCESS:
                                            Toast.makeText(thisContext, NetSendDiscussion.SUCCESS_INFO, Toast.LENGTH_SHORT).show();
                                            ItemEditActivity.this.finish();
                                            break;
                                        case NetSendDiscussion.FAIL:
                                            Toast.makeText(thisContext, NetSendDiscussion.FAIL_INFO, Toast.LENGTH_SHORT).show();
                                            break;
                                    }
                                }
                            });
                        }
                    }).start();
                } else {
                    Log.v(TAG, "shibai");
                }
            }
        });
    }
}