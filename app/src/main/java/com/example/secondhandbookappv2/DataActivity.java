package com.example.secondhandbookappv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class DataActivity extends MyActivity {

    private static final int REQUESTCODE = 2;
    private static final String TAG = DataActivity.class.getSimpleName();
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private List<Fragment> fragments;
    private TabLayoutMediator mediator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        findView();
        initFragment();
        setViewPage();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediator.detach();
        viewPager.unregisterOnPageChangeCallback(changeCallback);
        Log.d(TAG, "onDestroy: ");
    }

    private void findView() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPage);
    }

    private void initFragment() {
        fragments = new ArrayList<>();
        fragments.add(DataFragment_1.getInstance());
        fragments.add(DataFragment_2.getInstance());
    }

    private void setViewPage() {
        final String[] tabs = new String[]{"Step 1", "Step 2"};
        viewPager.setOffscreenPageLimit(ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT);
        viewPager.setAdapter(new FragmentStateAdapter(getSupportFragmentManager(), getLifecycle()){
            @Override
            public int getItemCount() {
                return 2;
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragments.get(position);
            }
        });
        viewPager.registerOnPageChangeCallback(changeCallback);

        mediator = new TabLayoutMediator(tabLayout, viewPager, false, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                TextView tv_tab = new TextView(DataActivity.this);
                tv_tab.setGravity(Gravity.CENTER);
                tv_tab.setText(tabs[position]);
                tab.setCustomView(tv_tab);
            }
        });
        mediator.attach();
    }

    private void getPermission() {
        List<String> permissionList = new ArrayList<>();
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int internetPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int ANSPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);
        if (writeExternalStoragePermission == PackageManager.PERMISSION_DENIED)
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (readExternalStoragePermission == PackageManager.PERMISSION_DENIED)
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (cameraPermission == PackageManager.PERMISSION_DENIED)
            permissionList.add(Manifest.permission.CAMERA);
        if (internetPermission == PackageManager.PERMISSION_DENIED)
            permissionList.add(Manifest.permission.INTERNET);
        if (ANSPermission == PackageManager.PERMISSION_DENIED)
            permissionList.add(Manifest.permission.ACCESS_NETWORK_STATE);
        if (!permissionList.isEmpty()){
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[0]), REQUESTCODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUESTCODE){
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    new AlertDialog.Builder(this)
                            .setTitle("提醒")
                            .setMessage("因有權限不允許，可能會導致程式無法執行，故需重新詢問權限")
                            .setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).show();
                }
            }
        }
    }

    private ViewPager2.OnPageChangeCallback changeCallback = new ViewPager2.OnPageChangeCallback() {

        @Override
        public void onPageSelected(int position) {
            //可以来设置选中时tab的大小
            int tabCount = tabLayout.getTabCount();
            for (int i = 0; i < tabCount; i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                TextView tabView = (TextView) tab.getCustomView();
                if (tab.getPosition() == position) {
                    int activeSize = 16;
                    tabView.setTextSize(activeSize);
                    tabView.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    int normalSize = 12;
                    tabView.setTextSize(normalSize);
                    tabView.setTypeface(Typeface.DEFAULT);
                }
            }
        }
    };

}