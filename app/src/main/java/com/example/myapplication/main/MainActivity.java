package com.example.myapplication.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.myapplication.R;
import com.example.myapplication.confg.ConfigActivity;
import com.example.myapplication.dagger.app.App;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.xml.Xml;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements Observer<List<ArrayList<String>>>{

    PlayerView player_view;

    @Inject
    SimpleExoPlayer simpleExoPlayer;
    @Inject
    DefaultTrackSelector trackSelector;
    @Inject
    DataSource.Factory daFactory;
    @Inject
    SharedPreferences sharedPreferences;

    Xml xml=new Xml();

    ArrayList<String> urls=new ArrayList<>();
    ArrayList<String> chNames=new ArrayList<>();

    TextView edt,title;

    ArrayList<String> idStr=new ArrayList<>();
    int counter=0;
    boolean shortPress=false;
    boolean longPress=false;
    String chNumber="";

    MediaSource mediaSource = null;
    Uri uri;

    CompositeDisposable compositeDisposable=new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding= DataBindingUtil.setContentView(this, R.layout.activity_main);
        ViewModelMain viewModelMain=new ViewModelMain();
        binding.setMain(viewModelMain);

        App.getApp().getDaggerComponent(this).getPlayer(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        player_view=binding.playerView;

        edt=binding.edt;

        edt.setVisibility(View.INVISIBLE);

        title=binding.title;


        sharedPreferences=getSharedPreferences("shared", Context.MODE_PRIVATE);
        String ip=sharedPreferences.getString("ip","192.168.10.106");

        Observable<List<ArrayList<String>>> observable=xml.getObservableXml(ip);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    public void setPlayer(int pos)
    {
        if (pos < urls.size())
        {
            String str=urls.get(pos);
            if (str=="")
            {
                Toast.makeText(this, "not find url", Toast.LENGTH_LONG).show();
                uri = Uri.parse(urls.get(pos));

                String titleText = chNames.get(pos);
                title.setText(titleText);

                mediaSource = new ProgressiveMediaSource.Factory(daFactory).createMediaSource(uri);

                player_view.setPlayer(simpleExoPlayer);
                simpleExoPlayer.prepare(mediaSource);

                simpleExoPlayer.setPlayWhenReady(true);
                chNumber = "";
                clearText();
            }

            else {
                uri = Uri.parse(urls.get(pos));

                String titleText = chNames.get(pos);
                title.setText(titleText);

                mediaSource = new ProgressiveMediaSource.Factory(daFactory).createMediaSource(uri);

                player_view.setPlayer(simpleExoPlayer);
                simpleExoPlayer.prepare(mediaSource);

                simpleExoPlayer.setPlayWhenReady(true);
                chNumber = "";
                clearText();
            }

            //simpleExoPlayer.getPlaybackState();

            simpleExoPlayer.addListener(new Player.EventListener() {

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    System.out.println(error.getMessage());
                   // edt.setVisibility(View.INVISIBLE);
                  //img.setVisibility(View.INVISIBLE);
                }
            });
        }

        else
        {
          //  edt.setText("");
          //  edt.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "خارج از محدوده", Toast.LENGTH_SHORT).show();
            chNumber="";
            clearText();
        }

    }

    public void channelNum(String chNum)
    {
        idStr.add(chNum);
        //edt.setText("");

        if (urls.size()<=10)
        {
            channelChange();
        }

        else {
            Observable.timer(3000, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Long aLong) {
                            //   idStr.add(chNum);
                            channelChange();
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }


      /*  handler.postDelayed(new Runnable() {
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
        },5000);*/
    }

    public void channelChange()
    {
        //idStr.add(a);
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

    public DisposableObserver<Long> clearText()
    {
        DisposableObserver<Long> observer= Observable.just(1).timer(5000,TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getObserver());
        return observer;

      /*  Observable.timer(5000,TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        edt.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });*/
    }

    public DisposableObserver<Long> getObserver()
    {
        return new DisposableObserver<Long>() {
            @Override
            public void onNext(Long aLong) {
                edt.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    public void dispose(DisposableObserver<Long> disposableObserver)
    {
        disposableObserver.dispose();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if (keyCode == 7 || keyCode == 8 || keyCode == 9 || keyCode == 10 || keyCode == 11 || keyCode == 12 ||
                keyCode == 13 || keyCode == 14 || keyCode == 15 || keyCode == 16 || keyCode == 19 || keyCode == 20 ||
                keyCode == 23 )
        {
            event.startTracking();

            if (longPress == true)
            {
                shortPress = false;
            }

            else
                {
                shortPress = true;
                longPress = false;

                if (urls!=null)
                {
                    int id=keyCode;
                    System.out.println(String.valueOf(id));
                    dispose(clearText());

                    switch (keyCode)
                    {
                        //Up
                        case 19:
                            if (counter==urls.size()-1)
                            {
                                edt.setVisibility(View.VISIBLE);
                                edt.setText(String.valueOf(counter));
                                setPlayer(counter);
                            }
                            else if(counter<urls.size()-1)
                            {
                                counter++;
                                edt.setVisibility(View.VISIBLE);
                                edt.setText(String.valueOf(counter));
                                setPlayer(counter);
                            }
                            break;

                            //Down
                        case 20:
                            if (counter==0)
                            {
                                edt.setVisibility(View.VISIBLE);
                                edt.setText(String.valueOf(counter));
                                setPlayer(counter);
                            }

                            else if (counter>0 && counter<=urls.size()-1)
                            {
                                counter--;
                                edt.setVisibility(View.VISIBLE);
                                edt.setText(String.valueOf(counter));
                                setPlayer(counter);
                            }

                            break;

                            //Ok
                        case 23:
                            Intent intent=new Intent(MainActivity.this, ConfigActivity.class);
                            startActivity(intent);
                            break;

                            //0
                        case 7:
                            chNumber=chNumber+"0";
                            edt.setVisibility(View.VISIBLE);
                            edt.setText(chNumber);
                            counter=Integer.parseInt(chNumber);
                            channelNum("0");
                            break;

                            //1
                        case 8:
                            chNumber=chNumber+"1";
                            edt.setVisibility(View.VISIBLE);
                            edt.setText(chNumber);
                            counter=Integer.parseInt(chNumber);
                            channelNum("1");
                            break;

                            //2
                        case 9:
                            chNumber=chNumber+"2";
                            edt.setVisibility(View.VISIBLE);
                            edt.setText(chNumber);
                            counter=Integer.parseInt(chNumber);
                            channelNum("2");
                            break;

                            //3
                        case 10:
                            chNumber=chNumber+"3";
                            edt.setVisibility(View.VISIBLE);
                            edt.setText(chNumber);
                            counter=Integer.parseInt(chNumber);
                            channelNum("3");
                            break;

                            //4
                        case 11:
                            chNumber=chNumber+"4";
                            edt.setVisibility(View.VISIBLE);
                            edt.setText(chNumber);
                            counter=Integer.parseInt(chNumber);
                            channelNum("4");
                            break;

                            //5
                        case 12:
                            chNumber=chNumber+"5";
                            edt.setVisibility(View.VISIBLE);
                            edt.setText(chNumber);
                            counter=Integer.parseInt(chNumber);
                            channelNum("5");
                            break;

                            //6
                        case 13:
                            chNumber=chNumber+"6";
                            edt.setVisibility(View.VISIBLE);
                            edt.setText(chNumber);
                            counter=Integer.parseInt(chNumber);
                            channelNum("6");
                            break;

                            //7
                        case 14:
                            chNumber=chNumber+"7";
                            edt.setVisibility(View.VISIBLE);
                            edt.setText(chNumber);
                            counter=Integer.parseInt(chNumber);
                            channelNum("7");
                            break;

                            //8
                        case 15:
                            chNumber=chNumber+"8";
                            edt.setVisibility(View.VISIBLE);
                            edt.setText(chNumber);
                            counter=Integer.parseInt(chNumber);
                            channelNum("8");
                            break;

                            //9
                        case 16:
                            chNumber=chNumber+"9";
                            edt.setVisibility(View.VISIBLE);
                            edt.setText(chNumber);
                            counter=Integer.parseInt(chNumber);
                            channelNum("9");
                            break;
                    }

                }

            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(List<ArrayList<String>> strings) {
        urls=strings.get(0);
        String str=urls.get(0);

        if (str.equals("error"))
        {
            Toast.makeText(this, "فایل xml یافت نشد", Toast.LENGTH_SHORT).show();
        }
        else {
            urls = strings.get(0);
            chNames=strings.get(1);
            int len=urls.size();
            if (len>=0 && len<=10)
            edt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
            else if (len>10 && len<100)
                edt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
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
