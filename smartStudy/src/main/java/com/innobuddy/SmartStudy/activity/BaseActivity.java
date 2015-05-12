package com.innobuddy.SmartStudy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.lidroid.xutils.HttpUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * Activity 的基类
 * 
 * @author tangyichao
 * 
 */
public class BaseActivity extends Activity implements OnClickListener {
	
	protected HttpUtils http;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		/******************/
		http=new HttpUtils();
		http.configCurrentHttpCacheExpiry(0); // 请求缓存
		
	}

	@Override
	public void onClick(View v) {

	}


	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// SDK已经禁用了基于Activity 的页面统计，所以需要再次重新统计页面
		// MobclickAgent.onPageEnd(mPageName);
		MobclickAgent.onPause(this);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// SDK已经禁用了基于Activity 的页面统计，所以需要再次重新统计页面
		// MobclickAgent.onPageStart(mPageName);
		MobclickAgent.onResume(this);
	}
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		//进入动画
		//overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit1);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		//overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit1);
	}
	@Override
	public void finish() {
		super.finish();
		//结束动画
		//overridePendingTransition(R.anim.zoom_exit,R.anim.zoom_enter1);
	}

}
