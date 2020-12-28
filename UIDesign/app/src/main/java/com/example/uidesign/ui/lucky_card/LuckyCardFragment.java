package com.example.uidesign.ui.lucky_card;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.uidesign.R;
import com.example.uidesign.data.CardType;
import com.example.uidesign.data.LogginedUser;
import com.example.uidesign.net.NetLuckyCard;
import com.example.uidesign.ui.chat.ChatActivity;

import java.util.ArrayList;

public class LuckyCardFragment extends Fragment {

    private ArrayList<CardType> result;
    private LuckyCardFragmentHandler luckyCardFragmentHandler;
    private View root;
    private NetLuckyCard netLuckyCard=new NetLuckyCard();
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;
    private Button refreshButton;

    public class LuckyCardFragmentHandler extends Handler
    {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 100:
                    button1.setText(result.get(0).cardName);
                    button2.setText(result.get(1).cardName);
                    button3.setText(result.get(2).cardName);
                    button4.setText(result.get(3).cardName);
                    button5.setText(result.get(4).cardName);
                    button6.setText(result.get(5).cardName);
                    EnabledAllButtons();
                    break;
                case 200:
//                    Intent intent=new Intent(getContext(), ChatActivity.class);
//                    intent.putExtra("otherUid",msg.arg1);
//                    startActivity(intent);
                case 300:
                    EnabledAllButtons();
                    Toast.makeText(getContext(),"匹配失败",Toast.LENGTH_SHORT);
            }
        }
    }

    private void refreshCard()
    {
        result=netLuckyCard.getLuckyCard(LogginedUser.getInstance().getUid());
        if(result!=null)
        {
            Message message=new Message();
            message.what=100;
            luckyCardFragmentHandler.sendMessage(message);
        }

    }

    private void DisabledAllButtons()
    {
        button1.setEnabled(false);
        button2.setEnabled(false);
        button3.setEnabled(false);
        button4.setEnabled(false);
        button5.setEnabled(false);
        button6.setEnabled(false);
        refreshButton.setEnabled(false);
    }

    private void EnabledAllButtons()
    {
        button1.setEnabled(true);
        button2.setEnabled(true);
        button3.setEnabled(true);
        button4.setEnabled(true);
        button5.setEnabled(true);
        button6.setEnabled(true);
        refreshButton.setEnabled(true);
    }

    private void doNet(int cid)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetLuckyCard.MatchResponseClass matchResponseClass=netLuckyCard.matchCard(cid,LogginedUser.getInstance().getUid());
                if(matchResponseClass.user!=0)
                {
                    //去聊天室
                    Intent intent=new Intent(getContext(),ChatActivity.class);
                    intent.putExtra("user",matchResponseClass.user);
                    intent.putExtra("nickname",matchResponseClass.nickname);
                    startActivity(intent);
                }else
                {
                    Message message=new Message();
                    message.what=300;
                    luckyCardFragmentHandler.sendMessage(message);
                }
            }
        }).start();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root=inflater.inflate(R.layout.fragment_lucky_card, container, false);
        luckyCardFragmentHandler=new LuckyCardFragmentHandler();
        button1=root.findViewById(R.id.card_button_1);
        button2=root.findViewById(R.id.card_button_2);
        button3=root.findViewById(R.id.card_button_3);
        button4=root.findViewById(R.id.card_button_4);
        button5=root.findViewById(R.id.card_button_5);
        button6=root.findViewById(R.id.card_button_6);
        refreshButton=root.findViewById(R.id.refresh_button);
        DisabledAllButtons();
        new Thread(new Runnable() {
            @Override
            public void run() {

                refreshCard();
            }
        }).start();


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisabledAllButtons();
                refreshButton.setEnabled(true);
                doNet(result.get(0).cid);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisabledAllButtons();
                refreshButton.setEnabled(true);
                doNet(result.get(1).cid);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisabledAllButtons();
                refreshButton.setEnabled(true);
                doNet(result.get(2).cid);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisabledAllButtons();
                refreshButton.setEnabled(true);
                doNet(result.get(3).cid);
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisabledAllButtons();
                refreshButton.setEnabled(true);
                doNet(result.get(4).cid);
            }
        });
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisabledAllButtons();
                refreshButton.setEnabled(true);
                doNet(result.get(5).cid);
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisabledAllButtons();
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        refreshCard();
                    }
                }).start();
            }
        });

        return root;
    }
}