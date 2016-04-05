package net.aiweimob.www.starter.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.view.View.OnClickListener;

import net.aiweimob.www.starter.R;
import net.aiweimob.www.starter.utils.MyUtils;

public class HomeActivity extends Activity {

    private static final String MyPwd = "jie";

    private ImageView ivShezhi;

    private GridView gridView;
    /**
     * 声明一个对自已的引用，方便在内部类中使用activity
     */
    public Activity act;

    private EditText et_text_pwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        act = this;

        ivShezhi = (ImageView) findViewById(R.id.iv_sz);
        gridView = (GridView) findViewById(R.id.gridView);
        et_text_pwd = (EditText) findViewById(R.id.et_input_pwd);
    }

    public void ivClick(View view){

        showInputPwdDialog();
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


}




