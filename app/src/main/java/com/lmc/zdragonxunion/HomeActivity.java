package com.lmc.zdragonxunion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.lmc.zdragonxunion.fragment.CourseFragment;
import com.lmc.zdragonxunion.fragment.DataFragment;
import com.lmc.zdragonxunion.fragment.HomeFragment;
import com.lmc.zdragonxunion.fragment.MyFragment;
import com.lmc.zdragonxunion.fragment.VipFragment;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private FrameLayout mFramelayout;
    private TabLayout mTablayout;
    private HomeFragment homeFragment;
    private CourseFragment courseFragment;
    private VipFragment vipFragment;
    private DataFragment dataFragment;
    private MyFragment myFragment;

    private Fragment lastFragment;
    private FragmentManager supportFragmentManager;

    public static final int HOME=0;
    public static final int COURSE=1;
    public static final int VIP=2;
    public static final int DATA=3;
    public static final int MY=4;
    int type ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
    }

    private void initView() {
        mFramelayout = findViewById(R.id.framelayout);
        mTablayout = (TabLayout) findViewById(R.id.tablayout);

        final ArrayList<Fragment> fragments = new ArrayList<>();
        homeFragment = new HomeFragment();
        courseFragment = new CourseFragment();
        vipFragment = new VipFragment();
        dataFragment = new DataFragment();
        myFragment = new MyFragment();
        fragments.add(homeFragment);
        fragments.add(courseFragment);
        fragments.add(vipFragment);
        fragments.add(dataFragment);
        fragments.add(myFragment);

        supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.framelayout, homeFragment)
                .add(R.id.framelayout, courseFragment)
                .add(R.id.framelayout, vipFragment)
                .add(R.id.framelayout, dataFragment)
                .add(R.id.framelayout, myFragment)
                .hide(courseFragment)
                .hide(vipFragment)
                .hide(dataFragment)
                .hide(myFragment)
                .commit();
        lastFragment = homeFragment;

        mTablayout.addTab(mTablayout.newTab(),0);
        mTablayout.addTab(mTablayout.newTab(),1);
        mTablayout.addTab(mTablayout.newTab(),2);
        mTablayout.addTab(mTablayout.newTab(),3);
        mTablayout.addTab(mTablayout.newTab(),4);
        View home = getView(R.drawable.home_select, "主页");
        View home1 = getView(R.drawable.course_select, "课程");
        View home2 = getView(R.drawable.vip_select, "VIP");
        View home3 = getView(R.drawable.data_select, "资料");
        View home4 = getView(R.drawable.my_select, "我的");
        mTablayout.getTabAt(0).setCustomView(home);
        mTablayout.getTabAt(1).setCustomView(home1);
        mTablayout.getTabAt(2).setCustomView(home2);
        mTablayout.getTabAt(3).setCustomView(home3);
        mTablayout.getTabAt(4).setCustomView(home4);

        mTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        type = HOME;
                        break;
                    case 1:
                        type = COURSE;
                        break;
                    case 2:
                        type = VIP;
                        break;
                    case 3:
                        type = DATA;
                        break;
                    case 4:
                        type = MY;
                        break;
                }
                switchFragment();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private Fragment getCurrentFragment(){
        switch (type){
            case HOME:
                if(homeFragment==null){
                    homeFragment = new HomeFragment();
                }
                return homeFragment;
            case COURSE:
                if(courseFragment==null){
                    courseFragment = new CourseFragment();
                }
                return courseFragment;
            case VIP:
                if(vipFragment==null){
                    vipFragment = new VipFragment();
                }
                return vipFragment;
            case DATA:
                if(dataFragment==null){
                    dataFragment = new DataFragment();
                }
                return dataFragment;
            case MY:
                if(myFragment==null){
                    myFragment = new MyFragment();
                }
                return myFragment;
        }
        return null;
    }

    private void switchFragment(){
        Fragment currentFragment = getCurrentFragment();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.show(currentFragment).hide(lastFragment).commit();
        lastFragment = currentFragment;
    }
    private View getView(int select, String text) {
        View root = LayoutInflater.from(HomeActivity.this).inflate(R.layout.tab_item, null);
        ImageView img = root.findViewById(R.id.img);
        TextView title = root.findViewById(R.id.title);
        title.setText(text);
        img.setImageResource(select);
        return root;
    }
}