package me.jy.danggi.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import me.jy.danggi.R;
import me.jy.danggi.databinding.ActivityWriteBinding;

public class WriteActivity extends AppCompatActivity {

    ActivityWriteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_write);
    }

    public void saveWrittenContent(View v) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
