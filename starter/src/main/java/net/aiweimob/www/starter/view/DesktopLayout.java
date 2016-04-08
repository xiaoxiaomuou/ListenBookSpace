package net.aiweimob.www.starter.view;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.aiweimob.www.starter.R;

/**
 * Created by Administrator on 2016/4/7.
 */
public class DesktopLayout extends LinearLayout{
    private static DesktopLayout sInstance;
    private Context mContext;
    private DesktopLayout(Context context) {
        super(context);
        this.mContext = context;
        setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams mLayoutParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        setLayoutParams(mLayoutParams);

        // 显示的ICON
        ImageView mImageView = new ImageView(context);
        mImageView.setImageResource(R.mipmap.logo1);
        addView(mImageView, mLayoutParams);

        // 显示的文字
        TextView mTextView = new TextView(context);
//        mTextView.setText("...");
        mTextView.setTextColor(Color.rgb(255, 236, 139));
        mTextView.setTextSize(30);
        mTextView.setGravity(Gravity.CENTER);
        addView(mTextView, mLayoutParams);

    }

    // 注意这是private 只供内部调用
    public static synchronized DesktopLayout getInstance(Context context)
    {
        if (sInstance == null)
        {
            sInstance = new DesktopLayout(context.getApplicationContext());
        }
        return sInstance;
    }
}
