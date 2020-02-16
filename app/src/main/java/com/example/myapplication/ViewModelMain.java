package com.example.myapplication;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

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
