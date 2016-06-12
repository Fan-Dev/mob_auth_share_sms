package com.crrain.testsms;

import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qzone.QZone;
import cn.smssdk.EventHandler;
import cn.smssdk.OnSendMessageHandler;
import cn.smssdk.SMSSDK;

public class MainActivity extends AppCompatActivity {
    EditText et_phone_num, et_sms_code;
    private String coutryCode = "86";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SMSSDK.initSDK(this, "13b1b75254784", "fcf81f7c414acb29ee7c218028d2cf5a", false);
        ShareSDK.initSDK(this);

        SMSSDK.registerEventHandler(new EventHandler() {
            @Override
            public void afterEvent(int event, int result, java.lang.Object data) {
                super.afterEvent(event, result, data);
                Log.d("aaa", event + "<afterEvent>" + result + "," + data);

                Log.d("aaa", event + "<afterEvent>" + result + ",myLooper=" + Looper.myLooper());
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        Log.d("aaa", event + "<afterEvent>" + result + ",校验成功");
                    } else {
                        Log.d("aaa", event + "<afterEvent>" + result + ",校验失败");
                    }
                }
            }
        });


        final Platform qZone = ShareSDK.getPlatform(QZone.NAME);
        qZone.setPlatformActionListener(new PlatformActionListener(){
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Log.d("aaa", platform + "<onComplete>" + hashMap);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.d("aaa", platform + "<onError>" + throwable);
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.d("aaa", platform + "<onCancel>" + i);
            }
        });
        if(qZone.isValid()){
            Log.d("aaa", "<qZone>授权成功");
            Log.d("aaa", qZone.getDb().getUserName());
            Log.d("aaa", qZone.getDb().getUserIcon());
            Log.d("aaa", qZone.getDb().getUserGender());
            Log.d("aaa", qZone.getDb().getUserId());
        }else{
            Log.d("aaa", "<qZone>授权失败");
        }
        qZone.isValid();

        et_phone_num = (EditText) findViewById(R.id.et_phone_num);
        et_sms_code = (EditText) findViewById(R.id.et_sms_code);

        findViewById(R.id.btn_getsmscode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SMSSDK.getVerificationCode(coutryCode, et_phone_num.getText().toString(), new OnSendMessageHandler() {
                    @Override
                    public boolean onSendMessage(String s, String s1) {
                        Log.d("aaa", s + "<onSendMessage>" + s1);
                        return false;
                    }
                });
            }
        });

        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SMSSDK.submitVerificationCode(coutryCode, et_phone_num.getText().toString(), et_sms_code.getText().toString());
            }
        });

        findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare();
            }
        });

        findViewById(R.id.btn_uinfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qZone.showUser(null);
            }
        });

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qZone.authorize();
                //移除授权
                //weibo.removeAccount(true);
            }
        });
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("分享的标题");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }
}
