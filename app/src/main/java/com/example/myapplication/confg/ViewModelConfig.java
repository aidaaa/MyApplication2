package com.example.myapplication.confg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.myapplication.BR;

public class ViewModelConfig extends BaseObservable
{
    private String ip;
    Context context;

    public ViewModelConfig(Context context) {
        this.context = context;
    }

    @Bindable
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
        notifyPropertyChanged(BR.ip);
    }

    public void onclick(View view)
    {
        if (ip!=null)
        {
            SharedPreferences.Editor editor = context.getSharedPreferences("shared",Context.MODE_PRIVATE).edit();
            editor.putString("ip",ip);
            editor.apply();
        }
    }
}
