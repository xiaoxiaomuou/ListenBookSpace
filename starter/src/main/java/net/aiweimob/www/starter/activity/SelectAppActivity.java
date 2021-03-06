package net.aiweimob.www.starter.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import net.aiweimob.www.starter.R;
import net.aiweimob.www.starter.bean.AppInfoBean;
import net.aiweimob.www.starter.utils.AppUtils;
import net.aiweimob.www.starter.utils.MyConstace;
import net.aiweimob.www.starter.utils.PrefUtils;

import java.util.ArrayList;
import java.util.List;

public class SelectAppActivity extends Activity{
    public static final String SelectBaoming = "PackageName";
    private List<AppInfoBean> allAppList;
    /**
     * 用户应用集合
     */
    private List<AppInfoBean> userAppList;
    /**
     * 系统应用集合
     */
    private List<AppInfoBean> sysAppList;
    protected SelectAppActivity ctx;

    private ListView listView;
    private CheckBox checkBox;

    /**
     * selectFinish 选择完成的按钮
     */
    private ImageView ivFinish;

    /**
     * 固定的小标题
     */
    private TextView tvSubTitle;

    /**
     * 系统设置启动按钮
     * @param savedInstanceState
     */
    private ImageView ivSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_app);

        listView = (ListView) findViewById(R.id.listView);

        tvSubTitle = (TextView) findViewById(R.id.tv_sub_title);

        ivFinish = (ImageView) findViewById(R.id.iv_finish);

        ivSystem = (ImageView) findViewById(R.id.ivSystem);

        ctx = this;

        initTitle();

        proDlg  = new ProgressDialog(this);
        proDlg.setMessage("努力获取中....请稍候...");

        fillData();

        regListener();

/*        // 注册 卸载应用的广播
        uninstallReceiver = new UninstallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED); // 删除应用时，系统广播的action
        // 下面一句，就只能记下来了，否则是收不到广播的。
        filter.addDataScheme("package");
        registerReceiver(uninstallReceiver, filter);*/
        /**
         * 完成按钮的点击事件
         */
        ivFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("ivFinish","ivFinish被点击了");
                /**
                 * 先清空原本的sp
                 */
                PrefUtils.setString(ctx,MyConstace.key_is_select_app,"");


                /**
                 * 保存选择的程序包名到sp
                 */
                saveToSp();

                /**
                 * 跳转到HomeActivity
                 */

                Intent intent = new Intent(ctx,HomeActivity.class);
                startActivity(intent);
            }
        });

        ivSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("ivFinish","ivSystem被点击了");
                Intent intent = new Intent(Settings.ACTION_SETTINGS);//系统设置界面
                startActivity(intent);
            }
        });

    }

    private PopupWindow popWindow;

    /**
     * 点击条目的位置
     */
    private int clickedPosition;
    //存放被点击应用的包名 的集合
    List<String> listBaoming = new ArrayList<String>();

    private void regListener() {

        // 设置条目的点击事件
        listView.setOnItemClickListener(new AbsListView.OnItemClickListener() {

            @Override
            /**
             * 点击某个条目时，调用此方法
             * @param position 点击的条目的下标
             * @param view 当前点击的条目的view,也是getView方法的返回值
             */
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                AppInfoBean bean = (AppInfoBean) listView.getItemAtPosition(position);
                bean.isSelect = !bean.isSelect;

                if(bean.isSelect){
                    Log.i("Select",bean.packageName);
                    listBaoming.add(bean.packageName);
                }else{
                    listBaoming.remove(bean.packageName);
                    Log.i("Quxiao",bean.packageName);
                }

                //刷新页面
                adapter.notifyDataSetChanged();

            }
        });


        // 设置滑动的监听
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            /**
             *  当滑动状态，发生改变时，调用此方法
             */
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            /**
             * 当listView在滑动时，不断调用此方法
             */
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                // 隐藏窗体
                dimissPopupWindow();

                if(userAppList == null){
                    return ;
                }

                int firstPosition = listView.getFirstVisiblePosition(); // 当前可见的第一个条目的位置

                // 判断可见的第一个条目是用户应用，还是系统应用
                if(firstPosition<userAppList.size()){ // 用户应用
                    tvSubTitle.setText("用户应用");
                }else{
                    tvSubTitle.setText("系统应用");
                }
            }
        });

    }

    /**
     * 把被选中的程序的包名 添加到sp文件中
     */

    private void saveToSp() {
        String str = "";

        for(int i = 0;i < listBaoming.size(); i ++){
            str = str + listBaoming.get(i);
            str += "$";
            Log.i("For",str);
        }
        Log.i("SaveTo",str);
        PrefUtils.setString(ctx, MyConstace.key_is_select_app, str);
    }

    private ProgressDialog proDlg;

    private void fillData() {

        new Thread(){
            public void run() {
                allAppList = AppUtils.getAllAppInfo(ctx);
                // 对总的应用信息，进行分组
                userAppList = new ArrayList<AppInfoBean>();
                sysAppList = new ArrayList<AppInfoBean>();

                for(AppInfoBean appInfo:allAppList){
                    if(appInfo.isSys){
                        sysAppList.add(appInfo);
                    }else{
                        userAppList.add(appInfo);
                    }
                }

                handler.sendEmptyMessage(FLUSH_UI);
            };
        }.start();
    }

    private final int FLUSH_UI = 100;

	/*
	 * 实现 思路：
	 * 1- listView 基本展示
	 * 2- 想办法分组展示
	 * 3- 添加小标题
	 * 4- 添加顶部固定标题
	 */

    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case FLUSH_UI: // 刷新页面
                    // 关闭进度框
                    proDlg.dismiss();

                    // 显示listview
                    adapter = new MyAdapter();
                    listView.setAdapter(adapter);

                    break;
            }
        };
    };

    /**
     * listView 定理一：
     * 	在getView 方法中，如果复用了convertView 在为子View赋值时，有 if 必须 写 else
     *
     */


    private MyAdapter adapter;

    private class MyAdapter extends BaseAdapter {
        @Override
        /**
         * 返回listview 的条目的个数
         */
        public int getCount() {
            return userAppList.size()+sysAppList.size();
        }

        @Override
        /**
         *  根据位置获得对应的数据bean
         */
        public Object getItem(int position) {
            AppInfoBean infoBean ;//= allAppList.get(position);

            // 当position 小于用户集合的 size时，应用信息，从用户集合中获得
            if(position<userAppList.size()){
                infoBean = userAppList.get(position);
            }else{

                // 应用信息，就从系统集合中获取
                infoBean = sysAppList.get(position - userAppList.size());
            }

            return infoBean;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        /**
         * 根据position 获得把定位置的view 对象
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder vh;

            if(convertView == null){
                view = getLayoutInflater().inflate(R.layout.list_item_app_manager, null);
                vh = new ViewHolder();

                // 找到子view
                ImageView icon = (ImageView) view.findViewById(R.id.iv_icon_list_item);
                TextView name = (TextView) view.findViewById(R.id.tv_name_list_item);
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.check_box);

                TextView subTitle = (TextView) view.findViewById(R.id.tv_sub_title_list_item);
                // 默认隐藏
                subTitle.setVisibility(View.GONE);

                // 将子view打包
                vh.icon = icon;
                vh.name = name;
                vh.checkBox = checkBox;
                vh.subTitle = subTitle;

                // 背包
                view.setTag(vh);

            }else{
                view = convertView;
                vh = (ViewHolder) view.getTag();
            }

            // 显示数据
            AppInfoBean infoBean  = (AppInfoBean) getItem(position);

            vh.icon.setBackgroundDrawable(infoBean.appIcon);

            vh.name.setText(infoBean.appName);
            vh.checkBox.setChecked(infoBean.isSelect);


            // 处理小标题：
            // 将用户的第一个，和系统的第一个条目，显示小标题
            if(position == 0){
                // 用户的第一个条目
                vh.subTitle.setVisibility(View.VISIBLE);
                vh.subTitle.setText("用户应用");
            }else if(position == userAppList.size()){
                // 第一个系统应用条目
                vh.subTitle.setVisibility(View.VISIBLE);
                vh.subTitle.setText("系统应用");
            }else{
                //
                vh.subTitle.setVisibility(View.GONE);
            }

            return view;
        }

    }

    private class ViewHolder{

        public TextView subTitle;

        public TextView name;
        public ImageView icon;
        public CheckBox checkBox;

    }


    private void initTitle() {

    }


    private void dimissPopupWindow() {
        if(popWindow!=null && popWindow.isShowing()){
            popWindow.dismiss();
            popWindow = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 隐藏并销毁popWindow
        dimissPopupWindow();

    }

}
