package me.jy.danggi.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import me.jy.danggi.R;
import me.jy.danggi.fragment.WidgetDialogFragment;
import me.jy.danggi.databinding.ActivitySettingBinding;

public class SettingActivity extends AppCompatActivity {

    ActivitySettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        binding.setSetting(this);
    }
    public void onMemoRadioClick(View v ){
        WidgetDialogFragment dialogFragment = new WidgetDialogFragment();
        dialogFragment.show(getFragmentManager(), "dd");
    }




}
