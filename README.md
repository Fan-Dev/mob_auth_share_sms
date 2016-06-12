# mob_auth_share_sms
Mob服务测试，包含 授权登录 分享 短信验证码
>>官网地址：http://www.mob.com/

##授权登录
>>授权用的是ShareSDK.initSDK(this);

```
//QQ空间为例
final Platform qZone = ShareSDK.getPlatform(QZone.NAME);
//授权/获取信息的回调
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
	Log.d("aaa", "<qZone>已经授权");
	Log.d("aaa", qZone.getDb().getUserName());
	Log.d("aaa", qZone.getDb().getUserIcon());
	Log.d("aaa", qZone.getDb().getUserGender());
	Log.d("aaa", qZone.getDb().getUserId());
}else{
	Log.d("aaa", "<qZone>尚未授权");
	//发起授权
	qZone.authorize();
}

//获取用户信息，结果会在回调方法onComplete的map中。
qZone.showUser(null);
```

##分享
>>1.授权用的是ShareSDK.initSDK(this);
>>2.注意分享使用到的图片资源不要放到 mipmap中，否则找不到。应放到drawable-xxx中。

```
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
```

##短信验证码
>>短信验证码用的是SMSSDK

```
//初始化
SMSSDK.initSDK(this, "KEY", "SECRET", false);
//注册事件监听,具体事件使用event判断，常量在SMSSDK.EVENT_XXXXX中。
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
```
###获得验证码
>>大陆的coutryCode是86
>>(而且没在文档中见到，虽说SMSSDK.getSupportedCountries();可以查询，但是首先要等回调，而且回调的map中说明也不明确)。

```
SMSSDK.getVerificationCode(coutryCode, et_phone_num.getText().toString(), new OnSendMessageHandler() {
	@Override
	public boolean onSendMessage(String s, String s1) {
		//发送成功，可以进入倒计时
		Log.d("aaa", s + "<onSendMessage>" + s1);
		return false;
	}
});
```

###校验验证码
```
//结果等待EventHandler
SMSSDK.submitVerificationCode(coutryCode, 手机号,验证码);
```
