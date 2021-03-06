package com.wangfangqi;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.gyf.barlibrary.ImmersionBar;
import com.wangfangqi.widget.TitleView;

import static com.wangfangqi.widget.TitleView.TYPE_CENTER_CUSTOM_VIEW;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TitleView titleView = findViewById(R.id.title);

        titleView.setOnChildClickListener(new TitleView.SimpleChildClickListener() {
            @Override
            public void onLeftClick(View view) {
                super.onLeftClick(view);
            }
        });
        titleView.setMenuInflater(getMenuInflater());
        titleView.setCenterView(R.layout.layout_custom_right);
        titleView.setCenterViewType(TYPE_CENTER_CUSTOM_VIEW);
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
