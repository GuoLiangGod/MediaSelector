package com.guoliang.glalbum;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class SelectMediaActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MediaListFragment videoListFragment;
    private MediaListFragment imageListFragment;
    private RadioGroup rgMediaType;
    private TextView tv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_media);
        viewPager = findViewById(R.id.view_pager);
        rgMediaType = findViewById(R.id.rg_media_type);
        tv_back = findViewById(R.id.tv_back);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        MediaSelectConfig.SelectType selectType = getIntent().getSerializableExtra(MediaSelectConfig.MEDIA_MIME_TYPE) == null
                ? MediaSelectConfig.SelectType.ALL : (MediaSelectConfig.SelectType) getIntent().getSerializableExtra(MediaSelectConfig.MEDIA_MIME_TYPE);
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
                videoListFragment = new MediaListFragment(MediaSelectConfig.SelectType.VIDEO,false);
                videoListFragment.setArguments(bundle);
                listFragment.add(videoListFragment);
                break;
            case IMAGE:
                imageListFragment = new MediaListFragment(MediaSelectConfig.SelectType.IMAGE,false);
                imageListFragment.setArguments(bundle);
                listFragment.add(imageListFragment);
                break;
            case ALL:
                videoListFragment = new MediaListFragment(MediaSelectConfig.SelectType.VIDEO,false);
                videoListFragment.setArguments(bundle);
                imageListFragment = new MediaListFragment(MediaSelectConfig.SelectType.IMAGE,false);
                imageListFragment.setArguments(bundle);
                listFragment.add(videoListFragment);
                listFragment.add(imageListFragment);
                break;
        }
        viewPager.setAdapter(new MediaPagerAdapter(getSupportFragmentManager(),listFragment));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((RadioButton)rgMediaType.getChildAt(position)).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        rgMediaType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_video) {
                    viewPager.setCurrentItem(0);
                } else if (checkedId == R.id.rb_photo) {
                    viewPager.setCurrentItem(1);
                }
            }
        });
    }


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
    }
}
