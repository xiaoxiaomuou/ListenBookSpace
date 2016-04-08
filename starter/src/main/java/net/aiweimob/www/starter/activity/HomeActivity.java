package net.aiweimob.www.starter.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.aiweimob.www.starter.R;
import net.aiweimob.www.starter.utils.MyConstace;
import net.aiweimob.www.starter.utils.MyUtils;
import net.aiweimob.www.starter.utils.PrefUtils;
import net.aiweimob.www.starter.view.DesktopLayout;

public class HomeActivity extends Activity implements AdapterView.OnItemClickListener {

    /**
     * 高优先级的窗体控件
     */
    private WindowManager mWindowManager;

    private WindowManager.LayoutParams mLayoutParams;

    private DesktopLayout mDesktopLayout;

    private long starttime;

    /**
     * 悬浮窗是否显示着
     */
    private Boolean isShow;

    /**
     * 管理员口令
     */
    private static final String MyPwd = "jie";

    /**
     * 是否是拖动，
     * 如果按下，移动，距离小于10个象素，我们认为是点击的动作，按点击的逻辑处理
     * 如果超过10个像素，我们认为发生了拖动，按拖动的逻辑处理
     */
    private boolean isDrop;

    /**
     * 创建悬浮窗体
     */
    private void createDesktopLayout() {

            Log.i("createDesk","act实例化了"+act);
            mDesktopLayout = DesktopLayout.getInstance(this);


        /**
         * 窗体的点击事件
         */
        mDesktopLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDrop){
                    Log.i("mDesktopLayout", "onclick不执行");
                    return;
                }
                Log.i("mDesktopLayout", "onclick执行了");
                restartMyapp();
            }
        });


        // 读取保存的数据，初始化位置
        mLayoutParams.x= sp.getInt("params_x", 0);
        mLayoutParams.y= sp.getInt("params_y", 0);

        mDesktopLayout.setOnTouchListener(new View.OnTouchListener() {

            /**
             * down 事件的X坐标
             */
            private int downX;
            /**
             * 上一个事件中的X坐标
             */
            private int lastX;
            /**
             * 上一个事件中的Y坐标
             */
            private int lastY;
            @Override
            /**
             * mDesktopLayout 时，不断调用此方法
             * 如果消费了事件，必须返回true
             */
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();

                        isDrop = false; // 按下时，肯定没有拖动

                        break;
                    case MotionEvent.ACTION_UP:

                        // 抬起手指时， 保存控件当前的位置
                        sp.edit().putInt("params_x", mLayoutParams.x).commit();
                        sp.edit().putInt("params_y", mLayoutParams.y).commit();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        System.out.println("ACTION_MOVE========");
                        // 二：移动手指时，记录移动时的坐标点moveX,moveY求得手指在屏幕移动的距离

                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();

                        int disX = moveX - lastX; // 此处应该求的的是二个相领事件之间的距离
                        int disY = moveY - lastY;

                        // 移动距离超过15象素，认为发生了拖动
                        if(Math.abs(disX)> 15 || Math.abs(disY)>15){
                            Log.i("Move",disX+"--大于15了--");
                            isDrop = true;
                        }

                        // 三: 让控件同样移动disX,和disY的距离

                        mLayoutParams.x+=disX;
                        Log.i("Paramsx",mLayoutParams.x+"");
                        mLayoutParams.y+=disY;

                        mWindowManager.updateViewLayout(mDesktopLayout, mLayoutParams); // 更新view在屏幕的位置
                        Log.i("update",mLayoutParams.x+"----"+mLayoutParams.y);

                        // 四，让downX downY 改变为当前的moveX,moveY,以便于获得二个相领事件之间的距离
                        lastX = moveX;
                        lastY = moveY;

                        break;
                }
                return false;
            }
        });
    }

    /**
     * 设置WindowManager
     */
    private void createWindowManager() {
        // 取得系统窗体
        mWindowManager = (WindowManager) getApplicationContext()
                .getSystemService(WINDOW_SERVICE);

        // 窗体的布局样式
        mLayoutParams = new WindowManager.LayoutParams();

/*        //透明效果
        mLayoutParams.format = PixelFormat.RGBA_8888; */

        mLayoutParams.alpha = 0.5f;

        // 设置窗体显示类型——TYPE_SYSTEM_ALERT(系统提示)
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

        // 设置窗体焦点及触摸：
        // FLAG_NOT_FOCUSABLE(不能获得按键输入焦点)
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

        // 设置显示的模式
        mLayoutParams.format = PixelFormat.RGBA_8888;

        // 设置对齐的方法
        mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;

        // 设置窗体宽度和高度
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

    }

    /**
     * 显示DesktopLayout
     */
    private void showDesk() {

            mWindowManager.addView(mDesktopLayout, mLayoutParams);

    }



    /**
     * 重启我的应用
     */
    private void restartMyapp() {
        closeDesk();
        startAPP("net.aiweimob.www.starter");//com.tencent.qqmusic
    }

    private void closeDesk() {
        Log.i("closeDesk","进来关闭"+mDesktopLayout+"窗体了");
        mWindowManager.removeView(mDesktopLayout);

    }

    /**
     * 包的管理器 PackageManager;
     */
    PackageManager pm ;

    private ImageView ivShezhi;

    private GridView gridView;
    /**
     * 声明一个对自已的引用，方便在内部类中使用activity
     */
    public Activity act;

    /**
     * 包名数组
     */
    String[] pageNames = new String[]{""};

    private EditText et_text_pwd;
    private Button btnXfc;

    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("config", MODE_PRIVATE);

        createWindowManager();
        createDesktopLayout();
        act = this;

        Log.i("onResume","oncreate执行了");

        pm = act.getPackageManager();//获得了包管理器

        ivShezhi = (ImageView) findViewById(R.id.iv_sz);
        gridView = (GridView) findViewById(R.id.gridView);
        et_text_pwd = (EditText) findViewById(R.id.et_input_pwd);

        pageNames = getPackageNames();

        gridView = (GridView) findViewById(R.id.gridView);

        adapter = new MyAdapter();
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(this);

        showDesk();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 通知gridView 数据发生变化了，就会将屏幕显示的每个条目都刷新一次，即，调用getView方法

        Log.i("onResume","onResume执行了");
        adapter.notifyDataSetChanged();
    }


    public void ivClick(View view){

        showInputPwdDialog();
    }

    /**
     * 获取应用图标
     */
    public Drawable getAppIcon(String packname){
        try {
            ApplicationInfo info = pm.getApplicationInfo(packname,0);
            return info.loadIcon(pm);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    /*
     * 获取程序的名字
     */
    public String getAppName(String packname){
        try {
            ApplicationInfo info = pm.getApplicationInfo(packname, 0);
            return info.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    /**
     * 响应gridView 条目点击事件
     *
     * @param position 点击的条目的下标
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /**
         * 得到点击的应用的包名
         */
        String packname = pageNames[position];
        Log.i("position",packname+position);

        startAPP(packname);
    }

    private MyAdapter adapter;

    private class MyAdapter extends BaseAdapter{

        @Override
        /**
         * 告诉系统，有多少个条目
         */
        public int getCount() {
            int items = pageNames.length;
            Log.i("getCount",items+"");
            return items;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        /**
         * 根据 position 返回一个 对应的条目的view
         * 该方法最少会调用 条目个数 次
         */
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = getLayoutInflater().inflate(R.layout.grid_item_home, null);

            ImageView icon = (ImageView) view.findViewById(R.id.iv_grid_item);
            TextView name = (TextView) view.findViewById(R.id.app_name_grid_item);
            /**
             * 应用程序的包名
             */
            String packname = pageNames[position];

            Log.i("packname",packname);
            /**
             * 设置图标 和 应用名称
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                icon.setBackground(getAppIcon(packname));
            }
            name.setText(getAppName(packname));

            return view;
        }
    }

    private AlertDialog dialog;
    private void showInputPwdDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(act);

        View view = getLayoutInflater().inflate(R.layout.dialog_input_pwd, null);

        final EditText et_text_pwd = (EditText) view.findViewById(R.id.et_input_pwd);

        Button btnOk = (Button) view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

        btnOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // 判断输入的密码是否正确
                String inputPwd = et_text_pwd.getText().toString();
                if (TextUtils.isEmpty(inputPwd)) {
                    MyUtils.showToast(act, "请输入密码");
                    return;
                }

                if (MyPwd.equals(inputPwd)) {
//					MyUtils.showToast(act, "密码正确");
                    // 进入选择应用的页面
                    Intent intent = new Intent(act, SelectAppActivity.class);
                    startActivity(intent);

                } else {
                    MyUtils.showToast(act, "密码不正确,请重新输入");
                    return;
                }

                // 关闭对话框
                dialog.dismiss();

            }
        });
        // 取消按钮的点击事件
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog = adb.create();
        dialog.setView(view, 0, 0, 0, 0); // 设置view 和左上右下的间距
        dialog.show();
    }

    /**
     * 得到 程序包名的数组
     */
    private String[] getPackageNames(){
        String[] names =  null;
        String packageName = PrefUtils.getString(act, MyConstace.key_is_select_app, "竟然空");
        Log.i("PackAge",packageName);

        names = packageName.split("\\$");

        Log.i("Packs", names.length + "");
        return names;
    }

    /**
     *通过包名 启动一个app
     */
    public void startAPP(String appPackageName){
        try{

            Intent intent = pm.getLaunchIntentForPackage(appPackageName);
            Log.i("startAPP",appPackageName+"准备启动");
            startActivity(intent);
        }catch(Exception e){
            Toast.makeText(this, "还木有安装", Toast.LENGTH_LONG).show();
        }
    }

}




