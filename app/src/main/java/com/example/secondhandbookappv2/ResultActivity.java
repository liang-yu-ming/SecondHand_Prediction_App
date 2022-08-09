package com.example.secondhandbookappv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends MyActivity {

    private static final String TAG = ResultActivity.class.getSimpleName();
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private List<Fragment> fragments;
    private TabLayoutMediator mediator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);


        findView();
        initFragment();
        setViewPage();
    }

    private void findView() {
        tabLayout = findViewById(R.id.tabLayout2);
        viewPager = findViewById(R.id.viewPage2);
    }

    private void initFragment() {
        String time = getIntent().getStringExtra("time");
        fragments = new ArrayList<>();
        Bundle bundle = new Bundle();
        bundle.putString("time", time);
        ResultFragment_1 fragment1 = ResultFragment_1.getInstance();
        ResultFragment_2 fragment2 = ResultFragment_2.getInstance();
        fragment1.setArguments(bundle);
        fragment2.setArguments(bundle);
        fragments.add(fragment1);
        fragments.add(fragment2);
    }

    private void setViewPage() {
        final String[] tabs = new String[]{"預測結果", "圖片展示"};
        viewPager.setAdapter(new FragmentStateAdapter(this){
            @Override
            public int getItemCount() {
                return 2;
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                Fragment fragment = fragments.get(position);
                return fragment;
            }
        });
        //viewPager 页面切换监听
        viewPager.registerOnPageChangeCallback(changeCallback);

        mediator = new TabLayoutMediator(tabLayout, viewPager, false, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                TextView tv_tab = new TextView(ResultActivity.this);
                tv_tab.setGravity(Gravity.CENTER);
                tv_tab.setText(tabs[position]);
                tab.setCustomView(tv_tab);
            }
        });
        //要执行这一句才是真正将两者绑定起来
        mediator.attach();
    }

    private ViewPager2.OnPageChangeCallback changeCallback = new ViewPager2.OnPageChangeCallback() {
        private int activeSize = 16;
        private int normalSize = 12;

        @Override
        public void onPageSelected(int position) {
            //可以来设置选中时tab的大小
            int tabCount = tabLayout.getTabCount();
            for (int i = 0; i < tabCount; i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                TextView tabView = (TextView) tab.getCustomView();
                if (tab.getPosition() == position) {
                    tabView.setTextSize(activeSize);
                    tabView.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    tabView.setTextSize(normalSize);
                    tabView.setTypeface(Typeface.DEFAULT);
                }
            }
        }
    };
}