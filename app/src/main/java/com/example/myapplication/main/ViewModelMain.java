package com.example.myapplication.main;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.myapplication.BR;

public class ViewModelMain extends BaseObservable
{
    private String channelNum;

    @Bindable
    public String getChannelNum() {
        return channelNum;
    }

    public void setChannelNum(String channelNum) {
        this.channelNum = channelNum;
        notifyPropertyChanged(BR.channelNum);
    }

}
