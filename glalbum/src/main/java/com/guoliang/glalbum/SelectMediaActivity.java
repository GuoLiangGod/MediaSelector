package com.guoliang.glalbum;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

public class SelectMediaActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MediaListFragment videoListFragment;
    private MediaListFragment imageListFragment;
    private TabLayout tabLayout;
    private TextView tv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_media);
        // 5.0以上系统状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //白色SYSTEM_UI_FLAG_LAYOUT_STABLE、深色SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }


        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabLayout);
        tv_back = findViewById(R.id.tv_back);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        MediaSelectConfig.SelectType selectType = getIntent().getParcelableExtra(MediaSelectConfig.MEDIA_MIME_TYPE) == null
                ? MediaSelectConfig.SelectType.ALL : (MediaSelectConfig.SelectType) getIntent().getParcelableExtra(MediaSelectConfig.MEDIA_MIME_TYPE);
        boolean media_countable = getIntent().getBooleanExtra(MediaSelectConfig.MEDIA_COUNTABLE, false);
        boolean media_camera = getIntent().getBooleanExtra(MediaSelectConfig.MEDIA_CAMERA, false);
        int media_count = getIntent().getIntExtra(MediaSelectConfig.MEDIA_COUNT, 9);
        Bundle bundle = new Bundle();
        bundle.putBoolean(MediaSelectConfig.MEDIA_COUNTABLE,media_countable);
        bundle.putBoolean(MediaSelectConfig.MEDIA_CAMERA,media_camera);
        bundle.putInt(MediaSelectConfig.MEDIA_COUNT,media_count);
        List<Fragment> listFragment = new ArrayList<>();
        switch (selectType) {
            case VIDEO:
                titles=new String[]{"视频"};
                videoListFragment = MediaListFragment.newInstance(MediaSelectConfig.SelectType.VIDEO,false);
                videoListFragment.setArguments(bundle);
                listFragment.add(videoListFragment);
                break;
            case IMAGE:
                titles=new String[]{"图片"};
                imageListFragment = MediaListFragment.newInstance(MediaSelectConfig.SelectType.IMAGE,false);
                imageListFragment.setArguments(bundle);
                listFragment.add(imageListFragment);
                break;
            case ALL:
                titles=new String[]{"视频","图片"};
                videoListFragment = MediaListFragment.newInstance(MediaSelectConfig.SelectType.VIDEO,false);
                videoListFragment.setArguments(bundle);
                imageListFragment = MediaListFragment.newInstance(MediaSelectConfig.SelectType.IMAGE,false);
                imageListFragment.setArguments(bundle);
                listFragment.add(videoListFragment);
                listFragment.add(imageListFragment);
                break;
        }
        viewPager.setAdapter(new MediaPagerAdapter(getSupportFragmentManager(),listFragment));
        tabLayout.setupWithViewPager(viewPager);
    }


    private String[] titles;
    class MediaPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> listFragment;
        public MediaPagerAdapter(@NonNull FragmentManager fm, List<Fragment> listFragment) {
            super(fm);
            this.listFragment = listFragment;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return listFragment.get(position);
        }

        @Override
        public int getCount() {
            return listFragment.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
