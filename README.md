# qq5_game_sdk
Third party game platform

QQ5手游SDK

Android接入文档


版本|作者|内容|时间
:---:|:---:|:---:|:---:
1.0.0	|Feng Jibo <br>Zhu Dongya|初始版本|2017/5/2
1.0.1	|Feng Jibo <br>Zhu Dongya|文档完善|2017/5/25

# 目录

[1.准备工作](#prepare)

[2.配置和导入SDK](#input)

[3.SDK接口说明](#main)
1. [初始化](#init)
2. [登录](#login)
3. [进入游戏](#enterGame)
4. [支付](#pay)
5. [游戏内返回键处理](#backPress)
6. [游戏角色升级](#levelUp)
7. [切换账号](#switchAccount)

[4.内容补充](#moreInfo)


<h1 id="input">1.准备工作</h1>

1.联系官方人员获取AppId，AppKey。

  **AppId:产品ID**

  **AppKey:SDK通信key**

2.下载官方提供的SDK以及demo。

<h1 id="main">2.配置和导入SDK</h1>

**1.手动导入**

A.若是eclipse用户，工程里 project.properties 文件中 manifestmerger.enabled 设置为
  false, 或默认不加该配置。

B.将 sdk 工程中 Androidmanifest 中 require 标记内容拷贝到
  游戏工程 Androidmanifest中：
```xml
    <!-- QQ5sdk required -->
    <!-- 请求网络 -->
    <uses-permission android:name="android.permission.INTERNET" />
    <!-- 获取设备信息 -->
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <!-- SD读写文件 -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <!-- 读取网络状态 -->
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.GET_TASKS" />
    <!-- QQ5sdk required -->

    <!-- QQ5sdk required -->
        <meta-data
            android:name="QQ5_CHANNEL"
            android:value="your channel" />

        <activity
            android:name="com.qq5sdk.ui.userWeb.UserWebActivity"
            android:configChanges="keyboardHidden|orientation|screenSize"
            android:hardwareAccelerated="true"
            android:screenOrientation="portrait"
            android:windowSoftInputMode="adjustResize" />

        <activity
            android:name="com.heepay.plugin.activity.WeChatNotityActivity"
            android:configChanges="orientation|keyboardHidden|screenSize"
            android:screenOrientation="behind"
            android:theme="@android:style/Theme.Translucent.NoTitleBar" />

        <activity
            android:name="com.alipay.sdk.app.H5PayActivity"
            android:configChanges="orientation|keyboardHidden|navigation|screenSize"
            android:exported="false"
            android:screenOrientation="behind"
            android:windowSoftInputMode="adjustResize|stateHidden" />

        <activity
            android:name="com.alipay.sdk.auth.AuthActivity"
            android:configChanges="orientation|keyboardHidden|navigation"
            android:exported="false"
            android:screenOrientation="behind" />

        <activity
            android:name="com.ipaynow.plugin.presenter.PayMethodActivity"
            android:configChanges="keyboardHidden|navigation|orientation|screenSize"
            android:exported="false"
            android:screenOrientation="portrait"
            android:theme="@android:style/Theme.Dialog" />

        <activity
            android:name="com.ipaynow.plugin.inner_plugin.wechatwp.activity.WeChatNotifyActivity"
            android:configChanges="keyboardHidden|navigation|orientation|screenSize"
            android:exported="false"
            android:launchMode="singleTop"
            android:screenOrientation="behind"
            android:theme="@android:style/Theme.Dialog" />
        <!-- QQ5sdk required -->

```


C.Lib目录下文件根据游戏工程所支持进行选择性拷贝
  如：游戏工程lib只有armeabi文件夹，则保留同名文件夹，删除其余文件夹。如果游戏工程没有任何同名文件夹，则默认使用armeabi文件夹。如图:

![avatar](qq5_lib_preview.png)

D.拷贝res目录下所有文件夹到游戏工程里面，注意命名是否重复，防止覆盖。res里面的文件请勿修改或删除，从而影响功能的使用。

![avatar](qq5_res_preview.png)

<h1 id="moreInfo">3.SDK接口说明</h1>

<h2 id="init">1.初始化</h2>

初始化方法 init() 定义在 QQ5SDK.java 中，请在 QQ5SDK.getInstance() 后即可调用。

**在启动游戏时调用，必须先调用初始化接口才能调用其他接口。**

调用示例：
```java
    /**
     * 初始化接口 (必接)
     *
     * @param activity   当前游戏的Activity
     * @param orientation    横屏 HORIZONTAL 竖屏 VERTICAL
     * @param appId    在QQ5游戏中心申请的appid
     * @param appKey   在QQ5游戏中心申请的appkey
     * @param splashDismissCallBack  初始化完毕回调接口
     */
     QQ5Sdk.getInstance().init(activity, orientation, appId, appKey, new SplashDismissCallBack() {
         @Override
         public void onDismiss() {
              //方法将在初始化完毕时调用
         }
     });

```

参数表：

参数名称|类型|备注|是否必填
:---:|:---:|:---:|:---:
activity|android.app.Activity|当前游戏的Activity|是
orientation|int|请填写 QQ5Sdk.HORIZONTAL 或 QQ5Sdk.VERTICAL|是
appId|String|在QQ5游戏中心申请的appid|是
appKey|String|在QQ5游戏中心申请的appkey|是
splashDismissCallBack|com.qq5sdk.api.SplashDismissCallBack|初始化完毕回调接口|是


<h2 id="login">2.登录</h2>

登录方法 login() 定义在 QQ5SDK.java 中，请在 QQ5SDK.getInstance() 后即可调用。

此方法具有两个重载方法，请根据需求自行选择。

调用示例：

```java
    /**
     * 登录接口，登录成功开启悬浮窗 (必接)
     *
     * @param activity 当前游戏的Activity
     * @param loginCallBack  登录回调接口
     * @param isAutoLogin 默认为 true ，当为true时，如果检测到之前登录过，则会自动登录，是否自动登录
     */
    QQ5Sdk.getInstance().login(activity, new LoginCallBack() {
      /**
       * @param userId 用户id
       * @param userName 用户名称
       * @param token  token
       */
        @Override
        public void success(String userId, String userName, String token) {
          //登录成功
        }

        @Override
        public void failed(String errorMsg) {
          //失败，请查看错误详情
        }

        @Override
        public void exit() {
          //登录过程中点击退出游戏按钮时触发
        }
    }, isAutoLogin);


    /**
     * 参见方法{@link QQ5Sdk#login(Activity, LoginCallBack, boolean)}

     * @param activity 当前游戏的Activity
     * @param loginCallBack  登录回调接口
     */
    QQ5Sdk.getInstance().login(activity,  new LoginCallBack() {

        @Override
        public void success(String userId, String userName, String token) {
          //登录成功
        }

        @Override
        public void failed(String errorMsg) {
          //失败，请查看错误详情
        }

        @Override
        public void exit() {
          //登录过程中点击退出游戏按钮时触发
        }
    });

```

参数表：

参数名称|类型|备注|是否必填
:---:|:---:|:---:|:---:
activity|android.app.Activity|当前游戏的Activity|是
loginCallBack|com.qq5sdk.api.LoginCallBack|登录回调接口|是
isAutoLogin|boolean|判断是否自动登录，默认为 true ，当为true时，如果检测到之前登录过，则会自动登录|否


<h2 id="enterGame">3.进入游戏</h2>

进入游戏方法 enterGame() 定义在 QQ5SDK.java 中，请在 QQ5SDK.getInstance() 后即可调用。
登录成功后才能调用该接口。

调用示例：

```java

    /**
     * 进入游戏
     *
     * @param serverId 服务器id
     * @param serverName 服务器名称
     * @param roleId 游戏角色id
     * @param roleName 游戏角色名称
     * @param roleLevel 游戏角色等级
     * @param enterGameCallBack 进入游戏回调接口
     */
    QQ5Sdk.getInstance().enterGame(serverId, serverName, roleId, roleName, roleLevel,
            new EnterGameCallBack() {
        @Override
        public void success() {
            //进入游戏成功
        }

        @Override
        public void failed(String message) {
          //失败，请查看错误详情
        }
    });
```


参数表：

参数名称|类型|备注|是否必填
:---:|:---:|:---:|:---:
serverId|java.lang.String| 服务器id|是
serverName|java.lang.String|服务器名称|是
roleId|java.lang.String|游戏角色id|是
roleName|java.lang.String|游戏角色名称|是
roleLevel|java.lang.String|游戏角色等级|是


<h2 id="pay">4.支付及其结果回调</h2>

支付方法 enterGame() 定义在 QQ5SDK.java 中，请在 QQ5SDK.getInstance() 后即可调用。
进入游戏后才能调用该接口。

调用示例：

```java
    /**
     * 支付接口 (必接)
     * 拉起支付界面，注册支付回调监听器。
     *
     * @param activity 当前游戏的Activity
     * @param gameOrderId 游戏订单id
     * @param gameCoin 游戏币名称（元宝）
     * @param gameCoinName 游戏币名称（元宝）
     * @param amount 充值金额须大于0.1，支持一位小数（单位：元）
     * @param extra 透传字段（SDK服务端回调原样返回）
     * @param payCallBack 支付回调接口
     */
    QQ5Sdk.getInstance().onPay(activity, gameOrderId, gameCoin, gameCoinName, amount, extra, new PayCallBack() {

        @Override
        public void success(String message) {
              //支付成功
        }

        @Override
        public void failed(String message) {
            //失败，请查看错误详情
        }

    });
```

参数表：

参数名称|类型|备注|是否必填
:---:|:---:|:---:|:---:
activity|android.app.Activity|当前游戏的Activity|是
gameOrderId|java.lang.String| 游戏订单id|是
gameCoin|java.lang.String|游戏币数量|是, 可为空字符串，不能为null
gameCoinName|java.lang.String|游戏币名称（元宝）|是
amount|java.lang.String|充值金额须大于0.1，支持一位小数（单位：元）|是
extra|java.lang.String|透传字段（SDK服务端回调原样返回）|否
payCallBack|com.qq5sdk.api.PayCallBack|支付回调接口|是

同时请在游戏支付的 Activity（即为 onPay方法的第一个参数 ）中，请复写该 Activity 的 onActivityResult 方法中调用 QQ5Sdk 的 handlerActivityResult方法，如果不添加的话，将无法收到支付结果的回调。

调用示例：

```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        QQ5Sdk.getInstance().handlerActivityResult(requestCode, resultCode, data);
    }
```

<h2 id="backPress">5.游戏内返回键处理</h2>

此方法可以在游戏内按下返回键触发，可以弹出退出提示等业务逻辑。

建议在 Activity#onBackPressed 方法或者 onKeyDown 方法中调用。

方法 handleBackAction() 定义在 QQ5SDK.java 中，请在 QQ5SDK.getInstance() 后即可调用。

调用示例：

```java
      /**
      * 游戏界面返回键处理（退出事件）
      * 打开退出应用提示
      *
      * @param activity 当前游戏的Activity
      * @param exitCallBack 返回事件回调
      */
      QQ5Sdk.getInstance().handleBackAction(activity, new ExitCallBack() {
          @Override
          public void exit() {
              //此回调将在点击“退出游戏”按钮时触发
          }
      });
```



参数表：

参数名称|类型|备注|是否必填
:---:|:---:|:---:|:---:
activity|android.app.Activity|当前游戏的Activity|是
exitCallBack|com.qq5sdk.api.ExitCallBack|返回事件回调|是


<h2 id="levelUp">6.游戏角色升级</h2>

方法 updateLevel() 定义在 QQ5SDK.java 中，请在 QQ5SDK.getInstance() 后即可调用。
进入游戏后，每次玩家名称等级有变化都需要调用该接口。

```java
    /**
    * 角色信息升级接口
    *
    * @param roleName 角色名称
    * @param roleLevel 角色等级
    * @param updateLevelCallBack 升级回调接口
    */
    QQ5Sdk.getInstance().updateLevel(roleName, roleLevel, new UpdateLevelCallBack() {
        @Override
        public void success() {
            //操作成功
        }

        @Override
        public void failed(String message) {
            //失败，请查看错误详情
        }
    });
```

参数表：

参数名称|类型|备注|是否必填
:---:|:---:|:---:|:---:
roleName|java.lang.String|游戏角色名称|是
roleLevel|java.lang.String|游戏角色id|是
updateLevelCallBack|com.qq5sdk.api.UpdateLevelCallBack|升级回调接口|是

<h2 id="switchAccount">7.切换账号</h2>


方法 switchAccount() 定义在 QQ5SDK.java 中，请在 QQ5SDK.getInstance() 后即可调用。

此方法调用后当前账号即退出登录。

```java
        /**
         * 切换账号接口 (可接)
         *
         * @param activity 当前游戏的Activity
         * @param switchAccountCallBack 切换账号回调
         */
        QQ5Sdk.getInstance().switchAccount(activity, new SwitchAccountCallBack() {
            @Override
            public void finish() {
                //当前账号已退出, 再次登录Login()接口，且是否自动设为false。
            }
        });
```

参数表：

参数名称|类型|备注|是否必填
:---:|:---:|:---:|:---:
activity|android.app.Activity|当前游戏的Activity|是
switchAccountCallBack|com.qq5sdk.api.SwitchAccountCallBack|切换账号回调|是

<h2 id="orientationChange">8.横竖屏切换回调接入接口(若游戏不支持横竖屏切换可不接)</h2>

方法 orientationChange() 定义在 QQ5SDK.java 中，请在 QQ5SDK.getInstance() 后即可调用。

调用示例：
```java
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        QQ5Sdk.getInstance().orientationChange(this);
    }
```

参数表：

参数名称|类型|备注|是否必填
:---:|:---:|:---:|:---:
activity|android.app.Activity|当前游戏的Activity|是

<h2 id="getAppId">9.获取AppId</h2>

方法 getAppId() 定义在 QQ5SDK.java 中，请在 QQ5SDK.getInstance() 后即可调用。

<h2 id="getAppKey">10.获取AppKey</h2>

方法 getAppKey() 定义在 QQ5SDK.java 中，请在 QQ5SDK.getInstance() 后即可调用。

<h1 id="moreInfo">4.补充内容</h1>

**调试时请优先设置QQ5Sdk.getInstance().setDebugEnabled(true)， 方便查看log。**

**具体使用场景，调用方式，请参考demo，必要时可联系官方技术人员。**
