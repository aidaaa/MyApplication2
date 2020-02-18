package com.example.myapplication.confg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.main.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityConfigBinding;

public class ConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelConfig viewModelConfig =new ViewModelConfig(this);
        ActivityConfigBinding binding= DataBindingUtil.setContentView(this, R.layout.activity_config);
        binding.setConfig(viewModelConfig);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(ConfigActivity.this, MainActivity.class);
        startActivity(intent);
    }

}
