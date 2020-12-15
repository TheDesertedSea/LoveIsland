package com.example.uidesign.ui.item_edit;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.uidesign.data.CachedLoginData;
import com.example.uidesign.data.LogginedUser;
import com.example.uidesign.net.NetLogin;
import com.example.uidesign.net.NetSendConfession;
import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivityItemEditBinding;
import com.example.uidesign.ui.MainActivity;
import com.royrodriguez.transitionbutton.TransitionButton;

public class ItemEditActivity extends BaseActivity {

    private ActivityItemEditBinding binding;
    private final Context thisContext = this;
    private LogginedUser Me = LogginedUser.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if ("edit".equals(intent.getAction())) {
            //只有intent的action是"edit"才能打开页面
        }

        super.onCreate(savedInstanceState);
        binding = ActivityItemEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送给服务器端用户编辑的内容
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        NetSendConfession netSendConfession = new NetSendConfession();
                        String result = netSendConfession.sendConfession(Me.getUid(), binding.commentInput.getText().toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch(result)
                                {
                                    case NetSendConfession.SUCCESS:
                                        Toast.makeText(thisContext, NetSendConfession.SUCCESS_INFO, Toast.LENGTH_SHORT).show();
                                        break;
                                    case NetSendConfession.FAIL:
                                        Toast.makeText(thisContext, NetSendConfession.FAIL_INFO, Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        });

                    }
                }).start();
            }
        });
    }
}