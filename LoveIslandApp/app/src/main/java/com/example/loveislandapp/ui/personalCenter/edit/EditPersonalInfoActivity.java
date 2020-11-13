package com.example.loveislandapp.ui.personalCenter.edit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.Environment;
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
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class EditPersonalInfoActivity extends AppCompatActivity {

    public static final int CHOOSE_PHOTO=2;
    public static final int CROP_PHOTO=3;

    private String[] items = new String[] { "相册", "拍照" };
    private static final String baseUrl
            = "http://192.168.1.112:30010/uers/userPortrait";
    private LoginedUser loginedUser=LoginedUser.getInstance();

    private ActivityEditPersonalInfoBinding binding;
    private Context context;
    private PersonalInfoHttp personalInfoHttp;
    private IconHttp iconHttp;

    private Uri photoUri;
    File picFile;

    private final int PIC_FROM_CAMERA = 1;
    private final int PIC_FROM_LOCALPHOTO = 2;
    private final int PIC_FROM_CROP=3;

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

                showDialog();

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
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
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
                    doHandlerPhoto(PIC_FROM_LOCALPHOTO);
                }else
                {
                    Toast.makeText(context,"你拒绝了访问相册权限",Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    doHandlerPhoto(PIC_FROM_CAMERA);
                }else
                {
                    Toast.makeText(context,"你拒绝了访问文件权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
                case PIC_FROM_CAMERA: // 拍照
                    try
                    {
                        cropImageUriByTakePhoto(photoUri);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    break;
            case PIC_FROM_LOCALPHOTO:// 相册
                try
                    {
                        Log.v("uri1",data.getData().getPath());
                        Log.v("uri2",photoUri.getPath());
                        Uri uri=FileProvider.getUriForFile(context,getPackageName()+
                                ".fileprovider",new File(handleImageOnKitKat(data.getData())));
                        Log.v("uri3",uri.getPath());
                        Bitmap bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        FileOutputStream fileOutputStream = new FileOutputStream(picFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
                        cropImageUriByTakePhoto(photoUri);

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    break;
            case PIC_FROM_CROP:
                Log.v("rt","finish");
                try {
                    Bitmap bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                    binding.UserIcon.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(iconHttp.setIcon(picFile))
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
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            default:

        }
    }

    private Bitmap decodeUriAsBitmap(Uri uri){
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
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

    private void showDialog() {
        // TODO Auto-generated method stub
        new AlertDialog.Builder(this).setTitle("选择头像")
                .setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        switch (which) {
                            case 0:
                                if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED)
                                {
                                    ActivityCompat.requestPermissions(EditPersonalInfoActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                                            ,1);
                                }else {
                                    doHandlerPhoto(PIC_FROM_LOCALPHOTO);// 从相册中去获取
                                }
                                break;
                            case 1:
                                if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED)
                                {
                                    ActivityCompat.requestPermissions(EditPersonalInfoActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                                            ,2);
                                }else {
                                    doHandlerPhoto(PIC_FROM_CAMERA);// 用户点击了从照相机获取
                                }

                                break;
                        }
                    }
                }).show();
    }

    private void doHandlerPhoto(int type)
    {

        try
        {

            //sd卡根目录
            picFile = new File(context.getExternalCacheDir(), "upload.jpeg");
            Log.v("path",context.getExternalCacheDir().toString());
            Log.v("path",picFile.getPath());
            // 文件不存在就创建


            if (!picFile.exists()) {
                picFile.createNewFile();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            Log.i("HandlerPicError", "处理图片出现错误");
            return;
        }
            // photoUri标识着图片地址
            photoUri = FileProvider.getUriForFile(context,getPackageName()+ ".fileprovider",picFile);

            if (type==PIC_FROM_LOCALPHOTO)
            {
                Intent intent = getChooseImageIntent();
                startActivityForResult(intent, PIC_FROM_LOCALPHOTO);
            }else if (type == PIC_FROM_CAMERA){
                // 隐式调用照相机程序
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                // 拍下的照片会被输入到upload.jpeg里面
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                Log.e("MainActivity", "doHandlerPhoto: "+MediaStore.EXTRA_OUTPUT );
                startActivityForResult(cameraIntent, PIC_FROM_CAMERA);
            }


    }

    /**
     * 调用图片剪辑程序
     */
    public Intent getChooseImageIntent() {
      /*//原始代码---
      Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
      intent.setType("image");
       Log.e("MainActivity", "可以使用路径: "+photoUri );
      setIntentParams(intent);
      return intent;*/
// 修改后的代码
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        // file:///storage/emulated/0/upload/upload.jpeg
        return intent;
    }

    /**
     * 启动裁剪
     */
    private void cropImageUriByTakePhoto(Uri uri) {
        // 此处启动裁剪程序
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        setIntentParams(intent);
        startActivityForResult(intent, PIC_FROM_CROP);
        Log.v("crop","here");
    }

    /**
     * 设置公用参数
     */
    private void setIntentParams(Intent intent)
    {
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("noFaceDetection", true); // no face detection
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
    }
}