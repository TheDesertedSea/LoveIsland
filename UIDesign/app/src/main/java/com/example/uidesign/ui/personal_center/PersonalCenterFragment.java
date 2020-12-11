package com.example.uidesign.ui.personal_center;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.uidesign.R;
import com.example.uidesign.data.LogginedUser;
import com.example.uidesign.net.NetPersonalCenter;
import com.example.uidesign.ui.personal_info_edit.PersonalInfoEditActivity;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;

public class PersonalCenterFragment extends Fragment {

    private Fragment thisFragment=this;

    private NetPersonalCenter.UserInfo userInfo;
    private NetPersonalCenter netPersonalCenter;
    private PersonalCenterFragmentHandler personalCenterFragmentHandler;

    private View root;
    private ImageView iconView;
    private TextView nickNameText;
    private TextView sexAndSchoolText;
    private TextView introductionText;

    private String HOST="";
    private String baseIconUrl="http://"+HOST+":30010/user/userPortrait/";


    public class PersonalCenterFragmentHandler extends Handler
    {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch(msg.what)
            {
                case 100:
                    Glide.with(thisFragment).load(baseIconUrl+userInfo.portraitName)
                            .into(iconView);
                    nickNameText.setText(userInfo.nickName);
                    String sexAndSchool=", "+userInfo.school;
                    if(userInfo.sex)
                    {
                        sexAndSchool="男生"+sexAndSchool;
                    }else
                    {
                        sexAndSchool="女生"+sexAndSchool;
                    }

                    sexAndSchoolText.setText(sexAndSchool);
                    introductionText.setText(userInfo.introduction);

            }

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_personal_center, container, false);
        iconView=root.findViewById(R.id.user_icon);
        nickNameText=root.findViewById(R.id.nick_name_personal_center);
        sexAndSchoolText=root.findViewById(R.id.sex_school_center);
        introductionText=root.findViewById(R.id.intro_personal_center);

        netPersonalCenter=new NetPersonalCenter();
        personalCenterFragmentHandler=new PersonalCenterFragmentHandler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                userInfo=netPersonalCenter.getUserInfo(LogginedUser.getInstance().getUid());
                Message message=new Message();
                message.what=100;
                personalCenterFragmentHandler.sendMessage(message);
            }
        }).start();

        ImageButton editButton=root.findViewById(R.id.button_edit_info);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), PersonalInfoEditActivity.class);
                Drawable iconDrawable=iconView.getDrawable();
                Bitmap bitmap=drawableToBitmap(iconDrawable);
                byte[] bytes = bitmap2Bytes(bitmap);
                intent.putExtra("icon",bytes);
                intent.putExtra("nickName",userInfo.nickName);
                intent.putExtra("school",userInfo.school);
                intent.putExtra("introduction",userInfo.introduction);
                startActivity(intent);
            }
        });

        return root;
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if(drawable==null)
        {
            return null;
        }
        Bitmap bitmap;
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        System.out.println("Drawable转Bitmap");
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        bitmap = Bitmap.createBitmap(w,h,config);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    private byte[] bitmap2Bytes(Bitmap bm){
        if(bm==null)
        {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}