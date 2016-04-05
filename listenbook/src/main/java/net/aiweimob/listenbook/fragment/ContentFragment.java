package net.aiweimob.listenbook.fragment;


import android.view.View;

import net.aiweimob.listenbook.R;

/**
 * 主内容 切换页面的fragment
 */
public class ContentFragment extends  BaseFragment {


    @Override
    public View initViews() {
        View view = View.inflate(mActivity,R.layout.fragment_content,null);
        return view;
    }

}
