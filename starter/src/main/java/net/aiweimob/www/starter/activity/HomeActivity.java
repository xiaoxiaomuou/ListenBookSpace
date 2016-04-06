package net.aiweimob.www.starter.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

public class HomeActivity extends Activity implements AdapterView.OnItemClickListener {

    private static final String MyPwd = "jie";

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        act = this;

        pm = act.getPackageManager();//获得了包管理器

        ivShezhi = (ImageView) findViewById(R.id.iv_sz);
        gridView = (GridView) findViewById(R.id.gridView);
        et_text_pwd = (EditText) findViewById(R.id.et_input_pwd);

        pageNames = getPackageNames();

        gridView = (GridView) findViewById(R.id.gridView);

        adapter = new MyAdapter();
        gridView.setAdapter(adapter);

        //刷新页面
        adapter.notifyDataSetChanged();

        gridView.setOnItemClickListener(this);
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
            startActivity(intent);
        }catch(Exception e){
            Toast.makeText(this, "还木有安装", Toast.LENGTH_LONG).show();
        }
    }

}




