package com.wangfangqi;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.gyf.barlibrary.ImmersionBar;
import com.wangfangqi.widget.SimpleChildClickListener;
import com.wangfangqi.widget.TitleView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TitleView titleView = findViewById(R.id.title);

        titleView.setOnChildClickListener(new SimpleChildClickListener() {
            @Override
            public void onLeftClick(View view) {
                super.onLeftClick(view);
            }
        });
        titleView.setMenuInflater(getMenuInflater());
//
        ImmersionBar.with(this)
                .keyboardEnable(true)
//                .statusBarColor(R.color.colorAccent)
                .init();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
