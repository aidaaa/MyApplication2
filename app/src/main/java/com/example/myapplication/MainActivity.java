package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements Observer<ArrayList<String>> {

    PlayerView player_view;
    SimpleExoPlayer simpleExoPlayer;
    DefaultTrackSelector trackSelector;
    DataSource.Factory daFactory;
    Xml xml=new Xml();
    ArrayList<String> urls=new ArrayList<>();
    EditText edt;
    int id;
    ArrayList<String> idStr=new ArrayList<>();
    Handler handler=new Handler();
    int counter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        ActivityMainBinding binding= DataBindingUtil.setContentView(this,R.layout.activity_main);
        ViewModelMain viewModelMain=new ViewModelMain();
        binding.setMain(viewModelMain);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //player_view=findViewById(R.id.player_view);
        player_view=binding.playerView;

        edt=binding.edt;
        edt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});


        trackSelector=new DefaultTrackSelector();
        simpleExoPlayer= ExoPlayerFactory.newSimpleInstance(this,trackSelector);
        daFactory=new DefaultHttpDataSourceFactory(Util.getUserAgent(this,"exoplayer"));

        SharedPreferences sharedPreferences=getSharedPreferences("shared", Context.MODE_PRIVATE);
        String ip=sharedPreferences.getString("ip","192.168.10.106");

        Observable<ArrayList<String>> observable=xml.getObservableXml(ip);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
        // urls=xml.getXML();
        // setPlayer(0);
    }

    public void setPlayer(int pos)
    {
        MediaSource mediaSource = null;

        Uri uri=Uri.parse(urls.get(pos));
        mediaSource=new ProgressiveMediaSource.Factory(daFactory).createMediaSource(uri);
     /*   switch (pos)
        {
            case 0:
                mediaSource=new ProgressiveMediaSource.Factory(daFactory).createMediaSource(uri);
                break;
            case 1:
                mediaSource=new ProgressiveMediaSource.Factory(daFactory).createMediaSource(uri1);
                break;
            case 2:
                mediaSource=new ProgressiveMediaSource.Factory(daFactory).createMediaSource(uri2);
                break;
            //case 3:
            //    mediaSource=new ProgressiveMediaSource.Factory(daFactory).createMediaSource(uri3);
            //    break;
        }*/


        player_view.setPlayer(simpleExoPlayer);
        simpleExoPlayer.prepare(mediaSource);
        simpleExoPlayer.setPlayWhenReady(true);
        simpleExoPlayer.getPlaybackState();

        simpleExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerError(ExoPlaybackException error) {
                System.out.println(error.getMessage());
            }
        });

    }

    public void channelNum(String chNum)
    {
        idStr.add(chNum);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (idStr.size()!=0)
                {
                    String fId;
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < idStr.size(); i++) {
                        sb.append(idStr.get(i));
                    }
                    fId=sb.toString();
                    setPlayer(Integer.parseInt(fId));
                    idStr.clear();
                }
            }
        },5000);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (urls!=null)
        {
            int id=keyCode;
            System.out.println(String.valueOf(id));
            //Up
            if (keyCode == 19)
            {
                if (counter==12)
                {
                    setPlayer(counter);
                }
                else if(counter<12)
                {
                    counter++;
                    setPlayer(counter);
                }
            }
            //Down
            else  if (keyCode == 20)
            {
                if (counter==0)
                {
                    setPlayer(counter);
                }
                else if (counter>0 && counter<=12)
                {
                    counter--;
                    setPlayer(counter);
                }
            }

            //ok
            else if (keyCode==23)
            {
                Intent intent=new Intent(MainActivity.this,ConfigActivity.class);
                startActivity(intent);
            }

            //0
            else if (keyCode==7){
                channelNum("0");
            }

            //1
            else if (keyCode==8){
              channelNum("1");
            }

            //2
            else if (keyCode==9){
              channelNum("2");
            }

            //3
            else if (keyCode==10){
                channelNum("3");
            }

            //4
            else if (keyCode==11){
                channelNum("4");
            }

            //5
            else if (keyCode==12){
                channelNum("5");
            }

            //6
            else if (keyCode==13){
                channelNum("6");
            }

            //7
            else if (keyCode==14){
                channelNum("7");
            }

            //8
            else if (keyCode==15){
                channelNum("8");
            }

            //9
            else if (keyCode==16){
                channelNum("9");
            }
        }

        return super.onKeyUp(keyCode, event);
    }


    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(ArrayList<String> strings) {
        String str=strings.get(0);
        if (str.equals("error"))
        {
            Toast.makeText(this, "فایل xml یافت نشد", Toast.LENGTH_SHORT).show();
        }
        else {
            urls = strings;
        }
    }

    @Override
    public void onError(Throwable e) {
        System.out.println(e.getMessage());
    }

    @Override
    public void onComplete() {
        setPlayer(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (simpleExoPlayer!=null)
            simpleExoPlayer.release();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (simpleExoPlayer!=null)
        simpleExoPlayer.release();
    }

      /* trackSelector.setParameters(
               trackSelector.getParameters().buildUpon().setMaxVideoSizeSd()
                            .setPreferredTextLanguage("eng")
                            .setPreferredAudioLanguage("eng").build());*/
}
