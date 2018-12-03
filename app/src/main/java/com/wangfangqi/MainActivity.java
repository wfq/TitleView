package com.wangfangqi;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.gyf.barlibrary.ImmersionBar;
import com.wangfangqi.widget.TitleView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuItemImpl;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TitleView titleView = findViewById(R.id.title);
//        ImmersionBar.with(this)
//                .titleBar(titleView).init();
//        titleView.setMenuInflater(getMenuInflater());
//
        ImmersionBar.with(this)
//                .statusBarColor(R.color.colorAccent)
                .init();

        titleView.setOnChildClickListener(new TitleView.OnChildClickListener() {
            @Override
            public void onLeftClick() {

            }

            @Override
            public void onRightClick() {
                titleView.setImmersion(false);
            }

            @Override
            public void onTitleClick() {
titleView.setImmersion(true);
            }

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
