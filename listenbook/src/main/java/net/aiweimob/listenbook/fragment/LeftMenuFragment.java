package net.aiweimob.listenbook.fragment;


import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import net.aiweimob.listenbook.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 侧滑栏菜单的fragment
 */
public class LeftMenuFragment extends BaseFragment {

    private String[] itemName = {"书籍","报纸","期刊","收藏","关于"};

    private int[] imageids = { R.mipmap.book, R.mipmap.baozhi,
            R.mipmap.qikan, R.mipmap.shoucang,R.mipmap.guanyu };

    private ListView listView;
    //listview当前位置
    public static int mPosition;

    private ListView lvList;

    @Override
    public View initViews() {

        View view = View.inflate(mActivity, R.layout.fragment_left_menu, null);

        lvList = (ListView) view.findViewById(R.id.lv_list);

        initMenuData();
        lvList.setAdapter(simplead);
        return view;
    }

    /**
     * 初始化菜单
     */
    SimpleAdapter simplead;
    private void initMenuData() {
        List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();

        for(int i = 0; i < itemName.length; i++) {
            Map<String, Object> listem = new HashMap<String, Object>();
            listem.put("itemImage", imageids[i]);
            listem.put("itemName", itemName[i]);
            listems.add(listem);
        }
        /**
         * 侧边栏的数据适配器
         */
        simplead = new SimpleAdapter(getActivity(), listems,
                R.layout.leftmenu_item, new String[] { "itemImage", "itemName",},
                new int[] {R.id.menu_image,R.id.menu_name});

    }




}
