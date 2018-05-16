package com.termproject.route.route;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TableLayout;

import com.google.android.gms.maps.MapFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.main_tab);

        ViewPager viewPager = (ViewPager)findViewById(R.id.view_pager);

        Fragment[] arrFragments = new Fragment[3];
        arrFragments[0] = new RunningFragment();
        arrFragments[1] = new RunningFragment();
        arrFragments[2] = new SettingFragment();

        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(),arrFragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        private Fragment[] arrFragments;

        //생성자
        public MyPagerAdapter(FragmentManager fm, Fragment[] arrFragments){
            super(fm);
            this.arrFragments = arrFragments;
        }


        @Override
        public Fragment getItem(int position){
            return arrFragments[position];
        }
        @Override
        public int getCount(){
            return arrFragments.length;
        }

        //Tab의 타이틀 설정
        @Override
        public CharSequence getPageTitle(int position){
            switch (position){
                case 0:
                    return "Running";
                case 1:
                    return "Sharing";
                case 2:
                    return "Settings";
                default:
                    return "";
            }
            //return super.getPageTitle(position);
        }
    }
}