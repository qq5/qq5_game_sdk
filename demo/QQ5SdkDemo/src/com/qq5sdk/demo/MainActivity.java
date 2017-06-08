package com.qq5sdk.demo;

import java.util.Locale;

import com.qq5sdk.api.EnterGameCallBack;
import com.qq5sdk.api.ExitCallBack;
import com.qq5sdk.api.LoginCallBack;
import com.qq5sdk.api.PayCallBack;
import com.qq5sdk.api.QQ5Sdk;
import com.qq5sdk.api.SplashDismissCallBack;
import com.qq5sdk.api.SwitchAccountCallBack;
import com.qq5sdk.api.UpdateLevelCallBack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String SERVER_ID = "13";
    private static final String SERVER_NAME = "江苏一区";
    private static final String ROLE_ID = "2";
    private static final String ROLE_LEVEL = "122";
    private static final String ROLE_NAME = "变形金刚";

    private TextView mTextView;
    private Button mLoginBtn;
    private Button mEnterGameBtn;
    private Button mUpdateLevelBtn;
    private Button mSwitchAccountBtn;
    private Button mPayBtn;
    private CheckBox mCheckBox;

    private boolean mIsPortrait = true;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text_result);
        mLoginBtn = (Button) findViewById(R.id.login_button);
        mEnterGameBtn = (Button) findViewById(R.id.enter_game_button);
        mUpdateLevelBtn = (Button) findViewById(R.id.update_level_button);
        mSwitchAccountBtn = (Button) findViewById(R.id.switch_account_button);
        mPayBtn = (Button) findViewById(R.id.pay_button);
        mCheckBox = (CheckBox) findViewById(R.id.checkbox);

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences activityPreferences = getSharedPreferences(
                        "sdk_demo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = activityPreferences.edit();
                editor.putBoolean("splash_visibility", isChecked);
                editor.commit();
            }
        });

        SharedPreferences setPreferences = getSharedPreferences(
                "sdk_demo", Context.MODE_PRIVATE);
        Boolean result = setPreferences.getBoolean("splash_visibility", true);

        mCheckBox.setChecked(result);
        
        /*
         *初始化接口
         * @param activity   当前游戏的Activity
         * @param orientation    横屏 HORIZONTAL 竖屏 VERTICAL
         * @param appId    在QQ5游戏中心申请的appid
         * @param appKey   在QQ5游戏中心申请的appkey
         * @param splashVisibility 是否启动闪屏 true or false
         * @param splashDismissCallBack  初始化完毕回调接口
         */
        QQ5Sdk.getInstance().init(MainActivity.this, QQ5Sdk.HORIZONTAL, "1", "H0ndi0tBTq", result, new SplashDismissCallBack() {
            @Override
            public void onDismiss() {
                setText("初始化回调成功。 AppId = " + QQ5Sdk.getInstance().getAppId() + "AppKey = " + QQ5Sdk.getInstance().getAppKey());
                //方法将在初始化完毕时调用
            }
        });
        
        //init 方法重载，不传splashVisibility 默认开启闪屏
        //QQ5Sdk.getInstance().init(MainActivity.this, QQ5Sdk.VERTICAL, "1", "H0ndi0tBTq", new SplashDismissCallBack() {
        //  @Override
        //  public void onDismiss() {
        //      setText("初始化回调成功");
        //      //方法将在初始化完毕时调用
        //  }
        //});

        
      //打开调试模式，方便查看log
        QQ5Sdk.getInstance().setDebugEnabled(true);

	}
	
	public void loginClick(View view) {
        /*
         * 登录接口，登录成功开启悬浮窗
         *
         * @param activity 当前游戏的Activity
         * @param loginCallBack  登录回调接口
         * @param isAutoLogin 默认为 true ，当为true时，如果检测到之前登录过，则会自动登录，是否自动登录
         */
        QQ5Sdk.getInstance().login(MainActivity.this, new LoginCallBack() {
            @Override
            public void success(String userId, String userName, String token) {
                setText("登录成功！欢迎你：" + userName);
                mLoginBtn.setEnabled(false);
                mEnterGameBtn.setEnabled(true);
                mSwitchAccountBtn.setEnabled(true);
            }

            @Override
            public void failed(String errorMsg) {
                setText("登录失败！原因:" + errorMsg);
            }

            @Override
            public void exit() {
                setText("登录中退出!");
                MainActivity.this.finish();
            }
        });
    }

    public void enterGameClick(View view) {
        /*
         * 进入游戏
         *
         * @param serverId 服务器id
         * @param serverName 服务器名称
         * @param roleId 游戏角色id
         * @param roleName 游戏角色名称
         * @param roleLevel 游戏角色等级
         * @param enterGameCallBack 进入游戏回调接口
         */
        QQ5Sdk.getInstance().enterGame(SERVER_ID, SERVER_NAME, ROLE_ID, ROLE_NAME, ROLE_LEVEL,
                new EnterGameCallBack() {
                    @Override
                    public void success() {
                        setText("进入游戏成功");

                        mEnterGameBtn.setEnabled(false);
                        mUpdateLevelBtn.setEnabled(true);
                        mSwitchAccountBtn.setEnabled(true);
                        mPayBtn.setEnabled(true);
                    }

                    @Override
                    public void failed(String message) {
                        setText("进入游戏失败！，原因：" + message);
                    }
                });
    }

    public void updateRoleClick(final View view) {
        EditText roleNameView = (EditText) findViewById(R.id.text_role_name);
        EditText roleLevelView = (EditText) findViewById(R.id.text_role_level);
        String roleName = roleNameView.getText().toString();
        String roleLevel = roleLevelView.getText().toString();
        if (TextUtils.isEmpty(roleName) || TextUtils.isEmpty(roleLevel)) {
            Toast.makeText(this, "用户角色名和等级", Toast.LENGTH_SHORT).show();
            return;
        }


        /*
         * 角色信息升级接口
         *
         * @param roleName 角色名称
         * @param roleLevel 角色等级
         * @param updateLevelCallBack 升级回调接口
         */
        QQ5Sdk.getInstance().updateLevel(roleName, roleLevel, new UpdateLevelCallBack() {
            @Override
            public void success() {
                setText("角色升级成功！");
                view.setEnabled(false);
                view.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        view.setEnabled(true);
                    }
                }, 2000);
            }

            @Override
            public void failed(String message) {
                setText("角色升级失败！原因：" + message);
            }
        });
    }

    public void switchAccountClick(View view) {
        /*
         * 切换账号接口
         *
         * @param activity 当前游戏的Activity
         * @param switchAccountCallBack 退出登录回调
         */
        QQ5Sdk.getInstance().switchAccount(MainActivity.this, new SwitchAccountCallBack() {
            @Override
            public void finish() {
                setText("切换账号回调！重新调用登录接口");
                mEnterGameBtn.setEnabled(false);
                mUpdateLevelBtn.setEnabled(false);
                mPayBtn.setEnabled(false);

                QQ5Sdk.getInstance().login(MainActivity.this, new LoginCallBack() {
                    @Override
                    public void success(String userId, String userName, String token) {
                        setText("登录成功！欢迎你：" + userName);
                        mEnterGameBtn.setEnabled(true);
                    }

                    @Override
                    public void failed(String errorMsg) {
                        setText("登录失败！原因:" + errorMsg);
                    }

                    @Override
                    public void exit() {
                        setText("登录中退出!");
                        //指明Activity finish(); 切勿直接写成finish();
                        MainActivity.this.finish();
                    }
                }, false);
            }
        });
    }

    public void pay(View view) {
        String amount = ((EditText) findViewById(R.id.qq5_demo_account)).getText().toString();
        if (amount.length() == 0) {
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
            return;
        }
        
        /*
         * 支付接口
         * 拉起支付界面，注册支付回调监听器。
         *
         * @param activity 当前游戏的Activity
         * @param gameOrderId 游戏订单id
         * @param gameCoin 游戏币数量(1)
         * @param gameCoinName 游戏币名称（元宝）
         * @param amount 充值金额须大于0.1，支持一位小数（单位：元）
         * @param extra 透传字段（SDK服务端回调原样返回）
         * @param payCallBack 支付回调接口
         */
        QQ5Sdk.getInstance().onPay(this, "12412412", "50000", "刀币", amount, "", new PayCallBack() {
            @Override
            public void failed(String message) {
                setText("支付失败！原因:" + message);
            }

            @Override
            public void success(String message) {
                setText("支付成功！" + message);
            }
        });
    }

    public void changeScreenClick(View view) {
        if (mIsPortrait) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        mIsPortrait = !mIsPortrait;
    }

    //切换横竖屏回调时接入该接口
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        QQ5Sdk.getInstance().orientationChange(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Bundle extras = data.getExtras();
            if (extras != null)
                Log.v("MainActivity", extras.toString());
        }
        QQ5Sdk.getInstance().handlerActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        /*
         * 游戏界面返回键处理（退出事件）
         * 打开退出应用提示
         *
         */
        QQ5Sdk.getInstance().handleBackAction(MainActivity.this, new ExitCallBack() {
            @Override
            public void exit() {
                finish();
            }
        });
    }

    private void setText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextView.setText(text);
            }
        });
    }
}
