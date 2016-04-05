package com.hwang.listenbook;

import net.tsz.afinal.annotation.view.ViewInject;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AboutUsActivity extends BaseActivity{

	private static final String TAG = "AboutUsActivity";
	@ViewInject(id=R.id.top_back) ImageView back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_back:
			exitActivity();
			break;

		default:
			break;
		}
		super.onClick(v);
	}

}
