package net.aiweimob.www.starter.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import net.aiweimob.www.starter.R;
import net.aiweimob.www.starter.bean.AppInfoBean;
import net.aiweimob.www.starter.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

public class SelectAppActivity extends Activity{
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

    /**
     * 固定的小标题
     */
    private TextView tvSubTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_app);

        listView = (ListView) findViewById(R.id.listView);

        tvSubTitle = (TextView) findViewById(R.id.tv_sub_title);

        ctx = this;

        initTitle();

        proDlg  = new ProgressDialog(this);
        proDlg.setMessage("努力获取Ing....请稍候...");

        fillData();

        regListener();

/*        // 注册 卸载应用的广播
        uninstallReceiver = new UninstallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED); // 删除应用时，系统广播的action
        // 下面一句，就只能记下来了，否则是收不到广播的。
        filter.addDataScheme("package");
        registerReceiver(uninstallReceiver, filter);*/

    }

    private PopupWindow popWindow;

    /**
     * 点击条目的位置
     */
    private int clickedPosition;

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

//				Object item = listView.getItemAtPosition(position);
//				System.out.println("item:"+item);

                // 隐藏并销毁之前的popupWindow
                dimissPopupWindow();

                clickedPosition = position;


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

    private ProgressDialog proDlg;

    private void fillData() {
      //  proDlg.show();

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

                TextView location = (TextView) view.findViewById(R.id.tv_location_list_item);
                TextView subTitle = (TextView) view.findViewById(R.id.tv_sub_title_list_item);
                // 默认隐藏
                subTitle.setVisibility(View.GONE);

                // 将子view打包
                vh.icon = icon;
                vh.name = name;

                vh.location = location;
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


            // 设置位置

            if(infoBean.isInSd){
                vh.location.setText("在SD卡中");
            }else{
                vh.location.setText("内部存储");
            }

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
        public TextView location;
        public TextView size;
        public TextView name;
        public ImageView icon;

    }


    private void initTitle() {

//		TextView tvSpaceSd = (TextView) findViewById(R.id.tv_space_sd);
//		long freeSpaceSd = Environment.getExternalStorageDirectory().getFreeSpace();
//		String freeSpaceSdStr = Formatter.formatFileSize(this, freeSpaceSd);
//		tvSpaceSd.setText("SD卡空间:"+freeSpaceSdStr);
//
//		// 内部存储
//		TextView tvSpaceInner = (TextView) findViewById(R.id.tv_space_inner);
//		long freeSpaceInner = Environment.getDataDirectory().getFreeSpace();
//		String freeSpaceInnerStr = Formatter.formatFileSize(this, freeSpaceInner);
//		tvSpaceInner.setText("内部存储:"+freeSpaceInnerStr);

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
