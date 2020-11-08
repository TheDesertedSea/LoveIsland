package com.example.loveislandapp.ui.personalCenter.edit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.loveislandapp.R;
import com.example.loveislandapp.data.LoginedUser;
import com.example.loveislandapp.databinding.ActivityEditPersonalInfoBinding;
import com.example.loveislandapp.databinding.ActivityPersonalCenterBinding;
import com.example.loveislandapp.http.IconHttp;
import com.example.loveislandapp.http.PersonalInfoHttp;

import java.io.ByteArrayOutputStream;

public class EditPersonalInfoActivity extends AppCompatActivity {

    public static final int CHOOSE_PHOTO=2;
    public static final int CROP_PHOTO=3;

    private static final String baseUrl
            = "http://192.168.1.102:30010/usericon/";
    private LoginedUser loginedUser=LoginedUser.getInstance();

    private ActivityEditPersonalInfoBinding binding;
    private Context context;
    private PersonalInfoHttp personalInfoHttp;
    private IconHttp iconHttp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context=this;
        binding= ActivityEditPersonalInfoBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        personalInfoHttp=new PersonalInfoHttp();
        iconHttp=new IconHttp();

        //将个人中心的数据拷贝过来
        Intent intent=getIntent();
        byte[] appIcons = intent.getByteArrayExtra("appIcon");
        if(appIcons!=null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(appIcons, 0, appIcons.length);
            binding.UserIcon.setImageBitmap(bitmap);
        }

        binding.EditTextNickname.setText(intent.getStringExtra("nickname"));
        binding.EditTextSchool.setText(intent.getStringExtra("school"));
        binding.EditTextIntroduction.setText(intent.getStringExtra("introduction"));

        binding.EditIconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(EditPersonalInfoActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                    ,1);
                }else {
                    openAlbum();
                }
            }
        });

        binding.SaveEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PersonalInfoHttp.EditResult editResult=personalInfoHttp
                                .setPersonalInfo(
                                        binding.EditTextNickname.getText().toString(),
                                binding.EditTextSchool.getText().toString(),
                                binding.EditTextIntroduction.getText().toString());

                        if(editResult.success)
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context,"编辑成功!",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else if(editResult.nickNameUsed)
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context,"昵称已被使用，编辑失败",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context,"编辑失败",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();

            }
        });


    }

    private void openAlbum()
    {
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    openAlbum();
                }else
                {
                    Toast.makeText(context,"你拒绝了访问相册权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode)
        {
            case CHOOSE_PHOTO:
                if(resultCode==RESULT_OK)
                {
                    Glide.with(context)
                            .load(data.getData())
                            .into(binding.UserIcon);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Uri uri=data.getData();
                            String imagePath=handleImageOnKitKat(uri);
                            boolean result=iconHttp.setIcon(imagePath,LoginedUser.getInstance().getUid());
                            if(result)
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context,"修改成功",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context,"修改失败",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();
                }
                break;
            case CROP_PHOTO:
                if(resultCode==RESULT_OK)
                {

                }
                break;
            default:

        }
    }

    //如果为content格式的获取方式
    private String getImagePath(Uri uri,String selection)
    {
        Log.v("uri",uri.toString());
        String path=null;
        Cursor cursor= getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null)
        {
            if(cursor.moveToFirst())
            {
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    //获取图像path的入口，适配各种uri类型
    private String handleImageOnKitKat(Uri uri) {
        String imagePath = null;

        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                //Log.d(TAG, uri.toString());
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                //Log.d(TAG, uri.toString());
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.parseLong(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //Log.d(TAG, "content: " + uri.toString());
            imagePath = getImagePath(uri, null);
        }
        return imagePath;
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

    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
// aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
// outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, CROP_PHOTO);
    }

}