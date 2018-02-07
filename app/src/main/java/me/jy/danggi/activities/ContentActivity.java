package me.jy.danggi.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.jy.danggi.R;
import me.jy.danggi.activities.model.Memo;
import me.jy.danggi.databinding.ActivityContentBinding;

public class ContentActivity extends AppCompatActivity {

    ActivityContentBinding binding;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_content);

        Memo memo = getIntent().getExtras().getParcelable("memo");

        binding.textContent.setText(memo.getContent());
    }
}
