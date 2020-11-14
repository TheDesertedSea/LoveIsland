package com.example.loveislandapp.ui.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.loveislandapp.R;
import com.example.loveislandapp.data.LoginedUser;
import com.example.loveislandapp.data.Msg;
import com.example.loveislandapp.databinding.ActivityChatBinding;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private Context context;

    private String ip="192.168.1.105";
    private String port="6666";
    private List<Msg> msgList = new ArrayList<>();
    private MsgAdapter adapter;
    private Socket socketSend;
    DataInputStream dis;
    DataOutputStream dos;
    boolean isRunning = false;
    private TextView myName;
    private String recMsg;
    private boolean isSend=false;
    private String name;
    private Handler handler = new Handler(Looper.myLooper()){//获取当前进程的Looper对象传给handler
        @Override
        public void handleMessage(@NonNull Message msg){//?
            if(!recMsg.isEmpty()){
                addNewMessage(recMsg,Msg.TYPE_RECEIVED);//添加新数据
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        binding=ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRunning)
                {
                    return;
                }
                String content = binding.inputText.getText().toString();
                if(!"".equals(content)){
                @SuppressLint("SimpleDateFormat")
                String date = new SimpleDateFormat("hh:mm:ss").format(new Date());
                StringBuilder sb = new StringBuilder();
                sb.append(content).append("\n\n"+date);
                content = sb.toString();

                    Msg msg = new Msg(content,Msg.TYPE_SENT);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size()-1);
                    binding.msgRecyclerView.scrollToPosition(msgList.size()-1);
                    isSend = true;
                    sb.delete(0,sb.length());
                }

            }
        });
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog= new AlertDialog.Builder(context);
                dialog.setTitle("退出");
                dialog.setMessage("退出登录?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();//finish()是在程序执行的过程中使用它来将对象销毁,finish（）方法用于结束一个Activity的生命周期
                    }
                });
                dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();//让返回键开始启动
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        binding.msgRecyclerView.setLayoutManager(layoutManager);
        ImageView imageView=findViewById(R.id.chatTargetIcon);
        TextView textView=findViewById(R.id.text_room);
        Glide.with(context).load("http://192.168.1.112:30010/userInfo").into(imageView);
        textView.setText(LoginedUser.getInstance().getNickname());
        adapter = new MsgAdapter(msgList,imageView.getDrawingCache(),imageView.getDrawingCache());
        binding.msgRecyclerView.setAdapter(adapter);
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    socketSend = new Socket(ip,Integer.parseInt(port));
                        isRunning = true;
                        Log.d("ttw","发送了一条消息2");
                        dis = new DataInputStream(socketSend.getInputStream());
                        dos = new DataOutputStream(socketSend.getOutputStream());
                        new Thread(new receive(),"接收线程").start();
                        new Thread(new Send(),"发送线程").start();

                }catch(Exception e){
                    isRunning = false;
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "连接服务器失败！！！", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    try{
                        if(socketSend!=null)
                        {
                            socketSend.close();
                        }
                    }catch(IOException e1){
                        e1.printStackTrace();
                    }


                }
            }
        }).start();

    }
    public void addNewMessage(String msg,int type){
        Msg message = new Msg(msg,type);
        msgList.add(message);
        adapter.notifyItemInserted(msgList.size()-1);
        binding.msgRecyclerView.scrollToPosition(msgList.size()-1);
    }
    class receive implements Runnable{
        public void run(){
            recMsg = "";
            while(isRunning){
                try{
                    recMsg = dis.readUTF();
                    Log.d("ttw","收到了一条消息"+"recMsg: "+ recMsg);
                }catch(Exception e){
                    e.printStackTrace();
                }
                if(!TextUtils.isEmpty(recMsg)){
                    Log.d("ttw","inputStream:"+dis);
                    Message message = new Message();
                    message.obj=recMsg;
                    handler.sendMessage(message);
                }
            }
        }
    }
    class Send implements Runnable{
        @Override
        public void run(){
            while(isRunning){
                String content = binding.inputText.getText().toString();
                Log.d("ttw","发了一条消息");
                if(!"".equals(content)&&isSend){
                    @SuppressLint("SimpleDateFormat")
                    String date = new SimpleDateFormat("hh:mm:ss").format(new Date());
                    StringBuilder sb = new StringBuilder();
                    sb.append(content).append("\n\n来自：").append(name).append("\n"+date);
                    content = sb.toString();
                    try{
                        dos.writeUTF(content);
                        sb.delete(0,sb.length());
                        Log.d("ttw","发送了一条消息");
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                    isSend = false;
                    binding.inputText.setText("");
                }
            }
        }
    }
}