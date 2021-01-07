package com.example.uidesign.ui.personal_info_edit;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.uidesign.data.LogginedUser;
import com.example.uidesign.net.NetPersonalCenter;
import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivityPersonalInfoEditBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PersonalInfoEditActivity extends BaseActivity {

    public static final int CHOOSE_PHOTO=2;
    public static final int CROP_PHOTO=3;

    private String[] items = new String[] { "相册", "拍照" };
    private Context thisContext;
    private Activity thisActivity=this;
    private NetPersonalCenter netPersonalCenter;

    private Uri photoUri;
    File picFile;

    private final int PIC_FROM_CAMERA = 1;
    private final int PIC_FROM_LOCALPHOTO = 2;
    private final int PIC_FROM_CROP=3;

    private boolean bEditingIcon=false;

    private ActivityPersonalInfoEditBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisContext=this;

        binding=ActivityPersonalInfoEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        netPersonalCenter=new NetPersonalCenter();

        Intent lastIntent=getIntent();
        byte[] appIcons = lastIntent.getByteArrayExtra("icon");
        if(appIcons!=null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(appIcons, 0, appIcons.length);
            binding.userIcon.setImageBitmap(bitmap);
        }
        String nickName=lastIntent.getStringExtra("nickName");
        String school=lastIntent.getStringExtra("school");
        String introduction=lastIntent.getStringExtra("introduction");

        binding.nickNameInput.setText(nickName);
        binding.schoolInput.setText(school);
        binding.personalIntroInput.setText(introduction);


        binding.userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bEditingIcon)
                {
                    showDialog();
                    bEditingIcon=true;
                }

            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thisActivity.finish();
            }
        });

        binding.ackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int result=netPersonalCenter.setUserInfo(LogginedUser.getInstance().getUid(),
                                binding.nickNameInput.getText().toString(),
                                binding.schoolInput.getText().toString(),
                                binding.personalIntroInput.getText().toString());
                        Log.v("result",String.valueOf(result));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(result==NetPersonalCenter.PERSONAL_INFO_EDIT_SUCCESS)
                                {
                                    Toast.makeText(thisContext,NetPersonalCenter.MESSAGE_PERSONAL_INFO_EDIT_SUCCESS,Toast.LENGTH_SHORT).show();
                                }else
                                {
                                    Toast.makeText(thisContext,NetPersonalCenter.MESSAGE_PERSONAL_INFO_EDIT_OTHER_FAIL,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).start();
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case 1:
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {
                    doHandlerPhoto(PIC_FROM_LOCALPHOTO);
                }else
                {
                    Toast.makeText(thisContext,"你拒绝了访问相册权限",Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    doHandlerPhoto(PIC_FROM_CAMERA);
                }else
                {
                    Toast.makeText(thisContext,"你拒绝了访问文件权限",Toast.LENGTH_SHORT).show();
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
                    Uri uri= FileProvider.getUriForFile(thisContext,getPackageName()+
                            ".fileprovider",new File(handleImageOnKitKat(data.getData())));
                    Log.v("uri3",uri.getPath());
                    Bitmap bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
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
                    binding.userIcon.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(netPersonalCenter.setIcon(picFile, LogginedUser.getInstance().getUid()))
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(thisContext,"头像修改成功",Toast.LENGTH_SHORT).show();
                                    bEditingIcon=false;
                                    Glide.get(thisContext).clearMemory();
                                }
                            });
                        }else
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(thisContext,"头像修改失败",Toast.LENGTH_SHORT).show();
                                    bEditingIcon=false;
                                }
                            });
                        }
                    }
                }).start();

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
                                if(ContextCompat.checkSelfPermission(thisContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED)
                                {
                                    ActivityCompat.requestPermissions(PersonalInfoEditActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                                            ,1);
                                }else {
                                    doHandlerPhoto(PIC_FROM_LOCALPHOTO);// 从相册中去获取
                                }
                                break;
                            case 1:
                                if(ContextCompat.checkSelfPermission(thisContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED)
                                {
                                    ActivityCompat.requestPermissions(PersonalInfoEditActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
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
            picFile = new File(thisContext.getExternalCacheDir(), "upload.jpeg");
            Log.v("path",thisContext.getExternalCacheDir().toString());
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
        photoUri = FileProvider.getUriForFile(thisContext,getPackageName()+ ".fileprovider",picFile);

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