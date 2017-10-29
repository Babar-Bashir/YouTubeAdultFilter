package com.babar.youtubefilter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.suke.widget.SwitchButton;

import custom.com.babar.youtubefilter.R;

/***********************************
 * Created by Babar on 10/27/2017.  *
 ***********************************/
public class MainActivity extends AppCompatActivity {
    private SwitchButton button_enable_acc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Util.prepareBadKeywords(this);
        button_enable_acc = (SwitchButton) findViewById(R.id.button_enable_acc);
        if (Util.isAccessibilityServiceEnabled(this)) {
            button_enable_acc.setChecked(true);
        }
        button_enable_acc.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivityForResult(intent, 0);
                }
            }
        });
    }
}
