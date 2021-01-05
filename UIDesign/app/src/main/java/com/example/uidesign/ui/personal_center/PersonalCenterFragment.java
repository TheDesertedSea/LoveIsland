package com.example.uidesign.ui.personal_center;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;


import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.uidesign.ProjectSettings;
import com.example.uidesign.R;
import com.example.uidesign.data.CachedLoginData;
import com.example.uidesign.data.LogginedUser;
import com.example.uidesign.data.UserInfo;
import com.example.uidesign.net.NetPersonalCenter;
import com.example.uidesign.net.NetSettings;
import com.example.uidesign.ui.my_confession.MyConfessionActivity;
import com.example.uidesign.ui.my_discussion.MyDiscussionActivity;
import com.example.uidesign.ui.personal_info_edit.PersonalInfoEditActivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;

public class PersonalCenterFragment extends Fragment {

    private Fragment thisFragment=this;

    private UserInfo userInfo;
    private NetPersonalCenter netPersonalCenter;
    private PersonalCenterFragmentHandler personalCenterFragmentHandler=new PersonalCenterFragmentHandler();

    private View root;
    private ImageView iconView;
    private TextView nickNameText;
    private TextView sexAndSchoolText;
    private TextView introductionText;
    private Button logOutButton;

    private final String baseIconUrl="http://"+ NetSettings.HOST_1 +":"+NetSettings.PORT_1+"/user/userPortrait/";


    public class PersonalCenterFragmentHandler extends Handler
    {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.v("?","?"+msg.what);
            switch(msg.what)
            {
                case 100:
                    Log.v("?","?"+msg.what);
                    nickNameText.setText(userInfo.nickname);
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
                    break;

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
        logOutButton=root.findViewById(R.id.logout_button);

        Log.v("iconUrl",baseIconUrl+LogginedUser.getInstance().getUid());



        ImageButton editButton=root.findViewById(R.id.button_edit_info);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), PersonalInfoEditActivity.class);
                Drawable iconDrawable=iconView.getDrawable();
                Bitmap bitmap=drawableToBitmap(iconDrawable);
                byte[] bytes = bitmap2Bytes(bitmap);
                intent.putExtra("icon",bytes);
                intent.putExtra("nickName",userInfo.nickname);
                intent.putExtra("school",userInfo.school);
                intent.putExtra("introduction",userInfo.introduction);
                startActivity(intent);
            }
        });

        ImageButton myConfessionButton=root.findViewById(R.id.button_my_confession);
        ImageButton myDiscussionButton=root.findViewById(R.id.button_my_discussion);
        myConfessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), MyConfessionActivity.class);
                intent.putExtra("uid",LogginedUser.getInstance().getUid());
                intent.putExtra("sex",userInfo.sex);
                intent.putExtra("me",true);
                startActivity(intent);
            }
        });
        myDiscussionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), MyDiscussionActivity.class);
                intent.putExtra("uid",LogginedUser.getInstance().getUid());
                intent.putExtra("sex",userInfo.sex);
                intent.putExtra("me",true);
                startActivity(intent);
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CachedLoginData cachedLoginData=new CachedLoginData(getContext());
                cachedLoginData.saveCachedLoginData("",
                        "");
                Intent intent = getContext().getPackageManager().getLaunchIntentForPackage(getContext().getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//与正常页面跳转一样可传递序列化数据,在Launch页面内获得
                intent.putExtra("REBOOT","reboot");
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

    @Override
    public void onResume() {
        super.onResume();
        netPersonalCenter=new NetPersonalCenter();
        if(!ProjectSettings.UI_TEST) {
            Glide.with(thisFragment).load(baseIconUrl + LogginedUser.getInstance().getUid())
//                .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(iconView);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                userInfo=netPersonalCenter.getUserInfo(LogginedUser.getInstance().getUid());
                if(userInfo==null)
                {
                    userInfo=new UserInfo();
                    userInfo.sex=true;
                    userInfo.introduction="";
                    userInfo.nickname="";
                    userInfo.school="";
                }
                Message message=personalCenterFragmentHandler.obtainMessage();
                message.what=100;
                personalCenterFragmentHandler.sendMessage(message);
            }
        }).start();
    }
}