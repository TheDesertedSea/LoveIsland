package com.example.uidesign.ui.coldboot;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.example.uidesign.data.ColdBootItem;
import com.example.uidesign.data.LogginedUser;
import com.example.uidesign.databinding.ActivityColdBootBinding;
import com.example.uidesign.net.NetColdBoot;
import com.example.uidesign.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class ColdBootActivity extends AppCompatActivity {

    private ActivityColdBootBinding binding;
    private Context thisContext=this;
    private Activity thisActivity=this;
    private List<ColdBootItem> coldBootItemList;
    private List<Integer> choose=new ArrayList<Integer>();

    private void enabledAllButton()
    {
        binding.checkBox1.setEnabled(true);
        binding.checkBox2.setEnabled(true);
        binding.checkBox3.setEnabled(true);
        binding.checkBox4.setEnabled(true);
        binding.checkBox5.setEnabled(true);
        binding.checkBox6.setEnabled(true);
        binding.checkBox7.setEnabled(true);
        binding.checkBox8.setEnabled(true);
        binding.checkBox9.setEnabled(true);
        binding.checkBox10.setEnabled(true);
        binding.nextButton.setEnabled(true);
    }

    private void disabledAllButton()
    {
        binding.checkBox1.setEnabled(false);
        binding.checkBox2.setEnabled(false);
        binding.checkBox3.setEnabled(false);
        binding.checkBox4.setEnabled(false);
        binding.checkBox5.setEnabled(false);
        binding.checkBox6.setEnabled(false);
        binding.checkBox7.setEnabled(false);
        binding.checkBox8.setEnabled(false);
        binding.checkBox9.setEnabled(false);
        binding.checkBox10.setEnabled(false);
        binding.nextButton.setEnabled(false);
    }

    private void setButtonInfo()
    {
        binding.checkBox1.setText(coldBootItemList.get(0).domainName);
        binding.checkBox2.setText(coldBootItemList.get(1).domainName);
        binding.checkBox3.setText(coldBootItemList.get(2).domainName);
        binding.checkBox4.setText(coldBootItemList.get(3).domainName);
        binding.checkBox5.setText(coldBootItemList.get(4).domainName);
        binding.checkBox6.setText(coldBootItemList.get(5).domainName);
        binding.checkBox7.setText(coldBootItemList.get(6).domainName);
        binding.checkBox8.setText(coldBootItemList.get(7).domainName);
        binding.checkBox9.setText(coldBootItemList.get(8).domainName);
        binding.checkBox10.setText(coldBootItemList.get(9).domainName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityColdBootBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        disabledAllButton();

        NetColdBoot netColdBoot=new NetColdBoot();
        new Thread(new Runnable() {
            @Override
            public void run() {
                coldBootItemList=netColdBoot.getColdBootItem(LogginedUser.getInstance().getUid());
                Log.v("coldBoot",String.valueOf(coldBootItemList.size()));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(coldBootItemList!=null)
                        {
                            setButtonInfo();
                        }
                        enabledAllButton();
                    }
                });
            }
        }).start();

        binding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disabledAllButton();
                if(binding.checkBox1.isChecked())
                {
                    choose.add(coldBootItemList.get(0).domainID);
                }
                if(binding.checkBox2.isChecked())
                {
                    choose.add(coldBootItemList.get(1).domainID);
                }
                if(binding.checkBox3.isChecked())
                {
                    choose.add(coldBootItemList.get(2).domainID);
                }
                if(binding.checkBox4.isChecked())
                {
                    choose.add(coldBootItemList.get(3).domainID);
                }
                if(binding.checkBox5.isChecked())
                {
                    choose.add(coldBootItemList.get(4).domainID);
                }
                if(binding.checkBox6.isChecked())
                {
                    choose.add(coldBootItemList.get(5).domainID);
                }
                if(binding.checkBox7.isChecked())
                {
                    choose.add(coldBootItemList.get(6).domainID);
                }
                if(binding.checkBox8.isChecked())
                {
                    choose.add(coldBootItemList.get(7).domainID);
                }
                if(binding.checkBox9.isChecked())
                {
                    choose.add(coldBootItemList.get(8).domainID);
                }
                if(binding.checkBox10.isChecked())
                {
                    choose.add(coldBootItemList.get(9).domainID);
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean result=netColdBoot.sendColdBootItem(LogginedUser.getInstance().getUid(),choose);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent=new Intent(thisContext, MainActivity.class);
                                startActivity(intent);
                                thisActivity.finish();
                            }
                        });
                    }
                }).start();
            }
        });

    }
}