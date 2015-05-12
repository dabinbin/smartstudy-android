package com.innobuddy.SmartStudy.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.innobuddy.SmartStudy.R;
import com.innobuddy.SmartStudy.utils.VersionUtils;

public class AboutUsActivity extends BaseActivity {
	private TextView mTvVersion;
	private LinearLayout mLlPhone;
	private TextView mTvCurrentVersion;
	private LinearLayout mLlAgreement;
	private LinearLayout mLlUpdateVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
		mTvVersion = (TextView) findViewById(R.id.tv_version);
		mLlPhone = (LinearLayout) findViewById(R.id.ll_phone);
		mTvCurrentVersion = (TextView) findViewById(R.id.tv_current_version);
		mLlAgreement = (LinearLayout) findViewById(R.id.ll_agreement);
		mLlUpdateVersion = (LinearLayout) findViewById(R.id.ll_update_version);
		String version = VersionUtils.getAppVersion(this);
		String currentVersion=VersionUtils.getAppCurrentVersion(this);
		mTvVersion.setText(version);
		mTvCurrentVersion.setText(currentVersion);
		mLlPhone.setOnClickListener(this);
		mLlAgreement.setOnClickListener(this); //
		mLlUpdateVersion.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.ll_phone:
			Intent intent=new Intent();
			intent.setAction(Intent.ACTION_DIAL);   //android.intent.action.DIAL
			intent.setData(Uri.parse("tel:4000119191"));
			startActivity(intent);
			break;
		case R.id.ll_agreement:
			//Toast.makeText(this, "协议", Toast.LENGTH_LONG).show();
			Intent agreementIntent=new Intent();
			agreementIntent.putExtra("url", "http://www.smartstudy.com/service");
			agreementIntent.setClass(this, WebViewActivity.class);
			startActivity(agreementIntent);
			break;
		case R.id.ll_update_version:
			Toast.makeText(this, "已经是最新版本", Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
	}
}
