package net.aiweimob.listenbook.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import net.aiweimob.listenbook.R;
import net.aiweimob.listenbook.fragment.ContentFragment;
import net.aiweimob.listenbook.fragment.LeftMenuFragment;


public class MainActivity extends SlidingFragmentActivity{
    private static final String FRAGMENT_LEFT_MENU = "fragment_left_menu";
    private static final String FRAGMENT_CONTENT = "fragment_content";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        setBehindContentView(R.layout.left_menu);// 设置侧边栏
        SlidingMenu slidingMenu = getSlidingMenu();// 获取侧边栏对象
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);// 设置全屏触摸

        int width = getWindowManager().getDefaultDisplay().getWidth();// 获取屏幕宽度

        slidingMenu.setBehindOffset(width / 2);// 设置预留屏幕的宽度

        initFragment();

        initView();
    }


    /**
     * 初始化view
     */
    private void initView(){


    }

    /**
     * 初始化fragment 将fragment数据填充给布局文件
     */
    private void initFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();// 开启事务

        transaction.replace(R.id.fl_left_menu, new LeftMenuFragment(),
                FRAGMENT_LEFT_MENU);// 用fragment替换framelayout
        transaction.replace(R.id.fl_content, new ContentFragment(),
                FRAGMENT_CONTENT);

        transaction.commit();// 提交事务
    }

}
